package com.online.languages.study.lang.data;

import android.os.Parcel;
import android.os.Parcelable;

public class BookmarkItem implements Parcelable {

    public String item = "";
    public String parent = "";

    public NavCategory navCategory;
    public String title = "";
    public String desc ="";
    public String image = "";

    public String divider = "";

    public int starred = 0;

    public String type = "";
    public String cat = "";
    public long time = 0;

    public BookmarkItem() {
    }

    public BookmarkItem(String _item) {
        item = _item;
    }

    public BookmarkItem(String _item, String _parent) {
        item = _item;
        parent = _parent;
    }


    public BookmarkItem(Parcel parcel){

        this.item = parcel.readString();
        this.desc = parcel.readString();
        this.image = parcel.readString();
        this.starred = parcel.readInt();

    }


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(item);
        dest.writeString(desc);
        dest.writeString(image);

        dest.writeInt(starred);

    }

    public static final Creator CREATOR = new Creator() {
        public BookmarkItem createFromParcel(Parcel source) {
            return new BookmarkItem(source);
        }

        public BookmarkItem[] newArray(int size) {
            return new BookmarkItem[size];
        }
    };
}
