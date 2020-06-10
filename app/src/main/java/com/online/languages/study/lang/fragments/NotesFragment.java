package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.online.languages.study.lang.BuildConfig;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class NotesFragment extends Fragment {

    SharedPreferences appSettings;
    public String themeTitle;

    View button;


    String htmlStart = "<!DOCTYPE html><html><head><style>";



    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_notes, container, false);


        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        themeTitle = appSettings.getString("theme", Constants.SET_THEME_DEFAULT);





        return rootview;

    }







}
