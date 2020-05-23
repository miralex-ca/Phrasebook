package com.online.languages.study.studymaster.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ExerciseTask implements Parcelable {

    public String quest;
    public String questInfo;
    public ArrayList<String> options;
    public int correct;
    public String savedInfo;
    public DataItem data = new DataItem();

    public ExerciseTask(ExerciseTask newTask) {
        quest = newTask.quest;
        questInfo = newTask.questInfo;
        options = new ArrayList<>(newTask.options);
        correct = newTask.correct;
        savedInfo = newTask.savedInfo;
        data = newTask.data;
    }

    public ExerciseTask(String _quest, String _info, ArrayList<String> _options, int _correct) {
        quest = _quest;
        questInfo = _info;
        options = _options;
        correct = _correct;
    }

    public ExerciseTask(String _quest, String _info, ArrayList<String> _options, int _correct, String _savedInfo) {
        quest = _quest;
        questInfo = _info;
        options = _options;
        correct = _correct;
        savedInfo = _savedInfo;
    }

    public ExerciseTask(Parcel parcel){

        options = new ArrayList<>();

        this.quest = parcel.readString();
        this.questInfo = parcel.readString();
        this.savedInfo = parcel.readString();
        this.correct = parcel.readInt();
        this.data = parcel.readParcelable(DataItem.class.getClassLoader());
        parcel.readList(options, null);

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quest);
        dest.writeString(questInfo);
        dest.writeString(savedInfo);
        dest.writeInt(correct);
        dest.writeParcelable(data, flags);
        dest.writeList(options);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ExerciseTask createFromParcel(Parcel source) {
            return new ExerciseTask(source);
        }

        public ExerciseTask[] newArray(int size) {
            return new ExerciseTask[size];
        }
    };

}
