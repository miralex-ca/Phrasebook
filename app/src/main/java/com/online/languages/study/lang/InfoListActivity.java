package com.online.languages.study.lang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.appcompat.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.adapters.InfoDataListAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.INFO_TAG;

public class InfoListActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;


    ArrayList<DataItem> dataItems;
    InfoDataListAdapter adapter;
    RecyclerView recyclerView;

    DataManager dataManager;

    String sectionId;
    NavStructure navStructure;
    NavSection navSection;

    View emptyTxt;

    String catId;

    int listType;

    NavCategory navCategory;
    OpenActivity openActivity;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    int adapterListType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);
        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_info_data_list);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        adapterListType = -1;

        dataManager = new DataManager(this);
        easy_mode = dataManager.easyMode();
        dataModeDialog = new DataModeDialog(this);

        listType = getIntent().getIntExtra(Constants.EXTRA_DATA_TYPE, 0); //

        sectionId = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        catId = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.title_custom_txt);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        navSection = navStructure.getNavSectionByID(sectionId);
        navCategory = navStructure.getNavCatFromSection(sectionId, catId);

        setTitle(navCategory.title);

        emptyTxt = findViewById(R.id.emptyTxt);



        getDataList(sectionId, listType);

        recyclerView = findViewById(R.id.recycler_saved);

        adapter = new InfoDataListAdapter(this, dataItems, 0, themeTitle);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration( new DividerItemDecoration(this) );
        recyclerView.setAdapter(adapter);

        openView(recyclerView);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View animObj = view.findViewById(R.id.animObj);
                onItemClick(animObj, position);
            }
            @Override
            public void onLongClick(View view, int position) {

                if ( !dataItems.get(position).filter.contains(INFO_TAG) ) {

                    //Toast.makeText(InfoListActivity.this, "Starrable", Toast.LENGTH_SHORT).show();
                    changeStarred(position);
                }

            }
        }));
    }


    public void changeStarred(int position){   /// check just one item

        String id = dataItems.get(position).id;
        boolean starred = dataManager.checkStarStatusById(id );

        int status = dataManager.dbHelper.setStarred(id, !starred); // id to id


        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        int vibLen = 30;

        if (status == 0) {
            Toast.makeText(this, R.string.starred_limit, Toast.LENGTH_SHORT).show();
            vibLen = 300;
        }

        checkStarred(position);

        assert v != null;
        v.vibrate(vibLen);
    }



    private void openView(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                manageListVew(dataItems.size());

            }
        }, 100);
    }


    private void getDataList(String sectionId, int listType) {
        dataItems = dataManager.getCatDBList(catId);
    }

    private void onItemClick(final View view, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAlertDialog(view, position);
            }
        }, 50);
    }

    public void showAlertDialog(View view, int position) {

        Intent intent = new Intent(InfoListActivity.this, ScrollingActivity.class);

        String id = view.getTag().toString();
        intent.putExtra("starrable", false);
        intent.putExtra("id", id );
        intent.putExtra("position", position);
        startActivityForResult(intent,1);

        overridePendingTransition(R.anim.slide_in_down, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (resultCode == InfoListActivity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                checkStarred(result);
            }
        } else {

            updateContent();
        }
    }

    private void updateContent() {
        getDataList(sectionId, listType);
        adapter = new InfoDataListAdapter(this, dataItems, 0, themeTitle);
        recyclerView.setAdapter(adapter);
        manageListVew(dataItems.size());
    }

    private void manageListVew(int size) {

        if (size < 1) {
            recyclerView.setVisibility(View.GONE);
            emptyTxt.setVisibility(View.VISIBLE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void checkStarred(final int result){

        dataItems = dataManager.checkDataItemsData(dataItems);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(result);
            }
        }, 200);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.stats_mode_info, menu);
        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
        if (easy_mode) modeMenuItem.setVisible(true);

        return true;
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

            case R.id.info_from_menu:
                openInfo();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public void openInfo() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_list_txt));
    }


    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
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
