package com.online.languages.study.lang.presentation.favorites;

import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_PARENT;
import static com.online.languages.study.lang.Constants.UC_PREFIX;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.BookmarksAdapter;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.BookmarkItem;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.ImageMapsData;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.data.ViewSection;
import com.online.languages.study.lang.presentation.category.CatActivity;
import com.online.languages.study.lang.presentation.core.BaseActivity;

import java.util.ArrayList;


public class StarredBookmarksActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    RecyclerView recyclerView;

    BookmarksAdapter mAdapter;

    RecyclerView.LayoutManager mLayoutManager;

    NavStructure navStructure;

    String tSectionID = "01010";
    String tCatID = "01010";

    ViewSection viewSection;

    NavSection navSection;

    Boolean full_version;

    ImageMapsData imageMapsData;

    ArrayList<BookmarkItem> dataItems;


    RelativeLayout itemsList, itemListWrap;

    int listType;

    DBHelper dbHelper;
    DataManager dataManager;

    final String STARRED = "starred";
    final String PIC_LIST = "pic_list";

    OpenActivity openActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmarks);

        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        listType = 1; /// default - 3 image grid


        openActivity = new OpenActivity(this);
        openActivity.setOrientation();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemsList = findViewById(R.id.items_list);
        itemListWrap = findViewById(R.id.itemListWrap);


        openView (itemsList);

        dataManager = new DataManager(this, 1);
        dbHelper = dataManager.dbHelper;

        imageMapsData = new ImageMapsData(this);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        tCatID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);
        viewSection = new ViewSection(this, navSection, tCatID);


        getImages();



        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new BookmarksAdapter(this, dataItems, 1, themeTitle);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration( new DividerItemDecoration(this) );
        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) { openCat(view); }
            @Override
            public void onLongClick(View view, int position) {  longClick(view, position); }
        }));

    }


    private void longClick(View view, int position) {
        removeBookmark(position);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        int vibLen = 30;

        assert v != null;
        v.vibrate(vibLen);
    }


    private void setPageTitle() {
        setTitle(String.format(getString(R.string.starred_bookmarks_title), dataItems.size()));
    }


    private void openView (final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, 50);
    }


    public String getListType () {
        return STARRED;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    compare();
                }
            }, 150);

        }

    }



    private void removeBookmark(int position) {

        BookmarkItem bookmark = dataItems.get(position);

        dataManager.setBookmark(bookmark.item, bookmark.parent, navStructure);

        compare();

    }

    private void compare() {

        ArrayList<BookmarkItem> bookmarkItems = dataManager.getBookmarks(navStructure);

        for (int i=0; i < dataItems.size(); i++) {

            BookmarkItem present = dataItems.get(i);
            boolean found = false;

            for (BookmarkItem saved: bookmarkItems) {
                if (present.item.equals(saved.item) && present.parent.equals(saved.parent)) {

                    if ( !present.title.equals(saved.title) ) {

                        dataItems.get(i).title = saved.title;

                        mAdapter.notifyItemChanged(i);
                    }

                    found = true;
                    break;
                }


            }

            if (!found) checkBookmarks(present, i);
        }

    }

    private void checkBookmarks(BookmarkItem bookmarkItem, int position) {

        itemListWrap.setMinimumHeight(recyclerView.getHeight());

        try {
            int count = recyclerView.getChildCount();

            if (count > 0) {
                setHR(recyclerView, itemListWrap);
            }
        } finally {
            mAdapter.remove(position);
        }

        setPageTitle();
    }



    private void setHR(final RecyclerView recycler, final View helper) {

        recycler.setMinimumHeight(recycler.getHeight());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                recyclerView.setMinimumHeight(0);


            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int h = recycler.getHeight();
                ResizeHeight resizeHeight = new ResizeHeight(helper, h);
                resizeHeight.setDuration(400);
                helper.startAnimation(resizeHeight);

            }
        }, 600);

    }

    public void getImages() {

        dataItems = dataManager.getBookmarks(navStructure);
        setPageTitle();

    }

    public ArrayList<DataItem> getDataItems() {
        return dataManager.getCatDBList(tCatID);
    }



    public void openCat(final View view) {

        View tagged = view.findViewById(R.id.tagged);

        final String id = (String) tagged.getTag();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAlertDialog(id, view);
            }
        }, 50);
    }


    public void showAlertDialog(String id, View view) {


        if (id.contains(UC_PREFIX)) {
            openUcat(id);
            return;
        }

        int position = 0;

        for ( int i = 0; i < dataItems.size(); i++ ) {
            if (dataItems.get(i).item.equals(id)) position = i;
        }

        ViewCategory viewCategory = new ViewCategory(dataItems.get(position).navCategory);

        openActivity.openFromViewCat(
                navStructure,
                dataItems.get(position).parent,
                viewCategory);

    }

    private void openUcat(String id) {

        Intent i = new Intent(this, CatActivity.class);

        i.putExtra(EXTRA_SECTION_ID, PARAM_UCAT_PARENT);
        i.putExtra(Constants.EXTRA_CAT_ID, id);
        i.putExtra("cat_title", PARAM_EMPTY);
        i.putExtra(Constants.EXTRA_CAT_SPEC, PARAM_EMPTY);

        startActivityForResult(i, 1);

        openActivity.pageTransition();

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
        String info = getString(R.string.info_bookmark_list); /// TODO check description
        dataModeDialog.createDialog(getString(R.string.info_txt), info);
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
