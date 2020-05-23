package com.online.languages.study.lang.data;

public class Category {
   public String tag;

   public String title;
   public String id;
    public String spec = "";

    public int studiedDataCount = 0;
    public int knownDataCount = 0;
    public int allDataCount = 0;


    public int familiarDataCount = 0;
    public int unknownDataCount = 0;

    public int customItemsCount = 0;


    public Category() {
    }


    Category(String _id, String _title) {
        tag = _id;
        title = _title;
    }


    Category(NavCategory navCategory) {
        id = navCategory.id;
        title = navCategory.title;
        spec = navCategory.spec;
    }


    public void calculateDataCount() {

        familiarDataCount = studiedDataCount + knownDataCount;
        unknownDataCount = allDataCount - familiarDataCount;

    }


}
