package com.online.languages.study.lang;


import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.ColorProgress;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.TestResultAdapter;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.TestResult;
import com.online.languages.study.lang.presentation.BaseActivity;

import java.util.ArrayList;

import static com.online.languages.study.lang.adapters.ViewAnimatorSlideUpDown.slideUp;

public class ExerciseResultActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    DBHelper dbHelper;
    ArrayList<DataItem> originList;
    TestResult testResult;

    RecyclerView recyclerView;
    TestResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, 2);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_result);

        originList = getIntent().getParcelableArrayListExtra("dataItems");
        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.test_result_title);

        int resType  = 0;
        if (getIntent().hasExtra("multichoice")) resType  = 1;

        testResult = new TestResult(this, originList, resType);

        ArrayList<TestResult.ResultCategory> cats;

        if (testResult.errorSections.size() > 2 ) {
            cats = testResult.errorSections;
        } else {
            cats = testResult.errorCategories;
        }

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new TestResultAdapter(this, cats);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        TextView taskCount = findViewById(R.id.tasksTotalCount);
        TextView errorCount = findViewById(R.id.errorsTotalCount);
        TextView skippedCount = findViewById(R.id.skippedTotalCount);
        TextView testProgress = findViewById(R.id.testProgress);

        taskCount.setText(String.format(getString(R.string.total_tasks), originList.size()));
        errorCount.setText(String.format(getString(R.string.test_errors), testResult.testErrors.size()));
        skippedCount.setText(String.format(getString(R.string.tasks_missed), testResult.unanswered.size()));

        int correct =  originList.size() - testResult.testErrors.size() - testResult.unanswered.size();

        double res = ((double) correct ) / originList.size() * 100;

        if (res < 0) res = 0;
        if (res > 100) res = 100;

        testProgress.setText((int)res +"%");
        ColorProgress colorProgress = new ColorProgress(this);
        testProgress.setTextColor(  colorProgress.getColorFromAttr( (int)res)  );

    }



    public void openView(View view) {

        View r = (View) ((ViewGroup) view.getParent()).getParent();

        final View v = r.findViewById(R.id.sectionContent);
        final ImageView icon = r.findViewById(R.id.expIcon);
        final TextView title = r.findViewById(R.id.sectionTitle);

        String status = (String) v.getTag(R.integer.view_tag_1);

        if (status.equals("closed")) {
            //slideDown(v);

            v.setTag(R.integer.view_tag_1, "open");

            int h = (int) v.getTag(R.integer.view_tag_2);

            icon.setImageResource(R.drawable.ic_chevron_up);

            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = 0;
            v.setLayoutParams(layoutParams);

            v.setVisibility(View.VISIBLE);

            ResizeHeight resizeHeight = new ResizeHeight(v, h, 0);
            resizeHeight.setDuration(170);
            v.startAnimation(resizeHeight);

            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    v.animate().alpha(1.0f);
                }
            }, 200);
        }

        if (status.equals("open")) {

            v.animate().alpha(0);

            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {

                    icon.setImageResource(R.drawable.ic_chevron_down);
                    slideUp(v);
                    v.setTag(R.integer.view_tag_1,"closed");
                }
            }, 200);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(0, R.anim.fade_out_2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out_2);
    }



}
