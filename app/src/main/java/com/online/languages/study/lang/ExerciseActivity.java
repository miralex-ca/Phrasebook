package com.online.languages.study.lang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.CustomViewPager;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.ExerciseController;
import com.online.languages.study.lang.data.ExerciseDataCollect;
import com.online.languages.study.lang.data.ExerciseTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.EX_AUDIO_TYPE;
import static com.online.languages.study.lang.Constants.EX_IMG_TYPE;

public class ExerciseActivity extends BaseActivity implements TextToSpeech.OnInitListener {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;


    static String topicTag;
    ArrayList<DataItem> wordList = new ArrayList<>();
    ArrayList<String> catWordsTxt = new ArrayList<>();

    ArrayList<DataItem> originWordsList;

    static TextView fCounterInfoBox;
    static TextView exResultTxt;
    static TextView exMarkTxt;
    static Button checkButton;
    Button nextButton;

    LinearLayout btnBoxWrapper;
    LinearLayout btnResultBox;
    LinearLayout btnGroupBox;
    LinearLayout exQuestWrapper;
    View buttonsContainer;

    static View exerciseField;
    static LinearLayout exResultBox;

    static int wordListLength = 0;

    Boolean phraseLayout;

    static int correctAnswers;
    static ArrayList<DataItem> completed;

    public static Boolean  exCheckedStatus;
    public static int exType = 1;
    public static int exTxtHeight = 120;
    public static int exTxtMoreHeight = 160;
    public int exCardHeight = 330;
    public int exCardMoreHeight = 360;

    private MenuItem exShowBtnRadio;

    private MenuItem exSaveStatsRadio;
    private static Snackbar mSnackbar;

    static CustomViewPager viewPager;
    ExercisePagerAdapter viewPagerAdapter;

    public static Boolean fShowTranscript;

    public static Boolean exButtonShow;
    static Context context;
    public static Boolean exShowTranscript;

    static Boolean revision;
    static Boolean testing;

    static Boolean forceSave;

    int sectionOrder;

    ExerciseController exerciseController;

    DBHelper dbHelper;
    DataManager dataManager;

    Boolean restore;
    ArrayList<DataItem> savedWords;

    static Boolean resultShow;

    static int taskCheckedStatus;


    Boolean tablet = false;

    static Boolean saveStats;


    ExerciseDataCollect exerciseAllData;


    DataModeDialog dataModeDialog;

    OpenActivity openActivity;


    private static TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;
    boolean speaking = false;
    static int initSpeak = 0;
    String autoPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Toast.makeText(this, "Len: " , Toast.LENGTH_SHORT).show();

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        dataManager = new DataManager(this);


        setContentView(R.layout.activity_exercise);

        openActivity = new OpenActivity(this);

        openActivity.setOrientation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.title_test_txt);


        exButtonShow = true;
        exCheckedStatus = false;
        revision = false;
        testing = false;

        resultShow = false;


        topicTag = getIntent().getStringExtra(Constants.EXTRA_CAT_TAG);

        initSpeak = 0;
        autoPlay = appSettings.getString("set_test_autoplay", getString(R.string.set_flash_autoplay_default)); // TODO

        dataModeDialog = new DataModeDialog(this);

        forceSave = true;
        taskCheckedStatus = 0;

        if (topicTag.equals(Constants.STARRED_CAT_TAG)) {
           String save =  appSettings.getString("starred_save_type", "0");

           if (save.equals("1")) forceSave = false;

        } else {
            forceSave = true;
        }


        exType = getIntent().getIntExtra("ex_type", 1); /* 1 - voc, 2 - phrase*/
        // exType = 2;


        fShowTranscript = true;
        context = this;

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        exButtonShow = appSettings.getBoolean("ex_buttons_show", false);
        Boolean showTranscript = appSettings.getBoolean("transcript_show", true) && appSettings.getBoolean("transcript_show_ex", true);

        saveStats = appSettings.getBoolean("test_all_save", true);


        exShowTranscript = showTranscript;


        fCounterInfoBox = findViewById(R.id.testInfoBox);

        checkButton = findViewById(R.id.exCheck);
        nextButton = findViewById(R.id.exNext);
        btnResultBox = findViewById(R.id.btnResultBox);
        btnGroupBox = findViewById(R.id.btnBox);
        exQuestWrapper = findViewById(R.id.exTestWrapper);
        btnBoxWrapper = findViewById(R.id.btnBoxWrapper);

        exResultBox = findViewById(R.id.exResultBox);
        exResultTxt = findViewById(R.id.exResultTxt);
        exMarkTxt = findViewById(R.id.exResultMark);

        buttonsContainer = findViewById(R.id.btnContainer);
        exerciseField = findViewById(R.id.exField);


        setExBtnStatus(exButtonShow);

        exTxtHeight = getResources().getInteger(R.integer.ex_text_wrap);
        exTxtMoreHeight = getResources().getInteger(R.integer.ex_text_wrap_high);

        exCardHeight = getResources().getInteger(R.integer.ex_card_wrap);
        exCardMoreHeight = getResources().getInteger(R.integer.ex_card_wrap_high);


        if (exType == 2) {
          //  exTxtHeight -= 0;
          //  exTxtMoreHeight -= 0;
        }

        viewPager = findViewById(R.id.testPager);
        viewPager.setPagingEnabled(false);

        btnGroupBox = findViewById(R.id.btnBox);

        originWordsList = getIntent().getParcelableArrayListExtra("dataItems");


        if (topicTag.equals(Constants.ALL_CAT_TAG)) {
            originWordsList = new ArrayList<>();
        }


        exerciseController = new ExerciseController();
        completed = new ArrayList<>();


        restore = false;

        if (savedInstanceState != null) {
            // Restore value of members from saved state

            boolean restaured = savedInstanceState.getBoolean("result_show");

            taskCheckedStatus  = savedInstanceState.getInt("checked_status");

            if (!restaured) {
                restore = true;  // TODO restore
                exerciseController = savedInstanceState.getParcelable("controller");
                correctAnswers = savedInstanceState.getInt("correct");
                completed = savedInstanceState.getParcelableArrayList("completed");

                if (taskCheckedStatus > 0) restaureChecked(taskCheckedStatus);
            }
        }



        int delay = 200;

        if (restore) delay = 0;

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                exerciseAllData = new ExerciseDataCollect(context, originWordsList, exType);
                startExercise();
            }
        }, delay);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                showPage(position);

               // Toast.makeText(getApplicationContext(), "Checked: "+ taskCheckedStatus, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });




        if (exType == EX_AUDIO_TYPE) {
            speaking = true;
        } else {
            speaking = false;
        }

        if (speaking) {
            checkTTSIntent();
        }


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







    private void restaureChecked (int type) {

        if (type == 1) {
            checkButton.setEnabled(false);
            exCheckedStatus = true;
        } else if (type == 2) {
            btnResultBox.setVisibility(View.VISIBLE);
            btnGroupBox.setVisibility(View.GONE);

            exCheckedStatus = true;

        }

    }


    public void openResult(View view) {

        Intent intent = new Intent(ExerciseActivity.this, ExerciseResultActivity.class);


        ArrayList<DataItem> results = new ArrayList<>();

        for (ExerciseTask task: exerciseController.tasks) {

            DataItem item = new DataItem();
            item.id = task.savedInfo;
            item.testError = -1;

            for (DataItem dataItem: completed) {
                if (task.savedInfo.equals(dataItem.id)) {
                    item.testError = dataItem.testError;
                }
            }

            results.add(item);

        }

        intent.putParcelableArrayListExtra("dataItems", results);
             startActivityForResult(intent,1);
             overridePendingTransition(R.anim.fade_in_2, 0);
    }



    private void getTasks () {
        int limit = Constants.QUEST_NUM;



        if (topicTag.equals(Constants.ALL_CAT_TAG)) {
            String lim =  appSettings.getString("test_all_limit", getString(R.string.set_test_all_limit_default));
            limit = Integer.parseInt(lim);
        }



        Collections.shuffle(originWordsList);

        ArrayList<DataItem> data = new ArrayList<>(originWordsList);


        if (topicTag.contains(Constants.SECTION_TEST_PREFIX)) {
            limit = Constants.SECTION_TEST_LIMIT;


            if (getIntent().hasExtra(EXTRA_SECTION_ID)) {

                //String msg = "Tags: " + topicTag + ":  "+ getIntent().getStringExtra(EXTRA_SECTION_ID);
                //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
               // data = dataManager.getSectionItems(getIntent().getStringExtra(EXTRA_SECTION_ID));

            }


            if (getIntent().hasExtra("ids")) {
                data = dataManager.getCatsItems(getIntent().getStringArrayExtra("ids"));
            }
        }


        if (topicTag.equals(Constants.ALL_CAT_TAG)) {
            data = dataManager.getAllItems();
            exerciseAllData = new ExerciseDataCollect(context, data, exType);
        }

        if (data.size() > limit) {
            data = new ArrayList<>(data.subList(0, limit));
        }


        exerciseAllData.generateTasks(data);
        exerciseAllData.shuffleTasks();

        exerciseController.tasks = exerciseAllData.tasks;

    }



    private void startExercise(){
        dataManager.getTime("Start start");

        resultShow = false;

        if (!restore) {

            getTasks ();

            correctAnswers = 0;
            completed = new ArrayList<>();
        }

        wordListLength = exerciseController.tasks.size();

        showPage(0);

        viewPagerAdapter = new ExercisePagerAdapter(this, exerciseController.tasks );
        viewPager.setAdapter(viewPagerAdapter);

        dataManager.getTime("End start", true);
    }


    public void restartExercise() {
        dataManager.getTime("Start restart");
        goToNextTask();

        btnResultBox.setVisibility(View.GONE);
        btnGroupBox.setVisibility(View.VISIBLE);

        exResultBox.setVisibility(View.GONE);
        exerciseField.setVisibility(View.VISIBLE);

        applyExBtnStatus(exButtonShow, false);

        taskCheckedStatus= 0;

        getTasks ();

        startExercise();

        dataManager.getTime("End restart", true);
    }

    public int convertDimen(int dimen) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, getResources().getDisplayMetrics());
    }


    private void manageCardHeightAndButtons() {

            changeHeight(exTxtHeight);
            manageCardHeight();
    }

    private void manageCardHeight(){
        final View card = findViewById(R.id.exCard);

        card.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                card.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h = card.getHeight(); //height is ready
                setCardHeight(h);
            }
        });
    }

    private void setCardHeight(int h) {
        View card = findViewById(R.id.exCard);
        card.getLayoutParams().height = h;
        card.setLayoutParams(card.getLayoutParams());

        applyExBtnStatus(exButtonShow, false);
    }

    private void applyExBtnStatus(Boolean btnStatus, Boolean animation) {
        if (btnStatus) {
            showBtn(animation);
        } else {
            hideBtn(animation);
        }
        exShowBtnRadio.setChecked(btnStatus);
    }



    private void setExBtnStatus(Boolean btnStatus) {
        if (btnStatus) {
            buttonsContainer.setVisibility(View.VISIBLE);
        } else {
            buttonsContainer.setVisibility(View.GONE);
        }
    }

    private void hideBtn(Boolean animation) {
        if (animation) {
            animatedBtnHide();
        } else {

            buttonsContainer.setVisibility(View.GONE);
            changeHeight(exTxtMoreHeight);
        }
    }

    private void animatedBtnHide() {
        buttonsContainer.animate().alpha(0.0f).setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        buttonsContainer.setVisibility(View.GONE);
                        changeHeightAnimated(exTxtMoreHeight);
                    }
                });
    }

    private void showBtn(Boolean animation) {
        if (animation) {
            animatedBtnShow();
        } else {
            changeHeight(exTxtHeight);
            buttonsContainer.setVisibility(View.VISIBLE);
        }
    }

    private void animatedBtnShow() {

        View tView = viewPager.findViewWithTag("myview" + viewPager.getCurrentItem());
        LinearLayout  exQuest = (LinearLayout) tView.findViewById(R.id.exQuest);

        final View nextView = viewPager.findViewWithTag("myview" + (viewPager.getCurrentItem()+1) );
        final int h = convertDimen(exTxtHeight);

        ResizeHeight resizeHeight = new ResizeHeight(exQuest, h);
        resizeHeight.setDuration(150);
        exQuest.startAnimation(resizeHeight);

        resizeHeight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {

                if (nextView != null) {
                    View nexQuest =  nextView.findViewById(R.id.exQuest);
                    nexQuest.getLayoutParams().height = h;
                    nexQuest.setLayoutParams(nexQuest.getLayoutParams());
                }


                buttonsContainer.setAlpha(0.0f);
                buttonsContainer.setVisibility(View.VISIBLE);
                buttonsContainer.animate().alpha(1f).setDuration(200)
                        .setListener(null);

            }

            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationStart(Animation arg0) {}
        });
    }


    public void changeHeightAnimated(int height) {

        View tView = viewPager.findViewWithTag("myview" + viewPager.getCurrentItem());
        LinearLayout  exQuest = (LinearLayout) tView.findViewById(R.id.exQuest);

        final View nextView = viewPager.findViewWithTag("myview" + (viewPager.getCurrentItem()+1) );
        final int h = convertDimen(height);

        ResizeHeight resizeHeight = new ResizeHeight(exQuest, h);
        resizeHeight.setDuration(150);
        exQuest.startAnimation(resizeHeight);

        resizeHeight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {

                if (nextView != null) {
                    View nexQuest =  nextView.findViewById(R.id.exQuest);
                    nexQuest.getLayoutParams().height = h;
                    nexQuest.setLayoutParams(nexQuest.getLayoutParams());
                }
            }

            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationStart(Animation arg0) {}
        });

    }




    public void changeHeight(int height) {

        int h = convertDimen(height);

        View tView = viewPager.findViewWithTag("myview" + viewPager.getCurrentItem());
        LinearLayout  exQuest = tView.findViewById(R.id.exQuest);

        exQuest.getLayoutParams().height = h;
        exQuest.setLayoutParams(exQuest.getLayoutParams());

        View nextView = viewPager.findViewWithTag("myview" + (viewPager.getCurrentItem()+1) );
        if (nextView != null) {
            View nexQuest =  nextView.findViewById(R.id.exQuest);
            nexQuest.getLayoutParams().height = h;
            nexQuest.setLayoutParams(nexQuest.getLayoutParams());
        }
    }


    public void clickRestart(View view) {
        restartExercise();
    }


    private void showPage(int position) {

        String counterTxt = String.format(getResources().getString(R.string.f_counter_txt), position+1, wordListLength);
        fCounterInfoBox.setText(counterTxt);

        //Toast.makeText(this, "Len: "+ wordListLength, Toast.LENGTH_SHORT).show();

        if (!nextButton.isEnabled()) { nextButton.setEnabled(true);}

       // if (!checkButton.isEnabled()) { checkButton.setEnabled(true);}

        if (position >=  (wordListLength-1) ){
            nextButton.setEnabled(false);
        }

       // if (resultShow) exGoToResult();



        if (position <  (wordListLength) ){
            autoPlay(position);
        }
    }

    public void clickToNext(View view) {
        goToNextTask();

    }

    public static void goToNextTask() {

        // Toast.makeText(context, "Correct: "+correctAnswers, Toast.LENGTH_SHORT).show();

        exCheckedStatus = false;

        taskCheckedStatus = 0;

        if (!checkButton.isEnabled()) { checkButton.setEnabled(true);}

        if  ( viewPager.getCurrentItem() >= (wordListLength-1) ) {
            exGoToResult();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true );
        }
        if (mSnackbar!= null && mSnackbar.isShown()) mSnackbar.dismiss();
    }

    public void exCheck(View view) {
        int position = viewPager.getCurrentItem();
        View tView = viewPager.findViewWithTag("myview" + position);
        RadioGroup group = tView.findViewById(R.id.radioGroup1);
        viewPagerAdapter.exCheckItem(group, position);

        taskCheckedStatus = 1;

        if ( position >= (wordListLength-1)  ) {
            if (exButtonShow)  {
                btnResultBox.setVisibility(View.VISIBLE);
                btnGroupBox.setVisibility(View.GONE);
                taskCheckedStatus = 2;

            }
        }
    }



    public void exShowResult(View view) {
        exGoToResult();
    }



    public static void exGoToResult() {
        exerciseField.setVisibility(View.GONE);
        exResultBox.setVisibility(View.VISIBLE);
        resultShow = true;
        taskCheckedStatus = 0;


        final View exMarkTxtV = exResultBox.findViewById(R.id.exResultMark);
        final View exResTxt =  exResultBox.findViewById(R.id.exResultTxt);
        final View exRestartBtn = exResultBox.findViewById(R.id.exBtnRestart);


        final View exResultDetail = exResultBox.findViewById(R.id.exResultDetail);
        final boolean showDetail = correctAnswers != wordListLength;

        exMarkTxtV.setAlpha(0.0f);
        exResTxt.setAlpha(0.0f);
        exResultDetail.setAlpha(0.0f);
        exRestartBtn.setAlpha(0.0f);


        double res = ((double) correctAnswers) / wordListLength * 100;

        String markTxt = context.getResources().getString(R.string.ex_result_txt_good);
        int markColor = ContextCompat.getColor(context, R.color.answer_good);

        if (res > 95 ) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_excellent);
            markColor = ContextCompat.getColor(context, R.color.answer_excellent);
        } else if (res > 79 && res < 96) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_verygood);
            markColor = ContextCompat.getColor(context, R.color.answer_verygood);
        } else if (res > 20 && res < 50 ) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_bad);
            markColor = ContextCompat.getColor(context, R.color.answer_satisfactory);
        } else if (res < 21 ) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_verybad);
            markColor = ContextCompat.getColor(context, R.color.answer_bad);
        }



        exMarkTxt.setText(markTxt);
        exMarkTxt.setTextColor(markColor);
        String txt = String.format(context.getResources().getString(R.string.ex_result_txt), correctAnswers, wordListLength, (int)res );
        exResultTxt.setText(txt);

        if (res > 0) {
            saveExResult(topicTag, exType, (int)res );
            correctAnswers = 0;
        }

        exMarkTxtV.animate().alpha(1.0f).setDuration(250).setInterpolator(new DecelerateInterpolator());

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                exResTxt.animate().alpha(1.0f).setDuration(250).setInterpolator(new DecelerateInterpolator());

               if (showDetail) exResultDetail.animate().alpha(.95f).setDuration(250).setInterpolator(new DecelerateInterpolator());

            }
        }, 160);

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                exRestartBtn.animate().alpha(1.0f).setDuration(250).setInterpolator(new DecelerateInterpolator());
            }
        }, 220);


        //Toast.makeText(context, "Saved: "+ completed.size() , Toast.LENGTH_SHORT).show();

    }

    //*/

    private static void saveExResult(String tag, int ex_type, int result) {

        if (!saveStats) return;

        DBHelper dbHelper = new DBHelper(context);

        dbHelper.setTestResult(tag, ex_type, result, forceSave);

        dbHelper.updateCatResult(tag, Constants.CAT_TESTS_NUM); // TODO check test count for cat



    }


    public static void saveCompleted(String tag, int result) {

        DataItem data = new DataItem();
        data.id = tag;
        data.testError = result;

        completed.add(data);

    }


    //*/


    public static void showCheckResult(View view, String message, int background, int textColor) {
        mSnackbar = Snackbar.make(view, message,
                Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarView = mSnackbar.getView();
        snackbarView.setBackgroundColor(background);
        TextView snackTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setTextColor(textColor);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        snackTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        if ( !(context.getResources().getBoolean(R.bool.small_height)) ) mSnackbar.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exercise, menu);
        exShowBtnRadio =  menu.findItem(R.id.exBtnSettings);
        exSaveStatsRadio =  menu.findItem(R.id.test_save);


        exShowBtnRadio.setChecked(exButtonShow);

        MenuItem autoplayMenuItem = menu.findItem(R.id.fAutoplay);
        if (speaking) autoplayMenuItem.setVisible(true);
        else autoplayMenuItem.setVisible(false);

        // if (tablet) manageCardHeightAndButtons();
       // else applyExBtnStatus(exButtonShow, false);

        applySaveStatsStatus(saveStats);

        setSaveStatsForAll();


        return true;

    }


    private void setSaveStatsForAll () {

        if (exType == EX_IMG_TYPE) {
            saveStats = false;
            exSaveStatsRadio.setVisible(false);
            return;
        }

        if (appSettings.getBoolean(Constants.SET_VERSION_TXT, false))  return;

        if ( topicTag.equals(Constants.ALL_CAT_TAG) ) {
            if (saveStats) applySaveStatsStatus(false);
            saveStats = false;
            if (! dataManager.simplified) exSaveStatsRadio.setEnabled(false);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }

    @Override
    public void finish() {


        if (speaking) {
            speakWords("");
        }


        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                openActivity.pageBackTransition();
                return true;
            case R.id.restart_from_menu:

                restartFromMenu();
                return true;
            case R.id.exBtnSettings:
                changeExBtnStatus();
                return true;
            case R.id.test_save:
                changeSaveStatsStatus();
                return true;
            case R.id.fAutoplay:
                autoPlayDialog();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void restartFromMenu() {

        int delay = 300;
        if (originWordsList.size() > 50) delay = 350;

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                restartExercise();
            }
        }, delay);


    }


    private void changeExBtnStatus() {
        if (exButtonShow) {
            exButtonShow = false;
            btnResultBox.setVisibility(View.GONE);
            if (exCheckedStatus)  {
                new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {
                        goToNextTask();
                    }
                }, 1000);
            }
        } else {
            exButtonShow = true;
        }
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("ex_buttons_show", exButtonShow);
        editor.apply();
        applyExBtnStatus(exButtonShow, true);

    }

    private void changeSaveStatsStatus() {
        saveStats = !saveStats;
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("test_all_save", saveStats);
        editor.apply();
        applySaveStatsStatus(saveStats);
    }

    private void applySaveStatsStatus(Boolean status) {
        exSaveStatsRadio.setChecked(status);

        if (!restore) if (!status) notifyNotSaved ();

    }

    public void notifyNotSaved() {

        final String statsMsg = getString(R.string.stats_not_saved_msg);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(fCounterInfoBox, Html.fromHtml("<font color=\"#ffffff\">"+ statsMsg +"</font>"), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }, 250);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (resultShow) viewPager.setCurrentItem(0);

        //---save whatever you need to persist—
        // outState.putParcelable("controller", control );
        outState.putParcelable("controller", exerciseController );

        outState.putParcelableArrayList(Constants.EXTRA_KEY_WORDS, wordList);
        outState.putInt("correct", correctAnswers);

        outState.putParcelableArrayList("completed", completed);

        outState.putBoolean("result_show", resultShow);

        outState.putInt("checked_status", taskCheckedStatus);

        super.onSaveInstanceState(outState);

    }




    //// TTS integration


    static public void speak(String text) {
        speakWords(text);
    }


    private static void speakWords(String speech) {
        //speak straight away

        if (myTTS != null) {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
            initSpeak++;
        }

    }

    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

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
    }

    //setup TTS
    public void onInit(int initStatus) {


        final Locale locale = dataManager.getLocale();

        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(locale)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(locale);
            //  speakBtn.setVisibility(View.VISIBLE);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            //Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }



    private void autoPlay(final int position) {

        if (autoPlay.equals("none")) return;
        if (!speaking) return;


        final String text = dataManager.getPronounce(exerciseController.tasks.get(position).data);

        if (position == 0 ) {

            if (initSpeak == 0 ) {

                new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {

                        if (initSpeak == 0) speakWords(text);

                    }
                }, 500);

            } else {

                speakWords(text);

            }

        } else {

            speakWords(text);
        }


    }


    private void autoPlayDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        autoPlay = appSettings.getString("set_test_autoplay", getString(R.string.set_flash_autoplay_default));

        int checkedItem = 0;

        if (autoPlay.equals("none"))  checkedItem = 1;

        builder.setTitle(getString(R.string.set_flash_autoplay_dialog_title))

                .setSingleChoiceItems(R.array.set_flash_autoplay_list, checkedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        saveAutoplay(which);
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

    private void saveAutoplay(int num) {

        String orderValue = getResources().getStringArray(R.array.set_flash_autoplay_values)[0];
        if (num == 1) orderValue  = getResources().getStringArray(R.array.set_flash_autoplay_values)[1];

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString("set_test_autoplay", orderValue);
        editor.apply();

        autoPlay = orderValue;


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(myTTS != null){
            myTTS.shutdown();
        }
    }



}
