package com.online.languages.study.studymaster.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class NavSection implements Parcelable {


    public String id;
    public String title;
    public String desc;

    public String title_short;
    public String desc_short;

    public String image;
    public boolean unlocked = true;
    public String spec;
    public String type;


    public ArrayList <NavCategory> navCategories = new ArrayList<>();
    public ArrayList <NavCategory> uniqueCategories = new ArrayList<>();
    ArrayList<String> catIdList = new ArrayList<>();

    public NavSection() {
    }


    public NavSection(Parcel parcel){
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.desc = parcel.readString();
        this.title_short = parcel.readString();
        this.desc_short = parcel.readString();
        this.image = parcel.readString();
        this.spec = parcel.readString();
        this.type = parcel.readString();

        this.unlocked = parcel.readInt() == 1;

        parcel.readTypedList(navCategories, NavCategory.CREATOR);
        parcel.readTypedList(uniqueCategories, NavCategory.CREATOR);

        parcel.readList(catIdList, null);


    }


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(title_short);
        dest.writeString(desc_short);
        dest.writeString(image);
        dest.writeString(spec);
        dest.writeString(type);

        dest.writeInt(unlocked ? 1 : 0);

        dest.writeTypedList(navCategories);
        dest.writeTypedList(uniqueCategories);

        dest.writeList(catIdList);

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NavSection createFromParcel(Parcel source) {
            return new NavSection(source);
        }
        public NavSection[] newArray(int size) {
            return new NavSection[size];
        }
    };


    public ArrayList<String> getNavCatList() {

        ArrayList<String> catIds = new ArrayList<>();

        for (NavCategory category: uniqueCategories) {
            catIds.add(category.id);
        }

        return catIds;
    }

}
