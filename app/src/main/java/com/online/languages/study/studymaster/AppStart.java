package com.online.languages.study.studymaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.online.languages.study.studymaster.data.DataManager;

public class AppStart extends AppCompatActivity {

    public static final String APP_LAUNCHES = "launches";  // имя файла настроек
    public static final String LAUNCHES_NUM = "launches_num"; // настройка

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



        if (Constants.DEBUG) {
            changeVersion();
            changeShowAd();

            DBHelper dbHelper = new DBHelper(this);

            dbHelper.sanitizeDB();
            dbHelper.populateDB();

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
