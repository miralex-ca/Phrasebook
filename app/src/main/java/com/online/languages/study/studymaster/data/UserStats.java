package com.online.languages.study.studymaster.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;


import com.online.languages.study.studymaster.Constants;
import com.online.languages.study.studymaster.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class UserStats {
    private Context context;

    private DataFromJson dataFromJson;
    private DBHelper dbHelper;

    private DataManager dataManager;

   // private String dataFile;

    public UserStatsData userStatsData;

    Boolean showWorld;

    private  NavStructure navStructure;
    public ArrayList<NavCategory> uniqueCats;


    public UserStats(Context _context, NavStructure _navStructure) {
        context = _context;
        navStructure = _navStructure;

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        showWorld = appSettings.getBoolean("world_section", false);

        dataManager = new DataManager(context);
        dbHelper = new DBHelper(context);
        userStatsData = new UserStatsData();
        uniqueCats = navStructure.getUniqueCats();

        getStatsStructure();
        getCatIds(userStatsData.sectionsDataList);


    }


    public void updateData() {

        getSectionsDataDB();
        getErrorsData();
        checkDataItemsStats();

    }


    private void getCatIds(ArrayList<Section> sections) {

        userStatsData.allUniqueIds = new ArrayList<>();
        userStatsData.idsToCheck = new ArrayList<>();

        for (Section section: sections) {
            if (! section.type.equals("gallery")) {
                userStatsData.idsToCheck.addAll(section.checkCatIds);
                userStatsData.allUniqueIds.addAll(section.allCatIds);
            }

        }
    }


    private void checkDataItemsStats() {
        dbHelper.getAllDataItemsStats(this);
    }


    private void getStatsStructure() {

        for (NavSection navSection: navStructure.sections) {

            if (navSection.spec.equals("world")) {
               if (showWorld) userStatsData.sectionsDataList.add(new Section(navSection, context));
            } else {
                if (!navSection.type.equals("gallery")) // TODO check to change to spec
                userStatsData.sectionsDataList.add(new Section(navSection, context));
            }

        }
    }


    private void getSectionsDataDB() {
        userStatsData  = dbHelper.checkAppStatsDB(userStatsData);

        //userStatsData = dataManager.getSectionsDataFromDB(userStatsData);
    }


    public UserStats(Context _context) {
        context = _context;

        dataFromJson = new DataFromJson(context);

        dbHelper = new DBHelper(context);

      //  dataFile = context.getString(R.string.app_data_file);
        userStatsData = new UserStatsData();

        getSectionsData();
        getErrorsData();
    }


    private void getErrorsData() {
            userStatsData = dbHelper.getErrorsData(userStatsData);
            sortUserStatsLists();
    }



    private void getSectionsData() {

        userStatsData.sectionsDataList = dataFromJson.getSectionsList();
        getSectionsDataFromDB();

        for (Section section: userStatsData.sectionsDataList) {
            section.calculateProgress();
            userStatsData.studiedDataCount = userStatsData.studiedDataCount+ section.studiedDataCount;
            userStatsData.familiarDataCount = userStatsData.familiarDataCount + section.familiarDataCount;
            userStatsData.unknownDataCount = userStatsData.unknownDataCount + section.unknownDataCount;
        }
    }

    private void getSectionsDataFromDB() {  /// TODO refactor
        userStatsData.sectionsDataList  = dbHelper.checkSectionsStats(userStatsData.sectionsDataList); // get data count from DB
    }


    public ArrayList<DataItem> getAllDataFromJson() {


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        userStatsData.allWords = dbHelper.selectSimpleDataItemsByIds(db, userStatsData.idsToCheck);

        db.close();

        return userStatsData.allWords;
    }


    public ArrayList<DataItem> getOldestLIst() {

        userStatsData.reviseWords = dbHelper.getOldestFromDB(userStatsData.allUniqueIds);

        Collections.sort(userStatsData.reviseWords, new TimeComparator());
        Collections.reverse(userStatsData.reviseWords);

        return userStatsData.reviseWords;
    }



    private void sortUserStatsLists() {
        Collections.sort(userStatsData.recentWords, new TimeComparator());
        Collections.sort(userStatsData.errorsWords, new ErrorsTimeComparator());
        Collections.sort(userStatsData.errorsWords, new ErrorsCountComparator());

        Collections.sort(userStatsData.mostErrorsWords, new ErrorsTimeComparator());
        Collections.sort(userStatsData.mostErrorsWords, new ErrorsCountComparator());
    }


    private void sortSectionErrors(Section section) {
        Collections.sort(section.errorsData, new ErrorsTimeComparator());
        Collections.sort(section.errorsData, new ErrorsCountComparator());
    }


    private class TimeComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return o1.time <= o2.time ? 1 : -1;
        }
    }

    private class TimeDownComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return o1.time >= o2.time ? 1 : -1;
        }
    }

    private class ErrorsTimeComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return o1.time_errors <= o2.time_errors ? 1 : -1;
        }
    }

    private class ErrorsCountComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return ( o2.errors - o1.errors);
        }
    }


}
