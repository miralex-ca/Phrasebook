package com.online.languages.study.lang.constructor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.flexbox.FlexboxLayout;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.ExerciseTask;


import java.util.ArrayList;
import java.util.Collections;

import static com.online.languages.study.lang.Constants.PARAM_EMPTY;

class ExerciseBuildPagerAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<ExerciseTask> tasks;

    private int type = 1;

    private final DBHelper dbHelper;

    private ExerciseTask exerciseTask;

    private final int RESPONSE_CORRECT = 1;
    private final int RESPONSE_CORRECT_ALT = 2;
    private final int RESPONSE_ERROR = 0;
    DataManager dataManager;
    boolean playResult;
    boolean playSelect;

    private final int BUILD_TYPE_INSERT = 1;

    TextView textView;

    ExerciseBuildPagerAdapter(Context _context, ArrayList<ExerciseTask> _tasks) {
        context = _context;
        tasks = _tasks;

        dataManager = new DataManager(context);
        dbHelper = dataManager.dbHelper;


        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        playResult = appSettings.getBoolean("play_result", true);
        playSelect = appSettings.getBoolean("play_select", false);


    }

    @Override
    public int getCount() {
        return tasks.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        View itemView = inflater.inflate(R.layout.exercise_build_item, container, false);

        type = ExerciseBuildActivity.exType;

        exerciseTask = new ExerciseTask(tasks.get(position));


        LinearLayout quest = itemView.findViewById(R.id.exQuest);
        //changeHeight(quest, ExerciseBuildActivity.exTxtHeight);

        textView = itemView.findViewById(R.id.fCardText);
        textView.setText( exerciseTask.quest);

        setExOptions(exerciseTask, itemView);


        itemView.setTag("myview" + position);

        container.addView(itemView);

       // if (isInsertType(exerciseTask)) prepopulate(itemView);

        return itemView;

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


    private void setExOptions(ExerciseTask task, View root) {

        ArrayList<String> options = getOptions(task.option);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        ViewGroup flexOptions = root.findViewById(R.id.flex_options);

        for (int i = 0; i < options.size(); ++i) {

            View btn = inflater.inflate(R.layout.tc_tv_template, null);
            TextView txtBtn = btn.findViewById(R.id.txtOrig);

            String text = options.get(i);

            text = text.replaceAll("_", " ");

            txtBtn.setText(text);

            btn.setTag("t"+i);

            btn.setOnClickListener(v -> {
                click(inflater, v, root, task);
            });

            flexOptions.addView(btn);

        }

    }


    private void prepopulate(View root) {

        String preText = "";

        ViewGroup flexTarget = root.findViewById(R.id.flex_target);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        ArrayList<String> preOptions = getOptions(preText);

        for (int i = 0; i < preOptions.size(); ++i) {
            RelativeLayout tvBox = (RelativeLayout) inflater.inflate(R.layout.tc_tv_target_pre, null);
            TextView txtBtn = (TextView) tvBox.findViewById(R.id.txtPre);
            txtBtn.setText(preOptions.get(i));
            flexTarget.addView(tvBox);
        }

        RelativeLayout flagBox = (RelativeLayout) inflater.inflate(R.layout.tc_tvt_flag, null);
        flexTarget.addView(flagBox, 3);

    }

    public void click(LayoutInflater inflater, View view, View root, ExerciseTask task) {

        // Toast.makeText(this, "Click ", Toast.LENGTH_SHORT).show();

        if  (view.getAlpha() == 1.0f) {

            TextView t = view.findViewById(R.id.txtOrig);

            RelativeLayout tvBox = (RelativeLayout) inflater.inflate(R.layout.tc_tv_target, null);

            ///////  text
            TextView tv = tvBox.findViewById(R.id.txtT);
            tv.setText( t.getText().toString());

            if (playSelect) ExerciseBuildActivity.speak(t.getText().toString());

            ////////////

            tvBox.setAlpha(0.0f);

            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(6);

            tvBox.setOnClickListener(view1 -> clickTarget(view1, root));

            ViewGroup flexTarget = root.findViewById(R.id.flex_target);

            if (isInsertType(task)) {

                RelativeLayout flag = (RelativeLayout) flexTarget.findViewWithTag("flag");
                int ind = flexTarget.indexOfChild(flag);


                flexTarget.addView(tvBox, ind);
            } else {

                flexTarget.addView(tvBox);
            }


            capitalizeString(flexTarget);


            tvBox.animate().alpha(1.0f);

            //Toast.makeText(this, "Tag: "+t.getTag(), Toast.LENGTH_SHORT).show();

            tvBox.setTag(view.getTag());

            view.animate()
                    .setDuration(200)
                    .alpha(0.0f);
        }

    }


    public void clickTarget(View view, View root) {

        if  (view.getAlpha() == 1.0f) {

            View t = view;

           View flexOptions = root.findViewById(R.id.flex_options);

            View tvr = flexOptions.findViewWithTag(t.getTag());

            final View tf = t;
            Runnable endAction = () -> targetEnd(tf, root);


            tvr.animate().alpha(1.0f);

            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(6);

            t.animate()
                    .alpha(0.0f)
                    .setDuration(150)
                    .withEndAction(endAction);

        }
    }

    private void capitalizeString(View root) {

        View v =  ((ViewGroup)root).getChildAt(1);

        if (v != null ) {
            TextView t = v.findViewById(R.id.txtT);

            if (t!=null) {

                String str = t.getText().toString();
                String cap = str.substring(0, 1).toUpperCase() + str.substring(1);

                t.setText(cap);

            }

            //Toast.makeText(this, "Text: "+ cap, Toast.LENGTH_SHORT).show();
        }


    }


    private ArrayList<String> getOptions(String string) {
        ArrayList<String> options = new ArrayList<>();
        String str = string.trim();
        str = str.replaceAll("[.,;?!¡¿]", "");

        String[] separated = str.split(" ");
        Collections.addAll(options, separated);
        Collections.shuffle(options);

        return options;
    }

    private void targetEnd(final View view, View root) {

        ViewGroup flexTarget = root.findViewById(R.id.flex_target);

        view.getLayoutParams().height = 0;
        view.setLayoutParams(view.getLayoutParams());
        ConstructorFragment.ResizeAnimation resizeAnimation = new ConstructorFragment.ResizeAnimation(view, 0);
        resizeAnimation.setDuration(250);
        view.startAnimation(resizeAnimation);
        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                //Functionality here
                ((ViewGroup) view.getParent()).removeView(view);
                capitalizeString( flexTarget);
            }
            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationStart(Animation arg0) {}
        });
    }


    void exCheckItem(View root, int position)  {

        boolean correct_answer = false;

        boolean addToCorrect = !ExerciseBuildActivity.exCheckedStatus;
        ExerciseBuildActivity.exCheckedStatus = true;

        Boolean saveStats = ExerciseBuildActivity.saveStats;

        StringBuilder response = new StringBuilder();

        getResponse(root, response);

        disableOptions(root);

        boolean correctResponse = checkResponse(String.valueOf(response.toString()), tasks.get(position), root);

        String savedInfo = tasks.get(position).savedInfo;

        if (correctResponse) {

                if (addToCorrect) {
                    saveCorrect(saveStats, savedInfo);
                }

            correct_answer = true;
            showCorrect(textView);

        } else {

            showWrong(textView);

                if (addToCorrect) {
                    saveError(saveStats, savedInfo);
                }

        }

    }

    private void getResponse(View root, StringBuilder response) {
        FlexboxLayout target = root.findViewById(R.id.flex_target);

        final int childCount = target.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View v = target.getChildAt(i);

            TextView textView = v.findViewById(R.id.txtT);

           if (textView != null)  response.append(" ").append(textView.getText().toString());

           v.setOnClickListener(null);

        }
    }

    private void disableOptions(View root) {
        FlexboxLayout optionsBox = root.findViewById(R.id.flex_options);

        final int optionsBoxChildCount = optionsBox.getChildCount();
        for (int i = 0; i < optionsBoxChildCount; i++) {

            View v = optionsBox.getChildAt(i);
            v.setOnClickListener(null);

        }
    }


    private boolean checkResponse(String responseString, ExerciseTask exerciseTask, View root) {

        String response = checkString(responseString);

        int responseChecking = checkResponse(exerciseTask, response);

        displayCorrectInfo(root, exerciseTask, responseChecking);

        return responseChecking == RESPONSE_CORRECT || responseChecking == RESPONSE_CORRECT_ALT;

    }

    private int checkResponse(ExerciseTask exerciseTask, String response) {

        int checkResult = RESPONSE_ERROR;

        if (exerciseTask.answers.size() > 1) {

            for (String answer: exerciseTask.answers) {

                String correctString = checkString(answer);

                if (response.equals(correctString)) checkResult = RESPONSE_CORRECT_ALT;
            }
        }

        String correctString = checkString(exerciseTask.response);
        if (response.equals(correctString)) checkResult = RESPONSE_CORRECT;

        return checkResult;
    }

    private void displayCorrectInfo(View root, ExerciseTask task, int responseChecking) {

        View msg = root.findViewById(R.id.correct_msg);

        View errorHeader = msg.findViewById(R.id.header_error);
        View correctHeader = msg.findViewById(R.id.header_correct);

        if (responseChecking == RESPONSE_CORRECT) {
            correctHeader.setVisibility(View.VISIBLE);
        } else if (responseChecking == RESPONSE_CORRECT_ALT) {
            correctHeader.setVisibility(View.VISIBLE);
            ((TextView)correctHeader.findViewById(R.id.tv_msg_title_correct)).setText("Ответ принят");
        }
        else errorHeader.setVisibility(View.VISIBLE);

        TextView text = root.findViewById(R.id.tv_msg_text);
        text.setText(task.response);

        if (msg.getVisibility() == View.GONE) {

            msg.setAlpha(0.0f);
            msg.setVisibility(View.VISIBLE);
            msg.animate().alpha(1.0f).setDuration(100);

        }

        setResponseSpeaker(task, msg);

    }

    private void setResponseSpeaker(ExerciseTask task, View msg) {
        View speaker = msg.findViewById(R.id.speakerIcon);

        if (ExerciseBuildActivity.speaking) speaker.setVisibility(View.VISIBLE);

        speaker.setOnClickListener(view -> {
            ExerciseBuildActivity.speak(task.response);
        });

        if (playResult) {
            new android.os.Handler().postDelayed(() -> ExerciseBuildActivity.speak(task.response), 200);
        }

    }


    private String checkString(String string) {

        string = string.trim();
        string = string.replaceAll("[.,;?!¡¿]", "").toUpperCase();

        return string;
    }



    /////////   Basic functions

    private void saveError(Boolean saveStats, String savedInfo) {
        if (saveStats && !savedInfo.equals("")) {

                dbHelper.setError(savedInfo);

        }
        saveCompleted(savedInfo, 1);
    }

    private void saveCorrect(Boolean saveStats, String savedInfo) {
        ExerciseBuildActivity.correctAnswers++;
        if (saveStats && !savedInfo.equals(PARAM_EMPTY)) {
            if (savedInfo.contains("pr_")) {

                dbHelper.setPracticeTask(savedInfo, type, "");

            } else {

                dbHelper.setWordResult(savedInfo);
            }


        }
        saveCompleted(savedInfo, 0);
    }


    private void saveCompleted(String id, int result) {
        ExerciseBuildActivity.saveCompleted(id, result);
    }

    private void showCorrect (View view) {
        String message = context.getResources().getString(R.string.ex_txt_correct);
        int background = ContextCompat.getColor(context, R.color.green_snack);
        int textColor = ContextCompat.getColor(context, R.color.green_snack_txt);
        ExerciseBuildActivity.showCheckResult (view, message, background, textColor);
    }

    private void showWrong (View view) {
        String message = context.getResources().getString(R.string.ex_txt_incorrect);
        int background = ContextCompat.getColor(context, R.color.red_snack);
        int textColor = ContextCompat.getColor(context, R.color.red_snack_txt);
        ExerciseBuildActivity.showCheckResult(view, message, background, textColor);
    }

    private boolean isInsertType(ExerciseTask task) {

        boolean isInsert = false;

        if (task.params.contains("insert")) isInsert = true;

        return isInsert;
    }

    public void setPlayResult(boolean checked) {
        playResult = checked;
        
    }

    public void setPlaySelect(boolean checked) {
        playSelect = checked;

    }





}


