package com.online.languages.study.lang.presentation.backup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.files.DBExport;
import com.online.languages.study.lang.files.DBImport;
import com.online.languages.study.lang.presentation.core.BaseActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.online.languages.study.lang.presentation.AppStart.APP_LAUNCHES;


public class BackupActivity extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    SharedPreferences mLaunches;
    public String themeTitle;
    TextView lastImportTxt;
    InfoDialog infoDialog;

    final static int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    final static int MY_PERMISSIONS_REQUEST_READ_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        setContentView(R.layout.activity_backup);

        setTitle(R.string.backup_page_title);

        infoDialog = new InfoDialog(this);

        mLaunches = getSharedPreferences(APP_LAUNCHES, Context.MODE_PRIVATE);

        OpenActivity openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lastImportTxt = findViewById(R.id.importInfo);

        String lastImportInfo = mLaunches.getString("last_import", getString(R.string.no_import_data));

        lastImportTxt.setText(String.format("%s%s", getString(R.string.import_last_info), lastImportInfo));

        TextView exportDesc = findViewById(R.id.exportDesc);

        String exportDescTxt = String.format(getString(R.string.export_desc), getString(R.string.backup_file_name));
        exportDesc.setText(Html.fromHtml(exportDescTxt));

        exportDescTxt.matches("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_info, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.info_item:
                String msg = String.format(getString(R.string.backup_info), getString(R.string.backup_file_name));
                infoDialog.openInfoHtmlDialog(msg, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void export(View view) {
        isExternalStorageWritable();
    }

    public void export() {

        DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        DBExport dbExport = new DBExport(this);

        dbExport.export(db, BackupActivity.this);

        db.close();

    }


    public void importDB(View view) {
        isExternalStorageReadable();
    }


    public void selectCSVFile(){

        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());

        String fileFormat = getString(R.string.backup_file_format);

       // File exportDir =  getDir("backups", Context.MODE_PRIVATE);

//        new MaterialFilePicker()
//                .withActivity(this)
//                .withCustomActivity(FinderDialogActivity.class)
//                .withRequestCode(10)
//                .withFilter(Pattern.compile("(.*\\."+fileFormat+"$)|(.*\\.csv$)|(.*\\.jpg$)"))
//                .withHiddenFiles(false)
//                .withPath(uri.getPath())
//                .start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {

//                String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
//                proImportCSV(Uri.fromFile(new File(filePath)));

            }
        }
    }


    private void proImportCSV(Uri uri){

        if (uri != null) {
            File file = new File(uri.getPath());
            // Toast.makeText(this, "Import: "+ file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }

        DBImport dbImport = new DBImport(this, uri);
        dbImport.importCSV();

        saveLastImportInfo();

    }


    private void saveLastImportInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        mLaunches.edit().putString("last_import", date).apply();
        lastImportTxt.setText(String.format(getString(R.string.last_import_dat), date));
    }



    private void isExternalStorageWritable() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

        } else {
            // Permission has already been granted
            export();

        }

    }


    private void isExternalStorageReadable() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            // Permission has already been granted
            selectCSVFile();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        String noAccess = getString(R.string.no_access);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    export();

                } else {
                    Snackbar.make(lastImportTxt, Html.fromHtml("<font color=\"#ffffff\">"+noAccess+"</font>"), Snackbar.LENGTH_SHORT).show();
                }

                return;

            }

            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectCSVFile();

                } else {
                    Snackbar.make(lastImportTxt, Html.fromHtml("<font color=\"#ffffff\">"+noAccess+"</font>"), Snackbar.LENGTH_SHORT).show();
                }
            }

        }
    }



}
