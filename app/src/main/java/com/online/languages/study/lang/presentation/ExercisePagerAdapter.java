package com.online.languages.study.lang.presentation;

import static com.online.languages.study.lang.Constants.EX_AUDIO_TYPE;
import static com.online.languages.study.lang.Constants.EX_IMG_TYPE;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.TASK_DELAY_CORRECT;
import static com.online.languages.study.lang.Constants.TASK_DELAY_INCORRECT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.RoundedCornersTransformation;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.ExerciseTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class ExercisePagerAdapter extends PagerAdapter {

    public static final String OPTION_UNCHECKED = "unchecked";
    public static final String OPTION_CHECKED = "checked";
    private final Context context;
    private final ArrayList<ExerciseTask> tasks;

    private int type = 1;

    private int optionColor;
    private int disableColor;
    private int activeColor;

    private final int correctColor;
    private final int errorColor;

    private DBHelper dbHelper;

    private Boolean lessOptions = false;

    private ExerciseTask exerciseTask;


    private final String exOptTitleLong;
    private String exOptTitleShort;
    private String exOptTitleLongLess;

    private int taskDelay = TASK_DELAY_INCORRECT;


    private final int textLongNum;
    DataManager dataManager;
    private final static int CLICK_SOURCE_OPTION = 1;
    public final static int CLICK_SOURCE_BUTTON = 2;


    boolean shortTextTest = false;

    ExercisePagerAdapter(Context _context, ArrayList<ExerciseTask> _tasks) {
        context = _context;
        tasks = _tasks;
        dbHelper = new DBHelper(context);
        dataManager = new DataManager(context);

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        ThemeAdapter themeAdapter = new ThemeAdapter(context, appSettings.getString(Constants.SET_THEME_TXT, Constants.SET_THEME_DEFAULT), false );
        int styleTheme = themeAdapter.styleTheme;

        TypedArray o = context.getTheme().obtainStyledAttributes(styleTheme, new int[] {R.attr.colorExOptionTxt});
        optionColor = o.getResourceId(0, 0);
        o.recycle();

        TypedArray d = context.getTheme().obtainStyledAttributes(styleTheme, new int[] {R.attr.colorExDisabledTxt});
        disableColor = d.getResourceId(0, 0);
        d.recycle();

        TypedArray a = context.getTheme().obtainStyledAttributes(styleTheme, new int[] {R.attr.colorExActiveTxt});
        activeColor = a.getResourceId(0, 0);
        a.recycle();

        TypedArray c = context.getTheme().obtainStyledAttributes(styleTheme, new int[] {R.attr.colorExOptionTxtCorrect});
        correctColor = a.getResourceId(0, 0);
        a.recycle();

        TypedArray e = context.getTheme().obtainStyledAttributes(styleTheme, new int[] {R.attr.colorExOptionTxtError});
        errorColor = a.getResourceId(0, 0);
        a.recycle();


        exOptTitleShort = context.getResources().getString(R.string.ex_opt_short);  /// used for option with short text
        exOptTitleLong = context.getResources().getString(R.string.ex_opt_long);    /// used for normal option

        exOptTitleLongLess = context.getResources().getString(R.string.ex_opt_long_less);  /// used for long option with fewer number of options

        textLongNum = context.getResources().getInteger(R.integer.ex_text_long_num);

        shortTextTest = checkTestItemsLength(tasks);


    }

    private boolean checkTestItemsLength(ArrayList<ExerciseTask> tasks) {
        boolean shortItems = false;

        if (!context.getResources().getBoolean(R.bool.shorter_option))  return false;

        int shortItemsCount = 0;
        int allItemsCount = 0;

        for (ExerciseTask exerciseTask: tasks) {

            for (String option: exerciseTask.options) {
                allItemsCount ++;
                if (option.length() <= 17) shortItemsCount++;
            }
        }

        if (shortItemsCount > (allItemsCount/10*7)) shortItems = true; // short options if 70% are short

        //Toast.makeText(context, "Short options: " + shortItemsCount + " / " + allItemsCount + " - " + shortItems, Toast.LENGTH_SHORT ).show();

        return shortItems;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.exercise_item, container, false);

        type = ExerciseActivity.exType; // TODO remove

        exerciseTask = new ExerciseTask(tasks.get(position));

        TextView text = itemView.findViewById(R.id.fCardText);
        TextView transcript = itemView.findViewById(R.id.fTranscriptBox);
        final ViewGroup radioGroup = itemView.findViewById(R.id.radioGroup1);

       // radioGroup.removeAllViews();

        if (shortTextTest) setGroupPadding(radioGroup);

        lessOptions = false;

        checkOptionsLength();

        int optionLen = exerciseTask.options.size();
        Random rand = new Random();
        int correctOptionIndex = rand.nextInt(optionLen);
        Collections.rotate(exerciseTask.options.subList(0, correctOptionIndex+1), -1);

        exerciseTask.correct = correctOptionIndex;

        LinearLayout quest = itemView.findViewById(R.id.exQuest);

        if (ExerciseActivity.exButtonShow) {
            changeHeight(quest, ExerciseActivity.exTxtHeight);
        } else {
            changeHeight(quest, ExerciseActivity.exTxtMoreHeight);
        }

        text.setText( exerciseTask.quest);
        setTextStyle(text, exerciseTask.quest);
        transcript.setText( exerciseTask.questInfo );

        if (!ExerciseActivity.exShowTranscript || ExerciseActivity.exType == 2) {
            transcript.setVisibility(View.GONE);
        }

        if (type == EX_IMG_TYPE ) {
            insertImage(exerciseTask, itemView, position);
        }

        if (type == EX_AUDIO_TYPE ) {
             insertAudio(exerciseTask, itemView, position);
        }

        insertOptions(inflater, radioGroup, optionLen);

        setExOptions(radioGroup, exerciseTask);

        setResponsesClick(position, radioGroup);

        itemView.setTag("myview" + position);

        container.addView(itemView);

        return itemView;

    }

    private void checkOptionsLength() {
        int longCount = 0;
        for (String optionTxt: exerciseTask.options) {
            if (optionTxt.length() > textLongNum) longCount++;
        }

        if (longCount > 1) lessOptions = true;

        if (lessOptions) {
            exerciseTask.options = new ArrayList<>(exerciseTask.options.subList(0, Constants.TEST_LONG_OPTIONS_NUM));
        }
    }

    private void insertOptions(LayoutInflater inflater, ViewGroup radioGroup, int optionLen) {
        for (int i = 0; i < optionLen; i++) {
            buildRadio(inflater, radioGroup);
        }
    }

    private void buildRadio(LayoutInflater inflater, ViewGroup radioGroup) {

        // String exOpt = exOptTitleLong;
        // if (lessOptions)    exOpt = exOptTitleLongLess;
        // possibility to inflate view depending on the cases (exOpt):
        // "long" - normal (not short) option, may be used both for words and phrases
        // "long_less" used for decreased number of very long options
        // default - anything else

        View radioItem =  inflater.inflate(R.layout.radion_option_tem, null);

        radioItem.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));

        radioGroup.addView(radioItem);

    }

    private void setGroupPadding(View radioGroup) {
        int paddingDp = 12;
        float density = context.getResources().getDisplayMetrics().density;
       // int paddingPixel = (int)(paddingDp * density);

        int paddingPixel = convertDimen(paddingDp);

        radioGroup.setPadding(paddingPixel,0,paddingPixel,0);  //// left padding for options
    }

    public int convertDimen(int dimen) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, context.getResources().getDisplayMetrics());
    }



    private void setResponsesClick(int position, ViewGroup radioGroup) {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {

            View radio =   radioGroup.getChildAt(i);
                radio.setTag(OPTION_UNCHECKED);
                ((RadioButton) radio.findViewById(R.id.radio_button)).setChecked(false);

                radio.findViewById(R.id.option_selector_active).setVisibility(View.GONE);

            radio.setOnClickListener(v -> {

                setItemChecked(radioGroup, v);


                if (!ExerciseActivity.exCheckedStatus) {

                    int checkedRadioButtonIndex = getCheckedItemIndex(radioGroup);

                    if (checkedRadioButtonIndex != -1) {

                        highlightCheckedItem(radioGroup, checkedRadioButtonIndex);

                        if (!ExerciseActivity.exButtonShow) {

                            ExerciseActivity.exCheckedStatus = true;
                            checkItem(radioGroup, position);

                        }
                    }
                }

            });

        }
    }


    private void setItemChecked(ViewGroup radioGroup, View v) {
        for (int n = 0; n < radioGroup.getChildCount(); n++) {

            View option = radioGroup.getChildAt(n);

            option.setTag(OPTION_UNCHECKED);

            ((RadioButton) option.findViewById(R.id.radio_button)).setChecked(false);
            option.findViewById(R.id.option_selector_active).setVisibility(View.GONE);
        }

        v.setTag(OPTION_CHECKED);
        ((RadioButton) v.findViewById(R.id.radio_button)).setChecked(true);

         v.findViewById(R.id.option_selector_active).setVisibility(View.VISIBLE);

    }

    private int getCheckedItemIndex(ViewGroup radioGroup) {

        int foundViewIndex = -1;

        for (int n = 0; n < radioGroup.getChildCount(); n++) {
           View v=  radioGroup.getChildAt(n);

           if (v.getTag().toString().equals("checked")) {
               foundViewIndex = n;
           }
        }

        return foundViewIndex;

    }

    private void highlightCheckedItem(ViewGroup radioGroup, int checkedIndex) {

        setDefaultRadio(radioGroup);

        View radio = radioGroup.getChildAt(checkedIndex);

        ((TextView) radio.findViewById(R.id.option_text)).setTextColor(ContextCompat.getColor(context, activeColor));
        ((RadioButton) radio.findViewById(R.id.radio_button)).setTextColor(ContextCompat.getColor(context, activeColor));

    }


    private void changeHeight(LinearLayout quest, int height) {
        quest.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());
        quest.setLayoutParams(quest.getLayoutParams());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void setDefaultRadio(ViewGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View radio = radioGroup.getChildAt(i);
            ((TextView) radio.findViewById(R.id.option_text)).setTextColor(ContextCompat.getColor(context, optionColor));
        }
    }


    private void setTextStyle(TextView textView, String text) {

        if (type == 2) {
            textView.setTypeface(null, Typeface.NORMAL);
        } else {
            textView.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        }

        int questLongTextSize = textLongSize(text);
        textView.setTextSize(questLongTextSize);

    }

    private int textLongSize(String text) {
        int textLength = text.length();

        int tSize = context.getResources().getInteger(R.integer.ex_quest_txt_size_norm);
        if ( textLength > 60) tSize = context.getResources().getInteger(R.integer.ex_quest_txt_size_medium);
        if ( textLength > 80 ) tSize = context.getResources().getInteger(R.integer.ex_quest_txt_size_small);
        if ( textLength > 100 ) tSize = context.getResources().getInteger(R.integer.ex_quest_txt_size_smallest);

        return tSize;
    }

    private void setExOptions(ViewGroup radiogroup, ExerciseTask task) {

        int[] correctTag = new int[]{ task.correct };

        radiogroup.setTag(correctTag);

        for (int i = 0; i < radiogroup.getChildCount(); i++) {

            String optionTxt = task.options.get(i).trim();

            View radio = radiogroup.getChildAt(i);

            boolean textBold = type==2 || type == EX_IMG_TYPE;

             setTextStyleAndSize(optionTxt, radio, textBold);

            ((TextView)radio.findViewById(R.id.option_text)).setText(optionTxt);


        }

       // setDefaultRadio(radiogroup);

    }


    private void setTextStyleAndSize(String optionTxt, View radio, boolean textBold) {

        if ( textBold ) {
            ((TextView)radio.findViewById(R.id.option_text)).setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        } else {
            ((TextView)radio.findViewById(R.id.option_text)).setTypeface(null, Typeface.NORMAL);
        }

        ((TextView)radio.findViewById(R.id.option_text)).setTextSize( optionTextSize(optionTxt) );
    }


    private int optionTextSize(String text) {

        int textLength = text.length();

        int tSize = context.getResources().getInteger(R.integer.ex_opt_txt_size_norm);

        if (textLength > context.getResources().getInteger(R.integer.ex_opt_txt_length_medium) ) {
            tSize = context.getResources().getInteger(R.integer.ex_opt_txt_size_medium);
        }

        if (textLength > context.getResources().getInteger(R.integer.ex_opt_txt_length_long) ) {
            tSize = context.getResources().getInteger(R.integer.ex_opt_txt_size_small);
        }

        if ( textLength > context.getResources().getInteger(R.integer.ex_opt_txt_length_longest) ) {
             tSize = context.getResources().getInteger(R.integer.ex_opt_txt_size_smallest);
        }

        return tSize;
    }


    private void checkItem(final ViewGroup _radioGroup, final int _position) {
        new android.os.Handler().postDelayed(() -> checkItemByRadio(_radioGroup, _position), 300);
    }

    private void checkItemByRadio(ViewGroup _radioGroup, int _position) {

       boolean correct = exCheckItem(_radioGroup, _position, CLICK_SOURCE_OPTION);

        if (correct) taskDelay = TASK_DELAY_CORRECT;
        else taskDelay = TASK_DELAY_INCORRECT;

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                ExerciseActivity.goToNextTask();
            }
        }, taskDelay);
    }

    boolean exCheckItem(ViewGroup radioGroup, int position, int source)  {

        boolean correct_answer = false;

        boolean addToCorrect = !ExerciseActivity.exCheckedStatus;
        ExerciseActivity.exCheckedStatus = true;

        Boolean saveStats = ExerciseActivity.saveStats;

        int checkedIndex = getCheckedItemIndex(radioGroup);

        int correctTag = getCorrectTag(radioGroup);

        String savedInfo = tasks.get(position).savedInfo;  ///// must get position

        if (checkedIndex == correctTag) {
            if (ExerciseActivity.exButtonShow) {
                if (addToCorrect) {
                    saveCorrect(saveStats, savedInfo);
                }
            } else {
                saveCorrect(saveStats, savedInfo);
            }

            correct_answer = true;
            showCorrect(radioGroup);

        }else{

            //highlightError(checkedRadioButton);
            showErrorBanner(radioGroup);

            if (ExerciseActivity.exButtonShow) {
                if (addToCorrect) {
                    saveError(saveStats, savedInfo);
                }
            } else {
                saveError(saveStats, savedInfo);
            }
        }

        return correct_answer;
    }


    private int getCorrectTag(ViewGroup radioGroup) {

        int[] data = (int[]) radioGroup.getTag() ;
        int correctTag = data[0];

        highlightCorrect(radioGroup, correctTag);

        return correctTag;
    }

    private void highlightCorrect(ViewGroup radioGroup, int correctTag) {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View radio =  radioGroup.getChildAt(i);

            radio.setEnabled(false);
            radio.setOnClickListener(null);

            //((TextView) radio.findViewById(R.id.option_text)).setTextColor(ContextCompat.getColor(context, disableColor));

            boolean checked = radio.getTag().toString().equals("checked");
            radio.findViewById(R.id.option_selector_active).setAlpha(0.0f);

            if (i == correctTag) {
                ((TextView) radio.findViewById(R.id.option_text)).setTextColor(ContextCompat.getColor(context, correctColor));

                if (checked) {
                    radio.findViewById(R.id.radio_button).animate().alpha(0.0f).setDuration(50);
                    radio.findViewById(R.id.icon_correct_tick).setVisibility(View.VISIBLE);
                    radio.findViewById(R.id.icon_correct_tick).animate().alpha(1.0f).setDuration(150);

                } else {
                    radio.findViewById(R.id.radio_button).setAlpha(0.4f);
                }

                radio.findViewById(R.id.option_selector_correct).setVisibility(View.VISIBLE);


            } else {

                if (!checked) radio.animate().alpha(0.3f).setDuration(100);
                else {
                    ((TextView) radio.findViewById(R.id.option_text)).setTextColor(ContextCompat.getColor(context, errorColor));

                    radio.findViewById(R.id.radio_button).animate().alpha(0.0f).setDuration(50);
                    radio.findViewById(R.id.icon_error).setVisibility(View.VISIBLE);
                    radio.findViewById(R.id.icon_error).animate().alpha(1.0f).setDuration(150);
                    radio.findViewById(R.id.option_selector_error).setVisibility(View.VISIBLE);

                }
            }
        }
    }

    private void saveError(Boolean saveStats, String savedInfo) {
        if (saveStats && !savedInfo.equals("")) {

                dbHelper.setError(savedInfo);
        }
        saveCompleted(savedInfo, 1);
    }

    private void saveCorrect(Boolean saveStats, String savedInfo) {
        ExerciseActivity.correctAnswers++;
        if (saveStats && !savedInfo.equals(PARAM_EMPTY)) {
            if (savedInfo.contains("pr_")) {

                dbHelper.setPracticeTask(savedInfo, type, "");

            } else {

                dbHelper.setWordResult(savedInfo);
            }
        }
        saveCompleted(savedInfo, 0);
    }


    private void insertImage (ExerciseTask task, View itemView, int position) {

        ImageView image = itemView.findViewById(R.id.exImage);

        ExerciseActivity.fCounterInfoBox.setVisibility(View.GONE);

        TextView exImgCounter = itemView.findViewById(R.id.exImgCounter);

        View textWrapper = itemView.findViewById(R.id.exTextWrapper);
        View imageWrapper = itemView.findViewById(R.id.exImageWrapper);

        textWrapper.setVisibility(View.GONE);
        imageWrapper.setVisibility(View.VISIBLE);

        String counter = (position+1) + "/" +tasks.size();

        exImgCounter.setText(counter);

        Picasso.with(context )
                .load("file:///android_asset/pics/"+ task.quest )
                .transform(new RoundedCornersTransformation(20,0))
                .fit()
                .centerCrop()
                .into(image);

    }


    private void insertAudio (ExerciseTask task, View itemView, int position) {

        View textWrapper = itemView.findViewById(R.id.exTextWrapper);

        View audioWrapper = itemView.findViewById(R.id.exAudioWrapper);

        textWrapper.setVisibility(View.GONE);

        audioWrapper.setVisibility(View.VISIBLE);

        final String speakTxt = dataManager.getPronounce(exerciseTask.data);

        audioWrapper.setOnClickListener(v -> ExerciseActivity.speak(speakTxt)

        );



    }

    private void saveCompleted(String id, int result) {
        ExerciseActivity.saveCompleted(id, result);
    }

    private void showCorrect (View view) {
        String message = context.getResources().getString(R.string.ex_txt_correct);
        int background = ContextCompat.getColor(context, R.color.green_snack);
        int textColor = ContextCompat.getColor(context, R.color.green_snack_txt);
        ExerciseActivity.showCheckResult (view, message, background, textColor);
    }

    private void showErrorBanner(View view) {
        String message = context.getResources().getString(R.string.ex_txt_incorrect);
        int background = ContextCompat.getColor(context, R.color.red_snack);
        int textColor = ContextCompat.getColor(context, R.color.red_snack_txt);
        ExerciseActivity.showCheckResult(view, message, background, textColor);
    }


}
