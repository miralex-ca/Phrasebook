package com.online.languages.study.lang.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.view.ViewCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.MainActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataManager;

import java.util.Objects;


public class PrefsFragment extends PreferenceFragmentCompat {

    PreferenceScreen screen;
    PreferenceGroup preferenceParent;

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(getActivity(), themeTitle, false);
        themeAdapter.getTheme();

        addPreferencesFromResource(R.xml.settings);

        screen = getPreferenceScreen();
        preferenceParent = findPreference("interface");

        Preference hidden = getPreferenceManager().findPreference("hidden");
        screen.removePreference(hidden);

        Preference controlTests = getPreferenceManager().findPreference("control_tests");
        Preference sortPers = getPreferenceManager().findPreference("sort_pers");

        DataManager dataManager = new DataManager(getActivity(), true);



        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        Preference versionItem = getPreferenceManager().findPreference("version");

        if (full_version)  versionItem.setVisible(false);


        final ListPreference btm = (ListPreference) getPreferenceManager().findPreference("btm_nav");
        if (Build.VERSION.SDK_INT < 21) btm.setVisible(false);

        btm.setOnPreferenceChangeListener((preference, newValue) -> {
            new android.os.Handler().postDelayed(() -> {

                ((MainActivity) getActivity()).bottomNavDisplay();

            }, 200);
            return true;
        });



        final SwitchPreferenceCompat nightModePref = getPreferenceManager().findPreference("night_mode");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            nightModePref.setVisible(true);
        } else {
            nightModePref.setVisible(false);
        }


        nightModePref.setOnPreferenceChangeListener((preference, newValue) -> {
            new android.os.Handler().postDelayed(() -> {
                Intent intent = getActivity().getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
            }, 600);
            return true;
        });



        final ListPreference list = getPreferenceManager().findPreference("theme");

        list.setOnPreferenceChangeListener((preference, newValue) -> {
            Intent intent = getActivity().getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);

            return true;
        });

        final ListPreference transitions = getPreferenceManager().findPreference("set_transition");
        transitions.setVisible(Constants.DEBUG);
        //!getActivity().getResources().getBoolean(R.bool.tablet)

        final SwitchPreferenceCompat modeHint = getPreferenceManager().findPreference("set_mode_hint");
        modeHint.setVisible(Constants.DEBUG);



        final ListPreference mode = getPreferenceManager().findPreference("data_mode");
        boolean displayMode = getActivity().getResources().getBoolean(R.bool.display_mode);

        if (!displayMode) {
            if(mode != null)  mode.setVisible(false);
        }


        final ListPreference transcriptionsSettings = getPreferenceManager().findPreference("set_transript");
        boolean displayTranscriptionSettings = getActivity().getResources().getBoolean(R.bool.display_transcription_settings);

        if (!displayTranscriptionSettings) {
            if(transcriptionsSettings!= null)  transcriptionsSettings.setVisible(false);
        }



        transitions.setOnPreferenceChangeListener((preference, newValue) -> {
            Intent intent = getActivity().getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);
            return true;
        });



        final SwitchPreferenceCompat version = (SwitchPreferenceCompat) getPreferenceManager().findPreference(Constants.SET_VERSION_TXT);

        /*
                version.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        new android.os.Handler().postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = getActivity().getIntent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getActivity().startActivity(intent);
                            }
                        }, 600);
                        return true;
                    }
                });

*/
    }


    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater,
                                             ViewGroup parent, Bundle savedInstanceState) {



        RecyclerView list = super.onCreateRecyclerView(inflater, parent,
                savedInstanceState);
        if (list != null) {
            ViewCompat.setNestedScrollingEnabled(list, false);
        }
        return list;
    }




}
