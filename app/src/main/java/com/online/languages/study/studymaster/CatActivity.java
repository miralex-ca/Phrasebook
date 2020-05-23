package com.online.languages.study.studymaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.online.languages.study.studymaster.adapters.CatViewPagerAdapter;
import com.online.languages.study.studymaster.adapters.DataModeDialog;
import com.online.languages.study.studymaster.adapters.OpenActivity;
import com.online.languages.study.studymaster.adapters.ThemeAdapter;
import com.online.languages.study.studymaster.data.DataItem;
import com.online.languages.study.studymaster.data.DataManager;
import com.online.languages.study.studymaster.data.NavStructure;
import com.online.languages.study.studymaster.data.Section;
import com.online.languages.study.studymaster.fragments.CatTabFragment1;

import java.util.ArrayList;


public class CatActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    CatViewPagerAdapter adapter;
    ViewPager viewPager;

    public ArrayList<DataItem> exerciseData = new ArrayList<>();
    public ArrayList<DataItem> cardData = new ArrayList<>();

    public static Section section;

    public static String categoryID;
    public static String catSpec;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    ImageView placeholder;
    View adContainer;
    View adCard;

    Boolean showAd;
    AdView mAdView;

    OpenActivity openActivity;

    MenuItem sortMenuItem;

    DataManager dataManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_cat);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new DataModeDialog(this);
        dataManager = new DataManager(this, true);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        categoryID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);
        catSpec = getIntent().getStringExtra(Constants.EXTRA_CAT_SPEC);

        String title = getIntent().getStringExtra("cat_title");

        if (catSpec.equals("pers")) {
            if (easy_mode || !getResources().getBoolean(R.bool.wide))  {
                if (!getResources().getBoolean(R.bool.tablet))
                title = getResources().getString(R.string.persons_title);
            }
        }

        setTitle(title);

        getDataItems();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_tab1_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_tab2_title));
        viewPager = findViewById(R.id.container);

        adapter = new CatViewPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
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


        placeholder = findViewById(R.id.placeholder);
        adContainer = findViewById(R.id.adContainer);
        adCard = findViewById(R.id.adCard);

        showAd =false;

        mAdView = findViewById(R.id.adView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {  // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {  // Code to be executed when an ad request fails.
                showBanner();
            }
        });

        checkAdShow();

    }

    private void getDataItems() {
        DataManager dataManager = new DataManager(this);
        ArrayList<DataItem> data = dataManager.getCatDBList(categoryID);
        exerciseData = data;
        cardData = data;
    }


    private void sortDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String sort = appSettings.getString("sort_pers", getString(R.string.set_sort_pers_default));

        int checkedItem = 0;

        if (sort.equals("alphabet"))  checkedItem = 1;

        builder.setTitle(getString(R.string.sort_pers_dialog_title))

                .setSingleChoiceItems(R.array.set_sort_pers_list, checkedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        showSort(which);
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)

                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showSort(int num) {

        String orderValue = getResources().getStringArray(R.array.set_sort_pers_values)[0];
        if (num == 1) orderValue  = getResources().getStringArray(R.array.set_sort_pers_values)[1];

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString("sort_pers", orderValue);
        editor.apply();


        CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
        if (fragment != null) {
            fragment.updateSort();
        }

        chekMenuItem();
    }

    private void chekMenuItem() {

        String sort = appSettings.getString("sort_pers", getString(R.string.set_sort_pers_default));
        if (sort.contains("alphabet")) {
            sortMenuItem.setIcon(R.drawable.ic_sort_alphabet);
        } else {
            sortMenuItem.setIcon(R.drawable.ic_sort);
        }
    }



    public void showAlertDialog(View view, int position) {

        String id = view.getTag().toString();

        if (id.equals("divider")) return;

        Intent intent = new Intent(CatActivity.this, ScrollingActivity.class);

        intent.putExtra("starrable", true);
        intent.putExtra("id", id );
        intent.putExtra("position", position);

        startActivityForResult(intent,1);

        overridePendingTransition(R.anim.slide_in_down, 0);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
        sortMenuItem = menu.findItem(R.id.sort_from_menu);

        if (easy_mode) modeMenuItem.setVisible(true);

        if (catSpec.equals("pers")) {
            chekMenuItem();
            if (!dataManager.simplified)  sortMenuItem.setVisible(true);
        }

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
                dataModeDialog.openDialog();
                return true;
            case R.id.sort_from_menu:
                sortDialog();
                return true;
            case R.id.info_from_menu:
                infoMessage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void infoMessage() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_txt));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if(resultCode == CatActivity.RESULT_OK){

                int result=data.getIntExtra("result", -1);

                CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
                if (fragment != null) {
                    fragment.checkStarred(result);
                }
            }
        } else {

            CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
            if (fragment != null) {
                fragment.checkDataList();
            }
        }
    }



    public void nextPage(int pos) {

        Intent i = new Intent(CatActivity.this, ExerciseActivity.class) ;

        if (pos == 0 )  {
            i = new Intent(CatActivity.this, CardsActivity.class);

        } else {
            i.putExtra("ex_type", pos);
        }

        i.putExtra(Constants.EXTRA_CAT_TAG, categoryID);

        if (pos == 0) {
            i.putParcelableArrayListExtra("dataItems", cardData);
        } else {
            i.putParcelableArrayListExtra("dataItems", exerciseData);
        }

        startActivityForResult(i,2);
        openActivity.pageTransition();
    }



    private void checkAdShow() {
        showAd = appSettings.getBoolean(Constants.SET_SHOW_AD, false);

        if (Build.VERSION.SDK_INT < 21) showAd= false;

        SharedPreferences mLaunches = getSharedPreferences(AppStart.APP_LAUNCHES, Context.MODE_PRIVATE);
        int launchesNum = mLaunches.getInt(AppStart.LAUNCHES_NUM, 0);

        if (showAd) showAdCard();

        if (launchesNum < 2) {
            adContainer.setVisibility(View.GONE);
        } else  {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    manageAd();
                }
            }, 100);
        }
    }


    private void showAdCard() {
        if (showAd) adContainer.setVisibility(View.VISIBLE);
    }

    private void showBanner() {
        showAdCard();
        mAdView.setVisibility(View.GONE);
        placeholder.setAlpha(0f);
        placeholder.setVisibility(View.VISIBLE);
        placeholder.animate().alpha(1.0f).setDuration(150);

    }

    public void manageAd() {

        if (showAd) {

            showAdCard();

            String admob_ap_id = getResources().getString(R.string.admob_ap_id);
            MobileAds.initialize(getApplicationContext(), admob_ap_id);

            final AdRequest adRequest;

            if (Constants.DEBUG) {
                adRequest = new AdRequest.Builder()
                        .addTestDevice("721BA1B0D2D410F1335955C3EC0C8B71")
                        .addTestDevice("725EEA094EAF285D1BD37D14A7F78C90")
                        .addTestDevice("0B44FDBCD710428A565AA061F2BD1A98")
                        .addTestDevice("88B83495F2CC0AF4C2C431655749C546")
                        .build();
            } else {
                adRequest = new AdRequest.Builder().build();
            }

            mAdView.setVisibility(View.VISIBLE);
            mAdView.loadAd(adRequest);

        } else {

            adContainer.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {

        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
            if (!showAd) checkAdShow();
        }
    }

    @Override
    public void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


}
