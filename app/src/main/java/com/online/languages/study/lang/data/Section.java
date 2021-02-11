package com.online.languages.study.lang.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.online.languages.study.lang.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.online.languages.study.lang.Constants.CAT_SPEC_ITEMS_LIST;
import static com.online.languages.study.lang.Constants.CAT_SPEC_MAPS;
import static com.online.languages.study.lang.Constants.CAT_SPEC_TEXT;

public class Section {

    public String title;
    public String desc;
    public String title_short;
    public String desc_short;
    public String id;
    public String tag;
    public String image;
    public String type = "";

    public int progress = 0;

    public int testResults = 0;

    public int controlTests = 0;

    public int knownPart = 0;
    public int studiedPart = 0;

    public ArrayList<DataItem> allData;
    public ArrayList<DataItem> errorsData;


    public int studiedDataCount = 0;
    public int knownDataCount = 0;

    public int familiarDataCount = 0;
    public int unknownDataCount = 0;
    public int allDataCount = 0;

    public int customItemsCount = 0;

    public int studiedResult = 0;
    public int knownResult = 0;
    public int testResult = 0;

    public int stadiedCatsCount = 0;
    public int errorsCount = 0;

    public ArrayList<Category> categories = new ArrayList<>();;
    public ArrayList<String> catIds = new ArrayList<>();

    public ArrayList<String> checkCatIds = new ArrayList<>();
    public ArrayList<String> allCatIds = new ArrayList<>();

    public Map<String,String> controlMap = new HashMap<>();


    private int check_control = 1;
    private String dataSelect = "dates";
    private Boolean haveExtra;


    public Section() {}


    public Section(NavSection navSection, Context context) {

        id = navSection.id;
        title = navSection.title;
        desc = navSection.desc;
        title_short = navSection.title_short;
        desc_short = navSection.desc_short;
        image = navSection.image;
        type = navSection.type;

        haveExtra = false;

        catIds = navSection.catIdList;
        allCatIds = navSection.catIdList;

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        check_control = Integer.valueOf(appSettings.getString("control_tests", "1"));
        dataSelect = appSettings.getString("data_select", "dates");

        for (NavCategory navCategory: navSection.uniqueCategories) { /// TODO cat selection

            if (navCategory.type.equals(Constants.CAT_TYPE_EXTRA)) haveExtra = true;

            if (navCategory.spec.equals(CAT_SPEC_ITEMS_LIST)
                    || navCategory.spec.equals(CAT_SPEC_TEXT)
                    || navCategory.spec.equals(CAT_SPEC_MAPS)) continue;

            if (dataSelect.equals("dates")) {
                if (!navCategory.type.equals(Constants.CAT_TYPE_EXTRA)) {
                    categories.add(new Category(navCategory));
                    checkCatIds.add(navCategory.id);
                }
            } else {
                    categories.add(new Category(navCategory));
                    checkCatIds.add(navCategory.id);
            }
        }

        catIds = new ArrayList<>(checkCatIds);
    }


    public Section(String _title, String _desc) {
        title = _title;
        desc = _desc;
    }


    public void calculateProgress() {

        if (allDataCount > 0) {
            familiarDataCount = studiedDataCount + knownDataCount;
            unknownDataCount = allDataCount - familiarDataCount;

            studiedPart = (100 * studiedDataCount / allDataCount);
            knownPart = (100 * familiarDataCount/ allDataCount);
            studiedResult = (100 * studiedDataCount/ allDataCount) * 20 /100;
        }




        knownResult =  knownPart * 30 / 100 ;

        controlTests = calculateControl ();

        if (check_control == 1) {
            if (controlTests > testResults) testResults = controlTests;
        } else if (check_control == 2) {
            testResults = controlTests;
        }

        testResult  =  testResults * 50/100 ;

        progress = studiedResult + knownResult + testResult ;

    }


    private int calculateControl() {

        int result = 0;
        int testCount = Constants.SECTION_TESTS_NUM;
        if (dataSelect.equals("all") && haveExtra) testCount = Constants.SECTION_TESTS_NUM_ALL;

        for (Map.Entry<String, String> entry : controlMap.entrySet()) {
            String id = entry.getKey();
            int testResult = Integer.valueOf(entry.getValue()) ;

            if (id.contains("_gen")) {
                if (dataSelect.equals("all")) result += testResult;
            } else {
                result += testResult;
            }
        }

        result = result / testCount;

        return result;
    }

    public void sortSectionErrors() {
        Collections.sort(errorsData, new ErrorsTimeComparator());
        Collections.sort(errorsData, new ErrorsCountComparator());
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
