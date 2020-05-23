package com.online.languages.study.lang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.CardsPagerAdapter;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;

import java.util.ArrayList;
import java.util.Collections;

public class CardsActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    String topicTag;
    ArrayList<DataItem> wordList = new ArrayList<>();

    ArrayList<DataItem> originWordsList;

    TextView fCounterInfoBox;
    Button prevButton;
    Button nextButton;
    int counter = 0;
    int wordListLength = 0;

    private MenuItem fShowTranslateCheck;
    private MenuItem fRemixStatusCheckbox;
    private MenuItem fReverseDataCheck;
    private MenuItem exShowBtnRadio;

    public static Boolean fShowTranslate;
    public static Boolean fMixWords;
    public static Boolean fShowTranscript;
    public static Boolean fRevertData;

    public static Boolean fPhraseLayout = false;

    ViewPager viewPager;
    CardsPagerAdapter viewPagerAdapter;


    public static Boolean exButtonShow;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    OpenActivity openActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_cards);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.title_card_txt);

        topicTag = getIntent().getStringExtra(Constants.EXTRA_CAT_TAG);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new DataModeDialog(this);

        if (topicTag.equals(Constants.STARRED_CAT_TAG)) easy_mode = false;


        topicTag = "dates";


        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        fPhraseLayout = topicTag.contains("ph_");

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        fShowTranslate = appSettings.getBoolean("f_translate_show", false);
        fMixWords = appSettings.getBoolean("f_mix", false);
        fShowTranscript = appSettings.getBoolean("transcript_show", true) && appSettings.getBoolean("transcript_show_f", true);
        fRevertData = appSettings.getBoolean("f_reverse", false);

        exButtonShow = appSettings.getBoolean("card_buttons_show", true);


        String cat = "dates";
        DataManager dataManager = new DataManager(this);


        originWordsList = getIntent().getParcelableArrayListExtra("dataItems");


        getWordlist();

        wordListLength = wordList.size();

        fCounterInfoBox = findViewById(R.id.testInfoBox);
        prevButton = findViewById(R.id.back_btn);
        nextButton = findViewById(R.id.next_btn);

        viewPager = findViewById(R.id.cardsPager);

        startFlashcard(false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                showPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


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

            case R.id.fShowTranslate:
                changeShowTranslateStatus();
                return true;
            case R.id.fRemixWords:
                changeFRemixStatus();
                return true;
            case R.id.fReflectInfo:
                changeReverseDataStatus();
                return true;
            case R.id.fRestart:
                startFlashcard(true);
                return true;
            case R.id.exBtnSettings:
                changeExBtnStatus();
                return true;
            case R.id.easy_mode:
                dataModeDialog.openDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flashcard, menu);

        fShowTranslateCheck =  menu.findItem(R.id.fShowTranslate);
        fRemixStatusCheckbox = menu.findItem(R.id.fRemixWords);
        fReverseDataCheck = menu.findItem(R.id.fReflectInfo);

        applyShowTranslateStatus(fShowTranslate, false);
        applyFRemixStatus(fMixWords);
        applyReverseDataStatus(fRevertData, false);


        exShowBtnRadio =  menu.findItem(R.id.exBtnSettings);
        applyExBtnStatus(exButtonShow, false);

        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
        if (easy_mode) modeMenuItem.setVisible(true);

        return true;
    }

    private void changeExBtnStatus() {

        exButtonShow = !exButtonShow;
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("card_buttons_show", exButtonShow);
        editor.apply();
        applyExBtnStatus(exButtonShow, true);

    }

    private void changeShowTranslateStatus() {
        fShowTranslate = !fShowTranslate;
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("f_translate_show", fShowTranslate);
        editor.apply();
        applyShowTranslateStatus(fShowTranslate, false);
    }

    private void applyShowTranslateStatus(Boolean translateStatus, Boolean animation) {
        fShowTranslateCheck.setChecked(translateStatus);
        updateFlashcard(animation);
    }

    private void changeReverseDataStatus() {
        fRevertData = !fRevertData;
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("f_reverse", fRevertData);
        editor.apply();
        applyReverseDataStatus(fRevertData, true);
    }

    private void applyReverseDataStatus(Boolean reverseDataStatus, Boolean animation) {
        fReverseDataCheck.setChecked(reverseDataStatus);
        updateFlashcard(animation);
    }

    private void changeFRemixStatus() {
        fMixWords = !fMixWords;
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("f_mix", fMixWords);
        editor.apply();
        applyFRemixStatus(fMixWords);
        startFlashcard(true);
    }

    private void applyFRemixStatus(Boolean remixStatus) {
        fRemixStatusCheckbox.setChecked(remixStatus);
    }

    private void applyExBtnStatus(Boolean btnStatus, Boolean animation) {

        LinearLayout btnBox = findViewById(R.id.btnBox);

        if (wordList.size() == 1) {
            animation = false;
            btnStatus = false;
            exShowBtnRadio.setEnabled(false);
        }

        int duration = 400;

        if (!animation) duration = 0;

        if (!btnStatus) {

            btnBox.animate().alpha(0.0f).setDuration(duration);

        } else {
            btnBox.animate().alpha(1f).setDuration(duration);
        }

        exShowBtnRadio.setChecked(btnStatus);
    }


    private void startFlashcard(Boolean animation) {
        getWordlist();
        showPage(0);

        if (animation) {

            viewPager.animate()
                    .alpha(0.0f)
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    viewPagerAdapter = new CardsPagerAdapter(CardsActivity.this, wordList );
                                    viewPager.setAdapter(viewPagerAdapter);
                                    viewPager.animate().alpha(1.0f).setDuration(400).setListener(null);
                                }
                            }, 220);

                        }
                    });
        } else {
            viewPagerAdapter = new CardsPagerAdapter(this, wordList );
            viewPager.setAdapter(viewPagerAdapter);
        }

    }

    private void updateFlashcard(Boolean animation) {
        final int current = viewPager.getCurrentItem();

        if (animation) {

            viewPager.animate()
                    .alpha(0.0f)
                    .setDuration(100)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            viewPager.setAdapter(viewPagerAdapter);
                            viewPager.setCurrentItem(current, false);

                            viewPager.animate()
                                    .alpha(1.0f)
                                    .setDuration(400)
                                    .setListener(null);
                        }
                    });
        } else {
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(current, false);
        }

    }


    private void showPage(int position) {
        String counterTxt = String.format(getResources().getString(R.string.f_counter_txt), position+1, wordListLength);
        fCounterInfoBox.setText(counterTxt);

        if (!nextButton.isEnabled()) { nextButton.setEnabled(true);}
        if (!prevButton.isEnabled()) { prevButton.setEnabled(true);}

        if (position == 0) {
            prevButton.setEnabled(false);
        } else if (position >=  (wordListLength-1) ){
            nextButton.setEnabled(false);
        }
    }

    public void vPrev(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true );
    }

    public void vNext(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true );
    }


    void getWordlist()
    {
        wordList = new ArrayList<>();

        for (DataItem word: originWordsList ) {
            wordList.add(word);
        }

        if (fMixWords) {
            Collections.shuffle(wordList);
        }


    }



}
