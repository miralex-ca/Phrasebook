package com.online.languages.study.studymaster.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;

public class ExerciseController implements Parcelable {
    private int counter;
    private int correctNum;
    private int wrongNum;
    private int listSize;

    public ArrayList<ExerciseTask> tasks = new ArrayList<>();
    public ArrayList<DataItem> completed = new ArrayList<>();




    public ExerciseController() {
    }

    public ExerciseController(ArrayList<ExerciseTask> _tasks, int _counter, int _correctNum, int _wrongNum) {
        counter = _counter;
        correctNum = _correctNum;
        wrongNum = _wrongNum;


        tasks = _tasks;
        listSize = tasks.size();
    }


    public void shuffleTasks() {

        Collections.shuffle(tasks);
    }





    public ExerciseController(Parcel parcel){

        tasks = new ArrayList<>();

        this.correctNum = parcel.readInt();
        this.wrongNum = parcel.readInt();
        this.counter = parcel.readInt();
        this.listSize = parcel.readInt();

        parcel.readTypedList(tasks, ExerciseTask.CREATOR);
        parcel.readTypedList(completed, DataItem.CREATOR);

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(correctNum);
        dest.writeInt(wrongNum);
        dest.writeInt(counter);
        dest.writeInt(listSize);
        dest.writeTypedList(tasks);
        dest.writeTypedList(completed);

    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ExerciseController createFromParcel(Parcel source) {
            return new ExerciseController(source);
        }

        public ExerciseController[] newArray(int size) {
            return new ExerciseController[size];
        }
    };


}
