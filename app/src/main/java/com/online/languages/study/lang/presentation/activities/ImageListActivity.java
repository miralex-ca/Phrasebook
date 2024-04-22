package com.online.languages.study.lang.presentation.activities;

import static com.online.languages.study.lang.Constants.EX_IMG_TYPE;
import static com.online.languages.study.lang.Constants.GALLERY_TAG;
import static com.online.languages.study.lang.Constants.IMG_LIST_LAYOUT;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.adapters.ImageListAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.ImageData;
import com.online.languages.study.lang.data.ImageMapsData;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.data.ViewSection;
import com.online.languages.study.lang.presentation.core.BaseActivity;
import com.online.languages.study.lang.presentation.exercise.ExerciseActivity;

import java.util.ArrayList;

public class ImageListActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    RecyclerView recyclerView;
    RecyclerView recyclerViewCards;
    RecyclerView recyclerViewImages;
    ImageListAdapter mAdapter;
    ImageListAdapter cardsAdapter;
    ImageListAdapter imagesAdapter;

    RecyclerView.LayoutManager mLayoutManager;
    GridLayoutManager cardsManager;
    GridLayoutManager imagesManager;

    NavStructure navStructure;

    String tSectionID = "01010";
    String tCatID = "01010";

    ViewSection viewSection;

    NavSection navSection;

    Boolean full_version;

    ImageMapsData imageMapsData;
    ArrayList<DataItem> dataItems;

    RelativeLayout itemsList, cardsList, imagesList, itemListWrap;

    private MenuItem changeLayoutBtn;

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
        setContentView(R.layout.activity_images_list);

        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        listType = appSettings.getInt(IMG_LIST_LAYOUT, 3); /// default - 3 image grid



        openActivity = new OpenActivity(this);
        openActivity.setOrientation();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemsList = findViewById(R.id.items_list);
        itemListWrap = findViewById(R.id.itemListWrap);

        cardsList = findViewById(R.id.cards_list);
        imagesList = findViewById(R.id.images_list);


        dataManager = new DataManager(this, 1);
        dbHelper = dataManager.dbHelper;

        imageMapsData = new ImageMapsData(this);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        tCatID = getIntent().getStringExtra(Constants.EXTRA_CAT_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);
        viewSection = new ViewSection(this, navSection, tCatID);

        setTitle(getIntent().getStringExtra("title"));

        getImages();

        if (getListType().equals(STARRED)) setTitle(String.format(getString(R.string.starred_pic_title), dataItems.size()));

        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new ImageListAdapter(this, dataItems, 1, themeTitle);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration( new DividerItemDecoration(this) );
        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        /// cards list layout
        recyclerViewCards = findViewById(R.id.recycler_view_cards);
        cardsAdapter = new ImageListAdapter(this, dataItems, 2, themeTitle);
        cardsManager = new GridLayoutManager(this, 2);

        recyclerViewCards.setLayoutManager(cardsManager);
        recyclerViewCards.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCards.setSelected(true);
        recyclerViewCards.setAdapter(cardsAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCards, false);

        // images list layout
        recyclerViewImages = findViewById(R.id.recycler_view_images);
        imagesAdapter = new ImageListAdapter(this, dataItems, 3, themeTitle);
        imagesManager = new GridLayoutManager(this, 3);
        recyclerViewImages.setLayoutManager(imagesManager);
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setSelected(true);
        recyclerViewImages.setAdapter(imagesAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCards, false);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) { openCat(view); }
            @Override
            public void onLongClick(View view, int position) { longClick(view); }
        }));

        recyclerViewCards.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewCards, new ClickListener() {
            @Override
            public void onClick(View view, int position) { openCat(view);}
            @Override
            public void onLongClick(View view, int position) { longClick(view); }
        }));

        recyclerViewImages.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewImages, new ClickListener() {
            @Override
            public void onClick(View view, int position) { openCat(view); }
            @Override
            public void onLongClick(View view, int position) { longClick(view); }
        }));
    }



    private void longClick(View view) {
            changeStarred(view);
    }


    private void checkItemById(String id) {

        int position = -1;

        boolean starred = dataManager.checkStarStatusById(id);

        for (int i=0; i < dataItems.size() ; i++ ) {
            if (dataItems.get(i).id.equals(id))  {
                if (starred ) dataItems.get(i).starred = 1;
                else dataItems.get(i).starred = 0;
                position =i;
                break;
            }
        }

        if (position != -1) {
            updateStarInList(mLayoutManager, position, starred);
            updateStarInList(cardsManager, position, starred);
            updateStarInList(imagesManager, position, starred);
        }
    }


    private void updateStarInList(RecyclerView.LayoutManager manager, int position, boolean display) {
        View p = manager.findViewByPosition(position);
        updateStarIcon(p, display);
    }



    private void updateStarIcon(View parent, boolean display) {
        if (parent != null ) {
            View star = parent.findViewById(R.id.starIcon);
            if (display) {
                star.setAlpha(0f);
                star.setVisibility(View.VISIBLE);
                star.animate().alpha(1f).setDuration(150);
            }
            else {
                star.animate().alpha(0f).setDuration(150);
            }
        }
    }



    public void changeStarred(View view){   /// check just one item


        View tagged = view.findViewById(R.id.tagged);
        String id = (String) tagged.getTag();

        boolean starred = dataManager.checkStarStatusById(id );

        int status = dataManager.dbHelper.setStarred(id, !starred, GALLERY_TAG);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        int vibLen = 30;

        if (status == 0) {
            Toast.makeText(this, R.string.starred_limit, Toast.LENGTH_SHORT).show();
            vibLen = 300;
        }


        if (getListType().equals(STARRED)) findRemoved(dataItems);
        else checkItemById(id);


        assert v != null;
        v.vibrate(vibLen);
    }


    public String getListType () {
        return PIC_LIST;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                final String id = data.getStringExtra("result");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getListType().equals(STARRED)) findRemoved(dataItems);
                        else  checkItemById(id);
                    }
                }, 150);
            }
        }


    }

    private void setWrapContentHeight(View view) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

         view.setLayoutParams(params);

    }


    private void findRemoved(ArrayList<DataItem> wordList) {

        if (wordList != null) {

            wordList = dbHelper.checkStarredList(wordList);

            if (listType == 1) {
                removeFromList(wordList, recyclerView, itemListWrap, mAdapter);
                cardsAdapter.notifyDataSetChanged();
                imagesAdapter.notifyDataSetChanged();

                setWrapContentHeight(cardsList);
                setWrapContentHeight(imagesList);
            }

            if (listType == 2) {
                removeFromList(wordList, recyclerViewCards, cardsList, cardsAdapter);
                mAdapter.notifyDataSetChanged();
                imagesAdapter.notifyDataSetChanged();

                setWrapContentHeight(itemListWrap);
                setWrapContentHeight(imagesList);
            }

            if (listType == 3) {
                removeFromList(wordList, recyclerViewImages, imagesList, imagesAdapter);
                cardsAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
                setWrapContentHeight(cardsList);
                setWrapContentHeight(itemListWrap);
            }

            setTitle(String.format(getString(R.string.starred_pic_title), dataItems.size()));
        }
    }


    private void removeFromList(ArrayList<DataItem> wordList, RecyclerView recyclerView, View helper, ImageListAdapter adapter) {

        helper.setMinimumHeight(recyclerView.getHeight());  /// TODO lisst

        for (int i = 0; i < wordList.size(); i++) {
            if (wordList.get(i).starred < 1) {

                try {
                    int count = recyclerView.getChildCount();

                    if (count > 0) {
                        setHR(recyclerView, helper);
                    }
                } finally {
                    adapter.remove(i);
                }
            }
        }
    }


    private void setHR(final RecyclerView recycler, final View helper) {

        recycler.setMinimumHeight(recycler.getHeight());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                recyclerView.setMinimumHeight(0);
                recyclerViewCards.setMinimumHeight(0);
                recyclerViewImages.setMinimumHeight(0);

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

        dataItems = getDataItems();

        for (ViewCategory cat: viewSection.categories) {
            ImageData image = imageMapsData.getMapInfoById(cat.id);
            cat.image  = image.image;
        }
    }

    public ArrayList<DataItem> getDataItems() {
        return dataManager.getCatDBList(tCatID);
    }


    private void changeLayoutStatus() {

        if (listType == 3) {
            listType = 2;
        } else if (listType == 2) {
            listType = 1;
        } else {
            listType = 3;
        }

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(IMG_LIST_LAYOUT, listType);
        editor.apply();
        applyLayoutStatus(listType);
    }

    private void applyLayoutStatus(int type) {

        setWrapContentHeight(itemListWrap);
        setWrapContentHeight(imagesList);
        setWrapContentHeight(cardsList);

        itemListWrap.setMinimumHeight(0);
        imagesList.setMinimumHeight(0);
        cardsList.setMinimumHeight(0);

        if (type == 1) {
            imagesList.setVisibility(View.GONE);
            cardsList.setVisibility(View.GONE);
            itemsList.setVisibility(View.VISIBLE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconGrid));
        } else if (type == 2) {
            imagesList.setVisibility(View.GONE);
            itemsList.setVisibility(View.GONE);
            cardsList.setVisibility(View.VISIBLE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconList));
        } else {
            cardsList.setVisibility(View.GONE);
            itemsList.setVisibility(View.GONE);
            imagesList.setVisibility(View.VISIBLE);
            changeLayoutBtn.setIcon(getDrawableIcon(R.attr.iconGrid2));
        }

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

        // String id = view.getTag().toString();


        int position = 0;

        for (int i = 0; i < dataItems.size(); i++) {
            if (dataItems.get(i).id.equals(id)) position  = i;
        }


        ViewGroup p = (ViewGroup) view.getParent();

        View image = p.findViewById(R.id.mapImage);


        if (id.equals("divider")) return;

        Intent intent = new Intent(ImageListActivity.this, ImageDetailActivity.class);

        intent.putExtra("starrable", false);

        intent.putExtra("id", id );
        intent.putExtra("position", position);

         startActivityForResult(intent, 1);
         overridePendingTransition(R.anim.slide_in_down, 0);

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

            case R.id.test_from_menu:
                openTest();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps_list, menu);

        changeLayoutBtn =  menu.findItem(R.id.list_layout);


        String param = navStructure.getNavCatFromSection(tSectionID, tCatID).param;

        if (param.equals("img_test")) {
            MenuItem testMenu = menu.findItem(R.id.test_from_menu);
            testMenu.setVisible(true);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                applyLayoutStatus(listType);
            }
        }, 80);

        return true;
    }


    public void openTest() {

        Intent i = new Intent(this, ExerciseActivity.class) ;

        i.putExtra("ex_type", EX_IMG_TYPE);

        i.putExtra(Constants.EXTRA_CAT_TAG, tCatID);

        i.putParcelableArrayListExtra("dataItems", dataItems);

        startActivityForResult(i,2);
        openActivity.pageTransition();
    }


    public void showInfoDialog() {
        DataModeDialog dataModeDialog = new DataModeDialog(this);

        String info = getString(R.string.info_star_txt);

        if (getListType().equals(STARRED)) info = getString(R.string.info_img_starred);

        dataModeDialog.createDialog(getString(R.string.info_txt), info);
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
