package com.online.languages.study.lang.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DataObject implements Parcelable {

    public String id = "";

    public String title = "";
    public String desc ="";
    public String text ="";
    public String info ="";
    public String image = "";
    public String parent = "";

    public String status = "";
    public String filter = "";
    public String type = "";
    public String params = "";

    public int count = 0;
    public int progress = 0;
    public int progress_1 = 0;
    public int order = 0;

    public long time_created = 0;
    public long time_updated = 0;
    public long time_updated_sort = 0;



    public DataObject() {
    }

    public DataObject(NoteData noteData) {

        this.id = noteData.id;
        this.title = noteData.title;
        this.text = noteData.content;
        this.image = noteData.image;

        this.parent = noteData.parent;

        this.time_updated = noteData.time_updated;
        this.time_updated_sort = noteData.time_updated_sort;

    }


    public DataObject(Parcel parcel){

        this.id = parcel.readString();
        this.title = parcel.readString();
        this.desc = parcel.readString();
        this.text = parcel.readString();
        this.info = parcel.readString();
        this.image = parcel.readString();
        this.parent = parcel.readString();
        this.status = parcel.readString();
        this.filter = parcel.readString();
        this.type = parcel.readString();
        this.params = parcel.readString();

        this.count = parcel.readInt();
        this.progress = parcel.readInt();
        this.progress_1 = parcel.readInt();
        this.order = parcel.readInt();
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
        dest.writeString(text);
        dest.writeString(info);
        dest.writeString(image);
        dest.writeString(parent);
        dest.writeString(status);
        dest.writeString(filter);
        dest.writeString(type);
        dest.writeString(params);

        dest.writeInt(count);
        dest.writeInt(progress);
        dest.writeInt(progress_1);
        dest.writeInt(order);

    }

    public static final Creator CREATOR = new Creator() {
        public DataObject createFromParcel(Parcel source) {
            return new DataObject(source);
        }

        public DataObject[] newArray(int size) {
            return new DataObject[size];
        }
    };
}
