package com.online.languages.study.lang.data;

import android.content.Context;
import android.util.Log;

import com.online.languages.study.lang.R;

import java.util.ArrayList;

import static com.online.languages.study.lang.App.getAppContext;

public class CheckData {


    Context context = getAppContext();
    DataFromJson dataFromJson = new DataFromJson(context);
    public ArrayList<String> findingsData;


    public CheckData() {
        context = getAppContext();
    }

    public CheckData(Context context) {
        this.context = context;
    }


    public void checkData() {

        dataFromJson.categoryFile = "french/data.json"; //context.getResources().getStringArray(R.array.data_files)[0];
        ArrayList<DataItem> listOne = dataFromJson.getAllData();

        dataFromJson.categoryFile = "french/data_en.json"; //context.getResources().getStringArray(R.array.data_files)[1];
        ArrayList<DataItem> listTwo = dataFromJson.getAllData();

        findingsData= new ArrayList<>();


        for (DataItem dataItemOne: listOne) {

            int found = 0;
            boolean itemIsEqual = true;
            String comparedData = "";

            for (DataItem dataItemTwo: listTwo) {

                if (dataItemOne.id.equals(dataItemTwo.id)) {
                    found ++;

                    comparedData = dataItemOne.id + ": " + dataItemOne.item;

                    if (!dataItemOne.item.equals(dataItemTwo.item))  {
                        itemIsEqual = false;
                        comparedData += " not: " + dataItemTwo.item + " ";
                    }

                    if (!dataItemOne.grammar.equals(dataItemTwo.grammar))  {
                        itemIsEqual = false;
                        comparedData += " grammar: " + dataItemOne.grammar + " not " + dataItemTwo.grammar;
                    }

                }
            }

            if (found == 0)  findingsData.add("Not found: " + dataItemOne.id + ": " + dataItemOne.item);
            if (!itemIsEqual) findingsData.add(comparedData);

        }

        String text = "";

        for (String findingText: findingsData) {

            text +=  findingText + "\n";

        }


        String msg = "Findings: \n"
                +  text;

        Log.d("Checking data", msg );


    }


}
