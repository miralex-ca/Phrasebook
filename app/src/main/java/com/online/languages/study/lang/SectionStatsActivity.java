package com.online.languages.study.lang;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.ColorProgress;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedTransformation;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.Category;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.practice.PracticeActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SectionStatsActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    TextView sectionTitle, sectionDesc, sectionProgress, sectionStudiedCountTxt, sectionFamiliarCountTxt, sectionUnknownCountTxt;
    TextView testResult, familiarProgress, studiedProgress;
    ImageView placePicutre;

    Section section;
    int sectionNum;

    View errorsCard;
    TextView recentErrorsTxt, recentErrorsTxt2;

    NavStructure navStructure;
    String tSectionID = "01010";

    DBHelper dbHelper;

    OpenActivity openActivity;

    NavSection navSection;

    Boolean full_version;

    Boolean easy_mode;
    MenuItem modeMenuItem;
    MenuItem infoMenuItem;

    InfoDialog dataModeDialog;
    DataManager dataManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cat_stats);

        openActivity = new OpenActivity(this);

        openActivity.setOrientation();

        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        dataManager = new DataManager(this);

        dataModeDialog = new InfoDialog(this);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);

        dbHelper = new DBHelper(this);

        sectionTitle = findViewById(R.id.sectionTitle);
        sectionDesc = findViewById(R.id.sectionDesc);

        sectionProgress = findViewById(R.id.sectionProgress);
        sectionStudiedCountTxt = findViewById(R.id.sectionStudiedCount);
        sectionFamiliarCountTxt = findViewById(R.id.sectionFamiliarCount);
        sectionUnknownCountTxt = findViewById(R.id.sectionUnknownCount);

        errorsCard =  findViewById(R.id.errorsCard);
        recentErrorsTxt =  findViewById(R.id.recentErrors);
        recentErrorsTxt2 = findViewById(R.id.recentErrors2);

        testResult = findViewById(R.id.testResult);
        familiarProgress = findViewById(R.id.studiedResult);
        studiedProgress = findViewById(R.id.learnedResult);

        placePicutre = findViewById(R.id.catImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.stats_page_activity));

        View sectionListLink = findViewById(R.id.sectionListLink);
        View sectionTestLink = findViewById(R.id.sectionTestLink);

        displaySectionLink();

        if (navSection.type.equals("simple")) {
            sectionListLink.setVisibility(View.GONE);
            sectionTestLink.setVisibility(View.GONE);
        }

        if (!full_version) {
            if (!navSection.unlocked)  sectionTestLink.setVisibility(View.GONE);
        }

        updateContent();
        setStatsText(section);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkModeIcon();
        updateContent();
        setStatsText(section);

    }

    public void displaySectionLink() {
        if (getIntent().hasExtra("from_section"))
            findViewById(R.id.sectionLink).setVisibility(View.GONE);
        else
            findViewById(R.id.sectionLink).setVisibility(View.VISIBLE);
    }


    private void updateContent() {

        sectionNum = getIntent().getIntExtra(Constants.EXTRA_SECTION_NUM, 0);

        section = new Section(navStructure.getNavSectionByID(tSectionID), this); /// TODO initialize once

        section = dbHelper.checkSectionStatsDB(section);

        Picasso.with(this )
                .load("file:///android_asset/pics/"+section.image)
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(placePicutre);

       // if (themeTitle.equals("westworld")) placePicutre.setColorFilter(Color.argb(255, 50, 240, 240), PorterDuff.Mode.MULTIPLY);

    }


    private void setStatsText(Section section) {

        checkErrors(section);

        sectionTitle.setText(section.title_short);
        sectionDesc.setText(section.desc);

        sectionProgress.setText(section.progress + "%");

        ColorProgress colorProgress = new ColorProgress(this);

        sectionProgress.setTextColor(  colorProgress.getColorFromAttr(section.progress)  );

        sectionStudiedCountTxt.setText(String.valueOf(section.studiedDataCount));
        sectionFamiliarCountTxt.setText(String.valueOf(section.familiarDataCount));
        sectionUnknownCountTxt.setText(String.valueOf(section.unknownDataCount));

        testResult.setText(section.testResults + "%");

        familiarProgress.setText(section.knownPart + "% (" + section.familiarDataCount + "/"+section.allDataCount + ")" );

        studiedProgress.setText(section.studiedPart +  "% (" + section.studiedDataCount + "/"+section.allDataCount + ")" );
    }

    public void showResultDialog() {


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.result_dialog, null);

        TextView txt = content.findViewById(R.id.resultSection);

        TextView resultInfoTestTxt = content.findViewById(R.id.resultInfoTestTxt);
        TextView resultInfoKnownTxt = content.findViewById(R.id.resultInfoKnownTxt);
        TextView resultInfoStudiedTxt = content.findViewById(R.id.resultInfoStudiedTxt);

        TextView resultInfoTest = content.findViewById(R.id.resultInfoTest);
        TextView resultInfoKnown = content.findViewById(R.id.resultInfoKnown);
        TextView resultInfoStudied = content.findViewById(R.id.resultInfoStudied);

        txt.setText(String.format(getString(R.string.stats_total), section.progress));

        resultInfoTestTxt.setText(String.format(getString(R.string.stats_tests), section.testResults));
        resultInfoKnownTxt.setText(String.format(getString(R.string.stats_familiar), section.knownPart));
        resultInfoStudiedTxt.setText(String.format(getString(R.string.stats_studied), section.studiedPart));

        resultInfoTest.setText(String.format(getString(R.string.stats_percent), section.testResult));
        resultInfoKnown.setText(String.format(getString(R.string.stats_percent), section.knownResult));
        resultInfoStudied.setText(String.format(getString(R.string.stats_percent), section.studiedResult));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.total_result)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                .setView(content);

        AlertDialog alert = builder.create();
        alert.show();

    }


    private void checkErrors(Section section) {

        section.errorsData = dbHelper.getSectionErrorsData(section);
        section.sortSectionErrors();

        if (section.errorsData.size() > 0) {
            errorsCard.setVisibility(View.VISIBLE);
            String errors = "";
            String errors2 = "";

            int limit;

            if (section.errorsData.size() > 6) {
                limit = 6;
            } else {
                limit = section.errorsData.size();
            }

            List<DataItem> data = section.errorsData.subList(0, limit);

            for (int i = 0; i< data.size(); i++) {

                if (i < 3) {
                    errors = errors + "\n"+ data.get(i).item ;
                } else {
                    errors2 = errors2 +"\n"+ data.get(i).item ;
                }

            }

            recentErrorsTxt.setText(errors);
            recentErrorsTxt2.setText(errors2);

        } else {
            errorsCard.setVisibility(View.GONE);
            recentErrorsTxt.setText("");
            recentErrorsTxt2.setText("");
        }

    }


    public void openErrors(View view) {
        Intent i = new Intent(SectionStatsActivity.this, CustomDataListActivity.class);

        i.putParcelableArrayListExtra("dataItems", section.errorsData);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, Constants.ERRORS_CAT_TAG);

        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    public void openSectionTests(View view) {

        Intent i = new Intent(SectionStatsActivity.this, SectionTestActivity.class);

        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);


        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }

    public void openSectionTest(View view) {

        Intent i = new Intent(this, PracticeActivity.class);

        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);

        startActivityForResult(i, 1);
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
        switch(id) {
            case android.R.id.home:
                finish();
                openActivity.pageBackTransition();
                return true;
            case R.id.stats_info:
                showInfoDialog();
                return true;

            case R.id.easy_mode:
                dataModeDialog.openEasyModeDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_section_stats, menu);

        modeMenuItem = menu.findItem(R.id.easy_mode);
        infoMenuItem = menu.findItem(R.id.stats_info);

        checkModeIcon();

        return true;

    }

    private void checkModeIcon() {
        dbHelper.checkMode();
        easy_mode = dataManager.easyMode();

        if (modeMenuItem != null && infoMenuItem != null) {

            modeMenuItem.setVisible(easy_mode);

            if (easy_mode) infoMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
            else infoMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        }

    }


    public void openStudiedBySection (View view) {
        if (section.studiedDataCount == 0) {
            displayEmtySection();
        } else {
            openDataTypeBySections (0);
        }
    }

    public void openKnownBySection (View view) {

        if (section.familiarDataCount == 0) {
            displayEmtySection();
        } else {
            openDataTypeBySections (1);
        }

    }

    public void openUnknownBySection (View view) {

        if (section.unknownDataCount == 0) {
            displayEmtySection();
        } else {
            openDataTypeBySections (2);
        }

    }

    public void displayEmtySection() {

        String noData = getString(R.string.no_entries_msg);

        Snackbar.make(sectionTitle, Html.fromHtml("<font color=\"#ffffff\">"+noData+"</font>"), Snackbar.LENGTH_SHORT).show();
    }


    public void openDataTypeBySections (int type) {


        if (navSection.type.equals("simple")) {

            Intent i = new Intent(SectionStatsActivity.this, CustomDataActivity.class);
            i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
            i.putExtra(Constants.EXTRA_SECTION_ID, section.id);
            i.putExtra(Constants.EXTRA_DATA_TYPE, type);
            i.putExtra(Constants.EXTRA_CAT_ID, section.categories.get(0).id);

            startActivityForResult(i, 1);
            openActivity.pageTransition();

        } else {

            Intent intent = new Intent(this, SectionStatsListActivity.class);

            intent.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);

            intent.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);

            intent.putExtra(Constants.EXTRA_DATA_TYPE, type);

            startActivityForResult(intent, 1);
            openActivity.pageTransition();

        }

    }


    public void openCatActivity(View view) {

        if (navSection.type.equals("simple")) {
            Category cat = section.categories.get(0);
            Intent i = new Intent(SectionStatsActivity.this, CatActivity.class);
            openActivity.openCat(cat.id, cat.spec, cat.title, navSection.id);

        } else {

            Intent i = new Intent(SectionStatsActivity.this, SectionActivity.class);
            openActivity.openSection(i, navStructure, tSectionID, "root");
        }

    }

    public void openSectionList(View view) {

        Intent i = new Intent(SectionStatsActivity.this, SectionListActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);
        startActivityForResult(i, 1);
        openActivity.pageTransition();

    }

    public void openCat (int position) {
        Intent intent = new Intent(this, CustomDataListActivity.class);
        startActivity(intent);
        openActivity.pageTransition();
    }

    public void showInfo (View view) {
        showResultDialog();
    }

    public void showInfoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.starred_menu_info)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setMessage(R.string.section_stats_info);
        AlertDialog alert = builder.create();
        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);

    }


}
