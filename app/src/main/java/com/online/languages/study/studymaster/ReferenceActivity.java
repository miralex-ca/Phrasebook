package com.online.languages.study.studymaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.online.languages.study.studymaster.adapters.OpenActivity;
import com.online.languages.study.studymaster.adapters.ThemeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReferenceActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;

    String htmlStart = "<!DOCTYPE html><html><head><style>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();
        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        setContentView(R.layout.activity_reference);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = findViewById(R.id.webView);

        String resName = getString(R.string.app_reference_file);

        Context context = getBaseContext();

        String text = readRawTextFile(context, getResources().getIdentifier(resName, "raw", getPackageName()));

        String info = htmlStart + getThemeFont() + text;

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        webView.loadDataWithBaseURL(null, info, "text/html", "en_US", null);
        webView.setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getThemeFont () {

        String color = "body {color: #111;}";

        if (themeTitle.contains("dark") || themeTitle.contains("smoky") || themeTitle.contains("westworld")) {
            color= "body {color: #fff;} a {color: #7eafff;}";
        }

        if ( themeTitle.contains("default") || themeTitle.contains("red")|| themeTitle.contains("white") ) {
            if (appSettings.getBoolean("night_mode", false) && getResources().getBoolean(R.bool.night_mode))
                color= "body {color: #fff;} a {color: #7eafff;}";
        }

        return color;
    }

    public static String readRawTextFile(Context context, int resId)
    {
        InputStream inputStream = context.getResources().openRawResource(resId);

        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(inputReader);
        String line;
        StringBuilder builder = new StringBuilder();

        try {
            while (( line = buffReader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            return null;
        }
        return builder.toString();
    }


}
