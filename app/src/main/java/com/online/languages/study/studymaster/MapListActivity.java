package com.online.languages.study.studymaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.studymaster.adapters.DataModeDialog;
import com.online.languages.study.studymaster.adapters.DividerItemDecoration;
import com.online.languages.study.studymaster.adapters.ImageListAdapter;
import com.online.languages.study.studymaster.adapters.MapListAdapter;
import com.online.languages.study.studymaster.adapters.OpenActivity;
import com.online.languages.study.studymaster.adapters.ThemeAdapter;
import com.online.languages.study.studymaster.data.DataItem;
import com.online.languages.study.studymaster.data.DataManager;
import com.online.languages.study.studymaster.data.ImageData;
import com.online.languages.study.studymaster.data.ImageMapsData;
import com.online.languages.study.studymaster.data.NavSection;
import com.online.languages.study.studymaster.data.NavStructure;
import com.online.languages.study.studymaster.data.ViewCategory;
import com.online.languages.study.studymaster.data.ViewSection;

import java.util.ArrayList;


public class MapListActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    RecyclerView recyclerView;
    RecyclerView recyclerViewCards;
    ImageListAdapter mAdapter;
    ImageListAdapter cardsAdapter;

    NavStructure navStructure;

    String tSectionID = "01010";
    String tCatID = "01010";

    ViewSection viewSection;

    NavSection navSection;

    Boolean full_version;

    ImageMapsData imageMapsData;

    View itemsList, cardsList;

    private MenuItem changeLayoutBtn;

    int listType;

    OpenActivity openActivity;

    ArrayList<DataItem> dataItems;
    DBHelper dbHelper;
    DataManager dataManager;

    int groupType;  // 0 - images data, 2 - dataitems from data file


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        dataManager = new DataManager(this, 1);
        dbHelper = dataManager.dbHelper;


        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        listType = appSettings.getInt("maps_list_layout", 2);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.maps_list_title);

        itemsList = findViewById(R.id.items_list);
        cardsList = findViewById(R.id.cards_list);

        groupType = 2;

        imageMapsData = new ImageMapsData(this);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        tCatID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);
        viewSection = new ViewSection(this, navSection, tCatID);


        getImages();

        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new ImageListAdapter(this, dataItems, 1, themeTitle);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration( new DividerItemDecoration(this) );
        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        recyclerViewCards = findViewById(R.id.recycler_view_cards);
        cardsAdapter = new ImageListAdapter(this, dataItems, 2, themeTitle);

        recyclerViewCards.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewCards.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration( new DividerItemDecoration(this) );

        recyclerViewCards.setSelected(true);
        recyclerViewCards.setAdapter(cardsAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCards, false);



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) { openCat(view); }
            @Override
            public void onLongClick(View view, int position) {  }
        }));

        recyclerViewCards.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewCards, new ClickListener() {
            @Override
            public void onClick(View view, int position) { openCat(view);}
            @Override
            public void onLongClick(View view, int position) {  }
        }));


    }

    public void getImages() {

        dataItems = getDataItems();

        if (tCatID.contains("group")) {

            dataItems = new ArrayList<>();
            groupType = 0;

            for (ViewCategory cat: viewSection.categories) {
                ImageData image = imageMapsData.getMapInfoById(cat.id);
                cat.image  = image.image;

                dataItems.add(image.getDataItem());

            }
        }
    }

    public ArrayList<DataItem> getDataItems() {
        return dataManager.getCatDBList(tCatID);
    }


    private void changeLayoutStatus() {

        if (listType == 1) {
            listType = 2;
        } else {
            listType = 1;
        }

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt("maps_list_layout", listType);
        editor.apply();

        applyLayoutStatus(listType);
    }

    private void applyLayoutStatus(int type) {

        if (type == 2) {
            cardsList.setVisibility(View.VISIBLE);
            itemsList.setVisibility(View.GONE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconList));
        } else {
            cardsList.setVisibility(View.GONE);
            itemsList.setVisibility(View.VISIBLE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconGrid2));
        }

    }



    public void openCat(final View view) {

        View tagged = view.findViewById(R.id.tagged);
        final String id = (String) tagged.getTag();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openCatActivity(id);
            }
        }, 50);
    }

    public void openCatActivity(String id) {

        int position = 0;

        for (int i = 0; i < dataItems.size(); i++) {
            if (dataItems.get(i).id.equals(id)) position  = i;
        }

        Intent i;
        i = new Intent(this, MapActivity.class);
        i.putExtra("page_id", dataItems.get(position).id);
        i.putExtra("pic", groupType);

        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // updateContent();
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

            case R.id.list_layout:
                changeLayoutStatus();
                return true;

            case R.id.info_from_menu:
                showInfoDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps_list, menu);
        changeLayoutBtn =  menu.findItem(R.id.list_layout);
        applyLayoutStatus(listType);
        return true;
    }


    public void showInfoDialog() {
        String message = getString(R.string.maps_info);
        if (navSection.spec.equals("gallery")) message = getString(R.string.info_menu_map);
        DataModeDialog dataModeDialog = new DataModeDialog(this);
        dataModeDialog.createDialog(getString(R.string.info_txt), message);
    }


    private int getDrawableIcon(int iconAttr) {

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(iconAttr, typedValue, true);
        int drawableRes = typedValue.resourceId;

        return drawableRes;
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
