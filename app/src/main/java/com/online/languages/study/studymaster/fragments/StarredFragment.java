package com.online.languages.study.studymaster.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.online.languages.study.studymaster.Constants;
import com.online.languages.study.studymaster.R;
import com.online.languages.study.studymaster.adapters.StarredTabsPagerAdapter;
import com.online.languages.study.studymaster.adapters.WrapContentViewPager;
import com.online.languages.study.studymaster.data.DataItem;
import com.online.languages.study.studymaster.data.DataManager;

import java.util.ArrayList;

import static com.online.languages.study.studymaster.Constants.STARRED_TAB_ACTIVE;
import static com.online.languages.study.studymaster.Constants.TABS_NORMAL;
import static com.online.languages.study.studymaster.Constants.TAB_GALLERY;
import static com.online.languages.study.studymaster.Constants.TAB_ITEMS;


public class StarredFragment extends Fragment {


    DataManager dataManager;
    TextView text;
    TextView starredCount;
    View zero;
    View infoBox;
    TextView countZero, desc;

    ArrayList<DataItem> words;

    LinearLayout previewList;

    WrapContentViewPager tabsPager;
    TabLayout tabLayout;
    StarredTabsPagerAdapter tabsAdapter;

    View starredList;
    View starredTabs;

    SharedPreferences appSettings;
    int activeTab;

    String tabs_starred;
    View rootView;


    public StarredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_starred, container, false);


        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());


        tabs_starred = getString(R.string.tabs_starred_order);
        activeTab = getActiveTabNum();

        starredList = rootView.findViewById(R.id.starredList);
        starredTabs = rootView.findViewById(R.id.starredTabs);

        text = rootView.findViewById(R.id.starredWords);
        dataManager = new DataManager(getActivity(), 1);
        starredCount = rootView.findViewById(R.id.starred_count);

        zero = rootView.findViewById(R.id.starred_zero);
        countZero = rootView.findViewById(R.id.starred_count_preview);
        desc = rootView.findViewById(R.id.starred_zero_desc);

        infoBox = rootView.findViewById(R.id.starred_info);

        previewList = rootView.findViewById(R.id.starred_preview_list);

        words = updateTitle(words);

        createPreviewList(words);


        return rootView;
    }

    private void checkTabsDisplay() {

        ArrayList<DataItem> starredFromGallery = dataManager.getStarredWords(2,false);
        ArrayList<DataItem> starredItems = dataManager.getStarredWords(1,false);

        View tabs = rootView.findViewById(R.id.tab_layout);
        String tabsDisplay = getString(R.string.tabs_starred_display);

        starredList.setVisibility(View.GONE);
        starredTabs.setVisibility(View.VISIBLE);


        if (tabsDisplay.contains("always")) {
            tabs.setVisibility(View.VISIBLE);

        } else if (tabsDisplay.contains("never")) {
            tabs.setVisibility(View.GONE);

        } else if (tabsDisplay.contains("item_prior")) {

            if (starredFromGallery.size() == 0) {

                setStarredTab(getTabPositionByName(TAB_ITEMS));
                tabs.setVisibility(View.GONE);
                tabsPager.setPagingEnabled(false);

            } else {
                tabs.setVisibility(View.VISIBLE);
                tabsPager.setPagingEnabled(true);
            }

        } else if (tabsDisplay.contains("gallery_prior")) {
            if (starredItems.size() == 0) {

                setStarredTab(getTabPositionByName(TAB_GALLERY));
                tabs.setVisibility(View.GONE);
                tabsPager.setPagingEnabled(false);

            } else {
                tabs.setVisibility(View.VISIBLE);
                tabsPager.setPagingEnabled(true);
            }
        }



        if (tabsDisplay.contains("unscroll")) {
            tabsPager.setPagingEnabled(false);
        }


        /*

        if (dataFromGallery.size() > 0) {
            starredList.setVisibility(View.GONE);
            starredTabs.setVisibility(View.VISIBLE);
        }

        */

    }

    private int getActiveTabNum() {

        String defaultTab = TAB_ITEMS;
        if (!tabs_starred.equals(TABS_NORMAL)) defaultTab= TAB_GALLERY;

        String savedTab = appSettings.getString(STARRED_TAB_ACTIVE, defaultTab);

        return getTabPositionByName(savedTab);
    }

    private void setStarredTab(int tab) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(STARRED_TAB_ACTIVE, getTabByPosition(tab));
        editor.apply();
    }


    private String getTabByPosition(int position) {
        return tabs()[position];
    }

    private int getTabPositionByName(String name) {
        String[] tabs = tabs(); int position = 0;
        for ( int i = 0; i < tabs.length; i++) {
            if (tabs[i].equals(name)) position = i;
        }
        return position;
    }


    private String[] tabs() {
        String[] tabs = new String[] {TAB_ITEMS, TAB_GALLERY};
        if (!tabs_starred.equals(TABS_NORMAL)) tabs = new String[] {TAB_GALLERY, TAB_ITEMS};
        return tabs;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        String tab0 = getString(R.string.starred_tab_info);
        String tab1 = getString(R.string.starred_tab_gallery);

        if (!tabs_starred.equals(TABS_NORMAL)) {
            tab1 = getString(R.string.starred_tab_info);
            tab0 = getString(R.string.starred_tab_gallery);
        }

        tabLayout.addTab(tabLayout.newTab().setText(tab0));
        tabLayout.addTab(tabLayout.newTab().setText(tab1));

        tabsPager = view.findViewById(R.id.tabContainer);

        tabsAdapter = new StarredTabsPagerAdapter(getChildFragmentManager(), 2, tabs_starred);

        checkTabsDisplay();

        tabsPager.setAdapter(tabsAdapter);

        tabsPager.setOffscreenPageLimit(2);

        tabsPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                setStarredTab(tab.getPosition());
                tabsPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        activeTab = getActiveTabNum();

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                tabsPager.setCurrentItem(activeTab, false);
            }
        }, 30);


    }




    private void createPreviewList(ArrayList<DataItem> dataItems) {

        previewList.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (DataItem dataItem: dataItems) {

            View item = inflater.inflate(R.layout.starred_list_item, null);
            TextView txt = item.findViewById(R.id.itemText);
            TextView  desc = item.findViewById(R.id.itemInfo);
            txt.setText( dataItem.item);
            desc.setText( dataItem.info);

            previewList.addView(item);
        }

    }

    public void updateTabName(int tab, int count) {

        int itemTab = 0;
        int galleryTab = 1;

        if (!tabs_starred.equals("normal")) {
            itemTab = 1; galleryTab = 0;
        }

        if (tab == 1)  tabLayout.getTabAt(itemTab).setText(String.format(getString(R.string.starrd_tab_info_count), count));
        if (tab == 2)  tabLayout.getTabAt(galleryTab).setText(String.format(getString(R.string.starrd_tab_gallery_count), count));

    }


    private ArrayList<DataItem> updateTitle(ArrayList<DataItem> words) {

        words = dataManager.getStarredWords(false);

        if (words.size() < 1) {
            zero.setVisibility(View.VISIBLE);
            infoBox.setVisibility(View.GONE);
        } else {
            zero.setVisibility(View.GONE);
            infoBox.setVisibility(View.VISIBLE);
        }


        int limit = Constants.STARRED_LIMIT;

        String descTxt = String.format(getResources().getString(R.string.starred_words_info),  limit);

        String count = String.format("%d / %d", words.size(), limit);


        String zero = "0 / " + limit;

        countZero.setText(zero);
        desc.setText(descTxt);

        starredCount.setText(count);

        int displayLimit = 4;

        if (words.size() < displayLimit) displayLimit = words.size();

        words = new ArrayList<>(words.subList(0, displayLimit));

        return words;

    }


    @Override
    public void onResume() {
        super.onResume();
        words = updateTitle(words);
        createPreviewList(words);
        //checkTabsDisplay();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
           if (tabsPager !=null) tabsPager.setCurrentItem(getActiveTabNum(), false);
        }
    }




}
