package com.online.languages.study.lang.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NoteData;

public class AppStart extends AppCompatActivity {

    public static final String APP_LAUNCHES = "launches";  // имя файла настроек
    public static final String LAUNCHES_NUM = "launches_num"; // настройка
    public static final String LAUNCHES_RATE_START = "launch_rate_start";
    public static final String LAUNCHES_RATE_STARTED = "launch_rate_started";

    SharedPreferences appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mLaunches = getSharedPreferences(APP_LAUNCHES, Context.MODE_PRIVATE);
        appSettings = PreferenceManager.getDefaultSharedPreferences(this);

        DataManager dataManager = new DataManager(this);
        dataManager.getParamsAndSave();

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        int launchesNum = mLaunches.getInt(LAUNCHES_NUM, 0);
        launchesNum++;

        mLaunches.edit().putInt(LAUNCHES_NUM, launchesNum).apply();

        boolean requestStared = mLaunches.getBoolean(LAUNCHES_RATE_STARTED, false);

        if (!requestStared) {
            mLaunches.edit().putInt(LAUNCHES_RATE_START, launchesNum).apply();
            mLaunches.edit().putBoolean(LAUNCHES_RATE_STARTED, true).apply();
        }



        DBHelper dbHelper = new DBHelper(this);

        if (Constants.DEBUG) {
            changeVersion();
            changeShowAd();

            dbHelper.sanitizeDB();

        }

        dbHelper.populateDB();


        for (int i = 0; i < 1; i ++) {

            NoteData note = new NoteData();
            note.title = "Note " + (i+1);
            note.content = "Content";
            note.image = "info.png";

          //  dbHelper.createNote(note);
        }



        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    private void changeVersion() {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(Constants.SET_VERSION_TXT, false);
        editor.apply();
    }

    private void changeShowAd() {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(Constants.SET_SHOW_AD, false);
        editor.apply();
    }
}
