package com.online.languages.study.lang.data;



import java.util.ArrayList;

public class OptionsCatData {

    public String id;
    public ArrayList<DataItem> options;

    public OptionsCatData() {
        options = new ArrayList<>();
    }

    public OptionsCatData(String _id) {
        id = _id;
        options = new ArrayList<>();
    }





}
