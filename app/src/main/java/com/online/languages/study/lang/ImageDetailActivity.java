package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DetailItem;
import com.squareup.picasso.Picasso;

import static com.online.languages.study.lang.Constants.GALLERY_TAG;

public class ImageDetailActivity extends BaseActivity {


    DetailItem detailItem;

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    DBHelper dbHelper;
    int itemPostion = 0;

    Boolean starrable = false;

    Boolean starStatusChanged = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, 2);
        themeAdapter.getTheme();

        itemPostion = getIntent().getIntExtra("position", 0);

        //starrable = getIntent().getBooleanExtra("starrable", false);


        if (appSettings.getBoolean(Constants.SET_VERSION_TXT, false)) starrable = true;


        starrable = true;

        starStatusChanged= false;

        setContentView(R.layout.activity_image_detail);


        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String tag = getIntent().getStringExtra("id");

        DataManager dataManager = new DataManager(this);

        detailItem = dataManager.getDetailFromDB(tag);


        if (detailItem.title.equals("not found")) {
            DataItem dataItem =  dataManager.getDataItemFromDB(tag);
            detailItem  = new DetailItem(dataItem);
        }

        TextView imgTitle = findViewById(R.id.lbl_img_title);
        TextView infoT = findViewById(R.id.lbl_text);


        View appbar = findViewById(R.id.app_bar);
        View screem = findViewById(R.id.screem_btm);

        View coordiinator = findViewById(R.id.coordinator);

        imgTitle.setText(detailItem.title);

        if (detailItem.image.equals("none")) {

            coordiinator.getLayoutParams().height = convertDimen(380);
            coordiinator.setLayoutParams(coordiinator.getLayoutParams());
            appbar.getLayoutParams().height = convertDimen(130);
            appbar.setLayoutParams(appbar.getLayoutParams());
            screem.setVisibility(View.GONE);
        }


        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);

        final FloatingActionButton fab = findViewById(R.id.fab);

        if (starrable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.show();
                }
            }, 350);
        }


        itemPostion = getIntent().getIntExtra("position", 0);


        collapsingToolbar.setTitle(" ");


        infoT.setText(detailItem.desc);

        infoT.setTextSize(getInfoTxtSize());



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


        checkStarStatus(detailItem.id, fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeStarStatus(detailItem.id, fab);
                starStatusChanged = true;

                fab.hide();
                fab.show();

            }
        });
    }

    private int getInfoTxtSize() {

        int infoSize = getResources().getInteger(R.integer.detail_text_size_small);
        String txtVal = appSettings.getString("detail_txt_size", "small");
        if (txtVal.equals("medium")) infoSize = getResources().getInteger(R.integer.detail_text_size_medium);
        if (txtVal.equals("large")) infoSize = getResources().getInteger(R.integer.detail_text_size_large);

        return infoSize;
    }



    public void openImage() {

        Intent i;
        i = new Intent(ImageDetailActivity.this, MapActivity.class);
        i.putExtra("page_id", detailItem.id);
        i.putExtra("pic", 1);

        startActivityForResult(i, 1);
        overridePendingTransition(R.anim.fade_in_img, R.anim.fade_out_img);

    }


    public void pageTransition() {
        if ( !getResources().getBoolean(R.bool.wide_width)) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                if ( !getResources().getBoolean(R.bool.wide_width)) {
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                return true;

            case R.id.fullscreen:
                openImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        returnIntent.putExtra("result", detailItem.id);

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

        int status = dbHelper.setStarred(tag, !starred, GALLERY_TAG); // id to id

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

            button.setTag("starred");

        } else {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //  button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_border));
                d = this.getDrawable(starIconInactive);
            }else{
                d = VectorDrawableCompat.create(this.getResources(), R.drawable.ic_star_border_older, null);
            }
        }

        button.setImageDrawable(d);
        button.setTag("not");
    }


    private Boolean checkStarred(String text) {

        return dbHelper.checkStarred(text);

    }


}
