package com.online.languages.study.lang;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ProgressDataAdapter;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.UserStats;


public class ProgressStatsActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    NavStructure navStructure;
    UserStats userStats;

    ProgressDataAdapter adapter;
    RecyclerView recyclerView;

    ProgressBar knownProgress, studiedProgress;
    TextView knownProgressTxt, studiedProgressTxt;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    OpenActivity openActivity;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_stats);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        dataManager = new DataManager(this);
        easy_mode = dataManager.easyMode();
        dataModeDialog = new DataModeDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.stats_progress_title);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);

        userStats = new UserStats(this, navStructure);

        knownProgress = findViewById(R.id.knownProgress);
        studiedProgress = findViewById(R.id.studiedProgress);

        knownProgressTxt = findViewById(R.id.knonwnProgressTxt);
        studiedProgressTxt = findViewById(R.id.studiedProgressTxt);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setSelected(true);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        updateContent();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                onGridClick(position);

            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void onGridClick(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openSectionStats(position);
            }
        }, 80);
    }


    public void openSectionStats(int position) {
        Intent i = new Intent(ProgressStatsActivity.this, SectionStatsActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, navStructure.sections.get(position).id);
        i.putExtra(Constants.EXTRA_SECTION_NUM, position);
        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateContent();
    }


    public void updateContent() {

        userStats.updateData();

        int studiedProgressValue = (100 * userStats.userStatsData.studiedDataCount / userStats.userStatsData.allDataCount);
        int knownProgressValue = (100 * userStats.userStatsData.familiarDataCount / userStats.userStatsData.allDataCount);

        knownProgress.setProgress(knownProgressValue);
        studiedProgress.setProgress(studiedProgressValue);

        knownProgressTxt.setText(knownProgressValue+"%");
        studiedProgressTxt.setText(studiedProgressValue+"%");

        adapter = new ProgressDataAdapter(this, userStats.userStatsData.sectionsDataList);
        recyclerView.setAdapter(adapter);
    }



    public void showInfoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.stats_progress_title)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setMessage(R.string.progress_info);
        AlertDialog alert = builder.create();
        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);

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
            case R.id.stats_info:
                showInfoDialog();
                return true;

            case R.id.easy_mode:
                dataModeDialog.openDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_section_stats, menu);

        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
        MenuItem infoMenuItem = menu.findItem(R.id.stats_info);

        if (easy_mode) {
            modeMenuItem.setVisible(true);
            infoMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        return true;

    }



    public interface ClickListener{
        void onClick(View view,int position);
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
