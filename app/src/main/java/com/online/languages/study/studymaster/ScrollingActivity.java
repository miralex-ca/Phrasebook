package com.online.languages.study.studymaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.studymaster.adapters.ThemeAdapter;
import com.online.languages.study.studymaster.adapters.Typewriter;
import com.online.languages.study.studymaster.data.DataFromJson;
import com.online.languages.study.studymaster.data.DataItem;
import com.online.languages.study.studymaster.data.DataManager;
import com.online.languages.study.studymaster.data.DetailFromJson;
import com.online.languages.study.studymaster.data.DetailItem;
import com.squareup.picasso.Picasso;

import static com.online.languages.study.studymaster.Constants.GALLERY_TAG;
import static com.online.languages.study.studymaster.Constants.INFO_TAG;

public class ScrollingActivity extends BaseActivity {


    DetailItem detailItem;
    DataItem dataItem;

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    DBHelper dbHelper;
    int itemPostion = 0;

    Boolean starrable = false;
    Boolean starStatusChanged = false;
    int sourceType;

    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, 2);
        themeAdapter.getTheme();

        itemPostion = getIntent().getIntExtra("position", 0);

        starrable = getIntent().getBooleanExtra("starrable", false);


        sourceType = getIntent().getIntExtra("source", 0); // 0 - list, 1 - search



        if (appSettings.getBoolean(Constants.SET_VERSION_TXT, false)) starrable = true;

        starStatusChanged= false;

        setContentView(R.layout.activity_detail);

        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String tag = getIntent().getStringExtra("id");


        final DataManager dataManager = new DataManager(this);

        detailItem = dataManager.getDetailFromDB(tag);

        dataItem = dbHelper.getDataItemById(tag);


        if (detailItem.title.equals("not found")) {
            DataItem dataItem =  dataManager.getDataItemFromDB(tag);
            detailItem  = new DetailItem(dataItem);
            sourceType = 1; // the same height as in search
        }


        TextView infoT = findViewById(R.id.lbl_text);
        TextView imgIfo = findViewById(R.id.lbl_img_desc);

        View appbar = findViewById(R.id.app_bar);
        View screem = findViewById(R.id.screem_btm);
        View coordinator = findViewById(R.id.coordinator);


        if (sourceType==1) {
            int dialogHeight = getResources().getInteger(R.integer.search_dialog_height);
            int imgHeight = getResources().getInteger(R.integer.search_dialog_img_height);
            changeDialogSize(coordinator, appbar, dialogHeight, imgHeight);
        }

        if (detailItem.image.equals("none")) {
            changeDialogSize(coordinator, appbar, 380, 130);
            screem.setVisibility(View.GONE);
            imgIfo.setVisibility(View.GONE);
        }


        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);


        floatingActionButton = findViewById(R.id.fab);

        final boolean inStarred = checkStarred(tag);


        if (starrable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if ( dataItem.filter.contains(INFO_TAG) && !inStarred ) { }
                    else  floatingActionButton.show();
                }
            }, 350);
        }


        itemPostion = getIntent().getIntExtra("position", 0);

        manageTitle(collapsingToolbar, detailItem.title);

        infoT.setText(detailItem.desc);

        infoT.setTextSize(getInfoTxtSize());

        imgIfo.setText(String.format(getString(R.string.detail_img_info_txt), detailItem.img_info));

        if (detailItem.img_info.equals("")) {
            imgIfo.setVisibility(View.INVISIBLE);
        }

        ImageView placePicutre = findViewById(R.id.image);

        //*/
        Picasso.with(this)
                //.load(R.drawable.f)
                //.load(R.raw.e)
                .load("file:///android_asset/pics/"+detailItem.image)
                .fit()
                .centerCrop()
                .into(placePicutre);

        if (themeTitle.equals("westworld")) {
            if (Constants.DEBUG)  placePicutre.setColorFilter(Color.argb(255, 50, 255, 240), PorterDuff.Mode.MULTIPLY);
        }

        checkStarStatus(detailItem.id, floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeStarStatus(detailItem.id, floatingActionButton);
                starStatusChanged = true;

                floatingActionButton.hide();
                floatingActionButton.show();

            }
        });
    }


    private void manageTitle(CollapsingToolbarLayout collapsingToolbar, String title) {

        TextView textHelper = collapsingToolbar.findViewById(R.id.titleHelper);

        if (title.equals("")) {
            collapsingToolbar.setTitle(" ");
        } else {
            collapsingToolbar.setTitle(title);
        }


        if (title.length() > 20) collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpTextSmall);

        if (title.length() > 25) {

            collapsingToolbar.setExpandedTitleTextColor(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
            textHelper.setText(title);
            textHelper.setVisibility(View.VISIBLE);
        }

    }

    private void changeDialogSize(View coordinator, View appbar, int coordHeight, int barHeight) {

        appbar.getLayoutParams().height = convertDimen(barHeight);
        appbar.setLayoutParams(appbar.getLayoutParams());

        coordinator.getLayoutParams().height = convertDimen(coordHeight);
        coordinator.setLayoutParams(coordinator.getLayoutParams());

    }



    private int getInfoTxtSize() {

        int infoSize = getResources().getInteger(R.integer.detail_text_size_small);
        String txtVal = appSettings.getString("detail_txt_size", "small");

        if (txtVal.equals("medium")) infoSize = getResources().getInteger(R.integer.detail_text_size_medium);
        if (txtVal.equals("large")) infoSize = getResources().getInteger(R.integer.detail_text_size_large);

        return infoSize;
    }


    public int convertDimen(int dimen) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, getResources().getDisplayMetrics());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", itemPostion);

        if (starStatusChanged) {
            setResult(RESULT_OK,returnIntent);
        } else {
            setResult(RESULT_CANCELED,returnIntent);
        }

        super.finish();
        overridePendingTransition(0, R.anim.fade_out_2);
    }


    public void limitMessage() {

        Toast.makeText(this, R.string.starred_limit, Toast.LENGTH_SHORT).show();
        // Snackbar.make(mask, R.string.starred_limit, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    private void changeStarStatus(String tag, FloatingActionButton button) {

        Boolean starred = checkStarred(tag);

        String filter = "";
        if (dataItem.filter.contains(GALLERY_TAG)) filter = GALLERY_TAG;

        int status = dbHelper.setStarred(tag, !starred, filter); // id to id

        if (status == 0) {
            limitMessage();
        }

        checkStarStatus(tag, button);
    }


    private void checkStarStatus(String tag, FloatingActionButton button) {

        Boolean starred = checkStarred(tag);

        int styleTheme = themeAdapter.styleTheme;

        TypedArray attr = getTheme().obtainStyledAttributes(styleTheme, new int[] {R.attr.starButtonIconInactive});
        int starIconInactive = attr.getResourceId(0, 0);
        attr.recycle();


        Drawable d;

        if (starred) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                d = this.getDrawable(R.drawable.ic_star_dialog);

            }else{
                d = VectorDrawableCompat.create(this.getResources(), R.drawable.ic_star_dialog_older, null);
            }

        } else {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //  button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_border));
                d = this.getDrawable(starIconInactive);
            }else{
                d = VectorDrawableCompat.create(this.getResources(), R.drawable.ic_star_border_older, null);
            }
        }


        // issue with fab icon - https://issuetracker.google.com/issues/117476935



        //Toast.makeText(this, "Test: " + starred, Toast.LENGTH_SHORT).show();
        //button.setImageResource(R.drawable.ic_star_border_older);
       // floatingActionButton.setImageResource(R.drawable.ic_star_border_older);

        final Drawable f= d;

        floatingActionButton.setImageDrawable(d);






    }


    private Boolean checkStarred(String text) {

        Boolean starred = dbHelper.checkStarred(text);
        return starred;

    }


}
