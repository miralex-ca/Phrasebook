package com.online.languages.study.studymaster.data;


public class CatData {

    public String title;
    public String desc;

    public String tag;
    public int progress = 0;
    public int info_item = 0;
    public int item_info = 0;

    public int ex_txt_trsb = 0;


    public CatData() {}

    public CatData(String _title, String _desc) {
        title = _title;
        desc = _desc;
    }

    public CatData(String _tag, String exLabel, int result) {
        tag = _tag;
        updateData(exLabel, result);
    }


    public CatData(String _tag, int _progress, int _ex1, int _ex2) {
        tag = _tag;
        progress = _progress;
        info_item = _ex1;
        item_info = _ex2;
    }



    public CatData(String _tag, int _ex1, int _ex2) {
        tag = _tag;
        progress = countProgress ();
        info_item = _ex1;
        item_info = _ex2;
    }

    private int countProgress() {
        int progress = (info_item + item_info) / 2;
        if (progress > 100 ) progress = 100;
        return progress;
    }

    private void updateData(String exLabel, int result) {

    }
}
