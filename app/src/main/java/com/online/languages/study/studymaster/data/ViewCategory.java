package com.online.languages.study.studymaster.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ViewCategory {


    public String title;
    public String id;
    public String desc;
    public int progress;
    public String type;
    public String parent;
    public String spec;
    public String image;
    public String tag;

    public Boolean unlocked;

    public int subgroup = 0;


    public ViewCategory() {
    }


    public ViewCategory(NavCategory navCategory) {
        title = navCategory.title;
        desc = navCategory.desc;
        type = navCategory.type;
        id = navCategory.id;
        unlocked = navCategory.unlocked;
        parent = navCategory.parent;
        spec = navCategory.spec;
        image = navCategory.image;
    }

}
