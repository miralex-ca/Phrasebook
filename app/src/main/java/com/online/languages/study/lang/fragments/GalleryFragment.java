package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.adapters.GalleryAdapter;
import com.online.languages.study.lang.utils.OpenActivity;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.data.ViewSection;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.SET_GALLERY_LAYOUT;
import static com.online.languages.study.lang.Constants.SET_GALLERY_LAYOUT_DEFAULT;


public class GalleryFragment extends Fragment {


    SharedPreferences appSettings;
    public String themeTitle;

    Context context;

    NavStructure navStructure;

    String tSectionID = "gallery";
    String tCatID = "root";

    ViewSection viewSection;
    NavSection navSection;

    LinearLayout itemsList, cardsList;
    int listType;

    MenuItem changeLayoutBtn;

    OpenActivity openActivity;


    public GalleryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.content_gallery, container, false);

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        context = getActivity();

        setHasOptionsMenu(true);

        assert getArguments() != null;
        navStructure = getArguments().getParcelable("structure");
        tSectionID = getArguments().getString(EXTRA_SECTION_ID);;
        tCatID = getArguments().getString(EXTRA_CAT_ID);

        if (tSectionID.equals("root")) {
            for (NavSection navSection: navStructure.sections) {
                if (navSection.spec.equals("nav_gallery")) tSectionID = navSection.id;
            }
        }

        itemsList = rootview.findViewById(R.id.items_list);
        cardsList = rootview.findViewById(R.id.cards_list);

        navSection = navStructure.getNavSectionByID(tSectionID);

        //getActivity().setTitle(navSection.title);

        openActivity = new OpenActivity(getActivity());

        viewSection = new ViewSection(getActivity(), navSection, tCatID);

        organizeSection();

        return rootview;

    }



    private void organizeSection() {

        ArrayList<CatSet> catSetsList = new ArrayList<>();
        CatSet catSet = new CatSet();

        for (int i=0; i < viewSection.categories.size(); i++) {
            ViewCategory viewCategory = viewSection.categories.get(i);

            if (viewCategory.type.equals("set")) {
                if (catSet.catList.size()>0) catSetsList.add(catSet);
                catSet = new CatSet();
                catSet.title = viewCategory.title;
            } else {
                catSet.catList.add(viewCategory);
                if (i == (viewSection.categories.size()-1)) catSetsList.add(catSet);
            }
        }

        for (CatSet group: catSetsList) {
            addList(group);
            addGrid(group);
        }

    }


    private void addList(CatSet group) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.gallery_items_list, null);

        View titleWrap = item.findViewById(R.id.titleWrap);
        TextView title = item.findViewById(R.id.title);

        if (group.title.equals("none")) {
            title.setVisibility(View.GONE);
        } else if (group.title.equals("gone")) {
            titleWrap.setVisibility(View.GONE);
        } else {
            title.setText(group.title);
            title.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = item.findViewById(R.id.recycler_view);
        GalleryAdapter mAdapter = new GalleryAdapter(getActivity(), group.catList, 1, themeTitle);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration( new DividerItemDecoration(getActivity()) );
        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        itemsList.addView(item);

    }


    private void addGrid(CatSet group) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.gallery_grid_list, null);

        TextView title = item.findViewById(R.id.title);
        View titleWrap = item.findViewById(R.id.titleWrap);

        if (group.title.equals("none")) {
            title.setVisibility(View.GONE);
        } else if (group.title.equals("gone")) {
            titleWrap.setVisibility(View.GONE);
        } else {
            title.setText(group.title);
            title.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerViewCards = item.findViewById(R.id.recycler_view);
        GalleryAdapter cardsAdapter = new GalleryAdapter(getActivity(), group.catList, 2, themeTitle);

        recyclerViewCards.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewCards.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCards.setSelected(true);
        recyclerViewCards.setAdapter(cardsAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCards, false);
        cardsList.addView(item);
    }


    public class CatSet {
        public ArrayList<ViewCategory> catList = new ArrayList<>();
        public String title = "none";
        private CatSet() {
        }
    }


    private void changeLayoutStatus() {

        if (listType == 1) listType = 2;
        else listType = 1;

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(SET_GALLERY_LAYOUT, listType);
        editor.apply();
        applyLayoutStatus(listType);
    }

    private void applyLayoutStatus(int type) {
        if (type == 2) {
            cardsList.setVisibility(View.VISIBLE);
            itemsList.setVisibility(View.GONE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconList));

        } else {
            cardsList.setVisibility(View.GONE);
            itemsList.setVisibility(View.VISIBLE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconGrid2));

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery_fragement, menu);
        changeLayoutBtn = menu.findItem(R.id.list_layout);
        listType = appSettings.getInt(SET_GALLERY_LAYOUT, SET_GALLERY_LAYOUT_DEFAULT);
        applyLayoutStatus(listType);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.list_layout);
        menuItem.setVisible(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.list_layout) {
            changeLayoutStatus();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        listType = appSettings.getInt(SET_GALLERY_LAYOUT, SET_GALLERY_LAYOUT_DEFAULT);
        applyLayoutStatus(listType);
    }


    private int getDrawableIcon(int iconAttr) {
        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(iconAttr, typedValue, true);
        int drawableRes = typedValue.resourceId;
        return drawableRes;
    }


    public void openCatActivity(String tag) {

        ViewCategory viewCategory = new ViewCategory();

        for (ViewCategory category: viewSection.categories) {
            if (category.tag.equals(tag))   viewCategory = category;
        }
        openActivity.openFromViewCat(navStructure, tSectionID, viewCategory);
    }




}
