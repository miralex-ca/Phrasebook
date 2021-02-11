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

    // colors link: https://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html#background-colors

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final String PLAIN_TEXT = "\033[0;0m";
    private final String BOLD_TEXT = "\033[0;1m";

    boolean validateValue = true;
    boolean validateGrammar = true;
    boolean validateMode = true;
    boolean validateBase = false;
    boolean validateFilter = false;
    boolean validateTranscript = false;

    boolean onlyData = false;


    public CheckData() {
        this.context = getAppContext();
    }

    public CheckData(Context context) {
        this.context = context;
    }


    public static class Builder {
        boolean checkGrammar = true;
        boolean checkValue = true;
        boolean checkMode = true;
        boolean checkBase = false;
        boolean checkFilter = false;
        boolean checkTranscript = false;
        boolean onlyData = false;

        private final Context context;

        public Builder (Context context) {
            this.context = context;
        }

        public Builder checkValue(boolean check) {
            this.checkValue = check;
            return this;
        }

        public Builder onlyListData(boolean check) {
            this.onlyData = check;
            return this;
        }

        public Builder checkGrammar(boolean check) {
            this.checkGrammar = check;
            return this;
        }

        public Builder checkMode(boolean check) {
            this.checkMode = check;
            return this;
        }

        public Builder checkBase(boolean check) {
            this.checkBase = check;
            return this;
        }

        public Builder checkFilter(boolean check) {
            this.checkFilter = check;
            return this;
        }

        public Builder checkTranscript(boolean check) {
            this.checkTranscript = check;
            return this;
        }

        public Builder onlyGrammar() {
            this.checkGrammar = true;
            this.checkMode = false;
            this.checkValue = false;
            this.checkBase = false;
            this.checkFilter = false;
            this.checkTranscript = false;
            return this;
        }

        public Builder onlyValue() {
            this.checkValue = true;
            this.checkGrammar = false;
            this.checkMode = false;
            this.checkBase = false;
            this.checkFilter = false;
            this.checkTranscript = false;
            return this;
        }

        public Builder onlyMode() {
            this.checkGrammar = false;
            this.checkMode = true;
            this.checkValue = false;
            this.checkBase = false;
            this.checkFilter = false;
            this.checkTranscript = false;
            return this;
        }

        public Builder all() {
            this.checkGrammar = true;
            this.checkMode = true;
            this.checkValue = true;
            this.checkBase = true;
            this.checkFilter = true;
            this.checkTranscript = true;
            return this;
        }





        public CheckData build() {

            CheckData test = new CheckData(context);
            test.validateGrammar = this.checkGrammar;
            test.validateValue = this.checkValue;
            test.validateMode = this.checkMode;
            test.validateBase = this.checkBase;
            test.validateFilter = this.checkFilter;
            test.validateTranscript = this.checkTranscript;
            test.onlyData = this.onlyData;

            test.checkData();

            return test;
        }


    }




    public void checkData() {

        String listOneName = context.getResources().getStringArray(R.array.data_files)[0];
        String listTwoName = context.getResources().getStringArray(R.array.data_files)[1];

        dataFromJson.categoryFile =  listOneName;
        ArrayList<DataItem> listOne = dataFromJson.getAllData();

        dataFromJson.categoryFile = listTwoName;
        ArrayList<DataItem> listTwo = dataFromJson.getAllData();

        findingsData= new ArrayList<>();

        String res1 = validateList(listOne,  listOneName);
        if (!res1.equals("none"))
          findingsData.add(res1);

        
        String res2 = validateList(listTwo,  listTwoName);
        if (!res2.equals("none"))
            findingsData.add(res2);

        for (DataItem dataItemOne: listOne) {

            int found = 0;
            boolean itemIsEqual = true;
            String comparedData = "";

            for (DataItem dataItemTwo: listTwo) {

                if (dataItemOne.id.equals(dataItemTwo.id)) {
                    found ++;

                    comparedData = dataItemOne.id + ": " + ANSI_BLUE + dataItemOne.item + ANSI_RESET;

                    if (validateValue) {
                        if (!dataItemOne.item.equals(dataItemTwo.item))  {
                            itemIsEqual = false;
                            comparedData += " not: " + ANSI_RED + dataItemTwo.item + ANSI_RESET + " ";
                        }

                    }

                    if (validateGrammar) {
                        if (!dataItemOne.grammar.equals(dataItemTwo.grammar))  {
                            itemIsEqual = false;
                            comparedData += " grammar: " + dataItemOne.grammar + " not " + dataItemTwo.grammar;
                        }
                    }

                    if (validateMode) {
                        if (dataItemOne.mode != dataItemTwo.mode)  {
                            itemIsEqual = false;
                            comparedData += " mode: " + ANSI_GREEN  + dataItemOne.mode + ANSI_RESET
                                    + " not " + ANSI_RED + dataItemTwo.mode + ANSI_RESET;
                        }
                    }

                    if (validateBase) {
                        if (!dataItemOne.base.equals(dataItemTwo.base))  {
                            itemIsEqual = false;
                            comparedData += " base: "
                                    + BOLD_TEXT + dataItemOne.base + PLAIN_TEXT
                                    +" not " + ANSI_RED + dataItemTwo.base + ANSI_RESET;
                        }
                    }

                    if (validateFilter) {
                        if (!dataItemOne.filter.equals(dataItemTwo.filter))  {
                            itemIsEqual = false;
                            comparedData += " filter: " + dataItemOne.filter + " not " + dataItemTwo.filter;
                        }
                    }

                    if (validateTranscript) {
                        if (!dataItemOne.trans1.equals(dataItemTwo.trans1))  {
                            itemIsEqual = false;
                            comparedData += " transcript: " + dataItemOne.trans1 + " not " + dataItemTwo.trans1;
                        }
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


    private String validateList(ArrayList<DataItem> list, String listName) {

        String outcome = "";
        int issuesCount = 0;


        for (DataItem dataItem: list) {

            boolean hasIssue = false;
            String msg = dataItem.id + ": ";
            if (dataItem.item.equals("")) {
                hasIssue = true;
                msg += " has no item";
            }

            if (dataItem.info.equals("")) {
                hasIssue = true;
                msg += " has no info";
            }

            if (dataItem.trans1.equals("")) {
                hasIssue = true;
                msg += " has no transcription";
            }

            if (hasIssue) {
                issuesCount++;
                outcome += msg + "\n";
            }
        }

        String result = "";
        if (issuesCount>0)  {
            result  = "List " + BOLD_TEXT +listName +PLAIN_TEXT+ ": Found issues: "  + issuesCount +" \n";
            result+=outcome;
        } else {
            result = "none";
        }

        return result;
    }




}
