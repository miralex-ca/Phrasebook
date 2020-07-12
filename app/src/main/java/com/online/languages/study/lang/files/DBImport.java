package com.online.languages.study.lang.files;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.online.languages.study.lang.DBHelper;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.online.languages.study.lang.DBHelper.TABLE_BOOKMARKS_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_CAT_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_NOTES_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_TESTS_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_UCAT_UDATA;
import static com.online.languages.study.lang.DBHelper.TABLE_USER_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_USER_DATA_CATS;
import static com.online.languages.study.lang.DBHelper.TABLE_USER_DATA_ITEMS;

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
            Toast.makeText(context, "Несовместимый файл.", Toast.LENGTH_SHORT).show(); // todo check name
            return;
        }

        updateCatDataTable(importedDB);
        updateTestsDataTable(importedDB);
        updateUserItemsDataTable(importedDB);
        updateBookmarksDataTable(importedDB);
        updateNotesDataTable(importedDB);

        updateUserDataItems(importedDB);
        updateUserDataCats(importedDB);

        updateUcatUata(importedDB);


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


    private void updateBookmarksDataTable(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();

        BookmarkDataTable bookmarkDataTable = new BookmarkDataTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_BOOKMARKS_DATA)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            BookmarkData bookmarkData = new BookmarkData();
            bookmarkData.bookmarkItem = line.get(0);
            bookmarkData.bookmarkParent = line.get(1);
            bookmarkData.bookmarkTime = line.get(2);
            bookmarkData.bookmarkType = line.get(3);
            bookmarkData.bookmarkInfo = line.get(4);
            bookmarkData.bookmarkFilter = line.get(5);

            bookmarkDataTable.lines.add(bookmarkData);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importBookmarksData(db, bookmarkDataTable.lines);

        db.close();

    }

    private void updateNotesDataTable(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();

        NoteDataTable noteDataTable = new NoteDataTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_NOTES_DATA)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            NoteDataDB noteData = new NoteDataDB();
            noteData.notePrimaryKey = line.get(0);
            noteData.noteId = line.get(1);
            noteData.noteTitle = line.get(2);
            noteData.noteContent = line.get(3);
            noteData.noteIcon = line.get(4);
            noteData.noteInfo = line.get(5);
            noteData.noteStatus = line.get(6);
            noteData.noteParams = line.get(7);
            noteData.noteFilter = line.get(8);
            noteData.noteParent = line.get(9);
            noteData.noteOrder = line.get(10);
            noteData.noteCreated = line.get(11);
            noteData.noteUpdated = line.get(12);
            noteData.noteUpdatedSort = line.get(13);

            noteDataTable.lines.add(noteData);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importNotesData(db, noteDataTable.lines);

        db.close();

    }

    private void updateUserDataItems(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();

        UserDataItemsTable userDataItemsTable = new UserDataItemsTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_USER_DATA_ITEMS)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            UserDataItem userDataItem = new UserDataItem();
            userDataItem.udataPrimaryId = line.get(0);
            userDataItem.udataId = line.get(1);
            userDataItem.udataText = line.get(2);
            userDataItem.udataTranslate = line.get(3);
            userDataItem.udataTranscript = line.get(4);
            userDataItem.udataGrammar = line.get(5);
            userDataItem.udataSound = line.get(6);
            userDataItem.udataInfo = line.get(7);
            userDataItem.udataImage = line.get(8);
            userDataItem.udataStatus = line.get(9);
            userDataItem.udataFilter= line.get(10);
            userDataItem.udataOrder = line.get(11);
            userDataItem.udataCreated= line.get(12);
            userDataItem.udataUpdated= line.get(13);
            userDataItem.udataUpdatedSort = line.get(14);

            userDataItemsTable.lines.add(userDataItem);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importUserDataItems(db, userDataItemsTable.lines);

        db.close();

    }


    private void updateUserDataCats(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();

        UserDataCatsTable userDataCatsTable = new  UserDataCatsTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_USER_DATA_CATS)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            UserDataCat userDataCat = new UserDataCat();
            userDataCat.ucatPrimaryId = line.get(0);
            userDataCat.ucatId = line.get(1);
            userDataCat.ucatTitle = line.get(2);
            userDataCat.ucatDesc = line.get(3);
            userDataCat.ucatIcon = line.get(4);
            userDataCat.ucatInfo = line.get(5);
            userDataCat.ucatStatus = line.get(6);
            userDataCat.ucatFilter = line.get(7);
            userDataCat.ucatParams = line.get(8);
            userDataCat.ucatParent = line.get(9);
            userDataCat.ucatOrder= line.get(10);
            userDataCat.ucatCreated = line.get(11);
            userDataCat.ucatUpdated = line.get(12);
            userDataCat.ucatUpdatedSort  = line.get(13);

            userDataCatsTable.lines.add(userDataCat);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importUserDataCats(db, userDataCatsTable.lines);

        db.close();

    }


    private void updateUcatUata(ImportedDB importedDB) {

        ImportedTable helpTable = new ImportedTable();

        UCatUDataTable uCatUDataTable = new  UCatUDataTable();

        for (ImportedTable table: importedDB.tables) {
            if (table.tableName.equals(TABLE_UCAT_UDATA)) helpTable = table;
        }

        for (List<String> line: helpTable.lines) {
            UCatUData uCatUData= new UCatUData();
            uCatUData.udcUcatId = line.get(0);
            uCatUData.udcUdataId = line.get(1);

            uCatUDataTable.lines.add(uCatUData);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dbHelper.importUcatUdata(db, uCatUDataTable.lines);

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

    public class BookmarkDataTable {
        ArrayList<BookmarkData> lines = new ArrayList<>();
    }

    public class BookmarkData {
        public String bookmarkItem;
        public String bookmarkParent;
        public String bookmarkTime;
        public String bookmarkType;
        public String bookmarkInfo;
        public String bookmarkFilter;
    }

    public class NoteDataTable {
        ArrayList<NoteDataDB> lines = new ArrayList<>();
    }

    public class NoteDataDB {
        public String notePrimaryKey;
        public String noteId;
        public String noteTitle;
        public String noteContent;
        public String noteIcon;
        public String noteInfo;
        public String noteStatus;
        public String noteParams;
        public String noteFilter;
        public String noteParent;
        public String noteOrder;
        public String noteCreated;
        public String noteUpdated;
        public String noteUpdatedSort;
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


    public class UserDataItemsTable {
        ArrayList<UserDataItem> lines = new ArrayList<>();
    }

    public class UserDataItem {
        public String udataPrimaryId;
        public String udataId;
        public String udataText;
        public String udataTranslate;
        public String udataTranscript;
        public String udataGrammar;
        public String udataSound;
        public String udataInfo;
        public String udataImage;
        public String udataStatus;
        public String udataFilter;
        public String udataOrder;
        public String udataCreated;
        public String udataUpdated;
        public String udataUpdatedSort;

    }


    public class UserDataCatsTable {
        ArrayList<UserDataCat> lines = new ArrayList<>();
    }

    public class UserDataCat {
        public String ucatPrimaryId;
        public String ucatId;
        public String ucatTitle;
        public String ucatDesc;
        public String ucatIcon;
        public String ucatInfo;
        public String ucatStatus;
        public String ucatFilter;
        public String ucatParams;
        public String ucatParent;
        public String ucatOrder;
        public String ucatCreated;
        public String ucatUpdated;
        public String ucatUpdatedSort;
    }

    public class UCatUDataTable {
        ArrayList<UCatUData> lines = new ArrayList<>();
    }

    public class UCatUData {
        public String udcUcatId;
        public String udcUdataId;
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
