package com.online.languages.study.lang.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class NavCategory implements Parcelable {


    public String id;
    public String title;
    public String desc;

    public String parent;  /// parent

    public String type;    /// group or simple or set
    public String spec;
    public String image;
    public String param = "";
    public String sort;

    public ArrayList<Integer> tests = new ArrayList<>();

    protected int progress = 0;

    public boolean review = true;
    public boolean unlocked = true;




    public NavCategory() {
    }


    public NavCategory(Parcel parcel){
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.desc = parcel.readString();
        this.parent = parcel.readString();
        this.type = parcel.readString();
        this.spec = parcel.readString();
        this.image = parcel.readString();
        this.param = parcel.readString();

        this.review = parcel.readInt() == 1;
        this.unlocked = parcel.readInt() == 1;
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
        dest.writeString(parent);
        dest.writeString(type);
        dest.writeString(spec);
        dest.writeString(image);
        dest.writeString(param);

        dest.writeInt(review ? 1 : 0);
        dest.writeInt(unlocked ? 1 : 0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NavCategory createFromParcel(Parcel source) {
            return new NavCategory(source);
        }

        public NavCategory[] newArray(int size) {
            return new NavCategory[size];
        }
    };


}
