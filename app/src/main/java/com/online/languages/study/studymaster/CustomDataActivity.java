package com.online.languages.study.studymaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.online.languages.study.studymaster.adapters.CatViewPagerAdapter;
import com.online.languages.study.studymaster.adapters.CustomDataListAdapter;
import com.online.languages.study.studymaster.adapters.CustomDataPagerAdapter;
import com.online.languages.study.studymaster.adapters.DataModeDialog;
import com.online.languages.study.studymaster.adapters.DividerItemDecoration;
import com.online.languages.study.studymaster.adapters.OpenActivity;
import com.online.languages.study.studymaster.adapters.ThemeAdapter;
import com.online.languages.study.studymaster.data.DataItem;
import com.online.languages.study.studymaster.data.DataManager;
import com.online.languages.study.studymaster.data.NavCategory;
import com.online.languages.study.studymaster.data.NavSection;
import com.online.languages.study.studymaster.data.NavStructure;
import com.online.languages.study.studymaster.fragments.CatTabFragment1;
import com.online.languages.study.studymaster.fragments.CustomDataFragment;
import com.online.languages.study.studymaster.fragments.CustomTabFragment2;
import com.online.languages.study.studymaster.fragments.CustomTabFragment3;

import java.util.ArrayList;

public class CustomDataActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    ArrayList<DataItem> dataItems;

    RecyclerView recyclerView;
    DataManager dataManager;

    public static  String sectionId;
    public static String catId;

    NavStructure navStructure;
    NavSection navSection;

    View emptyTxt;

    int listType;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    int adapterListType;
    ViewPager viewPager;

    CustomDataPagerAdapter adapter;
    OpenActivity openActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_custom_data_tabs);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        adapterListType = -1;

        sectionId = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        catId = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new DataModeDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listType = getIntent().getIntExtra(Constants.EXTRA_DATA_TYPE, 0); //
        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        navSection = navStructure.getNavSectionByID(sectionId);

        setTitle(getCatTitleById(catId));

        emptyTxt = findViewById(R.id.emptyTxt);

        dataManager = new DataManager(this);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.studied_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.familiar_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.unknown_tab));

        viewPager = findViewById(R.id.container);

        adapter = new CustomDataPagerAdapter (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        setActiveTab(listType);

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


    private String getCatTitleById(String id) {

        String title = "Список";

        for (NavCategory cat: navStructure.getUniqueCats()) {
            if (cat.id.equals(id)) title = cat.title;
        }

        return title;
    }

    private void setActiveTab(int listType) { // 0 - studied, 1 - familiar, 2 - unknown;
        viewPager.setCurrentItem(listType);
    }

    public void showDetailDialog(View view, int position) {

        Intent intent = new Intent(CustomDataActivity.this, ScrollingActivity.class);

        String id = view.getTag().toString();
        intent.putExtra("starrable", navSection.unlocked);
        intent.putExtra("id", id );
        intent.putExtra("position", position);

        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.slide_in_down, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == CustomDataActivity.RESULT_OK){
                updateLists(0);
            }
        }
    }


    public void updateLists(int origin) {

        CustomDataFragment fragment = (CustomDataFragment) adapter.getRegisteredFragment(0);
        CustomTabFragment2 fragment2 = (CustomTabFragment2) adapter.getRegisteredFragment(1);
        CustomTabFragment3 fragment3 = (CustomTabFragment3) adapter.getRegisteredFragment(2);

        if (origin == 1) {
            if (fragment2 != null) fragment2.checkDataList();
            if (fragment3 != null) fragment3.checkDataList();

        } else if (origin == 2) {
            if (fragment != null) fragment.checkDataList();
            if (fragment3 != null) fragment3.checkDataList();

        } else if (origin == 3) {
            if (fragment != null) fragment.checkDataList();
            if (fragment2 != null) fragment2.checkDataList();

        }else {
            if (fragment != null) fragment.checkDataList();
            if (fragment2 != null) fragment2.checkDataList();
            if (fragment3 != null) fragment3.checkDataList();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (sectionId.equals(Constants.ERRORS_CAT_TAG)) {
            getMenuInflater().inflate(R.menu.menu_custom_list, menu);
        } else {
            getMenuInflater().inflate(R.menu.stats_mode_info, menu);
            MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
            if (easy_mode) modeMenuItem.setVisible(true);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
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
                dataModeDialog.openDialog();
                return true;
            case R.id.info_from_menu:
                openInfo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openInfo() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_txt));
    }


}
