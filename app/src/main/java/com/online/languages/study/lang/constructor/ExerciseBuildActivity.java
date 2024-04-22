package com.online.languages.study.lang.constructor;

import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.EX_IMG_TYPE;
import static com.online.languages.study.lang.Constants.PRACTICE_LIMIT_DEFAULT;
import static com.online.languages.study.lang.Constants.PRACTICE_LIMIT_SETTING;
import static com.online.languages.study.lang.Constants.PRACTICE_MIXED_PARAM;
import static com.online.languages.study.lang.Constants.PRACTICE_MIX_SETTING;
import static com.online.languages.study.lang.Constants.TASK_REVISE_TEST_LIMIT;
import static com.online.languages.study.lang.practice.QuestCollector.TEST_BUILD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.CustomViewPager;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.ExerciseController;
import com.online.languages.study.lang.data.ExerciseDataCollect;
import com.online.languages.study.lang.data.ExerciseTask;
import com.online.languages.study.lang.practice.QuestCollector;
import com.online.languages.study.lang.presentation.exercise.ExerciseResultActivity;
import com.online.languages.study.lang.presentation.core.ThemedActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ExerciseBuildActivity extends ThemedActivity implements TextToSpeech.OnInitListener {


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

    public static Boolean exCheckedStatus;
    public static int exType = 1;
    public static int exTxtHeight = 120;
    public static int exTxtMoreHeight = 160;
    public int exCardHeight = 330;
    public int exCardMoreHeight = 360;


    private MenuItem exSaveStatsRadio;
    private static Snackbar mSnackbar;

    static CustomViewPager viewPager;
    ExerciseBuildPagerAdapter viewPagerAdapter;

    public static Boolean fShowTranscript;


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
    static boolean speaking = false;
    static int initSpeak = 0;
    String autoPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dataManager = new DataManager(this);


        setContentView(R.layout.activity_exercise_build);

        openActivity = new OpenActivity(this);

        openActivity.setOrientation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.title_test_txt);


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
            String save = appSettings.getString("starred_save_type", "0");

            if (save.equals("1")) forceSave = false;

        } else {
            forceSave = true;
        }


        exType = getIntent().getIntExtra("ex_type", 1); /* 1 - voc, 2 - phrase*/
        // exType = 2;


        fShowTranscript = true;
        context = this;

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);

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

            taskCheckedStatus = savedInstanceState.getInt("checked_status");

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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                exerciseAllData = new ExerciseDataCollect(context, originWordsList, exType);
                startExercise();
            }
        }, delay);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showPage(position);

                // Toast.makeText(getApplicationContext(), "Checked: "+ taskCheckedStatus, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        speaking = appSettings.getBoolean("set_speak", true);

        if (speaking) {
            checkTTSIntent();
        }


    }


    private void checkTTSIntent() {

        if (!speaking) return;

        PackageManager pm = getPackageManager();
        final Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        ResolveInfo resolveInfo = pm.resolveActivity(checkTTSIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo == null) {
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


    private void restaureChecked(int type) {

        if (type == 1) {
            disableButton(checkButton); //checkButton.setEnabled(false);
            exCheckedStatus = true;
        } else if (type == 2) {
            btnResultBox.setVisibility(View.VISIBLE);
            btnGroupBox.setVisibility(View.GONE);

            exCheckedStatus = true;

        }

    }


    public void openResult(View view) {

        Intent intent = new Intent(ExerciseBuildActivity.this, ExerciseResultActivity.class);

        if (getIntent().hasExtra("practice")) intent.putExtra("multichoice", true);


        ArrayList<DataItem> results = new ArrayList<>();

        for (ExerciseTask task : exerciseController.tasks) {

            DataItem item = new DataItem();
            item.id = task.savedInfo;
            item.item = task.quest;
            item.info = task.response;
            item.testError = -1;


            for (DataItem dataItem : completed) {
                if (task.savedInfo.equals(dataItem.id)) {
                    item.testError = dataItem.testError;
                }
            }

            results.add(item);

        }

        intent.putParcelableArrayListExtra("dataItems", results);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.fade_in_2, 0);
    }


    private void getTasks() {
        int limit = Constants.QUEST_NUM;


        if (topicTag.equals(Constants.ALL_CAT_TAG)) {
            String lim = appSettings.getString("test_all_limit", getString(R.string.set_test_all_limit_default));
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


        if (topicTag.contains("revise")) {

            limit = TASK_REVISE_TEST_LIMIT;

            if (getIntent().hasExtra("ids")) {
                data = dataManager.getCatsItems(getIntent().getStringArrayExtra("ids"));
                exerciseAllData = new ExerciseDataCollect(context, data, exType);
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

        if (getIntent().hasExtra("multichoice")) {

            exerciseAllData.getMultiChoiceTaskList(topicTag); // also check TestResult getDataDirect()

            if (exerciseAllData.tasks.size() > limit) {

                Collections.shuffle(exerciseAllData.tasks);

                exerciseAllData.tasks = new ArrayList<>(exerciseAllData.tasks.subList(0, limit));
            }
        }

        exerciseAllData.shuffleTasks();


        if (getIntent().hasExtra("practice")) {

            limit = Integer.parseInt(appSettings.getString(PRACTICE_LIMIT_SETTING, String.valueOf(PRACTICE_LIMIT_DEFAULT)));

            if (getIntent().hasExtra("ids")) {

                String[] studiedList = getIntent().getStringArrayExtra("ids");
                String[] unstudiedList = getIntent().getStringArrayExtra("unstudied_ids");

                int testLevel = 0;

                if (getIntent().hasExtra("level"))
                    testLevel=getIntent().getIntExtra("level", 0);

                String sectionId = getIntent().getStringExtra(EXTRA_SECTION_ID);

                String mixWithUnstudied = appSettings.getString(PRACTICE_MIX_SETTING+sectionId, PRACTICE_MIXED_PARAM);

                QuestCollector questCollector = new QuestCollector(dataManager.dbHelper, exType, testLevel);

                questCollector.setMixWithUnstudied(mixWithUnstudied.equals(PRACTICE_MIXED_PARAM));

                questCollector.setType(TEST_BUILD);

                questCollector.setTaskLimit(limit);
                questCollector.setStudiedIds(studiedList);
                questCollector.setUnStudiedIds(unstudiedList);

                questCollector.processData();

                exerciseAllData.tasks = questCollector.getMainList();

            }
        }

        exerciseController.tasks = exerciseAllData.tasks;

    }


    private void startExercise() {
        // dataManager.getTime("Start start");

        resultShow = false;

        if (!restore) {

            getTasks();

            correctAnswers = 0;
            completed = new ArrayList<>();
        }

        wordListLength = exerciseController.tasks.size();

        showPage(0);

        viewPagerAdapter = new ExerciseBuildPagerAdapter(this, exerciseController.tasks);
        viewPager.setAdapter(viewPagerAdapter);

        // dataManager.getTime("End start", true);
    }


    public void restartExercise() {
        dataManager.getTime("Start restart");
        goToNextTask();

        btnResultBox.setVisibility(View.GONE);
        btnGroupBox.setVisibility(View.VISIBLE);

        exResultBox.setVisibility(View.GONE);
        exerciseField.setVisibility(View.VISIBLE);


        taskCheckedStatus = 0;

        getTasks();

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

    private void manageCardHeight() {
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

    }


    public void changeHeight(int height) {

        int h = convertDimen(height);

        View tView = viewPager.findViewWithTag("myview" + viewPager.getCurrentItem());
        LinearLayout exQuest = tView.findViewById(R.id.exQuest);

        exQuest.getLayoutParams().height = h;
        exQuest.setLayoutParams(exQuest.getLayoutParams());

        View nextView = viewPager.findViewWithTag("myview" + (viewPager.getCurrentItem() + 1));
        if (nextView != null) {
            View nexQuest = nextView.findViewById(R.id.exQuest);
            nexQuest.getLayoutParams().height = h;
            nexQuest.setLayoutParams(nexQuest.getLayoutParams());
        }
    }


    public void clickRestart(View view) {
        restartExercise();
    }


    private void showPage(int position) {

        String counterTxt = String.format(getResources().getString(R.string.f_counter_txt), position + 1, wordListLength);
        fCounterInfoBox.setText(counterTxt);


        if (!nextButton.isEnabled()) {
            enableButton(nextButton); //nextButton.setEnabled(true);
        }


        if (position >= (wordListLength - 1)) {

            disableButton(nextButton); // nextButton.setEnabled(false);

        }


    }

    public void clickToNext(View view) {
        goToNextTask();

    }

    public static void goToNextTask() {

        // Toast.makeText(context, "Correct: "+correctAnswers, Toast.LENGTH_SHORT).show();

        exCheckedStatus = false;

        taskCheckedStatus = 0;

        if (!checkButton.isEnabled()) {
            enableButton(checkButton); //checkButton.setEnabled(true);
        }

        if (viewPager.getCurrentItem() >= (wordListLength - 1)) {
            exGoToResult();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
        if (mSnackbar != null && mSnackbar.isShown()) mSnackbar.dismiss();
    }

    public void exCheck(View view) {
        int position = viewPager.getCurrentItem();

        View tView = viewPager.findViewWithTag("myview" + position);

        viewPagerAdapter.exCheckItem(tView, position);

        taskCheckedStatus = 1;

        if (position >= (wordListLength - 1)) {

            btnResultBox.setVisibility(View.VISIBLE);
            btnGroupBox.setVisibility(View.GONE);
            taskCheckedStatus = 2;


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
        final View exResTxt = exResultBox.findViewById(R.id.exResultTxt);
        final View exRestartBtn = exResultBox.findViewById(R.id.exBtnRestart);


        final View exResultDetail = exResultBox.findViewById(R.id.exResultDetail);
        final boolean showDetail = correctAnswers != wordListLength;

        exMarkTxtV.setAlpha(0.0f);
        exResTxt.setAlpha(0.0f);
        exResultDetail.setAlpha(0.0f);
        exRestartBtn.setAlpha(0.0f);

        ViewGroup icons = exResultBox.findViewById(R.id.result_icons);

        int iconsCount = icons.getChildCount();

        for (int i = 0; i < iconsCount; i++) {
            icons.getChildAt(i).setVisibility(View.GONE);
        }


        double res = ((double) correctAnswers) / wordListLength * 100;

        String markTxt = context.getResources().getString(R.string.ex_result_txt_good);
        int markColor = ContextCompat.getColor(context, R.color.answer_good);

        if (res > 95) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_excellent);
            markColor = ContextCompat.getColor(context, R.color.answer_excellent);
            icons.findViewById(R.id.box_great).setVisibility(View.VISIBLE);
        } else if (res > 79 && res < 96) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_verygood);
            markColor = ContextCompat.getColor(context, R.color.answer_verygood);
            icons.findViewById(R.id.box_very_good).setVisibility(View.VISIBLE);
        } else if (res > 20 && res < 50) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_bad);
            markColor = ContextCompat.getColor(context, R.color.answer_satisfactory);
            icons.findViewById(R.id.box_bad).setVisibility(View.VISIBLE);
        } else if (res < 21) {
            markTxt = context.getResources().getString(R.string.ex_result_txt_verybad);
            markColor = ContextCompat.getColor(context, R.color.answer_bad);
            icons.findViewById(R.id.box_very_bad).setVisibility(View.VISIBLE);
        } else {

            icons.findViewById(R.id.box_good).setVisibility(View.VISIBLE);
        }


        exMarkTxt.setText(markTxt);
        exMarkTxt.setTextColor(markColor);
        String txt = String.format(context.getResources().getString(R.string.ex_result_txt), correctAnswers, wordListLength, (int) res);
        exResultTxt.setText(txt);

        if (res > 0) {
            saveExResult(topicTag, exType, (int) res);
            correctAnswers = 0;
        }

        exMarkTxtV.animate().alpha(1.0f).setDuration(250).setInterpolator(new DecelerateInterpolator());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                exResTxt.animate().alpha(1.0f).setDuration(250).setInterpolator(new DecelerateInterpolator());

                if (showDetail)
                    exResultDetail.animate().alpha(.95f).setDuration(250).setInterpolator(new DecelerateInterpolator());

            }
        }, 160);

        new Handler().postDelayed(new Runnable() {
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

        if (!(context.getResources().getBoolean(R.bool.small_height))) mSnackbar.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exercise, menu);

        exSaveStatsRadio = menu.findItem(R.id.test_save);


        MenuItem autoplayMenuItem = menu.findItem(R.id.fAutoplay);
        if (speaking) autoplayMenuItem.setVisible(true);
        else autoplayMenuItem.setVisible(false);

        // if (tablet) manageCardHeightAndButtons();
        // else applyExBtnStatus(exButtonShow, false);

        applySaveStatsStatus(saveStats);

        setSaveStatsForAll();


        return true;

    }


    private void setSaveStatsForAll() {

        if (exType == EX_IMG_TYPE) {
            saveStats = false;
            exSaveStatsRadio.setVisible(false);
            return;
        }

        if (appSettings.getBoolean(Constants.SET_VERSION_TXT, false)) return;

        if (topicTag.equals(Constants.ALL_CAT_TAG)) {
            if (saveStats) applySaveStatsStatus(false);
            saveStats = false;
            if (!dataManager.simplified) {

                exSaveStatsRadio.setEnabled(false);
            }
        }

    }

    private void disableButton(Button btn) {
        btn.setEnabled(false);
        btn.setAlpha(0.6f);
    }

    private static void enableButton(Button btn) {
        btn.setEnabled(true);
        btn.setAlpha(1.0f);
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
        switch (id) {
            case android.R.id.home:
                finish();
                openActivity.pageBackTransition();
                return true;
            case R.id.restart_from_menu:

                restartFromMenu();
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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                restartExercise();
            }
        }, delay);


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

        if (!restore) if (!status) notifyNotSaved();

    }

    public void notifyNotSaved() {

        final String statsMsg = getString(R.string.stats_not_saved_msg);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(fCounterInfoBox, Html.fromHtml("<font color=\"#ffffff\">" + statsMsg + "</font>"), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }, 250);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (resultShow) viewPager.setCurrentItem(0);

        //---save whatever you need to persist—
        // outState.putParcelable("controller", control );
        outState.putParcelable("controller", exerciseController);

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
            if (myTTS.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(locale);
            //  speakBtn.setVisibility(View.VISIBLE);
        } else if (initStatus == TextToSpeech.ERROR) {
            //Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    private void autoPlay(final int position) {

        if (autoPlay.equals("none")) return;
        if (!speaking) return;


        final String text = dataManager.getPronounce(exerciseController.tasks.get(position).data);

        if (position == 0) {

            if (initSpeak == 0) {

                new Handler().postDelayed(new Runnable() {
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


        boolean playResult = appSettings.getBoolean("play_result", true);
        boolean playSelect = appSettings.getBoolean("play_select", false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_audio_settings, null);


        CheckBox checkBoxAudioResult = content.findViewById(R.id.checkbox_result);
        checkBoxAudioResult.setChecked(playResult);

        checkBoxAudioResult.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeAudioResultStatus(isChecked);
        });

        CheckBox checkBoxAudioSelect = content.findViewById(R.id.checkbox_select);
        checkBoxAudioSelect.setChecked(playSelect);

        checkBoxAudioSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeAudiSelectStatus(isChecked);
        });

        builder.setView(content);

        builder.setTitle(R.string.set_flash_autoplay_dialog_title);

        builder.setCancelable(true);

        builder.setNegativeButton(R.string.dialog_close_txt,
                (dialog, id) -> dialog.cancel());

        builder.show();




    }

    private void changeAudioResultStatus(boolean checked) {

        viewPagerAdapter.setPlayResult(checked);
        saveAutoplay("play_result", checked);
    }

    private void changeAudiSelectStatus(boolean checked) {

        viewPagerAdapter.setPlaySelect(checked);
        saveAutoplay("play_select", checked);
    }

    private void saveAutoplay(String settingName, boolean checked) {

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(settingName, checked);
        editor.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myTTS != null) {
            myTTS.shutdown();
        }
    }


}
