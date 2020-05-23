package com.online.languages.study.lang.data;

import android.content.Context;

import com.online.languages.study.lang.DBHelper;

import java.util.ArrayList;

public class TestResult {


    private ArrayList<DataItem> dataItems;


    public ArrayList<DataItem> testErrors;
    public ArrayList<DataItem> unanswered;

    private Context context;
    private DBHelper dbHelper;

    private NavStructure navStructure;

    public ArrayList<ResultCategory> sections;
    public ArrayList<ResultCategory> categories;

    public ArrayList<ResultCategory> errorSections;
    public ArrayList<ResultCategory> errorCategories;


    public TestResult() {
    }


    public TestResult(Context _context, ArrayList<DataItem> _data) {
        dataItems = new ArrayList<>(_data);
        context  = _context;
        dbHelper = new DBHelper(context );
        DataFromJson dataFromJson = new DataFromJson(context);
        navStructure = dataFromJson.getStructure();

        sections = new ArrayList<>();
        categories = new ArrayList<>();
        testErrors = new ArrayList<>();
        unanswered = new ArrayList<>();

        errorSections = new ArrayList<>();
        errorCategories = new ArrayList<>();

        getData();
    }



    private void getData() {

        dataItems = dbHelper.getTestDataByIds(dataItems);

        for (DataItem dataItem: dataItems) {
            if (dataItem.testError == 1) testErrors.add(dataItem);
            if (dataItem.testError == -1) unanswered.add(dataItem);
        }

        structureData();
    }



    private void structureData() {

        for (NavSection navSection: navStructure.sections) {
            ResultCategory section = new ResultCategory();
            section.dataItems = new ArrayList<>();
            section.title = navSection.title;

            for (NavCategory navCategory: navSection.navCategories) {
                ResultCategory category = new ResultCategory();
                category.dataItems = new ArrayList<>();

                category.title = navCategory.title;

                for (DataItem dataItem: dataItems) {
                    if (dataItem.id.matches(navCategory.id+".*")) {

                        // adding entry to errors if error
                        if (dataItem.testError != 0) {
                            section.errors.add(dataItem);
                            category.errors.add(dataItem);
                        }

                        // adding entry
                        section.dataItems.add(dataItem);
                        category.dataItems.add(dataItem);
                    }
                }


                if (category.dataItems.size() > 0) {

                    String str = "";

                    for (int i = 0; i< category.errors.size(); i++) {

                        DataItem dataItem = category.errors.get(i);

                        if (i!=0) str += "<br><br>";
                        str = str + "<b>" + dataItem.item + "</b><br>" + dataItem.info;
                    }

                    category.content = str;
                    categories.add(category);
                }

            }

            if (section.dataItems.size()>0) {

                String str = "";

                for (int i = 0; i< section.errors.size(); i++) {

                    DataItem dataItem = section.errors.get(i);

                    if (i!=0) str += "<br><br>";
                    str = str + "<b>" + dataItem.item + "</b><br>" + dataItem.info;
                }

                section.content = str;
                sections.add(section);
            }
        }

        errorSections = getErrorCats(sections);
        errorCategories = getErrorCats(categories);

    }

    public ArrayList<ResultCategory> getErrorCats(ArrayList<ResultCategory> cats) {
        ArrayList<ResultCategory> errorSections = new ArrayList<>();
        for (ResultCategory resultCategory: cats) {
            if (resultCategory.errors.size() > 0) errorSections.add(resultCategory);
        }

        return errorSections;
    }




    public class ResultCategory {
        public ArrayList<DataItem> dataItems = new ArrayList<>();
        public ArrayList<DataItem> errors = new ArrayList<>();
        public String title;
        public String content;

        public ResultCategory() {
        }
    }




}
