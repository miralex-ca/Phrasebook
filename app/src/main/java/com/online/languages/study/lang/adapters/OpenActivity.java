package com.online.languages.study.lang.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.CatSimpleListActivity;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.GalleryActivity;
import com.online.languages.study.lang.ImageListActivity;
import com.online.languages.study.lang.InfoListActivity;
import com.online.languages.study.lang.MapActivity;
import com.online.languages.study.lang.MapListActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.SubSectionActivity;
import com.online.languages.study.lang.TextActivity;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;

import static com.online.languages.study.lang.Constants.CAT_SPEC_MAPS;
import static com.online.languages.study.lang.Constants.CAT_SPEC_TEXT;
import static com.online.languages.study.lang.Constants.GALLERY_REQUESTCODE;

public class OpenActivity  {

    Context context;



    private int requestCode = 1;

    public OpenActivity(Context _context) {
        context = _context;
    }


    public void openCat(Intent intent, String cat_id, String title, String spec) {
        Intent i = catIntent(intent, cat_id, title, spec);
        callActivity(i);
    }


    public void openCat(String cat_id, String spec, String title) {
        Intent i = createIntent(context, CatActivity.class);
        callActivity( catIntent(i, cat_id, title, spec) );
    }


    private Intent catIntent(Intent intent, String cat_id, String title, String spec) {
        intent.putExtra(Constants.EXTRA_CAT_ID, cat_id);
        intent.putExtra("cat_title", title);
        intent.putExtra(Constants.EXTRA_CAT_SPEC, spec);
        return intent;
    }


    private void callActivity(Intent intent) {
        ((Activity) context).startActivityForResult(intent, requestCode);
        pageTransition();
        requestCode = 1;
    }


    public void setOrientation() {
        if(context.getResources().getBoolean(R.bool.portrait_only)){
            ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    public void pageTransition() {
        if ( !  context.getApplicationContext().getResources().getBoolean(R.bool.wide_width)) {
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void pageBackTransition() {
        if ( !context.getResources().getBoolean(R.bool.wide_width)) {
            ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void openSection(Intent intent, NavStructure navStructure, String section_id, String parent) {
        intent = sectionIntent(intent, navStructure, section_id, parent);
        callActivity(intent);
    }

    private Intent sectionIntent(Intent intent, NavStructure navStructure, String section_id, String parent) {
        intent.putExtra(Constants.EXTRA_SECTION_PARENT, parent);
        intent.putExtra(Constants.EXTRA_SECTION_ID, section_id);
        intent.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        return intent;
    }

    private Intent createIntent(Context packageContext, Class<?> cls) {
        return new Intent(packageContext, cls);
    }

    public void openMapList(NavStructure navStructure, String sectionID, String catID ) {
        Intent i = createIntent(context, MapListActivity.class);
        callSubActivity(i, navStructure, sectionID, catID);
    }

    public void openGallery(NavStructure navStructure, String sectionID, String catID ) {
        requestCode = GALLERY_REQUESTCODE;
        Intent i = createIntent(context, GalleryActivity.class);
        callSubActivity(i, navStructure, sectionID, catID);
    }

    public void openSubSection(NavStructure navStructure, String sectionID, String catID ) {
        Intent i = createIntent(context, SubSectionActivity.class);
        callSubActivity(i, navStructure, sectionID, catID);
    }

    private void callSubActivity(Intent intent, NavStructure navStructure, String sectionID, String catID) {
        intent.putExtra(Constants.EXTRA_CAT_ID, catID);
        intent.putExtra(Constants.EXTRA_SECTION_ID, sectionID);
        intent.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        callActivity(intent);
    }

    public void openMap(String catID) {
        Intent intent = createIntent(context, MapActivity.class);
        intent.putExtra("page_id", catID);
        callActivity(intent);
    }

    public void openCatList(NavStructure navStructure, String sectionID, String catID, String title) {

        //Intent i = createIntent(context, CatSimpleListActivity.class);
        //i.putExtra("title", title);
        //callSubActivity(i, navStructure, sectionID, catID);

        Intent i = createIntent(context, CatSimpleListActivity.class);
        callActivity( catIntent(i, catID, title, "pers") );

    }

    public void openTextPage(NavStructure navStructure, String sectionID, String catID, String title) {
        Intent i = createIntent(context, TextActivity.class);
        i.putExtra("title", title);
        callSubActivity(i, navStructure, sectionID, catID);
    }


    public void openImageList(NavStructure navStructure, String sectionID, String catID, String title) {
        Intent i = createIntent(context, ImageListActivity.class);
        i.putExtra("title", title);
        callSubActivity(i, navStructure, sectionID, catID);
    }


    public void openFromViewCat(NavStructure navStructure, String tSectionID, ViewCategory viewCategory) {

        if (viewCategory.type.equals("set")) return;

        if (viewCategory.type.equals("group")) {
            if (viewCategory.spec.equals("gallery")) {
                openGallery(navStructure, tSectionID, viewCategory.id);
            } else if (viewCategory.spec.equals("maps")) {
                openMapList(navStructure, tSectionID, viewCategory.id);
            } else {
                openSubSection(navStructure, tSectionID, viewCategory.id);
            }
        } else {
            switch (viewCategory.spec) {
                case "map":
                    openMap(viewCategory.id);
                    break;
                case "image_list":
                    openImageList(navStructure, tSectionID, viewCategory.id, viewCategory.title);
                    break;
                case "items_list":
                    openCatList(navStructure, tSectionID, viewCategory.id, viewCategory.title);
                    break;
                case CAT_SPEC_TEXT:
                    openTextPage(navStructure, tSectionID, viewCategory.id, viewCategory.title);
                    break;
                case CAT_SPEC_MAPS:
                    openMapList(navStructure, tSectionID, viewCategory.id);
                    break;
                default:
                    openCat(viewCategory.id, viewCategory.spec, viewCategory.title);
                    break;
            }
        }

    }




    }
