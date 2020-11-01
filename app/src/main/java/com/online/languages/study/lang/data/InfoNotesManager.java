package com.online.languages.study.lang.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.tools.Computer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.online.languages.study.lang.Constants.BOOKMARKS_LIMIT;
import static com.online.languages.study.lang.Constants.NOTE_ARCHIVE;
import static com.online.languages.study.lang.Constants.OUTCOME_LIMIT;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;
import static com.online.languages.study.lang.Constants.PARAM_LIMIT_REACHED;
import static com.online.languages.study.lang.Constants.PARAM_POPULATE;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ARCHIVE;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ROOT;
import static com.online.languages.study.lang.Constants.PRO;
import static com.online.languages.study.lang.Constants.SET_DATA_LEVELS;
import static com.online.languages.study.lang.Constants.SET_DATA_LEVELS_DEFAULT;
import static com.online.languages.study.lang.Constants.SET_GALLERY;
import static com.online.languages.study.lang.Constants.SET_HOMECARDS;
import static com.online.languages.study.lang.Constants.SET_SIMPLIFIED;
import static com.online.languages.study.lang.Constants.SET_STATS;
import static com.online.languages.study.lang.Constants.STARRED_CAT_TAG;
import static com.online.languages.study.lang.Constants.UCATS_UNPAID_LIMIT;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_SORT;
import static com.online.languages.study.lang.Constants.UC_PREFIX;
import static com.online.languages.study.lang.Constants.VIBRO_FAIL;


public class InfoNotesManager {

    Context context;


    public InfoNotesManager(Context context) {
        this.context= context;
    }


    public void postStartNotes(DBHelper dbHelper, SQLiteDatabase db) {

        postNote(dbHelper, db,  // first note
                context.getString(R.string.first_note_title),
                context.getString(R.string.first_note_text),
                "info.png");


    }


    public void postUpdateNotes(DBHelper dbHelper, SQLiteDatabase db, int version) {

       // if (version == 40) { }


    }


    private void postNote(DBHelper dbHelper, SQLiteDatabase db, String title, String content, String image) {

        NoteData note = new NoteData();
        note.title = title;
        note.content = content;
        note.image = image;
        dbHelper.createNote(db, note);

    }



}