package com.online.languages.study.lang;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DetailFromJson;
import com.online.languages.study.lang.data.DetailItem;
import com.squareup.picasso.Picasso;


public class CatInfoDetailActivity extends BaseActivity {   //// TODO check to remove

    LinearLayout box;
    View mask;
    DetailItem detailItem;

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_info_dialog1);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        TextView infoT = findViewById(R.id.lbl_text);
        TextView imgIfo = findViewById(R.id.lbl_img_desc);

        box = findViewById(R.id.box);
        mask = findViewById(R.id.mask);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.collapsing_toolbar);


        String tag = getIntent().getStringExtra("id");

        detailItem = new DetailFromJson(this, tag).detail;

        collapsingToolbar.setTitle(detailItem.title);

        infoT.setText(detailItem.desc);
        imgIfo.setText(String.format(getString(R.string.pic_name_label), detailItem.img_info));


        ImageView placePicutre = findViewById(R.id.image);

        //*/
        Picasso.with(this)
                //.load(R.drawable.f)
                //.load(R.raw.e)
                .load("file:///android_asset/pics/"+detailItem.image)
                .fit()
                .centerCrop()
                .into(placePicutre);

        //*/

        if ( getResources().getBoolean(R.bool.tablet) || (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) ) {
            introAnimation();
        }
    }


    private void introAnimation() {
        box.setY(300.0f);
        box.setAlpha(0.0f);
        box.animate().
                translationY(0.0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        box.animate().alpha(1.0f).setDuration(300).start();

        mask.setAlpha(0.0f);
        mask.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void exitAnimation() {
        box.animate().
                translationY(1300.0f)
                .setDuration(250)
                .setInterpolator(new FastOutLinearInInterpolator())
                .start();

        box.animate().alpha(0.0f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .start();

        mask.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finish();
                        overridePendingTransition(0,0);
                    }
                })
                .start();
    }


    public void dialogClose(View view) {
        exitAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                exitAnimation();
                return true;
            case R.id.exit_from_menu:
                exitAnimation();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitAnimation();
    }
}
