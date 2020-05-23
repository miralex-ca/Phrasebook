package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.CatsListAdapter;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedTransformation;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.data.ViewSection;
import com.squareup.picasso.Picasso;

public class SectionActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;


    TextView sectionTitle, sectionDesc;
    ImageView placePicutre;

    RecyclerView recyclerView;

    CatsListAdapter mAdapter;

    NavStructure navStructure;

    NavSection navSection;

    String parent = "root";
    String tSectionID = "01010";

    ViewSection viewSection;

    Boolean full_version;

    Boolean easy_mode;
    InfoDialog dataModeDialog;
    OpenActivity openActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new InfoDialog(this);
        openActivity = new OpenActivity(this);

        openActivity.setOrientation();

        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        parent = getIntent().getStringExtra(Constants.EXTRA_SECTION_PARENT);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);

        viewSection = new ViewSection(this, navSection, parent);


        sectionTitle = findViewById(R.id.sectionTitle);
        sectionDesc = findViewById(R.id.sectionDesc);

        placePicutre = findViewById(R.id.catImage);

        recyclerView = findViewById(R.id.recycler_view);

        setTitle(getString(R.string.section_content_title));
        if (easy_mode) setTitle(R.string.section_content_title_short);

        viewSection.getProgress();

        mAdapter = new CatsListAdapter(this, viewSection.categories, full_version);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);

        View iconLock = findViewById(R.id.icon_lock);

        if (!full_version) {
            if (!navSection.unlocked ) iconLock.setVisibility(View.VISIBLE);
        }


        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        Picasso.with(this )
                .load("file:///android_asset/pics/"+navSection.image)
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(placePicutre);

        if (themeTitle.equals("westworld")) placePicutre.setColorFilter(Color.argb(255, 50, 240, 240), PorterDuff.Mode.MULTIPLY);;

        sectionTitle.setText(navSection.title_short);
        sectionDesc.setText(navSection.desc);




    }

    private void updateContent() {
        viewSection.getProgress();
        mAdapter = new CatsListAdapter(this, viewSection.categories, full_version);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateContent();
    }


    public void openTopCat(View view) {

        String catId = view.getTag(R.id.cat_id).toString();

        NavCategory navCategory = new NavCategory();


        for (NavCategory navCat: navSection.navCategories) {

            if (navCat.id.equals(catId) && navCat.parent.equals("root_top")) {
                navCategory = navCat;
            }
        }

        Intent i = new Intent(SectionActivity.this, CatActivity.class);
        i.putExtra(Constants.EXTRA_CAT_ID, navCategory.id);
        i.putExtra("cat_title", navCategory.title);

        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    public void openCat(final View view) {

        final int position = (int) view.getTag();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openCatActivity(position);
            }
        }, 50);
    }

    public void openCatActivity(int position) {

        ViewCategory viewCategory = viewSection.categories.get(position);

        if (viewCategory.type.equals("set")) return;

        if (!full_version) {
            if (!viewCategory.unlocked) {
                notifyLocked();
                return;
            }
        }

        openActivity.openFromViewCat(navStructure, tSectionID, viewCategory);
    }


    public void notifyLocked() {
        String proContent = getString(R.string.pro_content);
        Snackbar.make(recyclerView, Html.fromHtml("<font color=\"#ffffff\">"+proContent+"</font>"), Snackbar.LENGTH_SHORT).setAction("Action", null).show();


    }


    public void openSectionList(View view) {

        Intent i = new Intent(SectionActivity.this, SectionListActivity.class);

        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);

        // i.putExtra("show_ad", showAd);

        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }

    public void openSectionTest(View view) {

        if (!full_version) {
            if (!navSection.unlocked) {
                notifyLocked();
                return;
            }
        }


        Intent i = new Intent(SectionActivity.this, SectionTestActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);
        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats_mode_info, menu);
        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);

        if (easy_mode) modeMenuItem.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                openActivity.pageBackTransition();
                return true;

            case R.id.easy_mode:
                dataModeDialog.openEasyModeDialog();
                return true;
            case R.id.info_from_menu:
                dataModeDialog.modeInfoDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





}




