package com.online.languages.study.studymaster.data;


public class ImageData {

    public String id = "";
    public String title = "";
    public String desc = "";
    public String weblink = "";
    public String author = "";
    public String image = "";


    public ImageData() {}

    public ImageData(String _title, String _link, String _id, String _image) {
        title = _title;
        weblink = _link;
        id = _id;
        image = _image;
    }

    public ImageData(String _title, String _link, String _id, String _image, String _desc) {
        title = _title;
        weblink = _link;
        id = _id;
        image = _image;
        desc = _desc;
    }

    public DataItem getDataItem() {
        DataItem dataItem = new DataItem();

        dataItem.id = id;
        dataItem.item = title;
        dataItem.info = desc;
        dataItem.image = image;

        return dataItem;

    }

}
