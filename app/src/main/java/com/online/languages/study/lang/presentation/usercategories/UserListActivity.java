package com.online.languages.study.lang.presentation.usercategories;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_CARD;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.CategoryParamsDialog;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.utils.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.adapters.UserListViewPagerAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.fragments.UserListTabFragment1;
import com.online.languages.study.lang.fragments.UserListTabFragment2;
import com.online.languages.study.lang.fragments.UserListTrainingFragment;
import com.online.languages.study.lang.presentation.core.BaseActivity;
import com.online.languages.study.lang.presentation.details.ScrollingActivity;
import com.online.languages.study.lang.presentation.exercise.ExerciseActivity;
import com.online.languages.study.lang.presentation.flashcards.CardsActivity;

import java.util.ArrayList;
import java.util.Locale;

public class UserListActivity extends BaseActivity implements TextToSpeech.OnInitListener {


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

    boolean open;

    private TextToSpeech myTTS;
    private final int MY_DATA_CHECK_CODE = 15;
    private final int RESULT_CODE_FROM_TEST = 10;

    boolean speaking;


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

        open = true;

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

            new Handler(Looper.getMainLooper()).postDelayed(() -> startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE), 100);

        }
    }


    public void changeStarred(View view) {
        changeStarred(view, false);
    }


    public void changeStarred(View view, boolean vibe) {
        DataItem dataItem = (DataItem) view.getTag();
        UserListTabFragment1 fragment = (UserListTabFragment1 ) adapter.getFragmentOne();
        if (fragment != null) {
            fragment.changeStar(dataItem.id, vibe);
        }
    }


    public void openCard(View view) {

        View animObj = view.findViewById(R.id.animObj);

        if (open) openDetailDialog(animObj);

    }

    public void openDetailDialog(final View view) {
        if (open) {
            if (speaking) speakWords("");
            new Handler(Looper.getMainLooper()).postDelayed(() -> showAlertDialog(view), 50);
            open = false;

            new Handler(Looper.getMainLooper()).postDelayed(() -> open = true, 200);
        }
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

        startActivityForResult(i, RESULT_CODE_FROM_TEST);

        openActivity.pageTransition();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        open = true;

        if (requestCode == 1) {  /// back from detail activity
            if (resultCode == UserListActivity.RESULT_OK) {

                UserListTabFragment1 fragment = (UserListTabFragment1) adapter.getFragmentOne();
                if (fragment != null) {
                    fragment.checkStarred();
                }
            }
        }

        if (requestCode == MY_DATA_CHECK_CODE) {

            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        if (requestCode == RESULT_CODE_FROM_TEST) {
            updateTestsList();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        } else if (id == R.id.list_layout) {
            changeLayoutStatus();
            return true;
        } else if (id == R.id.settings_from_menu) {
            settingsDialog();
            return true;
        } else if (id == R.id.starred_del_results) {
            deleteStarredExResults();
            return true;
        } else if (id == R.id.info_item) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void settingsDialog() {

        CategoryParamsDialog categoryParamsDialog = new CategoryParamsDialog(this, null){
            @Override
            public void practiceDialogCloseCallback() {

                new Handler(Looper.getMainLooper()).postDelayed(() -> updateCatData(), 50);
            }
        };

        categoryParamsDialog.showParams();
    }

    private void updateCatData() {

        UserListTabFragment1 fragment1 = (UserListTabFragment1) adapter.getFragmentOne();

        if (fragment1 != null) {
            fragment1.updateList();
        }

        updateTestsList();

    }

    private void updateTestsList() {
        UserListTrainingFragment trainingFragment = (UserListTrainingFragment) adapter.getFragmentTwo();
        if (trainingFragment != null) {
            trainingFragment.getData();
        }
    }


    private void showInfoDialog() {
        InfoDialog infoDialog = new InfoDialog(this);
        infoDialog.simpleDialog(getString(R.string.info_txt), getString(R.string.info_img_starred));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_starred, menu);

        changeLayoutBtn = menu.findItem(R.id.list_layout);
        applyLayoutStatus();

        return true;
    }


    private void applyLayoutStatus() {

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);
        } else if (listType.equals(CAT_LIST_VIEW_NORM)) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
        } else {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_card);
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

        UserListTabFragment1 fragment = (UserListTabFragment1) adapter.getFragmentOne();
        if (fragment != null)   fragment.updateLayoutStatus();

        applyLayoutStatus();
    }

    private void  checkIconDisplay(int position) {

        if (position == 1) {

            new Handler(Looper.getMainLooper()).postDelayed(() -> changeLayoutBtn.setVisible(false), 400);

        } else {

            new Handler(Looper.getMainLooper()).postDelayed(() -> changeLayoutBtn.setVisible(true), 400);
        }

    }

    private  void deleteStarredExResults() {

        String[] topic = new String[3];
        topic[0] = Constants.STARRED_CAT_TAG +"_1";
        topic[1] = Constants.STARRED_CAT_TAG +"_2";
        topic[2] = Constants.STARRED_CAT_TAG +"_3";

        dbHelper.deleteExData(topic);

        updateTestsList();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if(myTTS != null){
            myTTS.shutdown();
        }
    }


    public void play(View view) {

        DataItem dataItem = (DataItem) view.getTag();

        //Toast.makeText(this, "Play: " + dataItem.item, Toast.LENGTH_SHORT).show();

        speakReading(dataItem);

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

        if (speaking) speakWords("");
        super.finish();
    }


}
