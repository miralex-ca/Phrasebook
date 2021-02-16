package com.online.languages.study.lang.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.RateDialog;


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

        rateView.setOnClickListener(view -> {
            rate();
        });


        View shareView = rootview.findViewById(R.id.contact_share);

        shareView.setOnClickListener(view -> {
            shareIntent();
        });


        return rootview;
    }


    private void rate() {
        RateDialog rateDialog = new RateDialog( getActivity());
        rateDialog.createDialog("Rate", "Rate");
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

    private void getShareIntent() {

        String pack = getString(R.string.app_market_link);

        String text = getString(R.string.share_advise_msg) + getString(R.string.google_play_address) + pack;

        ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setChooserTitle(R.string.share_chooser_title)
                .setText(text)
                .startChooser();

    }


    private void shareIntent() {
        //startActivity(getShareIntent());

        getShareIntent();
    }


}
