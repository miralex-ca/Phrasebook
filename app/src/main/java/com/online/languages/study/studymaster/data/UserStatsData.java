package com.online.languages.study.studymaster.data;


import java.util.ArrayList;

public class UserStatsData {


    public int studiedWordsSize = 0;

    public String studiedWords = "0";
    public String knownWords = "0";
    public String familiarWords = "0";
    public String seenWords = "0";


    public int studiedDataCount = 0;
    public int familiarDataCount = 0;
    public int unknownDataCount = 0;
    public int allDataCount = 0;



    public ArrayList<Section> sectionsDataList;

    public ArrayList<String> allUniqueIds;
    public ArrayList<String> idsToCheck;


    public ArrayList<DataItem> recentWords;
    public ArrayList<DataItem> errorsWords;
    public ArrayList<DataItem> mostErrorsWords;

    public ArrayList<DataItem> allWords;

    public ArrayList<DataItem> reviseWords;
    public ArrayList<NavCategory> uniqueCats;


    UserStatsData() {
        recentWords = new ArrayList<>();
        errorsWords = new ArrayList<>();
        mostErrorsWords = new ArrayList<>();

        allUniqueIds = new ArrayList<>();
        idsToCheck = new ArrayList<>();
        sectionsDataList = new ArrayList<>();
    }


}
