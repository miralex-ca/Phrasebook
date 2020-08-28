package com.online.languages.study.lang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.online.languages.study.lang.adapters.ArchiveAdapter;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.adapters.UCatsListAdapter;
import com.online.languages.study.lang.data.BookmarkItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.NavStructure;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.ACTION_ARCHIVE;
import static com.online.languages.study.lang.Constants.ACTION_CHANGE_ORDER;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ARCHIVE;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_PARENT;
import static com.online.languages.study.lang.Constants.STATUS_DELETED;
import static com.online.languages.study.lang.Constants.STATUS_NEW;
import static com.online.languages.study.lang.Constants.STATUS_NORM;
import static com.online.languages.study.lang.Constants.STATUS_UPDATED;
import static com.online.languages.study.lang.Constants.UCAT_LIST_LIMIT;


public class UCatsArchiveActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager mLayoutManager;

    ArrayList<BookmarkItem> dataItems;


    NavStructure navStructure;

    DBHelper dbHelper;
    DataManager dataManager;


    OpenActivity openActivity;


    ArchiveAdapter adapter;
    ArrayList<DataObject> catsList;

    boolean cutList;
    RelativeLayout helperView;


    boolean activeAction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ucat_archive);

        navStructure = new NavStructure(this);

        openActivity = new OpenActivity(this);
        //openActivity.setOrientation();

        cutList = true;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTitle(R.string.archive_title);

        activeAction = false;


        helperView = findViewById(R.id.list_wrapper);


        dataManager = new DataManager(this, 1);
        dbHelper = dataManager.dbHelper;

        dataManager.plus_Version = dataManager.checkPlusVersion();

        recyclerView = findViewById(R.id.recycler_view);

        updateList();

        openListView();


    }

    private void openListView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                helperView.setVisibility(View.VISIBLE);

            }
        }, 30);

    }


    public void performAction(final DataObject dataObject, String type) {

        if (type.equals(ACTION_UPDATE)) openCatEdit(dataObject);

        if (type.equals(ACTION_ARCHIVE)) unarchive(dataObject);

    }


    public void openCompleteList(View view) {

        cutList = false;

        updateList();

        helperView.clearAnimation();
        setWrapContentHeight(helperView);
    }


    public void updateList() {

        catsList  = getCatList();


        adapter = new ArchiveAdapter(this, catsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }



    public ArrayList<DataObject> getCatList() {

        setWrapContentHeight(helperView);


        ArrayList<DataObject> completeList = dataManager.getUcatsForArchive();

        if (!dataManager.plus_Version) {
            completeList = dataManager.getUcatsListForUnpaid(PARAM_UCAT_ARCHIVE);
        }




        ArrayList<DataObject> displayList = new ArrayList<>(completeList);


        int limit = UCAT_LIST_LIMIT;


        if (completeList.size() > limit) {
            if (cutList) displayList = new ArrayList<>(completeList.subList(0, limit));
        }

        return addLast(displayList, completeList);

    }


    private ArrayList<DataObject> addLast(ArrayList<DataObject> displayList, ArrayList<DataObject> completeList) {

        DataObject lastObject = checkMoreItem(displayList, completeList);

        displayList.add( lastObject );


        return displayList;
    }


    private DataObject checkMoreItem(ArrayList<DataObject> displayList, ArrayList<DataObject> completeList) {

        DataObject lastObject = new DataObject();
        lastObject.id = "last";


        int dif = completeList.size() - displayList.size();

        if (dif > 0) {
            lastObject.title = String.format(getString(R.string.load_more_items), String.valueOf(dif));;
            lastObject.info = "show";
        } else {
            lastObject.info = "hide";
        }

        return  lastObject;

    }

    private void updateMoreIem() {
        adapter.notifyItemChanged(catsList.size()-1);
    }




    public void openMyCat(DataObject dataObject) {

        if (dataObject.count > 0 ) {

            String id = dataObject.id;
            String title = dataObject.title;

            Intent i = new Intent(this, CatActivity.class);

            i.putExtra(EXTRA_SECTION_ID, PARAM_UCAT_PARENT);
            i.putExtra(Constants.EXTRA_CAT_ID, id);
            i.putExtra("cat_title", title);
            i.putExtra(Constants.EXTRA_CAT_SPEC, PARAM_EMPTY);

            startActivityForResult(i, 10);

            openActivity.pageTransition();

        } else {

            openCatEdit( dataObject );
        }

    }


    public void unarchive(DataObject dataObject) {

        dataManager.dbHelper.unarchiveUCat(dataObject);

        checkListAnimation();

    }



    private void checkListAnimation() {

        ArrayList<DataObject> newCatlist = getCatList();


        for (DataObject catData: catsList) catData.status = STATUS_DELETED;
        for (DataObject newCat: newCatlist)  newCat.status = STATUS_NEW;


        for (int i = 0; i < catsList.size(); i ++ ) {

            DataObject catData = catsList.get(i);

            for (int n = 0; n < newCatlist.size(); n ++ ) {


                DataObject newCat = newCatlist.get(n);

                if (newCat.id.equals(catData.id)) {

                    newCat.status = STATUS_NORM;
                    catData.status = STATUS_NORM;

                    if ( catData.time_updated != newCat.time_updated) {

                        catData.time_updated = newCat.time_updated;

                        catData.status = STATUS_UPDATED;
                        newCat.status = STATUS_UPDATED;

                    }

                    if (catData.id.equals("last")) {
                        catData.title  = newCat.title;
                        catData.info = newCat.info;
                    }

                    break;
                }
            }
        }



        for(int i = 0; i < catsList.size(); i++) {

            DataObject dataObject = catsList.get(i);

            if (dataObject.status.equals(STATUS_UPDATED)) {

                setHR( recyclerView, helperView);
                catsList.remove(i);
                adapter.notifyItemRemoved(i); /// normal
            }

            if (dataObject.status.equals(STATUS_DELETED)) {

                setHR( recyclerView, helperView);
                catsList.remove(i);
                adapter.notifyItemRemoved(i);

            }
        }

        for(int i = 0; i < newCatlist.size(); i++) {

            DataObject dataObject = newCatlist.get(i);

            if (dataObject.status.equals(STATUS_UPDATED)) {
                if (i > (newCatlist.size()-1 )) {
                    catsList.add(dataObject);
                    adapter.notifyItemInserted(catsList.size() -1 );
                } else {
                    catsList.add(i, dataObject);
                    adapter.notifyItemInserted(i);
                }
            }

        }


        for(int i = 0; i < newCatlist.size(); i++) {

            DataObject dataObject = newCatlist.get(i);

            if (dataObject.status.equals(STATUS_NEW)) {

                catsList.add(i,dataObject);
                adapter.notifyItemInserted(i);
            }

        }

        updateMoreIem();

    }


    private void setHR(final RecyclerView recycler, final RelativeLayout helper) {



        recycler.setMinimumHeight(recycler.getHeight());
        helper.setMinimumHeight(recycler.getHeight());



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                 recycler.setMinimumHeight(0);

            }
        }, 400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                int h = recycler.getHeight();

                ResizeHeight resizeHeight = new ResizeHeight(helper, h);
                resizeHeight.setDuration(300);

                helper.clearAnimation();
                helper.startAnimation(resizeHeight);

            }
        }, 450);

    }


    private void setWrapContentHeight(View view) { //// should aply to the target view parent

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(params);

    }



    public void openCatEdit(DataObject dataObject) {

        Intent i = new Intent(this, MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, dataObject.id);
        startActivityForResult(i, 10);
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

            case R.id.info_item:
                showInfoDialog();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_info, menu);

        return true;
    }

    public void showInfoDialog() {
        DataModeDialog dataModeDialog = new DataModeDialog(this);
        String info = getString(R.string.info_ucat_archive); /// TODO check description
        dataModeDialog.createDialog(getString(R.string.info_txt), info);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            updateList();
        }


    }




    public interface ClickListener {
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
