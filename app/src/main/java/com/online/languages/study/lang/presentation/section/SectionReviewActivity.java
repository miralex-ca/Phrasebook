package com.online.languages.study.lang.presentation.section;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.PremiumDialog;
import com.online.languages.study.lang.adapters.SectionListAdapter;
import com.online.languages.study.lang.adapters.SectionReviewListAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.presentation.category.CatActivity;
import com.online.languages.study.lang.presentation.core.ThemedActivity;
import com.online.languages.study.lang.presentation.details.ScrollingActivity;
import com.online.languages.study.lang.tools.CheckPlusVersion;
import com.online.languages.study.lang.tools.TopicIcons;

import java.util.ArrayList;

public class SectionReviewActivity extends ThemedActivity {


    private static final String SECTION_LIST_VIEW = "section_list_layout";
    ArrayList<DataItem> data = new ArrayList<>();
    SectionListAdapter adapter;
    DataManager dataManager;

    NavSection navSection;

    DataModeDialog dataModeDialog;

    boolean fullVersion;

    OpenActivity openActivity;

    ArrayList<CatSet> catSetsList;
    ArrayList<ArrayList<DataItem>> groupsList;

    LinearLayout itemsList;
    private MenuItem changeLayoutBtn;
    String sectionListLayout = CAT_LIST_VIEW_COMPACT;

    private static final int DIALOG_OPEN = 1;
    private static final int DIALOG_CLOSED = 0;
    private int dialogStatus = DIALOG_CLOSED;
    CheckPlusVersion checkPlusVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        NavStructure navStructure;

        String tSectionID;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_review_list);

        dataModeDialog = new DataModeDialog(this);

        //fullVersion = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);

        setTitle(navSection.title);

        catSetsList = new ArrayList<>();
        groupsList = new ArrayList<>();

        checkPlusVersion = new CheckPlusVersion(this, appSettings);
        dataManager = new DataManager(this);
        itemsList = findViewById(R.id.items_list);

        getGroupsData();

        organizeSection();

        openView(itemsList);

    }

    private void getGroupsData() {
        groupsList = dataManager.dbHelper.getSectionGroupsDataItems(navSection.uniqueCategories);
    }


    private void organizeSection() {

        sectionListLayout = appSettings.getString(SECTION_LIST_VIEW, CAT_LIST_VIEW_COMPACT);
        catSetsList = new ArrayList<>();

        for (int i = 0; i < navSection.uniqueCategories.size(); i++) {

            NavCategory category = navSection.uniqueCategories.get(i);

            ArrayList<DataItem> items = groupsList.get(i);

            CatSet catSet = new CatSet();

            catSet.category = category;
            catSet.dataItemsList = items;


                catSetsList.add(catSet);
                addList(catSet);



        }

    }


    private void addList(CatSet group) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.section_items_list, null);

        TextView title = item.findViewById(R.id.section_list_title);
        title.setText(group.category.title);

        View header = item.findViewById(R.id.section_list_header);

        if (checkPlusVersion.isNotPlusVersion() && !group.category.unlocked) {

            ImageView img = item.findViewById(R.id.section_list_img);

            img.setImageResource(R.drawable.ic_lock);

            header.setOnClickListener(v -> openPremiumDialog());

        } else {

            if (group.category.spec.equals("example")) {

                ImageView img = item.findViewById(R.id.section_list_img);
                img.setVisibility(View.GONE);
                header.setBackground(null);
                title.setTypeface(null, Typeface.NORMAL);

            } else {
                header.setOnClickListener(v -> openCategory(group.category));

            }



        }


        ImageView titleIcon = item.findViewById(R.id.section_list_title_icon);

        int resourceId = new TopicIcons().getIcon(group.category.image);

        if (group.category.image.equals("none")) titleIcon.setVisibility(View.GONE);

        titleIcon.setImageResource(resourceId);

        RecyclerView recyclerView = item.findViewById(R.id.recycler_view);

        recyclerView.setTag(group.category.id);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        setListAdapter(group, recyclerView);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        itemsList.addView(item);

    }

    private void openPremiumDialog() {
        PremiumDialog premiumDialog = new PremiumDialog(this);
        premiumDialog.createDialog(getString(R.string.plus_version_btn), getString(R.string.topic_access_in_plus_version));
    }

    private void setListAdapter(CatSet group, RecyclerView recyclerView) {

        SectionReviewListAdapter tAdapter = new SectionReviewListAdapter(group.dataItemsList, sectionListLayout) {
            @Override
            public void callBack(DataItem dataitem) {
                showAlertDialog(dataitem);
            }
        };
        recyclerView.setAdapter(tAdapter);
    }

    public void openCategory(NavCategory category) {

        Intent i = new Intent(this, CatActivity.class);

        i.putExtra(EXTRA_SECTION_ID, navSection.id);
        i.putExtra(EXTRA_CAT_ID, category.id);
        i.putExtra(Constants.EXTRA_CAT_SPEC, category.spec);
        i.putExtra("cat_title", category.title);

        startActivityForResult(i, 2);
        openActivity.pageTransition();
    }


    public static class CatSet {

        public ArrayList<DataItem> dataItemsList = new ArrayList<>();
        public NavCategory category = new NavCategory();

        private CatSet() {
        }
    }


    public void changeStarred(int position) {   /// check just one item

        String id = data.get(position).id;
        boolean starred = dataManager.checkStarStatusById(id);

        int status = dataManager.dbHelper.setStarred(id, !starred); // id to id


        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        int vibLen = 30;

        if (status == 0) {
            Toast.makeText(this, R.string.starred_limit, Toast.LENGTH_SHORT).show();
            vibLen = 300;
        }

        // checkStarred(position);

        v.vibrate(vibLen);
    }


    private void openView(final View view) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                view.setVisibility(View.VISIBLE), 80);
    }



    public void showAlertDialog(DataItem dataItem) {

        if (dialogStatus == DIALOG_OPEN) return;

        Intent intent = new Intent(this, ScrollingActivity.class);

        intent.putExtra("id", dataItem.id);

        intent.putExtra("position", 0);
        intent.putExtra("item", dataItem);

        intent.putExtra(EXTRA_CAT_ID, dataItem.cat);

        startActivityForResult(intent, 1);

        overridePendingTransition(R.anim.slide_in_down, 0);

        dialogStatus = DIALOG_OPEN;

        new Handler(Looper.getMainLooper()).postDelayed(() -> dialogStatus = DIALOG_CLOSED, 200);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        dialogStatus = DIALOG_CLOSED;

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                if (data.hasExtra(EXTRA_CAT_ID)) {
                    String cat = data.getStringExtra(EXTRA_CAT_ID);

                    checkStarred(cat);
                }
            }
        }

        if (requestCode == 2) {

            if (data != null && data.hasExtra(EXTRA_CAT_ID) ) {

                String cat = data.getStringExtra(EXTRA_CAT_ID);

                checkStarred(cat);
            }
        }
    }


    public void checkStarred(String cat_id) {   /// check just one item

        RecyclerView list = itemsList.findViewWithTag(cat_id);

        getGroupsData();

        int position = -1;

        for (int i = 0; i < catSetsList.size(); i++) {

            CatSet cat = catSetsList.get(i);

            if (cat.category.id.equals(cat_id)) {
                position = i;
            }
        }

        catSetsList.get(position).dataItemsList = groupsList.get(position);

        if (position > -1) setListAdapter(catSetsList.get(position), list);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_section_review, menu);

        changeLayoutBtn = menu.findItem(R.id.list_layout);
        applyLayoutStatus();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        } else if (id == R.id.info_from_menu) {
            infoMessage();
            return true;
        } else if (id == R.id.list_layout) {
            changeLayoutAndApply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeLayoutAndApply() {

        changeLayoutStatus();

        new Handler().postDelayed(() -> {
            itemsList.removeAllViews();
            organizeSection();
        }, 500);

    }


    public void changeLayoutStatus() {

        String listType = appSettings.getString(SECTION_LIST_VIEW, CAT_LIST_VIEW_COMPACT);

        if (listType.equals(CAT_LIST_VIEW_NORM)) {
            listType = CAT_LIST_VIEW_COMPACT;
        } else {
            listType = CAT_LIST_VIEW_NORM;
        }

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(SECTION_LIST_VIEW, listType);
        editor.apply();

        new Handler().postDelayed(this::applyLayoutStatus, 700);


    }

    private void applyLayoutStatus() {

        String listType = appSettings.getString(SECTION_LIST_VIEW, CAT_LIST_VIEW_COMPACT);

        if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
        } else if (listType.equals(CAT_LIST_VIEW_NORM)) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);

        }
    }


    private void infoMessage() {
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_review_txt));
    }



}
