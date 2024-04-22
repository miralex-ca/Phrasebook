package com.online.languages.study.lang.presentation;

import static com.online.languages.study.lang.Constants.ACTION_ARCHIVE;
import static com.online.languages.study.lang.Constants.ACTION_CHANGE_ORDER;
import static com.online.languages.study.lang.Constants.ACTION_EDIT_GROUP;
import static com.online.languages.study.lang.Constants.ACTION_MOVE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.GROUPS_UNPAID_LIMIT;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ARCHIVE;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_PARENT;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ROOT;
import static com.online.languages.study.lang.Constants.STATUS_DELETED;
import static com.online.languages.study.lang.Constants.STATUS_NEW;
import static com.online.languages.study.lang.Constants.STATUS_NORM;
import static com.online.languages.study.lang.Constants.STATUS_UPDATED;
import static com.online.languages.study.lang.Constants.UCATS_UNPAID_LIMIT;
import static com.online.languages.study.lang.Constants.UCAT_LIST_LIMIT;
import static com.online.languages.study.lang.Constants.UC_PREFIX;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.GroupPickerAdapter;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.NewGroupDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.PremiumDialog;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.adapters.UCatsListAdapter;
import com.online.languages.study.lang.data.BookmarkItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.NavStructure;

import java.util.ArrayList;


public class UCatsListActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    RecyclerView recyclerView;

    LinearLayoutManager mLayoutManager;

    ArrayList<BookmarkItem> dataItems;

    NavStructure navStructure;

    DBHelper dbHelper;
    DataManager dataManager;

    OpenActivity openActivity;

    UCatsListAdapter adapter;
    ArrayList<DataObject> catsList;

    FloatingActionButton fab;

    String listLayout;

    MenuItem archiveMenuIcon;
    MenuItem newGroupMenuIcon;
    MenuItem editGroupMenuIcon;
    MenuItem deleteGroupMenuIcon;

    boolean cutList;
    RelativeLayout helperView;

    String uGroup;
    AlertDialog alert;
    GroupPickerAdapter groupPickerAdapter;
    RecyclerView groupList;
    ArrayList<DataObject> groupsDataList;
    int groupIndexSelected;


    boolean typeGroup;
    DataObject groupObject;

    NewGroupDialog newGroupDialog;
    InfoDialog infoDialog;

    View emptyMsg ;
    boolean listIsEmpty;
    int archiveListSize = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ucat_list);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        listIsEmpty = false;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uGroup = getIntent().getStringExtra(EXTRA_CAT_ID);
        infoDialog = new InfoDialog(this);

        setTitle(R.string.user_vocabulary_title);

        cutList = true;
        typeGroup = false;

        helperView = findViewById(R.id.list_wrapper);

        emptyMsg = findViewById(R.id.empty_list_msg);


        dataManager = new DataManager(this, 1);
        dbHelper = dataManager.dbHelper;
        navStructure = dataManager.getNavStructure();

        dataManager.plus_Version = dataManager.checkPlusVersion();

        recyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        updateList();

        if (!uGroup.equals(PARAM_UCAT_ROOT)) {
            typeGroup = true;
            setGroupData();
        }

        fab = findViewById(R.id.fab_add);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              if (!typeGroup)  fab.show();
            }
        }, 350);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewCat();
            }
        });


        openListView();

        if (!dataManager.plus_Version) {
            ((TextView)findViewById(R.id.newTopicHint)).setText(R.string.new_topic_hint_unpaid);
            ((TextView)findViewById(R.id.newSectionHint)).setText(R.string.new_section_hint_unpaid);
        }



    }

    private void setGroupData() {

        groupObject = dataManager.getGroupData(uGroup);
        setTitle(groupObject.title);

    }


    private void openListView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                helperView.setVisibility(View.VISIBLE);

            }
        }, 30);

    }


    public void openCompleteList(View view) {
        cutList = false;
        updateList();
        helperView.clearAnimation();
        setWrapContentHeight(helperView);
    }


    public void updateList() {
        catsList  = getCatList();
        adapter = new UCatsListAdapter(this, catsList, this);
        recyclerView.setAdapter(adapter);
        checkArchiveIcon();
    }


    public ArrayList<DataObject> getCatList() {

        setWrapContentHeight(helperView);

       ArrayList<DataObject> completeList;

       if (!dataManager.plus_Version) {
           completeList =   dataManager.getUcatsListForUnpaid(uGroup);
       } else {
           if (uGroup.contains(UC_PREFIX)) {
               completeList = dataManager.getUcatsGroup(uGroup);
           } else {
               completeList = dataManager.getUcatsList();
           }

       }

       ArrayList<DataObject> displayList = new ArrayList<>(completeList);

       int limit = UCAT_LIST_LIMIT;

       if (completeList.size() > limit) {
          if (cutList) displayList = new ArrayList<>(completeList.subList(0, limit));
       }

       listIsEmpty = completeList.size() == 0;

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
            lastObject.title = String.format(getString(R.string.load_more_items), String.valueOf(dif));
            lastObject.info = "show";
        } else {
            lastObject.info = "hide";
        }

        return  lastObject;

    }

    private void updateMoreIem() {
        adapter.notifyItemChanged(catsList.size()-1);

    }



    private void checkArchiveIcon() {

       int archiveSize = dataManager.getUcatsForArchive().size();

       if (! dataManager.plus_Version) {
           archiveSize = dataManager.getUcatsListForUnpaid(PARAM_UCAT_ARCHIVE).size();
           archiveListSize = archiveSize;
       }

       if (archiveMenuIcon != null) {
           if (archiveSize > 0) {
               if (!typeGroup)  archiveMenuIcon.setVisible(true);
           } else {
               archiveMenuIcon.setVisible(false);
           }
       }

        checkEmptyMsg();

    }

    private void checkEmptyMsg()  {

        if (listIsEmpty && archiveListSize <1) {
            emptyMsg.setAlpha(0.0f);
            emptyMsg.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    emptyMsg.animate().alpha(1.0f).setDuration(150);
                }
            }, 50);

        }
        else {
            emptyMsg.setVisibility(View.GONE);
            emptyMsg.setAlpha(0.0f);
        }
        if (typeGroup) {
            emptyMsg.setVisibility(View.GONE);
            emptyMsg.setAlpha(0.0f);

        }

    }



    private void listLayoutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        listLayout = appSettings.getString("set_ucat_list", getString(R.string.set_ucat_layout_default));

        int checkedItem = 0;

        if (listLayout.equals("compact"))  checkedItem = 1;
        if (listLayout.equals("mixed"))  checkedItem = 2;

        builder.setTitle(getString(R.string.set_ucat_layout_dialog_title))

                .setSingleChoiceItems(R.array.set_ucat_layout_list, checkedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        saveListLayout(which);
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)

                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alert = builder.create();
        alert.show();

    }

    private void saveListLayout(int num) {

        String orderValue = getResources().getStringArray(R.array.set_ucat_layout_values)[0];
        if (num == 1) orderValue  = getResources().getStringArray(R.array.set_ucat_layout_values)[1];
        if (num == 2) orderValue  = getResources().getStringArray(R.array.set_ucat_layout_values)[2];

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString("set_ucat_list", orderValue);
        editor.apply();

        updateList();

    }



    public void openMyCat(DataObject dataObject) {

        if (dataObject.type.equals(PARAM_GROUP)) {
            openGroup(dataObject);
            return;
        }

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

    public void openGroup(DataObject dataObject) {

        Intent i = new Intent(this, UCatsListActivity.class);

        i.putExtra(EXTRA_CAT_ID, dataObject.id);

        startActivityForResult(i, 10);

        openActivity.pageTransition();
    }


    public void performAction(final DataObject dataObject, String type) {

        if (type.equals(ACTION_UPDATE)) openCatEdit(dataObject);

        if (type.equals(ACTION_CHANGE_ORDER)) {
            dataManager.dbHelper.updateUCatSortTime(dataObject);
            checkListAnimation();
        }

        if (type.equals(ACTION_ARCHIVE)) archiveCat(dataObject);

        if (type.equals(ACTION_MOVE)) moveToGroup(dataObject);

        if (type.equals(ACTION_EDIT_GROUP)) editGroupFromList(dataObject);

        checkArchiveIcon();

    }

    public void archiveCat(DataObject dataObject) {

        DataObject data = new DataObject();
        data.id = dataObject.id;
        dataManager.dbHelper.archiveUCat(dataObject);
        checkListAnimation();
    }

    public void moveToGroup(DataObject dataObject) {
        buildDialog(dataObject);
    }


    public ArrayList<DataObject> getGroups() {

        // Toast.makeText(this, "Groups: " + groups.size(), Toast.LENGTH_SHORT).show();

        return dataManager.getGroupsForDialog(uGroup);
        

    }



    public void buildDialog(final DataObject categoryObject) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_move_group, null);


        groupList = content.findViewById(R.id.recycler_view);

        groupsDataList = getGroups();



        groupIndexSelected = -1;

        groupPickerAdapter = new GroupPickerAdapter(this, groupsDataList, groupIndexSelected);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);

        groupList.setLayoutManager(mLayoutManager);

        groupList.setAdapter( groupPickerAdapter);

        ViewCompat.setNestedScrollingEnabled(groupList, false);

        if (groupsDataList.size() < 1) {
            View noGroups = content.findViewById(R.id.noGroups);
            noGroups.setVisibility(View.VISIBLE);
        }

        dialog.setView(content);

        if (groupsDataList.size() < 1) {

            dialog.setNegativeButton(R.string.dialog_close_txt,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        } else {

            dialog.setNegativeButton(R.string.cancel_txt,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            dialog.setPositiveButton(R.string.move_to_section_btn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (groupIndexSelected > -1) {
                                moveCategoryToGroup(categoryObject, groupsDataList.get(groupIndexSelected));
                            }

                            dialog.cancel();
                        }
                    });
        }



        alert = dialog.create();

        alert.show();

        Button yesButton =  alert.getButton(AlertDialog.BUTTON_POSITIVE);
        yesButton.setEnabled(false);


        Button createBtn = content.findViewById(R.id.createGroupBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alert.dismiss();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newGroup();
                    }
                }, 150);

            }
        });


    }

    private void moveCategoryToGroup(DataObject categoryObject, DataObject groupObject) {


        dataManager.dbHelper.parentUCat(categoryObject, groupObject.id);

        DataObject updateGroupObject = dataManager.getGroupData(groupObject.id);

        int count = updateGroupObject.count;

        for (int i = 0; i < catsList.size(); i ++) {

            DataObject cat = catsList.get(i);

            if (cat.id.equals(groupObject.id)) {
                catsList.get(i).count = count;
            }

            adapter.notifyItemChanged(i);

        }

        checkListAnimation();

    }


    public void checkGroup(View view) {

        View icon = view.findViewById(R.id.icon);

        int t = (int) icon.getTag();

        if (t > -1) {
            Button yesButton =  alert.getButton(AlertDialog.BUTTON_POSITIVE);
            yesButton.setEnabled(true);
        }

        groupIndexSelected = t;

        groupPickerAdapter = new GroupPickerAdapter(this, groupsDataList, t);
        groupList .setAdapter(groupPickerAdapter);

    }



    private static int dpToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }


    public boolean bookmarkCat(DataObject dataObject) {

            dataManager.setBookmark(dataObject.id, PARAM_UCAT_PARENT, navStructure);

            return dataManager.dbHelper.checkBookmark(dataObject.id, PARAM_UCAT_PARENT);
    }


    private void checkListAnimation() {

        ArrayList<DataObject> newCatlist = getCatList();

        checkEmptyMsg();

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
        }, 450);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int h = recycler.getHeight();
                ResizeHeight resizeHeight = new ResizeHeight(helper, h);
                resizeHeight.setDuration(300);
                helper.startAnimation(resizeHeight);


            }
        }, 550);

    }


    private void setWrapContentHeight(View view) { //// should aply to the target view parent

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(params);

    }

    public void openNewCat(View view) {
        openNewCat( );
    }


    public void openNewCat( ) {


        if (!dataManager.plus_Version) {

            String[] countsVaules = dataManager.getTotalCounts();

            int listSize = Integer.parseInt(countsVaules[0]);

            if (listSize >= UCATS_UNPAID_LIMIT) {

                PremiumDialog premiumDialog = new PremiumDialog(this);
                premiumDialog.createDialog(getString(R.string.plus_version_btn), getString(R.string.limit_cats_for_unpaid));

            } else {
                createNewCat( );
            }


        } else {

            createNewCat( );

        }
    }

    public void createNewCat( ) {
        Intent i = new Intent(this, MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, "new");
        startActivityForResult(i, 10);

    }

    public void createNewGroup(DataObject groupObject ) {

            dataManager.dbHelper.createGroup( groupObject );
            checkListAnimation();

    }


    public void updateGroup(DataObject groupObject) {
        dataManager.dbHelper.updateGroup( groupObject );
        setGroupData();
    }

    public void updateGroupFromList(DataObject groupObject) {

        dataManager.dbHelper.updateGroup( groupObject );

        DataObject updateGroupObject = dataManager.getGroupData(groupObject.id);

        for (int i = 0; i < catsList.size(); i ++) {

            DataObject cat = catsList.get(i);

            if (cat.id.equals(groupObject.id)) {
                catsList.get(i).title = updateGroupObject.title;
                catsList.get(i).desc = updateGroupObject.desc;
                catsList.get(i).image = updateGroupObject.image;

                adapter.notifyItemChanged(i);
            }

        }

    }

    public void openCatEdit(DataObject dataObject) {
        Intent i = new Intent(this, MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, dataObject.id);
        startActivityForResult(i, 10);
    }


    public void openArchive() {
        Intent i = new Intent(this, UCatsArchiveActivity.class);
        startActivityForResult(i, 10);
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
        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        } else if (id == R.id.archive_item) {
            openArchive();
            return true;
        } else if (id == R.id.archive_icon) {
            openArchive();
            return true;
        } else if (id == R.id.menu_ucat_layout) {
            listLayoutDialog();
            return true;
        } else if (id == R.id.info_item) {
            showInfoDialog();
            return true;
        } else if (id == R.id.new_group) {
            newGroup();
            return true;
        } else if (id == R.id.new_group_tool) {
            newGroup();
            return true;
        } else if (id == R.id.edit_group) {
            editGroup();
            return true;
        } else if (id == R.id.delete_group) {
            deleteGroup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void newGroup(View view) {
        newGroup();
    }

    private void newGroup() {

        int count = dataManager.getGroupsCount();

        if (!dataManager.plus_Version && count >= GROUPS_UNPAID_LIMIT  ) {

            //infoDialog.simpleDialog(getString(R.string.create_dialog_unpaid_title), getString(R.string.create_dialog_unpaid_txt));

            PremiumDialog premiumDialog = new PremiumDialog(this);
            premiumDialog.createDialog(getString(R.string.create_dialog_unpaid_title), getString(R.string.limit_groups_for_unpaid));

        } else {

            newGroupDialog = new NewGroupDialog(this, UCatsListActivity.this);
            newGroupDialog.showCustomDialog(getString(R.string.create_group_title));

        }
    }

    private void editGroup() {

        newGroupDialog = new NewGroupDialog(this, UCatsListActivity.this);

        newGroupDialog.showCustomDialog(getString(R.string.edit_group_title), ACTION_UPDATE, groupObject );

    }

    private void editGroupFromList(DataObject group) {

        newGroupDialog = new NewGroupDialog(this, UCatsListActivity.this);

        newGroupDialog.showCustomDialog(getString(R.string.edit_group_title), ACTION_EDIT_GROUP, group );

    }


    public void checkPic(View view) {

        View icon = view.findViewById(R.id.icon);

        int t = (int) icon.getTag();

        newGroupDialog.updateList( t);

    }

    private void deleteGroup() {


        DataObject group = dataManager.getGroupData(uGroup);

        if (group.count > 0) {
            infoDialog.simpleDialog(getString(R.string.delete_group_title), getString(R.string.delete_group_msg));
        } else {

            confirmDeletion(uGroup);

        }

    }


    private void confirmDeletion(final String id) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirmation_txt);
        builder.setMessage(R.string.delete_groupt_confirm);

        builder.setCancelable(false);

        builder.setPositiveButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteUCat(id);
            }
        });

        builder.setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }


    private void deleteUCat(String id) {

        int num =  dataManager.dbHelper.deleteUCat(id);

        Toast.makeText(this, R.string.delete_process , Toast.LENGTH_SHORT).show();

        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ucat_list, menu);

        archiveMenuIcon = menu.findItem(R.id.archive_icon);
        newGroupMenuIcon = menu.findItem(R.id.new_group);
        editGroupMenuIcon = menu.findItem(R.id.edit_group);
        deleteGroupMenuIcon = menu.findItem(R.id.delete_group);

        MenuItem archiveMenuItem = menu.findItem(R.id.archive_item);

        if (typeGroup) {
            menu.findItem(R.id.new_group_tool).setVisible(false);
            archiveMenuItem.setVisible(false);
            newGroupMenuIcon.setVisible(false);
            editGroupMenuIcon.setVisible(true);
            deleteGroupMenuIcon.setVisible(true);
        }

        checkArchiveIcon();
        return true;
    }

    public void showInfoDialog() {
        DataModeDialog dataModeDialog = new DataModeDialog(this);
        String info = getString(R.string.info_ucat_list); /// TODO check description
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
