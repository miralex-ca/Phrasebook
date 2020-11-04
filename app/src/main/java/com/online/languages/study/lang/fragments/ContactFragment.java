package com.online.languages.study.lang.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;


public class ContactFragment extends Fragment {

    SharedPreferences appSettings;


    public ContactFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_contact, container, false);

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        View rateView = rootview.findViewById(R.id.rateAppLink);
        checkRateDisplay(rateView);

        return rootview;
    }



    public void checkRateDisplay(View rateView) {

        boolean full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);
        boolean hideRate = getResources().getBoolean(R.bool.hide_rate);


        if (hideRate) {
            if (full_version) {
                rateView.setVisibility(View.VISIBLE);
            } else {
                rateView.setVisibility(View.GONE);
            }
        } else {
            rateView.setVisibility(View.VISIBLE);
        }
    }


}
