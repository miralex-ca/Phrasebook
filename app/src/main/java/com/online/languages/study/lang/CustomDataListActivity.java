package com.online.languages.study.lang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.CustomDataListAdapter;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;

import java.util.ArrayList;

public class CustomDataListActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;


    ArrayList<DataItem> dataItems;
    CustomDataListAdapter adapter;
    RecyclerView recyclerView;

    DataManager dataManager;

    String sectionId;
    String catId;
    NavStructure navStructure;
    NavSection navSection;

    View emptyTxt;

    int listType;

    NavCategory navCategory;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    int adapterListType;

    OpenActivity openActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_custom_data_list);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        adapterListType = -1;

        sectionId = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        catId = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new DataModeDialog(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.title_custom_txt);


        listType = getIntent().getIntExtra(Constants.EXTRA_DATA_TYPE, 0); //
        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        navSection = navStructure.getNavSectionByID(sectionId);


        emptyTxt = findViewById(R.id.emptyTxt);

        setPageTitle(listType);

        dataManager = new DataManager(this);

        getDataList(sectionId, listType);

        recyclerView = findViewById(R.id.recycler_saved);

        adapter = new CustomDataListAdapter(dataItems, adapterListType);

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

                // Toast.makeText(getActivity(), "Dialog OnClick", Toast.LENGTH_SHORT).show();

                onItemClick(animObj, position);

            }
            @Override
            public void onLongClick(View view, int position) {
                changeStarred(position);
            }
        }));

    }


    public void changeStarred(int position){   /// check just one item

        String id = dataItems.get(position).id;
        Boolean starred = dataManager.checkStarStatusById(id );

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



    private void setPageTitle(int listType) { // 0 - studied, 1 - familiar, 2 - unknown;

        String title = " (список)";
        String list = "";

        if (listType == 0) list = "Изученно";
        if (listType == 1) list = "Пройдено";
        if (listType == 2) list = "Не пройдено";

        title = list + title;
        setTitle(title);

        if (sectionId.equals(Constants.ERRORS_CAT_TAG)) setTitle(R.string.title_errors_txt);

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

        if (sectionId.equals("errors") ) {
            adapterListType = 0;
            dataItems = getIntent().getParcelableArrayListExtra("dataItems");
            dataItems = dataManager.checkDataItemsData(dataItems);

        } else {

            ArrayList<NavCategory> navCategories = new ArrayList<>();
            for (NavCategory cat: navSection.uniqueCategories) {

                if (cat.id.equals(catId)) {
                    navCategory = cat;
                    navCategories.add(cat);
                }
            }

            dataItems = dataManager.getCatCustomList(navCategories, listType);

        }

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

        Intent intent = new Intent(CustomDataListActivity.this, ScrollingActivity.class);

        String id = view.getTag().toString();

        intent.putExtra("starrable", navSection.unlocked);
        intent.putExtra("id", id );
        intent.putExtra("position", position);
        startActivityForResult(intent,1);

        overridePendingTransition(R.anim.slide_in_down, 0);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == CustomDataListActivity.RESULT_OK){
                int result=data.getIntExtra("result", -1);
                checkStarred(result);
            }
        } else {

            if (sectionId.equals("errors") ) {
                dataItems = dataManager.checkDataItemsData(dataItems);
                adapter.notifyDataSetChanged();
            } else {
                updateContent();
            }

        }

    }

    private void updateContent() {
        getDataList(sectionId, listType);
        adapter = new CustomDataListAdapter(dataItems);
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


    public void openCat () {

        Intent intent = new Intent(CustomDataListActivity.this, CatActivity.class);
        intent.putExtra(Constants.EXTRA_FORCE_STATUS, "always");

        intent.putExtra(Constants.EXTRA_CAT_SPEC, navCategory.spec);

        intent.putExtra(Constants.EXTRA_CAT_ID, navCategory.id);
        intent.putExtra("cat_title", navCategory.title);
        startActivityForResult(intent, 2);
        openActivity.pageTransition();
    }


    public void openExercise() {

        Intent i = new Intent(CustomDataListActivity.this, ExerciseActivity.class) ;
        i.putParcelableArrayListExtra("dataItems", dataItems);
        if (sectionId.equals(Constants.ERRORS_CAT_TAG)) {
            i.putExtra(Constants.EXTRA_CAT_TAG, Constants.ERRORS_CAT_TAG);

        } else {
            i.putExtra(Constants.EXTRA_CAT_TAG, "custom");
        }

        startActivityForResult(i, 2);
        openActivity.pageTransition();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (sectionId.equals(Constants.ERRORS_CAT_TAG)) {
            getMenuInflater().inflate(R.menu.menu_custom_list, menu);
        } else {
            getMenuInflater().inflate(R.menu.stats_mode_info, menu);
            MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
            if (easy_mode) modeMenuItem.setVisible(true);
        }

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

            case R.id.open_test:
                openExercise();
                return true;

            case R.id.easy_mode:
                dataModeDialog.openDialog();
                return true;
            case R.id.info_from_menu:
                dataModeDialog.modeInfoDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
