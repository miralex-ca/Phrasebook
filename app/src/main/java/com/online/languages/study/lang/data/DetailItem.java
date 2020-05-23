package com.online.languages.study.lang.data;

public class DetailItem {
    public String id = "";
    public String title ="";
    public String desc ="";
    public String image ="";
    public String img_info ="";



    public DetailItem() {
    }


    public DetailItem(String _tag, String _desc, String _image) {
        id = _tag;
        desc = _desc;
        image = _image;

    }


    public DetailItem(DataItem dataItem) {
        id = dataItem.id;
        title = dataItem.item;
        desc = dataItem.info;
        image = dataItem.image;
    }

    public DetailItem(String _tag, String _title, String _desc, String _image) {
        id = _tag;
        title = _title;
        desc = _desc;
        image = _image;

    }

    public DetailItem(String _id, String _title, String _desc, String _image, String _img_info) {
        id = _id;
        title = _title;
        desc = _desc;
        image = _image;
        img_info = _img_info;

    }

}
