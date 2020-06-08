package com.online.languages.study.lang.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.Computer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.online.languages.study.lang.Constants.FILTER_CHRONO;
import static com.online.languages.study.lang.Constants.SET_GALLERY;
import static com.online.languages.study.lang.Constants.SET_HOMECARDS;
import static com.online.languages.study.lang.Constants.SET_SIMPLIFIED;
import static com.online.languages.study.lang.Constants.SET_STATS;


public class DataManager {

    private Context context;

    public DBHelper dbHelper;
    public ArrayList<NavCategory> navCategories;
    public SharedPreferences appSettings;
    private Computer computer;


    public DataManager(Context _context) {
        context = _context;
        dbHelper = new DBHelper(context);
        computer = new Computer();
        appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        getParams();
    }

    public DataManager(Context _context, Boolean getParams) {
        context = _context;
        appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        if (getParams) getParams();
    }

    public DataManager(Context _context, int type) {
        context = _context;
        dbHelper = new DBHelper(context);
        appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        if (type == 1) getUniquesCats();
    }

    private void getUniquesCats() {
        DataFromJson dataFromJson = new DataFromJson(context);
        navCategories = dataFromJson.getAllUniqueCats();
    }


    public ArrayList<DataItem> getCatDBList(String cat) {
        return dbHelper.getCatByTag(cat);
    }

    public ArrayList<DataItem> getSectionDBList(NavSection navSection) {
        return dbHelper.getAllDataItems(navSection.uniqueCategories);
    }


    public String getTranscriptType() {

       String type = "ipa";

        if (context.getResources().getBoolean(R.bool.changeTranscript)) {
            type = appSettings.getString("set_transript", "ipa");
        }

        return type;
    }

    public String getTranscriptFromData(DataItem dataItem) {

        String type = getTranscriptType();

        String text = dataItem.trans1;

        if (type.equals("ru")) text = dataItem.trans2;

        if (type.equals("none")) text = "";

        return text;
    }


    public ArrayList<DataItem> checkDataItemsData(ArrayList<DataItem> dataItems) {
        return dbHelper.checkStarredList(dataItems);
    }

    public boolean checkStarStatusById(String id) {
        return dbHelper.checkStarred(id);
    }


    public DetailItem getDetailFromDB(String id) {
        return dbHelper.getDetailById(id);
    }

    public DataItem getDataItemFromDB(String id) {
        return dbHelper.getDataItemById(id);
    }

    int getMapsCount(String catId) {
        ArrayList<DataItem> data = dbHelper.getCatByTag(catId);
        return data.size();
    }

    public ArrayList<DataItem> getStarredWords(Boolean sort) {
        return getStarredWords(1, sort);
    }


    public ArrayList<DataItem> getStarredWords(int type, Boolean sort) {

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);

        String sortType = appSettings.getString("starred_sort_type", "0");

        if (!sort) sortType = "0";

        ArrayList<DataItem> dataItems;

        if (type == 2) dataItems = dbHelper.getStarredFromDB(2, navCategories);
        else dataItems = dbHelper.getStarredFromDB(navCategories);

        if (sortType.equals("0")) {
            Collections.sort(dataItems, new TimeStarredComparator());
        } else if (sortType.equals("1")) {
            Collections.sort(dataItems, new TimeStarredComparator());
            Collections.reverse(dataItems);
        }
        return dataItems;
    }


    public ArrayList<DataItem> getDataForSectionReview(ArrayList<DataItem> dataItems) {

        ArrayList<DataItem> checkedData = new ArrayList<>();


        for (DataItem dataItem: dataItems) {

            if (dataItem.mode == -1  || dataItem.type.equals("group_title")) {

                checkedData.add(dataItem);
            }

        }

        return checkedData;
    }

    public ArrayList<DataItem> getCatCustomList(ArrayList<NavCategory> categories, int type) {

        ArrayList<DataItem> dataItems = dbHelper.getDataItemsByCats(categories);

        ArrayList<DataItem> resultDataItems = new ArrayList<>();

        for (DataItem dataItem : dataItems) {
            if (type == 0) { // studied
                if (dataItem.rate > 2) resultDataItems.add(dataItem);
            } else if (type == 1) { // familiar
                if (dataItem.rate > 0) resultDataItems.add(dataItem);
            } else if (type == 2) { // unknown
                if (dataItem.rate < 1) resultDataItems.add(dataItem);
            }
        }

        if (type == 1) {
            Collections.sort(resultDataItems, new ScoreCountComparator());
        }

        return resultDataItems;

    }


    public UserStatsData getSectionsDataFromDB(UserStatsData userStatsData) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Section section : userStatsData.sectionsDataList) {


            section = dbHelper.selectSectionDataFromDB(db, section);

            section.calculateProgress();
        }

        db.close();
        return userStatsData;

    }


    public ArrayList<DataItem> getCatCustomList(String cat, int type) {


        ArrayList<DataItem> dataItems = getCatDBList(cat);
        ArrayList<DataItem> resultDataItems = new ArrayList<>();

        ArrayList<DataItem> helperDataItems = new ArrayList<>();

        for (DataItem dataItem : dataItems) {
            if (type == 0) { // studied
                if (dataItem.rate > 2) resultDataItems.add(dataItem);
            } else if (type == 1) { // familiar
                if (dataItem.rate > 0 && dataItem.rate < 3) resultDataItems.add(dataItem);

                if (dataItem.rate > 2) helperDataItems.add(dataItem);

            } else if (type == 2) { // unknown
                if (dataItem.rate < 1) resultDataItems.add(dataItem);
            }
        }

        if (type == 1) resultDataItems.addAll(helperDataItems);


        return resultDataItems;
    }





    public Map<String, String> getCatProgress(ArrayList<String> catIds) {

        boolean speaking = appSettings.getBoolean("set_speak", true);

        String mode = "sound";
        if (!speaking) mode = "nosound";

        Map<String, String> catMapWithProgress = new HashMap<>();

        Map<String, ArrayList<String>> catMapWithTests = getExResults(catIds); /// got all tests of the sections

        for (String catId: catIds) {

            ArrayList<String> results = catMapWithTests.get(catId);

            assert results != null;
            int  progress = calculateProgressByList(results, mode);

            catMapWithProgress.put(catId, String.valueOf(progress));
        }

        return catMapWithProgress;
    }



    private String getFilterValue(DataItem dataItem, String filterTag) {

        String dataFilter = dataItem.filter;

        String filterValue = "3000";

        String[] filters = dataFilter.split("&");

        for (String filter: filters) {

            if (filter.contains(filterTag)) {
                String[] filterSplit = filter.split("=");
               if (filterSplit.length >1) filterValue = filterSplit[1];
            }

        }

        return filterValue;
    }


    private class TimeStarredComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return o1.starred_time <= o2.starred_time ? 1 : -1;
        }
    }

    private class ScoreCountComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return (o1.rate - o2.rate);
        }
    }

    private class OrderComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return (o1.order - o2.order);
        }
    }


    public void getParamsAndSave() {
        getParamsFromJSON();
        saveParams();
    }

    public boolean simplified = false;
    public boolean homecards = false;
    public boolean gallerySection = false;
    public boolean statsSection = true;

    public void getParamsFromJSON() {

        DataFromJson dataFromJson = new DataFromJson(context);
        Map<String, Boolean> paramsList = dataFromJson.getParams();

        simplified = paramsList.get("simplified");
        homecards = paramsList.get("homecards");
        gallerySection = paramsList.get("gallery");
        statsSection = paramsList.get("stats");
    }

    private void saveParams() {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(SET_SIMPLIFIED, simplified);
        editor.putBoolean(SET_HOMECARDS, homecards);
        editor.putBoolean(SET_GALLERY, gallerySection);
        editor.putBoolean(SET_STATS, statsSection);
        editor.apply();
    }

    public void getParams() {
        simplified = appSettings.getBoolean(SET_SIMPLIFIED, false);
        homecards = appSettings.getBoolean(SET_HOMECARDS, false);
        gallerySection = appSettings.getBoolean(SET_GALLERY, false);
        statsSection = appSettings.getBoolean(SET_STATS, true);
    }

    public void getScreenSize() {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Toast.makeText(context, "H: " + dpHeight + ": W"+ dpWidth, Toast.LENGTH_SHORT).show();
    }


    public Map<String, ArrayList<String>> getExResults(ArrayList<String> catIdsList) {
        Map<String, String> testsMap =  dbHelper.getTestsByCatId(catIdsList);
        return getCatExResults(catIdsList, testsMap);
    }

    private Map<String, ArrayList<String>> getCatExResults(ArrayList<String> catIdsList, Map<String, String> testsMap) {
        return computer.getCatExResults(catIdsList, testsMap);
    }

    public int calculateProgressByList(ArrayList<String> results, boolean sound) {
        String mode = "sound";
        if (!sound) mode = "nosound";
        return calculateProgressByList(results, mode);
    }


    public int calculateProgressByList(ArrayList<String> results, String mode) {
        return computer.calculateProgressByList(results, mode);
    }




}