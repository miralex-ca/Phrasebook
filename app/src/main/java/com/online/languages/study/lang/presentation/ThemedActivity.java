package com.online.languages.study.lang.presentation;

import static com.online.languages.study.lang.Constants.SET_THEME_DEFAULT;

import android.os.Bundle;

import com.online.languages.study.lang.adapters.ThemeAdapter;

public class ThemedActivity extends BaseActivity {
    ThemeAdapter themeAdapter;
    public String themeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeTitle= appSettings.getString("theme", SET_THEME_DEFAULT);
        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

    }
}