package com.online.languages.study.studymaster.files;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.online.languages.study.studymaster.DBHelper;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.online.languages.study.studymaster.DBHelper.TABLE_CAT_DATA;
import static com.online.languages.study.studymaster.DBHelper.TABLE_TESTS_DATA;
import static com.online.languages.study.studymaster.DBHelper.TABLE_USER_DATA;

public class DBImport {

    private Context context;
    private File file;
    private Uri uri;
    DBHelper dbHelper;



    public DBImport(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
        file = new File(uri.getPath());
        dbHelper = new DBHelper(context);
    }

    public DBImport(Context context, String path) {
        this.context = context;

        file = new File(uri.getPath());
        dbHelper = new DBHelper(context);
    }





    public void importCSV () {


        List<List<String>> lines = new ArrayList<>();

        try {

            InputStream in =  context.getContentResolver().openInputStream(uri);

            CSVReader reader = null;
            if (in != null) {
                reader = new CSVReader(new InputStreamReader(in));
            }

            String [] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                lines.add(new ArrayList<>(Arrays.asList(nextLine)));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        parseData(lines);

    }

    private void parseData(List<List<String>> lines) {

        ImportedDB importedDB = new ImportedDB();

        ImportedTable currentTable = new ImportedTable();
        int tableLines = 0;

        for (List<String> line: lines)   {
                if (line.get(0).contains("table=")) {
                    if (currentTable.lines.size() > 0) {
                        importedDB.tables.add(currentTable);
                    }
                    currentTable = new ImportedTable();
                    currentTable.tableName = line.get(0).replace("table=", "");
                    tableLines = 0;
                    continue;
                }

            if (tableLines == 0) {
                currentTable.columns.addAll(line);
                tableLines++;
            } else {
                currentTable.lines.add(line);
                tableLines ++;
            }
        }

        if (! currentTable.tableName.equals("empty")) importedDB.tables.add(currentTable);

        if (importedDB.tables.size() < 1) {
            Toast.makeText(context, "Несовместимый файл.", Toast.LENGTH_SHORT).show();
            return;
        }

        updateCatDataTable(importedDB);
        updateTestsDataTable(importedDB);
        updateUserItemsDataTable(importedDB);
    }




    private void updateCatDataTable(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();
        CatDataTable catDataTable = new CatDataTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_CAT_DATA)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            CatDataLine catDataLine = new CatDataLine();
            catDataLine.catId = line.get(0);
            catDataLine.progress = line.get(1);

            catDataTable.lines.add(catDataLine);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importCatData(db, catDataTable.lines);

        db.close();

    }


    private void updateTestsDataTable(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();
        TestsDataTable testsDataTable = new TestsDataTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_TESTS_DATA)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            TestData testData = new TestData();
            testData.tag = line.get(0);
            testData.progress = line.get(1);
            testData.testTime = line.get(2);

            testsDataTable.lines.add(testData);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importTestsData(db, testsDataTable.lines);

        db.close();

    }


    private void updateUserItemsDataTable(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();
        UserItemsDataTable userItemsDataTable = new UserItemsDataTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_USER_DATA)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            UserItemData userItemsDataLine = new UserItemData();
            userItemsDataLine.id = line.get(0);
            userItemsDataLine.itemInfo = line.get(1);
            userItemsDataLine.itemProgress = line.get(2);
            userItemsDataLine.itemErrors = line.get(3);
            userItemsDataLine.itemScore = line.get(4);
            userItemsDataLine.itemStatus = line.get(5);
            userItemsDataLine.itemStarred = line.get(6);
            userItemsDataLine.itemTime = line.get(7);
            userItemsDataLine.itemTimeStarred = line.get(8);
            userItemsDataLine.itemTimeError = line.get(9);
            userItemsDataTable.lines.add(userItemsDataLine);
        }

        //Toast.makeText(context, "Lines: " + userItemsDataTable.lines.size(), Toast.LENGTH_SHORT).show();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importUserData(db, userItemsDataTable.lines);

        db.close();

    }



    public class CatDataTable {
        ArrayList<CatDataLine> lines = new ArrayList<>();
    }

    public class CatDataLine {
        public String catId;
        public String progress;
    }

    public class TestsDataTable {
        ArrayList<TestData> lines = new ArrayList<>();

    }

    public class TestData {
        public String tag;
        public String progress;
        public String testTime;
    }


    public class UserItemsDataTable {
        ArrayList<UserItemData> lines = new ArrayList<>();
    }

    public class UserItemData {
        public String id;
        public String itemInfo;
        public String itemProgress;
        public String itemErrors;
        public String itemScore;
        public String itemStatus;
        public String itemStarred;
        public String itemTime;
        public String itemTimeStarred;
        public String itemTimeError;
    }





    class ImportedDB {
        String dbVersion;
        ArrayList<ImportedTable> tables = new ArrayList<>();
    }

    class ImportedTable {
        String tableName;
        String tableVersion;

        List<String> columns;
        List<List<String>> lines;

        ImportedTable () {
            tableName = "empty";
            columns = new ArrayList<>();
            lines = new ArrayList<>();
        }
    }




}
