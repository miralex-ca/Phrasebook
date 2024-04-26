package com.online.languages.study.lang.presentation.section.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.utils.OpenActivity;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.practice.PracticeFragment;
import com.online.languages.study.lang.practice.SectionPagerAdapter;
import com.online.languages.study.lang.presentation.core.ThemedActivity;

public class SectionActivity extends ThemedActivity {

    NavStructure navStructure;

    String parent = "root";
    String tSectionID = "01010";

    Boolean easy_mode;
    InfoDialog dataModeDialog;
    OpenActivity openActivity;
    DataManager dataManager;

    MenuItem modeMenuItem;
    MenuItem modeInfoItem;

    ViewPager viewPager;
    SectionPagerAdapter adapter;

    boolean displayPractice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        displayPractice = getResources().getBoolean(R.bool.display_section_practice);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section_tabs);

        dataManager = new DataManager(this);

        dataModeDialog = new InfoDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        if (!displayPractice) {
            //params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            params.setScrollFlags(0);
        }

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        parent = getIntent().getStringExtra(Constants.EXTRA_SECTION_PARENT);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);

        addSectionsTabs();

        setTitle(getString(R.string.section_content_title));

    }

    private void addSectionsTabs() {

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_topics_tab));

        if (displayPractice) {

            tabLayout.addTab(tabLayout.newTab().setText(R.string.section_practice_tab));
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }

        viewPager = findViewById(R.id.container);

        adapter = new SectionPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void updateContent() {
        checkModeIcon();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        updateContent();

        PracticeFragment fragment = (PracticeFragment) adapter.getFragmentTwo();
        if (fragment != null ) fragment.getData();

    }


    public void openSectionList(View view) {

        Intent i = new Intent(this, SectionListActivity.class);

        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);

        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    public void openSectionTest(View view) {

        Intent i = new Intent(this, SectionTestActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);
        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }

    private void checkModeIcon() {
        dataManager.dbHelper.checkMode();
        easy_mode = dataManager.easyMode();
        if (modeMenuItem != null) {

            modeMenuItem.setVisible(easy_mode);

            if (!getResources().getBoolean(R.bool.display_mode)) {
                modeMenuItem.setVisible(false);
                modeInfoItem.setVisible(false);
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.section_tabs_menu, menu);
        modeMenuItem = menu.findItem(R.id.easy_mode);
        modeInfoItem = menu.findItem(R.id.info_from_menu);

        checkModeIcon();


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        } else if (id == R.id.easy_mode) {
            dataModeDialog.openEasyModeDialog();
            return true;
        } else if (id == R.id.info_from_menu) {
            dataModeDialog.modeInfoDialog();
            return true;
        } else if (id == R.id.delete_note) {
            deleteQuestsStats();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void deleteQuestsStats() {

        int t = dataManager.dbHelper.deleteQuestsStast();

        Toast.makeText(this, "Cleared: " + t, Toast.LENGTH_SHORT).show();

    }


}














