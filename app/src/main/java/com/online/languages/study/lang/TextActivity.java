package com.online.languages.study.lang;

import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_NAV_STRUCTURE;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;

import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.presentation.ThemedActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextActivity extends ThemedActivity {

    OpenActivity openActivity;

    NavStructure navStructure;
    NavSection navSection;

    String htmlStart = "<!DOCTYPE html><html><head><style>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        setContentView(R.layout.activity_reference);

        String resName = "";
        String title = "";

        if (getIntent().hasExtra("source_id")) {

            resName = getIntent().getStringExtra("source_id");
            title = getIntent().getStringExtra("page_title");

        } else {

            String sectionId = getIntent().getStringExtra(EXTRA_SECTION_ID);
            String catId = getIntent().getStringExtra(EXTRA_CAT_ID);
            navStructure = getIntent().getParcelableExtra(EXTRA_NAV_STRUCTURE);
            NavCategory navCategory = navStructure.getNavCatFromSection(sectionId, catId);

            resName = navCategory.id;
            title = navCategory.title;
        }



        setTitle(title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = findViewById(R.id.webView);

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

        if (themeTitle.contains("dark") || themeTitle.contains("smoky")) {
            color= "body {color: #fff;} a {color: #7eafff;}";
        }

        if (themeTitle.contains("westworld")) {
            color= "body {color: #90ffff;} a {color: #7eafff;}";
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
