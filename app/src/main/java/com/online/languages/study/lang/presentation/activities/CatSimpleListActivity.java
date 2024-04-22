package com.online.languages.study.lang.presentation.activities;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.OUTCOME_ADDED;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.ContentAdapter;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.presentation.AppStart;
import com.online.languages.study.lang.presentation.core.BaseActivity;
import com.online.languages.study.lang.presentation.details.ScrollingActivity;
import com.online.languages.study.lang.presentation.flashcards.CardsActivity;

import java.util.ArrayList;


public class CatSimpleListActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    public ArrayList<DataItem> exerciseData = new ArrayList<>();
    public ArrayList<DataItem> cardData = new ArrayList<>();

    public static Section section;

    private String categoryID;
    private String catSpec;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    ImageView placeholder;
    View adContainer;
    View adCard;

    Boolean showAd;
    AdView mAdView;

    OpenActivity openActivity;

    MenuItem sortMenuItem;


    DataManager dataManager;
    private MenuItem changeLayoutBtn;

    private MenuItem bookmarkRadio;
    NavStructure navStructure;


    ContentAdapter adapter, adapterCompact;
    RecyclerView recyclerView, recyclerViewCompact;
    View listWrapper, listWrapperCompact;

    ArrayList<DataItem> data = new ArrayList<>();

    public String parentSectionId;
    String catParam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_cat_simple_list);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new DataModeDialog(this);
        dataManager = new DataManager(this);


        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        categoryID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);
        catSpec = getIntent().getStringExtra(Constants.EXTRA_CAT_SPEC);

        parentSectionId = getIntent().getStringExtra(EXTRA_SECTION_ID);
        navStructure = dataManager.getNavStructure();

        catParam  = navStructure.getNavCatFromSection(parentSectionId, categoryID).param;

        String title = getIntent().getStringExtra("cat_title");


        setTitle(title);

        getDataItems();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //////  ads show
        placeholder = findViewById(R.id.placeholder);
        adContainer = findViewById(R.id.adContainer);
        adCard = findViewById(R.id.adCard);

        showAd =false;

        mAdView = findViewById(R.id.adView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {  // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {  // Code to be executed when an ad request fails.
                showBanner();
            }
        });

        checkAdShow();
        ///////////////// ads


        listWrapper =  findViewById(R.id.listContainer);
        listWrapperCompact =  findViewById(R.id.listContainerCompact);

        recyclerView =  findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerViewCompact =  findViewById(R.id.my_recycler_view_compact);
        RecyclerView.LayoutManager mLayoutManagerCompact = new LinearLayoutManager(this);
        recyclerViewCompact.setLayoutManager(mLayoutManagerCompact);

        updateLayoutStatus();

        openView(recyclerView);
        openView(recyclerViewCompact);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        ((SimpleItemAnimator) recyclerViewCompact.getItemAnimator()).setSupportsChangeAnimations(false);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCompact, false);


        recyclerView.addOnItemTouchListener(new  RecyclerTouchListener(this, recyclerView, new CatSimpleListActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View animObj = view.findViewById(R.id.animObj);

                onItemClick(animObj, position);
            }
            @Override
            public void onLongClick(View view, int position) {
                changeStarred(position);
            }
        }));


        recyclerViewCompact.addOnItemTouchListener(new  RecyclerTouchListener(this, recyclerViewCompact, new CatSimpleListActivity.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                View animObj = view.findViewById(R.id.animObj);

                onItemClick(animObj, position);
            }
            @Override
            public void onLongClick(View view, int position) {
                changeStarred(position);
            }
        }));

    }



    private void onItemClick(final View view, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                 showAlertDialog(view, position);
            }
        }, 50);
    }


    public void updateLayoutStatus() {

        String listType =  appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            listWrapper.setVisibility(View.GONE);
            listWrapperCompact.setVisibility(View.VISIBLE);
        } else {
            listWrapperCompact.setVisibility(View.GONE);
            listWrapper.setVisibility(View.VISIBLE);
        }

        updateList();
    }

    private void updateList() {

        getDataList();
        adapter = new ContentAdapter( this, data, 0, themeTitle, false, CAT_LIST_VIEW_NORM);
        if (catParam.contains("_abc"))
            adapter = new ContentAdapter(this, data, 0, themeTitle, false, CAT_LIST_VIEW_NORM, true);

        recyclerView.setAdapter(adapter);

        adapterCompact = new ContentAdapter(this, data, 0, themeTitle, false, CAT_LIST_VIEW_COMPACT);
        recyclerViewCompact.setAdapter(adapterCompact);
    }




    public void getDataList() {
        String id =  categoryID;
        data = dataManager.getCatDBList(id);
        data = insertDivider(data);
    }

    public void changeStarred(int position) {   /// check just one item

        String id = data.get(position).id;
        boolean starred = dataManager.checkStarStatusById(id );

        int status = dataManager.dbHelper.setStarred(id, !starred); // id to id

        Vibrator v = (Vibrator)  getSystemService(Context.VIBRATOR_SERVICE);

        int vibLen = 30;

        if (status == 0) {
            Toast.makeText(this, R.string.starred_limit, Toast.LENGTH_SHORT).show();
            vibLen = 300;
        }

        checkStarred(position);

        assert v != null;
        v.vibrate(vibLen);
    }

    private ArrayList<DataItem> insertDivider(ArrayList<DataItem> data) {

        ArrayList<DataItem> list = new ArrayList<>();

        for (DataItem dataItem: data) {

            if (!dataItem.divider.equals("no")) {

                DataItem item = new DataItem();

                item.item = dataItem.divider;
                item.type = "divider";

                list.add(item);
            }

            list.add(dataItem);
        }

        return list;
    }


    private void openView(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, 70);
    }



    public void checkDataList() {   /// check all items

        data = dataManager.checkDataItemsData(data);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapterCompact.notifyDataSetChanged();
            }
        }, 80);
    }



    public void checkStarred(final int result){   /// check just one item
        data = dataManager.checkDataItemsData(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(result);
                adapterCompact.notifyItemChanged(result);
            }
        }, 200);
    }



    private void getDataItems() {
        DataManager dataManager = new DataManager(this);
        ArrayList<DataItem> data = dataManager.getCatDBList(categoryID);
        exerciseData = data;
        cardData = data;
    }



    public void showAlertDialog(View view, int position) {

        String id = view.getTag().toString();

        if (id.equals("divider")) return;

        Intent intent = new Intent(CatSimpleListActivity.this, ScrollingActivity.class);

        intent.putExtra("starrable", true);
        intent.putExtra("id", id );
        intent.putExtra("position", position);

        startActivityForResult(intent,1);

        overridePendingTransition(R.anim.slide_in_down, 0);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category_list, menu);

        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
        if (easy_mode) modeMenuItem.setVisible(true);
        if (!getResources().getBoolean(R.bool.display_mode)) {
            modeMenuItem.setVisible(false);
        }

        changeLayoutBtn = menu.findItem(R.id.list_layout);
        applyLayoutStatus();

        bookmarkRadio = menu.findItem(R.id.bookmark);
        applyBookmarkStatus();

        return true;
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
            case R.id.list_layout:
                changeLayoutStatus();
                return true;

            case R.id.bookmark:
                changeBookmark();
                return true;

            case R.id.card_from_menu:
                openCards();
                return true;

            case R.id.info_from_menu:
                infoMessage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void applyBookmarkStatus() {

        boolean status = dataManager.dbHelper.checkBookmark(categoryID, parentSectionId);

        if (status) bookmarkRadio.setIcon(R.drawable.ic_bookmark_active);
        else bookmarkRadio.setIcon(R.drawable.ic_bookmark_inactive);

        bookmarkRadio.setChecked(status);
    }

    private void changeBookmark() {


        int status = dataManager.setBookmark(categoryID, parentSectionId, navStructure);

        boolean radioChecked;

        if (status == OUTCOME_ADDED) {
            bookmarkRadio.setIcon(R.drawable.ic_bookmark_active);
            radioChecked = true;
        } else {
            bookmarkRadio.setIcon(R.drawable.ic_bookmark_inactive);
            radioChecked = false;
        }

        bookmarkRadio.setChecked(radioChecked);

    }


        private void applyLayoutStatus() {

            String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

            if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
                changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);
            } else {
                changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
            }
    }

    public void changeLayoutStatus() {

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            listType = CAT_LIST_VIEW_NORM;
        } else {

            listType = CAT_LIST_VIEW_COMPACT;

        }

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(CAT_LIST_VIEW, listType);
        editor.apply();

         updateLayoutStatus();
         applyLayoutStatus();
    }


    private void openCards() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openCards(true);
            }
        }, 80);
    }


    private void openCards(boolean action) {

        Intent i = new Intent(this, CardsActivity.class);
        i.putExtra(Constants.EXTRA_CAT_TAG, categoryID);
        i.putParcelableArrayListExtra("dataItems", cardData);
        startActivityForResult(i,2);
        openActivity.pageTransition();

    }



    private void infoMessage() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_txt));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if(resultCode == CatSimpleListActivity.RESULT_OK){

                int result = data.getIntExtra("result", -1);

                 checkStarred(result);
            }
        } else {

            //// checkDataList
        }
    }





    private void checkAdShow() {
        showAd = appSettings.getBoolean(Constants.SET_SHOW_AD, false);

        if (Build.VERSION.SDK_INT < 21) showAd= false;

        SharedPreferences mLaunches = getSharedPreferences(AppStart.APP_LAUNCHES, Context.MODE_PRIVATE);
        int launchesNum = mLaunches.getInt(AppStart.LAUNCHES_NUM, 0);

        if (showAd) showAdCard();

        if (launchesNum < 2) {
            adContainer.setVisibility(View.GONE);
        } else  {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    manageAd();
                }
            }, 100);
        }
    }

    private void showAdCard() {
        if (showAd) adContainer.setVisibility(View.VISIBLE);
    }

    private void showBanner() {
        showAdCard();
        mAdView.setVisibility(View.GONE);
        placeholder.setAlpha(0f);
        placeholder.setVisibility(View.VISIBLE);
        placeholder.animate().alpha(1.0f).setDuration(150);

    }

    public void manageAd() {

        if (showAd) {

            showAdCard();

            String admob_ap_id = getResources().getString(R.string.admob_ap_id);
            MobileAds.initialize(getApplicationContext(), admob_ap_id);

            final AdRequest adRequest;

            if (Constants.DEBUG) {
                adRequest = new AdRequest.Builder()
                        .addTestDevice("721BA1B0D2D410F1335955C3EC0C8B71")
                        .addTestDevice("725EEA094EAF285D1BD37D14A7F78C90")
                        .addTestDevice("0B44FDBCD710428A565AA061F2BD1A98")
                        .addTestDevice("88B83495F2CC0AF4C2C431655749C546")
                        .build();
            } else {
                adRequest = new AdRequest.Builder().build();
            }

            mAdView.setVisibility(View.VISIBLE);
            mAdView.loadAd(adRequest);

        } else {

            adContainer.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
            if (!showAd) checkAdShow();
        }
    }

    @Override
    public void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }



    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private  ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final  ClickListener clicklistener){
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
