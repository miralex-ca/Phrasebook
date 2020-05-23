package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavStructure;

import java.util.ArrayList;


public class SectionTestActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;


    NavStructure navStructure;
    String tSectionID = "01010";

    DBHelper dbHelper;

    int[] exResults = {0,0,0,0};

    View testOneBox;
    View testTwoBox;
    View testAllBox;

    ArrayList<DataItem> basicData  = new ArrayList<>();
    ArrayList<DataItem> extraData  = new ArrayList<>();
    ArrayList<DataItem> allData  = new ArrayList<>();

    String dataSelect;

    Boolean easy_mode;
    DataModeDialog dataModeDialog;

    OpenActivity openActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_test);

        easy_mode = appSettings.getString(Constants.SET_DATA_MODE, "2").equals("1");
        dataModeDialog = new DataModeDialog(this);

        navStructure = getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        tSectionID = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);

        dbHelper = new DBHelper(this);
        dataSelect = appSettings.getString("data_select", "dates");

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.section_tests_titile);

        getAllDataFromJson();

        testOneBox = findViewById(R.id.testOne);
        testTwoBox = findViewById(R.id.testTwo);
        testAllBox = findViewById(R.id.testAll);

        showExtraTests();

        getTestsResults();
    }

    private void showExtraTests() {

        Boolean haveExtra = false;
        for (NavCategory navCategory: navStructure.getNavSectionByID(tSectionID).uniqueCategories) {
            if (navCategory.type.equals(Constants.CAT_TYPE_EXTRA)) {
                haveExtra = true;
            }
        }

        if (haveExtra && dataSelect.equals("all")) {
            testAllBox.setVisibility(View.VISIBLE);
        } else {
            testAllBox.setVisibility(View.GONE);
        }

    }

    private void getTestsResults() {

        exResults = new int[]{0, 0, 0, 0};

        exResults[1] = dbHelper.getTestResult(Constants.SECTION_TEST_PREFIX + tSectionID+"_1");
        exResults[2] = dbHelper.getTestResult(Constants.SECTION_TEST_PREFIX + tSectionID+"_2");
        exResults[3] = dbHelper.getTestResult(Constants.SECTION_TEST_PREFIX + tSectionID+Constants.SECTION_TEST_EXTRA_POSTFIX+"_1");

        displayTestData(testOneBox, exResults[1]);
        displayTestData(testTwoBox, exResults[2]);
        displayTestData(testAllBox, exResults[3]);

    }


    public void getAllDataFromJson() {

        ArrayList<String> basicCatIds = new ArrayList<>();
        ArrayList<String> allCatIds = new ArrayList<>();
        ArrayList<String> extraCatIds = new ArrayList<>();

        for (NavCategory navCategory: navStructure.getNavSectionByID(tSectionID).uniqueCategories) {

            if (!navCategory.type.equals(Constants.CAT_TYPE_EXTRA)) {
                basicCatIds.add(navCategory.id);
            } else {
                extraCatIds.add(navCategory.id);
            }

            allCatIds.add(navCategory.id);

        }


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        basicData = dbHelper.selectSimpleDataItemsByIds(db, basicCatIds);
        if (extraCatIds.size() > 0) extraData = dbHelper.selectSimpleDataItemsByIds(db, extraCatIds);
        allData = dbHelper.selectSimpleDataItemsByIds(db, allCatIds);

        db.close();

    }


    private  void deleteSectionExResults() {

        String[] topic = new String[3];

        topic[0] = Constants.SECTION_TEST_PREFIX + tSectionID+"_1";
        topic[1] = Constants.SECTION_TEST_PREFIX + tSectionID+"_2";
        topic[2] = Constants.SECTION_TEST_PREFIX + tSectionID + Constants.SECTION_TEST_EXTRA_POSTFIX+"_1";


        dbHelper.deleteExData(topic);
        getTestsResults();

    }


    public void testOne(View view) {
        testPage(1);
    }

    public void testTwo(View view) {
        testPage(2);
    }


    public void testAll(View view) {
        testAll();
    }

    private void displayTestData(View test, int result) {

        View iconBox = test.findViewWithTag("testIconBox");
        TextView resultTxt = test.findViewWithTag("testResultTxt");

        if (result > 0) iconBox.setVisibility(View.VISIBLE);
        resultTxt.setText(result+"%");

    }


    private void testPage(int type) {

        Intent i = new Intent(SectionTestActivity.this, ExerciseActivity.class) ;

        i.putExtra("ex_type", type);

        String exTag = Constants.SECTION_TEST_PREFIX + tSectionID;
        i.putExtra(Constants.EXTRA_CAT_TAG, exTag);

        i.putParcelableArrayListExtra("dataItems", basicData);

        startActivityForResult(i, 1);;
        openActivity.pageTransition();
    }


    private void testAll() {

        Intent i = new Intent(SectionTestActivity.this, ExerciseActivity.class) ;

        i.putExtra("ex_type", 1);

        String exTag = Constants.SECTION_TEST_PREFIX + tSectionID +Constants.SECTION_TEST_EXTRA_POSTFIX;
        i.putExtra(Constants.EXTRA_CAT_TAG, exTag);

        i.putParcelableArrayListExtra("dataItems", extraData);

        startActivityForResult(i, 1);;
        openActivity.pageTransition();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTestsResults();

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

            case R.id.starred_del_results:
                deleteSectionExResults();
                return true;

            case R.id.easy_mode:
                dataModeDialog.openDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_section_tests, menu);

        MenuItem modeMenuItem = menu.findItem(R.id.easy_mode);
        if (easy_mode) modeMenuItem.setVisible(true);

        return true;
    }

}
