package com.online.languages.study.lang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.CustomSectionAdapter;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;

public class SectionStatsListActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    private CustomSectionAdapter mAdapter;

    RecyclerView recyclerView;

    int dataType = 0;

    int mainColor = -1;

    TextView dataCountTxt, dataTitle, dataDesc;

    NavStructure navStructure;
    String tSectionID = "01010";
    Section section;
    NavSection navSection;

    DBHelper dbHelper;

    Boolean full_version;

    Boolean easy_mode;
    MenuItem modeMenuItem;

    DataModeDialog dataModeDialog;
    DataManager dataManager;

    OpenActivity openActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        dataManager = new DataManager(this);
        easy_mode = dataManager.easyMode();
        dataModeDialog = new DataModeDialog(this);

        setContentView(R.layout.activity_cat_data_stats);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        section = new Section(navStructure.getNavSectionByID(tSectionID), this);
        navSection = navStructure.getNavSectionByID(tSectionID);

        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_custom_txt);

        dataCountTxt = findViewById(R.id.sectionCustomDataCount);
        dataTitle = findViewById(R.id.customListTile);
        dataDesc = findViewById(R.id.customListDesc);
        dataType = getIntent().getIntExtra(Constants.EXTRA_DATA_TYPE, 0);

        recyclerView = findViewById(R.id.recycler_view);

        section = dbHelper.getSectionCatItemsStats(section);
        setContent();

        mAdapter = new CustomSectionAdapter(this, section.categories, mainColor, dataType);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                onListItemClick(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.stats_mode, menu);

        modeMenuItem = menu.findItem(R.id.easy_mode);
        checkModeIcon();

        return true;
    }

    private void checkModeIcon() {
        easy_mode = dataManager.easyMode();
        modeMenuItem.setVisible(easy_mode);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateContent();
    }

    public void updateContent() {
        section = dbHelper.getSectionCatItemsStats(section);
        setContent();
        mAdapter.notifyDataSetChanged();
        checkModeIcon();
    }

    public void setContent() {



        int dataCount = 0;

        String title = getString(R.string.section_studied_title);
        String desc = getString(R.string.status_studied);

        if (dataType == 0) {
            dataCount = section.studiedDataCount;
        }
        if (dataType == 1) {
            dataCount = section.familiarDataCount;

            title = getString(R.string.section_familiar_title);
            desc = getString(R.string.status_familiar);
        }
        if (dataType == 2) {
            dataCount = section.unknownDataCount;
            title = getString(R.string.section_unknown_title);
            desc = getString(R.string.status_unknown);
        }

        dataCountTxt.setText(String.valueOf(dataCount));
        dataTitle.setText(title);
        dataDesc.setText(desc);
    }


    public void onListItemClick(int position) {
            final int act = position;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openList (act);
                }
            }, 50);
    }


    public void openCat (int position) {

        if (!full_version) {
            if (!navSection.unlocked) {
                notifyLocked();
                return;
            }
        }

        Intent intent = new Intent(this, CatActivity.class);
        intent.putExtra(Constants.EXTRA_FORCE_STATUS, "always");
        intent.putExtra(Constants.EXTRA_DATA_TYPE, dataType);
        intent.putExtra(Constants.EXTRA_CAT_SPEC, section.categories.get(position).spec);
        intent.putExtra(Constants.EXTRA_CAT_ID, section.categories.get(position).id);
        intent.putExtra("cat_title", section.categories.get(position).title);
        startActivityForResult(intent, 1);
        openActivity.pageTransition();
    }


    public void openList (int position) {

        if (!full_version) {
            if (!navSection.unlocked) {
                notifyLocked();
                return;
            }
        }

        Intent intent = new Intent(this, CustomDataActivity.class);
        intent.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);
        intent.putExtra(Constants.EXTRA_DATA_TYPE, dataType);
        intent.putExtra(Constants.EXTRA_CAT_ID, section.categories.get(position).id);
        intent.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);

        startActivityForResult(intent, 1);
        openActivity.pageTransition();

    }


    public void notifyLocked() {

        String inPro = getString(R.string.pro_content);

        Snackbar.make(recyclerView, Html.fromHtml("<font color=\"#ffffff\">"+inPro+"</font>"), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

    }



    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }


}
