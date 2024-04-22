package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.fragments.GalleryFragment;
import com.online.languages.study.lang.presentation.BaseActivity;

import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.GALLERY_REQUESTCODE;


public class GalleryActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;

    NavStructure navStructure;

    String tSectionID = "01010";
    String tCatID = "01010";

    NavSection navSection;

    GalleryFragment galleryFragment;
    FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(EXTRA_SECTION_ID);
        tCatID = getIntent().getStringExtra(EXTRA_CAT_ID);

        String title;

        if (tCatID.equals("root")) {
           title =  navStructure.getNavSectionByID(tSectionID).title;
        } else {
            title = navStructure.getNavCatFromSection(tSectionID, tCatID).title;
        }

        setTitle(title);

        galleryFragment = new GalleryFragment();

        Bundle bundle = new Bundle();

        bundle.putParcelable("structure", navStructure);
        bundle.putString(EXTRA_SECTION_ID, tSectionID);
        bundle.putString(EXTRA_CAT_ID, tCatID);

        galleryFragment.setArguments(bundle);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, galleryFragment, "gallery").commit();


    }


    public void openCatFromGallery(final View view) {
        ViewGroup v = (ViewGroup) view.getParent();
        View tagged = v.findViewById(R.id.tagged);
        final String tag = (String) tagged.getTag();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                GalleryFragment fragment = (GalleryFragment)mFragmentManager.findFragmentByTag("gallery");
                if (fragment!=null) fragment.openCatActivity(tag);

            }
        }, 50);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // updateContent();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUESTCODE) {

            GalleryFragment fragment = (GalleryFragment)mFragmentManager.findFragmentByTag("gallery");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }

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
            case R.id.info_from_menu:
                showInfoDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery_activity, menu);
        return true;
    }


    public void showInfoDialog() {
        DataModeDialog dataModeDialog = new DataModeDialog(this);
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_gallery_txt));
    }



}
