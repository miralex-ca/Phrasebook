package com.online.languages.study.lang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.NotesArchiveAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.BookmarkItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.presentation.BaseActivity;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.ACTION_ARCHIVE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ACTION;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;
import static com.online.languages.study.lang.Constants.NOTES_LIST_LIMIT;
import static com.online.languages.study.lang.Constants.STATUS_DELETED;
import static com.online.languages.study.lang.Constants.STATUS_NEW;
import static com.online.languages.study.lang.Constants.STATUS_NORM;
import static com.online.languages.study.lang.Constants.STATUS_UPDATED;


public class NotesArchiveActivity extends BaseActivity {


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


    NotesArchiveAdapter adapter;
    ArrayList<DataObject> notesList;

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


        setTitle(R.string.notes_archive_title);

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

    private void editGroupFromList(DataObject group) {

      //  newGroupDialog = new NewGroupDialog(this, UCatsListActivity.this);

       // newGroupDialog.showCustomDialog(getString(R.string.edit_group_title), ACTION_EDIT_GROUP, group );

    }


    public void openCompleteList(View view) {

        cutList = false;

        updateList();

        helperView.clearAnimation();
        setWrapContentHeight(helperView);
    }


    public void updateList() {

        notesList = getCatList();

        adapter = new NotesArchiveAdapter(this, notesList, this);


       // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);


        int spanCount = 1;
        if (getResources().getBoolean(R.bool.tablet))  spanCount = 2;

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(spanCount,1);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }



    public ArrayList<DataObject> getCatList() {

        setWrapContentHeight(helperView);

        ArrayList<DataObject> completeList = dataManager.getNotesForArchive();

        ArrayList<DataObject> displayList = new ArrayList<>(completeList);

        int limit = NOTES_LIST_LIMIT;

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
        adapter.notifyItemChanged(notesList.size()-1);
    }



    public void openMyCat(DataObject dataObject) {

        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra(EXTRA_NOTE_ID, dataObject.id );

        startActivityForResult(i, 10);
        openActivity.pageTransition();

    }



    public void unarchive(DataObject dataObject) {

        dataManager.dbHelper.parentNote(dataObject.id, "");

        checkListAnimation();

    }



    private void checkListAnimation() {

        ArrayList<DataObject> newCatlist = getCatList();


        for (DataObject catData: notesList) catData.status = STATUS_DELETED;
        for (DataObject newCat: newCatlist)  newCat.status = STATUS_NEW;


        for (int i = 0; i < notesList.size(); i ++ ) {

            DataObject catData = notesList.get(i);

            for (int n = 0; n < newCatlist.size(); n ++ ) {


                DataObject newCat = newCatlist.get(n);

                if (newCat.id.equals(catData.id)) {

                    newCat.status = STATUS_NORM;
                    catData.status = STATUS_NORM;

                    if ( (catData.time_updated != newCat.time_updated) || (catData.time_updated_sort != newCat.time_updated_sort)) {

                        catData.time_updated = newCat.time_updated;
                        catData.time_updated_sort = newCat.time_updated_sort;

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



        for(int i = 0; i < notesList.size(); i++) {

            DataObject dataObject = notesList.get(i);

            if (dataObject.status.equals(STATUS_UPDATED)) {

                setHR( recyclerView, helperView);
                notesList.remove(i);
                adapter.notifyItemRemoved(i); /// normal
            }

            if (dataObject.status.equals(STATUS_DELETED)) {

                setHR( recyclerView, helperView);
                notesList.remove(i);
                adapter.notifyItemRemoved(i);

            }
        }

        for(int i = 0; i < newCatlist.size(); i++) {

            DataObject dataObject = newCatlist.get(i);

            if (dataObject.status.equals(STATUS_UPDATED)) {
                if (i > (newCatlist.size()-1 )) {
                    notesList.add(dataObject);
                    adapter.notifyItemInserted(notesList.size() -1 );
                } else {
                    notesList.add(i, dataObject);
                    adapter.notifyItemInserted(i);
                }
            }

        }


        for(int i = 0; i < newCatlist.size(); i++) {

            DataObject dataObject = newCatlist.get(i);

            if (dataObject.status.equals(STATUS_NEW)) {

                notesList.add(i,dataObject);
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
        editNote(dataObject.id);
    }

    private void editNote(String id) {
        Intent i = new Intent(this, NoteEditActivity.class);
        i.putExtra(EXTRA_NOTE_ID, id );
        i.putExtra(EXTRA_NOTE_ACTION, ACTION_UPDATE );

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
        String info = getString(R.string.info_note_archive); /// TODO check description
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
