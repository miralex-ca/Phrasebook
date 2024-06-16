package com.online.languages.study.lang.presentation.category;

import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.EXTRA_UCAT_SOURCE;
import static com.online.languages.study.lang.Constants.OUTCOME_ADDED;
import static com.online.languages.study.lang.Constants.UCAT_SOURCE_EDIT;
import static com.online.languages.study.lang.Constants.UC_PREFIX;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.databinding.ActivityCatBinding;
import com.online.languages.study.lang.presentation.AppStart;
import com.online.languages.study.lang.presentation.category.category_exercise.TrainingFragment;
import com.online.languages.study.lang.presentation.category.category_list.CatTabFragment1;
import com.online.languages.study.lang.presentation.core.ThemedActivity;
import com.online.languages.study.lang.presentation.exercise.ExerciseActivity;
import com.online.languages.study.lang.presentation.flashcards.CardsActivity;
import com.online.languages.study.lang.presentation.usercategories.MyCatEditActivity;

import java.util.ArrayList;
import java.util.Locale;


public class CatActivity extends ThemedActivity implements TextToSpeech.OnInitListener, CategoryViewActions {

    private CategoryViewController viewController;
    private ActivityCatBinding mBinding;

    public static String categoryID;
    public static String catSpec;
    private String parentSectionId;

    private CatViewPagerAdapter adapter;
    private ViewPager viewPager;

    private ArrayList<DataItem> exerciseData = new ArrayList<>();
    private ArrayList<DataItem> cardData = new ArrayList<>();

    private DataModeDialog dataModeDialog;
    private CategoryParamsDialog categoryParamsDialog;

    private ImageView placeholder;
    private View adContainer;

    private Boolean showAd;
    private AdView mAdView;

    private MenuItem bookmarkRadio;
    private DataManager dataManager;
    private NavStructure navStructure;

    private boolean showDelStats;
    boolean open;

    private TextToSpeech myTTS;
    private final int MY_DATA_CHECK_CODE = 15;

    private boolean speaking;
    private boolean fromEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityCatBinding.inflate(getLayoutInflater());
        openActivity.setOrientation();
        setContentView(mBinding.getRoot());
        setupToolbar();

        categoryID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);
        catSpec = getIntent().getStringExtra(Constants.EXTRA_CAT_SPEC);
        parentSectionId = getIntent().getStringExtra(EXTRA_SECTION_ID);
        String title = getIntent().getStringExtra("cat_title");
        setTitle(title);

        initViewController(categoryID);
        initTabsPager();

        dataManager = new DataManager(this);
        open = true;

        fromEdit = getIntent().hasExtra("from_edit");
        showDelStats = appSettings.getBoolean("set_del_stats_cat", false);
        navStructure = dataManager.getNavStructure();

        if (categoryID.contains(UC_PREFIX)) {
            DataObject ucat = dataManager.dbHelper.getUCat(categoryID);
            setTitle(ucat.title);
        }

        getDataItems();

        placeholder = findViewById(R.id.placeholder);
        adContainer = findViewById(R.id.adContainer);

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

        speaking = appSettings.getBoolean("set_speak", true);
        checkTTSIntent();

        initParamsDialog();
        dataModeDialog = new DataModeDialog(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewController(String categoryId) {
        viewController = new CategoryViewControllerImpl(
                mBinding,
                appContainer.getModels().provideCategoryViewModel(this),
                this
        );
        viewController.setup(categoryId);
    }

    private void initTabsPager() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_tab1_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.section_tab2_title));

        viewPager = findViewById(R.id.container);
        adapter = new CatViewPagerAdapter (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (getIntent().hasExtra("open_tab_1")) {
            viewPager.setCurrentItem(1);
        }

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

    private void initParamsDialog() {
        categoryParamsDialog = new CategoryParamsDialog(this, appContainer.getRepository()){
            @Override
            public void practiceDialogCloseCallback() {
                updateCatData();
            }
        };
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
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        }
    }

    private void getDataItems() {
        DataManager dataManager = new DataManager(this);
        ArrayList<DataItem> data = dataManager.getCatDBList(categoryID);
        exerciseData = data;
        cardData = data;
    }

    public void play(View view) {
        DataItem dataItem = (DataItem) view.getTag();
        speakReading(dataItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        bookmarkRadio = menu.findItem(R.id.bookmark);
        applyBookmarkStatus();

        if (categoryID.contains(UC_PREFIX)) {
            if (!fromEdit) menu.findItem(R.id.edit_from_menu).setVisible(true);
        }

        if (!showDelStats) {
            menu.findItem(R.id.remove_stats_from_menu).setVisible(false);
        }

        if (!getResources().getBoolean(R.bool.display_mode)) {
            menu.findItem(R.id.mode_from_menu).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
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
        } else if (id == R.id.settings_from_menu) {
            settingsDialog();
            return true;
        } else if (id == R.id.edit_from_menu) {
            openEditCat();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void settingsDialog() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            categoryParamsDialog.showParams();
        }, 280);
    }

    public void updateCatData() {
        getDataItems();
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
        StatsDeleteConfirmationDialog dialog = new StatsDeleteConfirmationDialog(this, () -> {
            deleteCatResults();
            return null;
        });
        dialog.show();
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
        i.putExtra(EXTRA_UCAT_SOURCE, UCAT_SOURCE_EDIT);
        startActivityForResult(i, 10);
        openActivity.pageTransitionOpen();
    }

    private void applyBookmarkStatus() {
        boolean status = dataManager.dbHelper.checkBookmark(categoryID, parentSectionId);
        if (status) bookmarkRadio.setIcon(R.drawable.ic_bookmark_active);
        else bookmarkRadio.setIcon(R.drawable.ic_bookmark_inactive);
        bookmarkRadio.setChecked(status);
    }

    private void changeBookmark() {
        int status = dataManager.setBookmark(categoryID, parentSectionId, navStructure);
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

    private void infoMessage() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_txt));
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_CAT_ID, categoryID);
        setResult(RESULT_OK,returnIntent);
        if (speaking) speakWords("");
        super.finish();
        openActivity.pageBackTransition();
    }

    public void backTransition() {
       if (!fromEdit) openActivity.pageBackTransition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        open = true;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {  ///// editing category
            if (resultCode == 50) {
                finish();
            } else {
                DataObject ucat = dataManager.dbHelper.getUCat(categoryID);
                setTitle(ucat.title);

                cardData = dataManager.getCatDBList(categoryID);
                exerciseData = cardData;

                if (cardData.isEmpty()) {
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
                myTTS = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void nextPage(int pos) {
        Intent intent = new Intent(this, ExerciseActivity.class) ;
        if (pos == 0 )  {
            intent = new Intent(this, CardsActivity.class);
        } else {
            intent.putExtra("ex_type", pos);
        }

        intent.putExtra(Constants.EXTRA_CAT_TAG, categoryID);

        if (pos == 0) {
            intent.putParcelableArrayListExtra("dataItems", cardData);
        } else {
            intent.putParcelableArrayListExtra("dataItems", exerciseData);
        }

        startActivityForResult(intent,2);
        openActivity.pageTransition();
    }

    private void checkAdShow() {
        showAd = appSettings.getBoolean(Constants.SET_SHOW_AD, false);
        SharedPreferences mLaunches = getSharedPreferences(AppStart.APP_LAUNCHES, Context.MODE_PRIVATE);
        int launchesNum = mLaunches.getInt(AppStart.LAUNCHES_NUM, 0);

        if (showAd) showAdCard();

        if (launchesNum < 2) {
            adContainer.setVisibility(View.GONE);
        } else  {
            new Handler().postDelayed(this::manageAd, 100);
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

        if (myTTS != null) {
            myTTS.shutdown();
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

    public void speakReading(DataItem dataItem) {
        String text = dataManager.getPronounce(dataItem);
        speakWords(text);
    }

    private void speakWords(String speech) {
        if (myTTS != null) myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onInit(int initStatus) {
        final Locale locale = dataManager.getLocale();

        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(locale)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(locale);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Log.d("CatActivity", "Problem to init TextToSpeech");
        }
    }

}
