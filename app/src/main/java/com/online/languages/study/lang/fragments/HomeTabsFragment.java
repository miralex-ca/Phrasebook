package com.online.languages.study.lang.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.HomeTabsPagerAdapter;

import static com.online.languages.study.lang.Constants.HOME_TAB_ACTIVE;


public class HomeTabsFragment extends Fragment {


    ViewPager viewPager;
    HomeTabsPagerAdapter adapter;
    SharedPreferences appSettings;
    int activeTab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_tabs, container, false);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.home_tab_1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.home_tab_2));

        int tabs = 2;


        viewPager = view.findViewById(R.id.container);

        adapter = new HomeTabsPagerAdapter(getChildFragmentManager(), tabs);

        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(2);


        viewPager.addOnPageChangeListener(  new TabLayout.TabLayoutOnPageChangeListener(tabLayout) );

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                setTab( tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        activeTab = appSettings.getInt(HOME_TAB_ACTIVE, 0);

        if (activeTab > tabLayout.getTabCount()) activeTab = (tabLayout.getTabCount() -1);

        viewPager.setCurrentItem(activeTab, false);

    }



    public void setTab(int num) {
        viewPager.setCurrentItem(num);
        setStarredTab(num);

    }


    private int getActiveTabNum() {
        //Toast.makeText(getActivity(), "Active tab: "+ activeTab, Toast.LENGTH_SHORT).show();
        return appSettings.getInt(HOME_TAB_ACTIVE, 0);
    }

    private void setStarredTab(int tab) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(HOME_TAB_ACTIVE, tab);
        editor.apply();
    }



    @Override
    public void onResume() {
        super.onResume();

       // Toast.makeText(getActivity(), "Update: " + i++, Toast.LENGTH_SHORT).show();

    }



}
