package com.online.languages.study.lang.files;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.online.languages.study.lang.R;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.online.languages.study.lang.DBHelper.TABLE_BOOKMARKS_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_CAT_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_NOTES_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_TESTS_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_UCAT_UDATA;
import static com.online.languages.study.lang.DBHelper.TABLE_USER_DATA;
import static com.online.languages.study.lang.DBHelper.TABLE_USER_DATA_CATS;
import static com.online.languages.study.lang.DBHelper.TABLE_USER_DATA_ITEMS;


public class DBExport {

    private Context context;
    private static final String TAG = DBExport.class.getSimpleName();

    private SharedPreferences appSettings;


    public DBExport(Context context) {
        this.context = context;

        appSettings = PreferenceManager.getDefaultSharedPreferences(context);


    }

    public void export(SQLiteDatabase db, Activity activity) {

        boolean showNotification = appSettings.getBoolean("download_notification", false);


        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");

      //  exportDir =  context.getDir("backups", Context.MODE_PRIVATE);

        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/endata");


        //Toast.makeText(context, "Dir: "+ exportDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        // TODO CHECK GET PERMISSION

        if (!exportDir.exists())  {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, context.getString(R.string.backup_file_name));

        // Saving data in Downloads folder
       // DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
       // downloadManager.addCompletedDownload(file.getName(), file.getName(), false, "text/plain", file.getAbsolutePath(), file.length(), showNotification);



        try
        {
            file.createNewFile();

            List<String> tables = getTablesOnDataBase(db);
            Log.d(TAG, "Started to fill the backup file in " + file.getAbsolutePath());
            long starTime = System.currentTimeMillis();
            writeCsv(file, db, tables);
            long endTime = System.currentTimeMillis();
            Log.d(TAG, "Creating backup took " + (endTime - starTime) + "ms.");

        }
        catch(Exception sqlEx)
        {
            Log.e("DBexport", sqlEx.getMessage(), sqlEx);
        }


        /*
        File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");

        moveFile(file, downloadDir.getPath());

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(downloadDir.getPath()); //filename is string with value 46_1244625499.gif
        intent.setDataAndType(uri, "*//*");
        context.startActivity(Intent.createChooser(intent, "Open folder"));

        */

    }


    public static boolean moveFile(File source, String destPath){
        if(source.exists()){
            File dest = new File(destPath);
            checkMakeDirs(dest.getParent());
            try (FileInputStream fis = new FileInputStream(source);
                 FileOutputStream fos = new FileOutputStream(dest)){
                if(!dest.exists()){
                    dest.createNewFile();
                }
                writeToOutputStream(fis, fos);
                source.delete();
                return true;
            } catch (IOException ioE){
                Log.e(TAG, ioE.getMessage());
            }
        }
        return false;
    }

    private static void writeToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        if (is != null) {
            while ((length = is.read(buffer)) > 0x0) {
                os.write(buffer, 0x0, length);
            }
        }
        os.flush();
    }

    public static boolean checkMakeDirs(String dirPath){
        try {
            File dir = new File(dirPath);
            return dir.exists() || dir.mkdirs();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static void makeDirs(String dirPath){
        try {
            File dir = new File(dirPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }


    private List<String> getTablesOnDataBase(SQLiteDatabase db){
        Cursor c = null;
        List<String> tables = new ArrayList<>();

        try {
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {

                    if (c.getString(0).equals(TABLE_CAT_DATA)
                            || c.getString(0).equals(TABLE_USER_DATA)
                            || c.getString(0).equals(TABLE_TESTS_DATA)
                            || c.getString(0).equals(TABLE_BOOKMARKS_DATA)
                            || c.getString(0).equals(TABLE_NOTES_DATA)
                            || c.getString(0).equals(TABLE_USER_DATA_CATS)
                            || c.getString(0).equals(TABLE_USER_DATA_ITEMS)
                            || c.getString(0).equals(TABLE_UCAT_UDATA)
                    ) {
                        tables.add(c.getString(0));
                    }

                    c.moveToNext();
                }
            }
        }
        catch(Exception throwable){
            Log.e(TAG, "Could not get the table names from db", throwable);
        }
        finally{
            if(c!=null)
                c.close();
        }
        return tables;
    }


    private void writeCsv(File backupFile, SQLiteDatabase db, List<String> tables){
        CSVWriter csvWrite = null;
        Cursor curCSV = null;
        try {
            csvWrite = new CSVWriter(new FileWriter(backupFile));
            String DB_BACKUP_DB_VERSION_KEY = "dbVersion";
            writeSingleValue(csvWrite, DB_BACKUP_DB_VERSION_KEY + "=" + db.getVersion());
            for(String table: tables){
                String DB_BACKUP_TABLE_NAME = "table";
                writeSingleValue(csvWrite, DB_BACKUP_TABLE_NAME + "=" + table);
                curCSV = db.rawQuery("SELECT * FROM " + table,null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    int columns = curCSV.getColumnCount();
                    String[] columnArr = new String[columns];
                    for( int i = 0; i < columns; i++){
                        columnArr[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(columnArr);
                }
            }

            String msg = context.getString(R.string.export_notification) + backupFile.getAbsolutePath();

            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
        catch(Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }finally {
            if(csvWrite != null){
                try {
                    csvWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( curCSV != null ){
                curCSV.close();
            }
        }
    }


    private void writeSingleValue(CSVWriter writer, String value){
        writer.writeNext(new String[]{value});
    }





}
