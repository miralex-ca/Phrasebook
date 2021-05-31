package com.online.languages.study.lang;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import static com.online.languages.study.lang.Constants.SET_THEME_DEFAULT;

public class ThemedActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    public SharedPreferences appSettings;
    public String themeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", SET_THEME_DEFAULT);
        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

    }
}