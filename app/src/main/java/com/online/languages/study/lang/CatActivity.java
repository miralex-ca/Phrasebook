package com.online.languages.study.lang;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.online.languages.study.lang.adapters.CatViewPagerAdapter;
import com.online.languages.study.lang.adapters.CategoryParamsDialog;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.fragments.CatTabFragment1;
import com.online.languages.study.lang.fragments.CatTabFragment2;
import com.online.languages.study.lang.fragments.TrainingFragment;

import java.util.ArrayList;
import java.util.Locale;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_CARD;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;
import static com.online.languages.study.lang.Constants.DATA_MODE_HINT_DEFAULT;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.OUTCOME_ADDED;
import static com.online.languages.study.lang.Constants.UC_PREFIX;


public class CatActivity extends BaseActivity implements TextToSpeech.OnInitListener {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    CatViewPagerAdapter adapter;
    ViewPager viewPager;

    public ArrayList<DataItem> exerciseData = new ArrayList<>();
    public ArrayList<DataItem> cardData = new ArrayList<>();


    public static String categoryID;
    public static String catSpec;

    public String parentSectionId;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    ImageView placeholder;
    View adContainer;
    View adCard;

    Boolean showAd;
    AdView mAdView;

    OpenActivity openActivity;

    MenuItem sortMenuItem;
    private MenuItem bookmarkRadio;
    MenuItem modeMenuItem;
    MenuItem hintMenuItem;


    DataManager dataManager;
    private MenuItem changeLayoutBtn;

    NavStructure navStructure;

    boolean showDelStats;

    boolean open;

    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 15;

    boolean speaking;
    boolean fromEdit;
    boolean displayModeHint;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_cat);


        dataManager = new DataManager(this);
        open = true;


        easy_mode = dataManager.easyMode();
        dataModeDialog = new DataModeDialog(this);

        fromEdit = getIntent().hasExtra("from_edit");

        showDelStats = appSettings.getBoolean("set_del_stats_cat", false);

        navStructure = dataManager.getNavStructure();

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        categoryID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);
        catSpec = getIntent().getStringExtra(Constants.EXTRA_CAT_SPEC);

        String title = getIntent().getStringExtra("cat_title");

        parentSectionId = getIntent().getStringExtra(EXTRA_SECTION_ID);


        setTitle(title);

        if (categoryID.contains(UC_PREFIX)) {
            DataObject ucat = dataManager.dbHelper.getUCat(categoryID);
            setTitle(ucat.title);
        }

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

        if ( getIntent().hasExtra("open_tab_1")) {
            viewPager.setCurrentItem(1);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                checkIconDisplay(tab.getPosition());

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

        //check for TTS data
        //speakBtn = findViewById(R.id.speakBtn);

        speaking = appSettings.getBoolean("set_speak", true);
        checkTTSIntent();

    }

    private void checkTTSIntent() {

        if (! speaking ) return;

        PackageManager pm = getPackageManager();
        final Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        ResolveInfo resolveInfo = pm.resolveActivity( checkTTSIntent, PackageManager.MATCH_DEFAULT_ONLY );

        if( resolveInfo == null ) {
            Toast.makeText(this, "TTS not available", Toast.LENGTH_SHORT).show();
            speaking = false;

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

                }
            }, 100);

        }
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


    private void chekMenuItem() {

        String sort = appSettings.getString("sort_pers", getString(R.string.set_sort_pers_default));
        if (sort.contains("alphabet")) {
            sortMenuItem.setIcon(R.drawable.ic_sort_alphabet);
        } else {
            sortMenuItem.setIcon(R.drawable.ic_sort);
        }
    }

    public void changeStarred(View view) { // from xml star icon

        changeStarred( view, false);
    }

    public void changeStarred(View view, boolean vibe) {

        DataItem dataItem = (DataItem) view.getTag();

        CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
        if (fragment != null) {
            fragment.changeStarred(dataItem.id, vibe);
        }
    }


    public void play(View view) {

        DataItem dataItem = (DataItem) view.getTag();

        //Toast.makeText(this, "Play: " + dataItem.item, Toast.LENGTH_SHORT).show();

        speakReading(dataItem);

    }

    public void openCard(View view) {

        View animObj = view.findViewById(R.id.animObj);
        int position = (int) view.getTag();

        if (open) openDetailDialog(animObj, position);

    }


    public void openDetailDialog(final View view, final int position) {

        if (open) {

            if (speaking) speakWords("");

            new Handler(Looper.getMainLooper()).postDelayed(() -> showAlertDialog(view,
                    position), 50);

            open = false;

            new Handler(Looper.getMainLooper()).postDelayed(() -> open = true, 200);
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

    private void checkModeIcon() {
        easy_mode = dataManager.easyMode();
        modeMenuItem.setVisible(easy_mode);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);

        modeMenuItem = menu.findItem(R.id.easy_mode);
        checkModeIcon();

        changeLayoutBtn = menu.findItem(R.id.list_layout);
        applyLayoutStatus();

        bookmarkRadio = menu.findItem(R.id.bookmark);
        applyBookmarkStatus();

        hintMenuItem = menu.findItem(R.id.hint_from_menu);
        checkHint();

        if (categoryID.contains(UC_PREFIX)) {
            if (!fromEdit) menu.findItem(R.id.edit_from_menu).setVisible(true);
            menu.findItem(R.id.mode_from_menu).setVisible(false);
            modeMenuItem.setVisible(false);
        }

        if (!showDelStats) {
            menu.findItem(R.id.remove_stats_from_menu).setVisible(false);
        }

        if (!getResources().getBoolean(R.bool.display_mode)) {
            modeMenuItem.setVisible(false);
            hintMenuItem.setVisible(false);
            menu.findItem(R.id.mode_from_menu).setVisible(false);
        }


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            backTransition();
            return true;
        } else if (id == R.id.easy_mode) {
            easyModeHint();
            return true;
        } else if (id == R.id.sort_from_menu) {
            sortDialog();
            return true;
        } else if (id == R.id.list_layout) {
            changeLayoutStatus();
            return true;
        } else if (id == R.id.bookmark) {
            changeBookmark();
            return true;
        } else if (id == R.id.info_from_menu) {
            infoMessage();
            return true;
        } else if (id == R.id.remove_stats_from_menu) {
            deleteConfirmDialog();
            return true;
        } else if (id == R.id.mode_from_menu) {
            listModeDialog();
            return true;
        } else if (id == R.id.settings_from_menu) {
            settingsDialog();

            return true;
        } else if (id == R.id.hint_from_menu) {
            modeHint();
            return true;
        } else if (id == R.id.edit_from_menu) {
            openEditCat();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void settingsDialog() {

        CategoryParamsDialog categoryParamsDialog = new CategoryParamsDialog(this){
            @Override
            public void practiceDialogCloseCallback() {

                new Handler(Looper.getMainLooper()).postDelayed(() -> updateCatData(), 50);
            }
        };

        categoryParamsDialog.showParams();
    }

    private void checkHint() {

        displayModeHint = appSettings.getBoolean("set_mode_hint", DATA_MODE_HINT_DEFAULT);
        if (categoryID.contains(UC_PREFIX)) displayModeHint = false;

       if (displayModeHint)  {
           if (easy_mode) {
               displayModeHint = false;
               saveModeHintDisplay();
           }
       }

       hintMenuItem.setVisible(displayModeHint);
    }

    private void saveModeHintDisplay() {

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("set_mode_hint", false);
        editor.apply();
    }

    private void easyModeHint() {
        //dataModeDialog.openDialog();

        DataMode dataMode = new DataMode(this);
        dataMode.createEasyDialog(getString(R.string.mode_dialog_title),
                getString(R.string.mode_dialog_info), getString(R.string.mode_dialog_info_2));

    }

    private void modeHint() {
        DataMode dataMode = new DataMode(this);
        dataMode.createModeHint(getString(R.string.mode_dialog_title),
                getString(R.string.mode_dialog_info), getString(R.string.mode_dialog_info_2));
    }

    private void listModeDialog() {
        DataMode dataMode = new DataMode(this);
        dataMode.createDialog2(getString(R.string.mode_dialog_title),
                getString(R.string.mode_dialog_info), getString(R.string.mode_dialog_info_2));
    }

    private class DataMode extends DataModeDialog {

        public DataMode(Context _context) {  super(_context);  }

        @Override
        public void callMode(int index) {
           saveListMode(index);
        }

        @Override
        public void callHint() {
            saveModeHintDisplay();
        }
    }



    private void saveListMode(int num) {

        String orderValue = getResources().getStringArray(R.array.set_data_mode_values)[0];
        if (num == 1) orderValue  = getResources().getStringArray(R.array.set_data_mode_values)[1];

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(Constants.SET_DATA_MODE, orderValue);
        editor.apply();

        updateCatData();

    }


    public void updateCatData() {

        getDataItems();

        checkModeIcon();
        checkHint();

        updateFragmentsData();
    }

    private void updateFragmentsData() {
        CatTabFragment1 fragment1 = (CatTabFragment1) adapter.getFragmentOne();

        if (fragment1 != null) {
            fragment1.updateList();
        }

        TrainingFragment trainingFragment = (TrainingFragment) adapter.getFragmentTwo();
        if (trainingFragment != null) {
            trainingFragment.getData();
        }
    }


    public void deleteConfirmDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle(R.string.confirmation_txt);

        builder.setMessage(R.string.delete_stats_confirm_text);

        builder.setCancelable(true);

        builder.setPositiveButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteCatResults();

            }
        });

        builder.setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }


    private void deleteCatResults() {
        String catId = categoryID;
        
        Toast.makeText(this, R.string.delete_stats_process, Toast.LENGTH_LONG).show();

        dataManager.removeCatData(catId);

        updateDataList();
    }

    private void updateDataList() {
        updateFragmentsData();
    }

    private void openEditCat() {

        Intent i = new Intent(this, MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, categoryID);
        i.putExtra("view_ucat", "hide");
        startActivityForResult(i, 10);

    }


    private void applyBookmarkStatus() {

         boolean status = dataManager.dbHelper.checkBookmark(categoryID, parentSectionId);

        if (status) bookmarkRadio.setIcon(R.drawable.ic_bookmark_active);
        else bookmarkRadio.setIcon(R.drawable.ic_bookmark_inactive);

         bookmarkRadio.setChecked(status);
    }

    private void changeBookmark() {


        int status = dataManager.setBookmark(categoryID, parentSectionId, navStructure );

        boolean radioChecked;

        if (status == OUTCOME_ADDED) {
            bookmarkRadio.setIcon(R.drawable.ic_bookmark_active);
            radioChecked = true;
        } else {
            bookmarkRadio.setIcon(R.drawable.ic_bookmark_inactive);
            radioChecked = false;
        }

        bookmarkRadio.setChecked(radioChecked);
    }

    private void applyLayoutStatus() {

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        switch (listType) {
            case CAT_LIST_VIEW_COMPACT:
                changeLayoutBtn.setIcon(R.drawable.ic_view_list_card);
                break;
            case CAT_LIST_VIEW_NORM:
                changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);
                break;
            default:
                changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
                break;
        }
    }

    public void changeLayoutStatus() {

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        switch (listType) {
            case CAT_LIST_VIEW_NORM:
                listType = CAT_LIST_VIEW_COMPACT;
                break;
            case CAT_LIST_VIEW_COMPACT:
                listType = CAT_LIST_VIEW_CARD;
                break;
            case CAT_LIST_VIEW_CARD:
                listType = CAT_LIST_VIEW_NORM;
                break;
        }

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(CAT_LIST_VIEW, listType);
        editor.apply();

        new Handler().postDelayed(() -> {

            CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
            if (fragment != null)   fragment.updateLayoutStatus();

        }, 500);

        new Handler().postDelayed(this::applyLayoutStatus, 700);
    }


    private void infoMessage() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_txt));
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backTransition();
    }

    public void backTransition() {
       if (!fromEdit) openActivity.pageBackTransition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        open = true;

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {  /// return from detail dialog

            if(resultCode == RESULT_OK){

                int result=data.getIntExtra("result", -1);

                CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
                if (fragment != null) {
                    fragment.checkStarred(result, 180);
                }
            }
        } else if (requestCode == 10) {  ///// editing category

            if(resultCode == 50) {

                finish();

            } else {

                DataObject ucat = dataManager.dbHelper.getUCat(categoryID);
                setTitle(ucat.title);

                cardData = dataManager.getCatDBList(categoryID);
                exerciseData = cardData;

                if (cardData.size() == 0) {

                    finish();

                } else {

                    CatTabFragment1 fragment = (CatTabFragment1) adapter.getFragmentOne();
                    if (fragment != null) {
                        fragment.updateDataList();
                    }
                }
            }

        } else {
            updateFragmentsData();
        }


        if (requestCode == MY_DATA_CHECK_CODE) {

            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);

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
            new Handler().postDelayed(() -> manageAd(), 100);
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

        if(myTTS != null){
            myTTS.shutdown();
        }
    }

    @Override
    public void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }



    //// TTS integration

    public void speakReading(DataItem dataItem) {
        String text = dataManager.getPronounce(dataItem);
        speakWords(text);
    }


    private void speakWords(String speech) {
        //speak straight away
        if (myTTS != null) myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    //act on result of TTS data check


    //setup TTS
    public void onInit(int initStatus) {


        final Locale locale = dataManager.getLocale();

        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(locale)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(locale);
            //  speakBtn.setVisibility(View.VISIBLE);
        }
        else if (initStatus == TextToSpeech.ERROR) {

        }
    }

    @Override
    public void finish() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_CAT_ID, categoryID);
        setResult(RESULT_OK,returnIntent);

        if (speaking) speakWords("");
        super.finish();


    }


}
