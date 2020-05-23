package com.online.languages.study.lang.data;



import java.util.ArrayList;

public class CustomCatData {

    public String title;
    public String tag;
    public int customItemsCount = 0;
    public ArrayList<DataItem> words;

    public CustomCatData() {
        words = new ArrayList<>();
    }

    public CustomCatData(String _text, int _count) {
        title = _text;
        customItemsCount = _count;
        words = new ArrayList<>();
    }

    public void updateData() {
        customItemsCount = words.size();
    }



}
