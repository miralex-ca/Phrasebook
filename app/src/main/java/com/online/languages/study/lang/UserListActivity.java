package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.adapters.UserListViewPagerAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.fragments.CatTabFragment1;
import com.online.languages.study.lang.fragments.UserListTabFragment1;
import com.online.languages.study.lang.fragments.UserListTabFragment2;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;

public class UserListActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    UserListViewPagerAdapter adapter;
    ViewPager viewPager;

    public ArrayList<DataItem> topicData = new ArrayList<>();
    public ArrayList<DataItem> exerciseData = new ArrayList<>();
    public ArrayList<DataItem> cardData = new ArrayList<>();

    DBHelper dbHelper;

    DataManager dataManager;

    public static Boolean showRes;

    OpenActivity openActivity;

    private MenuItem changeLayoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_user_list);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        showRes = appSettings.getBoolean("show_starred_results", true);

        dataManager = new DataManager(this, 1);

        dbHelper = new DBHelper(this);
        getVocab();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_tab1_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_tab2_title));
        viewPager = findViewById(R.id.container);
        adapter = new UserListViewPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                checkEx();

                checkIconDisplay(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


    public void showAlertDialog(View view) {

        Intent intent = new Intent(UserListActivity.this, ScrollingActivity.class);
        String tag = view.getTag().toString();
        intent.putExtra("starrable", true);
        intent.putExtra("id", tag );
        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.slide_in_down, 0);
    }


    private void getVocab() {

        topicData = dataManager.getStarredWords(true);
        exerciseData = topicData;
        cardData = topicData;
        setPageTitle(topicData.size());

    }

    public void setPageTitle(int count) {
        String title = String.format(getString(R.string.starred_title), count);
        setTitle(title);
    }


    public void checkEx() {
        if (topicData.size() < Constants.LIMIT_STARRED_EX) {
            UserListTabFragment2 fragment = (UserListTabFragment2) adapter.getFragmentTwo();
            if (fragment != null) {
                fragment.checkStarred(topicData.size());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }


    public void nextPage(int pos) {

        Intent i = new Intent(UserListActivity.this, ExerciseActivity.class) ;

        if (pos == 0 )  {
            i = new Intent(UserListActivity.this, CardsActivity.class);
        }

        if (pos == 0) {
            i.putParcelableArrayListExtra("dataItems", cardData);
        } else {
            i.putParcelableArrayListExtra("dataItems", exerciseData);
            i.putExtra("ex_type", pos);
        }

        i.putExtra(Constants.EXTRA_CAT_TAG, Constants.STARRED_CAT_TAG);

        startActivity(i);
        openActivity.pageTransition();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == UserListActivity.RESULT_OK){

                UserListTabFragment1 fragment = (UserListTabFragment1) adapter.getFragmentOne();
                if (fragment != null) {
                    fragment.checkStarred();

                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                openActivity.pageBackTransition();
                return true;
            case R.id.list_layout:
                changeLayoutStatus();
                return true;
            case R.id.starred_del_results:
                deleteStarredExResults();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_starred, menu);

        changeLayoutBtn = menu.findItem(R.id.list_layout);
        applyLayoutStatus();

        return true;
    }


    private void applyLayoutStatus() {

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_NORM);
        if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);
        } else {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
        }
    }

    public void changeLayoutStatus() {

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_NORM);

        if (listType.equals(CAT_LIST_VIEW_NORM)) {
            listType = CAT_LIST_VIEW_COMPACT;
        } else if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            listType = CAT_LIST_VIEW_NORM;
        }

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(CAT_LIST_VIEW, listType);
        editor.apply();

        UserListTabFragment1 fragment = (UserListTabFragment1) adapter.getFragmentOne();
        if (fragment != null)   fragment.updateLayoutStatus();

        applyLayoutStatus();
    }

    private void  checkIconDisplay(int position) {

        if (position == 1) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeLayoutBtn.setVisible(false);
                }
            }, 400);

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeLayoutBtn.setVisible(true);
                }
            }, 400);

        }

    }



    private  void deleteStarredExResults() {

        String[] topic = new String[2];
        topic[0] = Constants.STARRED_CAT_TAG +"_1";
        topic[1] = Constants.STARRED_CAT_TAG +"_2";

        dbHelper.deleteExData(topic);

        UserListTabFragment2 fragment = (UserListTabFragment2) adapter.getFragmentTwo();
        if (fragment != null) {
            fragment.updateResults();
        }

    }



}
