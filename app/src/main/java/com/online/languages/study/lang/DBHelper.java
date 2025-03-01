package com.online.languages.study.lang;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Range;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.online.languages.study.lang.practice.QuestData;
import com.online.languages.study.lang.recommend.TaskItem;
import com.online.languages.study.lang.data.BookmarkItem;
import com.online.languages.study.lang.data.Category;
import com.online.languages.study.lang.data.DataFromJson;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.DetailFromJson;
import com.online.languages.study.lang.data.DetailItem;
import com.online.languages.study.lang.data.InfoNotesManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NoteData;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.data.UserStats;
import com.online.languages.study.lang.data.UserStatsData;
import com.online.languages.study.lang.files.DBImport;
import com.online.languages.study.lang.tools.Computer;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import kotlin.ranges.IntRange;

import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_DELETE;
import static com.online.languages.study.lang.Constants.EX_AUDIO_TYPE;
import static com.online.languages.study.lang.Constants.GALLERY_TAG;
import static com.online.languages.study.lang.Constants.GROUPS_UNPAID_LIMIT;
import static com.online.languages.study.lang.Constants.NOTE_ARCHIVE;
import static com.online.languages.study.lang.Constants.NOTE_TAG;
import static com.online.languages.study.lang.Constants.OUTCOME_ADDED;
import static com.online.languages.study.lang.Constants.OUTCOME_LIMIT;
import static com.online.languages.study.lang.Constants.OUTCOME_NONE;
import static com.online.languages.study.lang.Constants.OUTCOME_REMOVED;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;
import static com.online.languages.study.lang.Constants.PARAM_LIMIT_REACHED;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ARCHIVE;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_PARENT;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ROOT;
import static com.online.languages.study.lang.Constants.SECTION_REVIEW_MAX_MODE;
import static com.online.languages.study.lang.Constants.SET_DATA_MODE;
import static com.online.languages.study.lang.Constants.STARRED_TAB_ACTIVE;
import static com.online.languages.study.lang.Constants.TAB_GALLERY;
import static com.online.languages.study.lang.Constants.TAB_ITEMS;
import static com.online.languages.study.lang.Constants.TEST_CATS_MAX_FOR_BEST;
import static com.online.languages.study.lang.Constants.UCATS_UNPAID_LIMIT;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_BOOKMARK_OFF;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_BOOKMARK_ON;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_SORT_ASC;
import static com.online.languages.study.lang.Constants.UC_PREFIX;
import static com.online.languages.study.lang.Constants.UD_PREFIX;
import static com.online.languages.study.lang.practice.QuestCollector.TEST_BUILD;


public class DBHelper extends SQLiteOpenHelper {

    private final Context cntx;
    SharedPreferences appSettings;
    private final int MAX_SCORE = 4;
    private static final int DATABASE_VERSION = BuildConfig.VERSION_CODE;
    public static final String DATABASE_NAME = BuildConfig.DBNAME;

    public static final String TABLE_CAT_DATA = "cat_data";
    public static final String TABLE_USER_DATA = "user_items_data";
    public static final String TABLE_TESTS_DATA = "tests_data";
    public static final String TABLE_ITEMS_DATA = "items_data";
    public static final String TABLE_DETAILS_DATA = "details_data";
    public static final String TABLE_BOOKMARKS_DATA = "bookmarks_data";
    public static final String TABLE_NOTES_DATA = "notes_data";
    public static final String TABLE_USER_DATA_CATS = "user_data_cats";
    public static final String TABLE_USER_DATA_ITEMS = "user_data_items";
    public static final String TABLE_UCAT_UDATA = "table_ucat_udata";
    public static final String TABLE_TASKS_DATA = "table_tasks_data";
    public static final String TABLE_QUEST_DATA = "table_quest_data";
    public static final String TABLE_QUEST_BUILD = "table_quest_build";
    public static final String TABLE_QSTATS_DATA = "table_qstats";

    // common
    private static final String KEY_PRIMARY_ID = "id";

    //// items table

    private static final String KEY_ITEM_ID = "item_id";
    private static final String KEY_ITEM_TITLE = "item_title";
    private static final String KEY_ITEM_DESC = "item_desc";
    private static final String KEY_ITEM_IMAGE = "item_image";
    private static final String KEY_ITEM_INFO_1 = "item_info_1";
    private static final String KEY_ITEM_BASE = "item_base";
    private static final String KEY_ITEM_PRONOUNCE = "item_pronounce";
    private static final String KEY_ITEM_GRAMMAR = "item_grammar";
    private static final String KEY_ITEM_DIVIDER = "item_divider";
    private static final String KEY_ITEM_FILTER = "item_filter";
    private static final String KEY_ITEM_MODE = "item_mode";
    private static final String KEY_ITEM_SEARCH = "item_search";
    private static final String KEY_ITEM_TRANS1 = "item_trans1";
    private static final String KEY_ITEM_TRANS2 = "item_trans2";


    private static final String KEY_QUEST_ID = "quest_id";
    private static final String KEY_QUEST_CAT_ID = "quest_cat_id";
    private static final String KEY_QUEST_LEVEL = "quest_level";
    private static final String KEY_QUEST_LEVEL_GLOBAL = "quest_level_g";
    private static final String KEY_QUEST_MODE = "quest_mode";
    private static final String KEY_QUEST_QUEST = "quest_quest";
    private static final String KEY_QUEST_TASK = "quest_task";
    private static final String KEY_QUEST_CORRECT = "quest_correct";
    private static final String KEY_QUEST_OPTIONS = "quest_options";
    private static final String KEY_QUEST_PRONOUNCE = "quest_pronounce";
    private static final String KEY_QUEST_IMAGE= "quest_image";
    private static final String KEY_QUEST_FILTER= "quest_filter";


    private static final String KEY_QUEST_B_ID = "quest_b_id";
    private static final String KEY_QUEST_B_CAT_ID = "quest_b_cat_id";
    private static final String KEY_QUEST_B_LEVEL = "quest_b_level";
    private static final String KEY_QUEST_B_LEVEL_GLOBAL = "quest_b_level_g";
    private static final String KEY_QUEST_B_MODE = "quest_b_mode";
    private static final String KEY_QUEST_B_QUEST = "quest_b_quest";
    private static final String KEY_QUEST_B_TASK = "quest_b_task";
    private static final String KEY_QUEST_B_CORRECT = "quest_b_correct";
    private static final String KEY_QUEST_B_OPTIONS = "quest_b_options";
    private static final String KEY_QUEST_B_PRONOUNCE = "quest_b_pronounce";
    private static final String KEY_QUEST_B_IMAGE= "quest_b_image";
    private static final String KEY_QUEST_B_PARAMS= "quest_b_params";
    private static final String KEY_QUEST_B_FILTER= "quest_b_filter";


    private static final String KEY_QSTATS_ID = "qstats_id";
    private static final String KEY_QSTATS_COUNT_TR= "qstats_count_tr";
    private static final String KEY_QSTATS_COUNT_AUDIO = "qstats_count_audio";
    private static final String KEY_QSTATS_CORRECT = "qstats_correct";
    private static final String KEY_QSTATS_CORRECT_TIME = "qstats_correct_time";
    private static final String KEY_QSTATS_ERROR= "qstats_errors";
    private static final String KEY_QSTATS_ERROR_TIME = "qstats_errors_time";



    //// cats table
    private static final String KEY_CAT_ID = "cat_id";
    private static final String KEY_CAT_PROGRESS = "progress";

    //// test table
    private static final String KEY_TEST_TAG = "tag";
    private static final String KEY_TEST_PROGRESS = "progress";
    private static final String KEY_TEST_TIME = "test_time";


    //// dataItems and phrases tables columns
    private static final String KEY_USER_ITEM_ID = "user_item_id";

    private static final String KEY_ITEM_INFO = "item_info";
    private static final String KEY_ITEM_PROGRESS = "item_progress";
    private static final String KEY_ITEM_ERRORS = "item_errors";
    private static final String KEY_ITEM_SCORE = "item_score";
    private static final String KEY_ITEM_STATUS = "item_status";
    private static final String KEY_ITEM_STARRED = "item_starred";
    private static final String KEY_ITEM_TIME = "item_time";
    private static final String KEY_ITEM_TIME_STARRED = "item_time_starred";
    private static final String KEY_ITEM_TIME_ERROR = "item_time_error";

    //// detail table columns
    private static final String KEY_DETAIL_ID = "detail_id";
    private static final String KEY_DETAIL_TITLE = "detail_title";
    private static final String KEY_DETAIL_DESC = "detail_desc";
    private static final String KEY_DETAIL_IMAGE = "detail_image";
    private static final String KEY_DETAIL_IMG_INFO = "detail_img_imfo";


    //// bookmark table columns
    private static final String KEY_BOOKMARK_ITEM = "bookmark_item";
    private static final String KEY_BOOKMARK_PARENT = "bookmark_parent";
    private static final String KEY_BOOKMARK_TIME = "bookmark_time";
    private static final String KEY_BOOKMARK_TYPE = "bookmark_type";
    private static final String KEY_BOOKMARK_INFO = "bookmark_info";
    private static final String KEY_BOOKMARK_FILTER = "bookmark_filter";


    //// notes table columns
    private static final String KEY_NOTE_PRIMARY_ID = "note_primary_id";
    private static final String KEY_NOTE_ID = "note_id";
    private static final String KEY_NOTE_TITLE = "note_title";
    private static final String KEY_NOTE_TEXT = "note_text";
    private static final String KEY_NOTE_ICON = "note_icon";
    private static final String KEY_NOTE_INFO = "note_info";
    private static final String KEY_NOTE_STATUS = "note_status";
    private static final String KEY_NOTE_TYPE = "note_type";
    private static final String KEY_NOTE_PARAMS = "note_params";
    private static final String KEY_NOTE_FILTER = "note_filter";
    private static final String KEY_NOTE_PARENT = "note_parent";
    private static final String KEY_NOTE_ORDER = "note_order";
    private static final String KEY_NOTE_CREATED = "note_created";
    private static final String KEY_NOTE_UPDATED = "note_updated";
    private static final String KEY_NOTE_UPDATED_SORT = "note_updated_sort";

    //// user cats table columns
    private static final String KEY_UCAT_PRIMARY_ID = "ucat_primary_id";
    private static final String KEY_UCAT_ID = "ucat_id";
    private static final String KEY_UCAT_TITLE = "ucat_title";
    private static final String KEY_UCAT_DESC = "ucat_desc";
    private static final String KEY_UCAT_ICON = "ucat_icon";
    private static final String KEY_UCAT_INFO = "ucat_info";
    private static final String KEY_UCAT_TYPE = "ucat_type";
    private static final String KEY_UCAT_STATUS = "ucat_status";
    private static final String KEY_UCAT_FILTER = "ucat_filter";
    private static final String KEY_UCAT_PARAMS = "ucat_params";
    private static final String KEY_UCAT_PARENT = "ucat_parent";
    private static final String KEY_UCAT_ORDER = "ucat_order";
    private static final String KEY_UCAT_CREATED = "ucat_created";
    private static final String KEY_UCAT_UPDATED = "ucat_updated";
    private static final String KEY_UCAT_UPDATED_SORT = "ucat_updated_sort";


    //// user dataitem table columns
    private static final String KEY_UDATA_PRIMARY_ID = "udata_primary_id";
    private static final String KEY_UDATA_ID = "udata_id";
    private static final String KEY_UDATA_TEXT = "udata_text";
    private static final String KEY_UDATA_TRANSLATE = "udata_translate";
    private static final String KEY_UDATA_TRANSCRIPT = "udata_transcript";
    private static final String KEY_UDATA_GRAMMAR = "udata_grammar";
    private static final String KEY_UDATA_SOUND = "udata_sound";
    private static final String KEY_UDATA_INFO = "udata_info";
    private static final String KEY_UDATA_IMAGE = "udata_image";
    private static final String KEY_UDATA_STATUS = "udata_status";
    private static final String KEY_UDATA_FILTER = "udata_filter";
    private static final String KEY_UDATA_ORDER = "udata_order";
    private static final String KEY_UDATA_CREATED = "udata_created";
    private static final String KEY_UDATA_UPDATED = "udata_updated";
    private static final String KEY_UDATA_UPDATED_SORT = "udata_updated_sort";


    //// user dataitem table columns
    private static final String KEY_UDC_UCAT_ID = "ucat_id";
    private static final String KEY_UDC_UDATA_ID = "udata_id";


    //// task table columns

    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_TASK_SKIPPED_COUNT = "task_skipped_count";
    private static final String KEY_TASK_SKIPPED_TIME = "task_skipped_time";
    private static final String KEY_TASK_PRIORITY = "task_priority";
    private static final String KEY_TASK_INFO = "task_info";
    private static final String KEY_TASK_STATUS = "task_status";
    private static final String KEY_TASK_TYPE = "task_type";
    private static final String KEY_TASK_PARAMS = "task_params";
    private static final String KEY_TASK_FILTER = "task_filter";
    private static final String KEY_TASK_UPDATED = "task_updated";
    private static final String KEY_TASK_UPDATED_SORT = "task_updated_sort";


    private static final String TABLE_ITEM_STRUCTURE  = "("
            + KEY_PRIMARY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ITEM_ID + " TEXT,"
            + KEY_ITEM_TITLE + " TEXT,"
            + KEY_ITEM_DESC + " TEXT,"
            + KEY_ITEM_IMAGE + " TEXT,"
            + KEY_ITEM_TRANS1 + " TEXT,"
            + KEY_ITEM_TRANS2+ " TEXT,"
            + KEY_ITEM_INFO_1+ " TEXT,"
            + KEY_ITEM_PRONOUNCE + " TEXT,"
            + KEY_ITEM_BASE + " TEXT,"
            + KEY_ITEM_GRAMMAR + " TEXT,"
            + KEY_ITEM_FILTER + " TEXT,"
            + KEY_ITEM_SEARCH + " TEXT,"
            + KEY_ITEM_MODE + "  INTEGER DEFAULT 0,"
            + KEY_ITEM_DIVIDER + " TEXT"
            + ")";

    private static final String TABLE_QUEST_STRUCTURE  = "("

            + KEY_QUEST_ID + " TEXT,"
            + KEY_QUEST_CAT_ID + " TEXT,"
            + KEY_QUEST_LEVEL + " TEXT,"
            + KEY_QUEST_LEVEL_GLOBAL + " INTEGER DEFAULT 0,"
            + KEY_QUEST_MODE + " INTEGER DEFAULT 0,"
            + KEY_QUEST_QUEST + " TEXT,"
            + KEY_QUEST_TASK + " TEXT,"
            + KEY_QUEST_CORRECT + " TEXT,"
            + KEY_QUEST_OPTIONS + " TEXT,"
            + KEY_QUEST_PRONOUNCE + " TEXT,"
            + KEY_QUEST_IMAGE + " TEXT,"
            + KEY_QUEST_FILTER + " TEXT"
            + ")";

    private static final String TABLE_QUEST_B_STRUCTURE  = "("

            + KEY_QUEST_B_ID + " TEXT,"
            + KEY_QUEST_B_CAT_ID + " TEXT,"
            + KEY_QUEST_B_LEVEL + " TEXT,"
            + KEY_QUEST_B_LEVEL_GLOBAL + " INTEGER DEFAULT 0,"
            + KEY_QUEST_B_MODE + " INTEGER DEFAULT 0,"
            + KEY_QUEST_B_QUEST + " TEXT,"
            + KEY_QUEST_B_TASK + " TEXT,"
            + KEY_QUEST_B_CORRECT + " TEXT,"
            + KEY_QUEST_B_OPTIONS + " TEXT,"
            + KEY_QUEST_B_PRONOUNCE + " TEXT,"
            + KEY_QUEST_B_IMAGE + " TEXT,"
            + KEY_QUEST_B_PARAMS + " TEX,"
            + KEY_QUEST_B_FILTER + " TEXT"
            + ")";

    private static final String TABLE_QSTATS_STRUCTURE  = "("

            + KEY_QSTATS_ID + " TEXT,"
            + KEY_QSTATS_COUNT_TR + " INTEGER DEFAULT 0,"
            + KEY_QSTATS_COUNT_AUDIO + " INTEGER DEFAULT 0,"
            + KEY_QSTATS_CORRECT + " INTEGER DEFAULT 0,"
            + KEY_QSTATS_CORRECT_TIME + " INTEGER DEFAULT 0,"
            + KEY_QSTATS_ERROR + " INTEGER DEFAULT 0,"
            + KEY_QSTATS_ERROR_TIME + " INTEGER DEFAULT 0"
            + ")";


    private static final String TABLE_USER_STRUCTURE = "("
            + KEY_USER_ITEM_ID + " TEXT,"
            + KEY_ITEM_INFO + " TEXT,"
            + KEY_ITEM_PROGRESS + " INTEGER,"
            + KEY_ITEM_ERRORS + " INTEGER DEFAULT 0,"
            + KEY_ITEM_SCORE + " INTEGER DEFAULT 0,"
            + KEY_ITEM_STATUS + " INTEGER DEFAULT 0,"
            + KEY_ITEM_STARRED + " INTEGER DEFAULT 0,"
            + KEY_ITEM_TIME + " INTEGER,"
            + KEY_ITEM_TIME_STARRED + " INTEGER,"
            + KEY_ITEM_TIME_ERROR + " INTEGER"
            + ")";

    private static final String TABLE_DETAIL_STRUCTURE = "("
            + KEY_DETAIL_ID + " TEXT,"
            + KEY_DETAIL_TITLE + " TEXT,"
            + KEY_DETAIL_DESC + " TEXT,"
            + KEY_DETAIL_IMAGE + " TEXT,"
            + KEY_DETAIL_IMG_INFO + " TEXT"
            + ")";


    private static final String TABLE_TEST_STRUCTURE = "("
            + KEY_TEST_TAG + " TEXT,"
            + KEY_TEST_PROGRESS + " INTEGER,"
            + KEY_TEST_TIME + " INTEGER"
            + ")";



    private static final String TABLE_BOOKMARK_STRUCTURE = "("
            + KEY_BOOKMARK_ITEM + " TEXT,"
            + KEY_BOOKMARK_PARENT + " TEXT,"
            + KEY_BOOKMARK_TIME + " INTEGER,"
            + KEY_BOOKMARK_TYPE + " TEXT,"
            + KEY_BOOKMARK_INFO + " TEXT,"
            + KEY_BOOKMARK_FILTER + " TEXT"
            + ")";


    private static final String TABLE_NOTES_STRUCTURE = "("
            + KEY_NOTE_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + KEY_NOTE_ID + " TEXT,"
            + KEY_NOTE_TITLE + " TEXT,"
            + KEY_NOTE_TEXT + " TEXT,"
            + KEY_NOTE_ICON+ " TEXT,"
            + KEY_NOTE_INFO + " TEXT,"
            + KEY_NOTE_STATUS + " TEXT,"
            + KEY_NOTE_TYPE + " TEXT,"
            + KEY_NOTE_PARAMS + " TEXT,"
            + KEY_NOTE_FILTER + " TEXT,"
            + KEY_NOTE_PARENT + " TEXT,"
            + KEY_NOTE_ORDER + " INTEGER,"
            + KEY_NOTE_CREATED + " INTEGER,"
            + KEY_NOTE_UPDATED + " INTEGER,"
            + KEY_NOTE_UPDATED_SORT + " INTEGER"
            + ")";


    private static final String TABLE_USER_DATA_CATS_STRUCTURE = "("
            + KEY_UCAT_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + KEY_UCAT_ID + " TEXT,"
            + KEY_UCAT_TITLE + " TEXT,"
            + KEY_UCAT_DESC + " TEXT,"
            + KEY_UCAT_ICON+ " TEXT,"
            + KEY_UCAT_INFO + " TEXT,"
            + KEY_UCAT_STATUS + " TEXT,"
            + KEY_UCAT_TYPE + " TEXT,"
            + KEY_UCAT_FILTER + " TEXT,"
            + KEY_UCAT_PARAMS + " TEXT,"
            + KEY_UCAT_PARENT + " TEXT,"
            + KEY_UCAT_ORDER + " INTEGER,"
            + KEY_UCAT_CREATED + " INTEGER,"
            + KEY_UCAT_UPDATED + " INTEGER,"
            + KEY_UCAT_UPDATED_SORT + " INTEGER"
            + ")";


    private static final String TABLE_USER_DATA_ITEMS_STRUCTURE = "("
            + KEY_UDATA_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + KEY_UDATA_ID + " TEXT,"
            + KEY_UDATA_TEXT + " TEXT,"
            + KEY_UDATA_TRANSLATE + " TEXT,"
            + KEY_UDATA_TRANSCRIPT + " TEXT,"
            + KEY_UDATA_GRAMMAR + " TEXT,"
            + KEY_UDATA_SOUND + " TEXT,"
            + KEY_UDATA_INFO + " TEXT,"
            + KEY_UDATA_IMAGE + " TEXT,"
            + KEY_UDATA_STATUS + " TEXT,"
            + KEY_UDATA_FILTER + " TEXT,"
            + KEY_UDATA_ORDER + " INTEGER,"
            + KEY_UDATA_CREATED + " INTEGER,"
            + KEY_UDATA_UPDATED + " INTEGER,"
            + KEY_UDATA_UPDATED_SORT + " INTEGER"
            + ")";


    private static final String TABLE_UCAT_UDATA_STRUCTURE = "("
            + KEY_UDC_UCAT_ID + " TEXT,"
            + KEY_UDC_UDATA_ID + " TEXT"
            + ")";


    private static final String TABLE_TASKS_STRUCTURE = "("
            + KEY_TASK_ID + " TEXT,"
            + KEY_TASK_SKIPPED_COUNT + " INTEGER,"
            + KEY_TASK_SKIPPED_TIME + " INTEGER,"
            + KEY_TASK_PRIORITY + " INTEGER,"
            + KEY_TASK_INFO + " TEXT,"
            + KEY_TASK_STATUS + " TEXT,"
            + KEY_TASK_TYPE + " TEXT,"
            + KEY_TASK_PARAMS + " TEXT,"
            + KEY_TASK_FILTER + " TEXT,"
            + KEY_TASK_UPDATED + " INTEGER,"
            + KEY_TASK_UPDATED_SORT + " INTEGER"
            + ")";



    private static final String TABLE_ITEMS_STRUCTURE = TABLE_ITEMS_DATA + TABLE_ITEM_STRUCTURE;

    private static final String TABLE_QUESTS_STRUCTURE = TABLE_QUEST_DATA + TABLE_QUEST_STRUCTURE;


    private static final String TABLE_USER_ITEMS_STRUCTURE = TABLE_USER_DATA + TABLE_USER_STRUCTURE;

    private static final String TABLE_DETAILS_STRUCTURE = TABLE_DETAILS_DATA + TABLE_DETAIL_STRUCTURE;



    private static final String CREATE_USER_ITEMS_TABLE = "CREATE TABLE " + TABLE_USER_ITEMS_STRUCTURE;

    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS_STRUCTURE;


    private static final String CREATE_USER_ITEMS_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_ITEMS_STRUCTURE;

    private static final String CREATE_ITEMS_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS_STRUCTURE;

    private static final String CREATE_QUESTS_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_QUESTS_STRUCTURE;

    private static final String CREATE_QUESTS_B_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_QUEST_BUILD + TABLE_QUEST_B_STRUCTURE;

    private static final String CREATE_STATS_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_QSTATS_DATA + TABLE_QSTATS_STRUCTURE;

    private static final String CREATE_DETAILS_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_DETAILS_STRUCTURE;


    private String CREATE_CATDATA_TABLE = "CREATE TABLE " + TABLE_CAT_DATA + "("
            + KEY_CAT_ID + " TEXT,"
            + KEY_CAT_PROGRESS + " INTEGER"
            + ")";


    private static final String CREATE_TESTS_TABLE = "CREATE TABLE " + TABLE_TESTS_DATA + TABLE_TEST_STRUCTURE;
    private static final String CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + TABLE_BOOKMARKS_DATA + TABLE_BOOKMARK_STRUCTURE;
    private static final String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES_DATA + TABLE_NOTES_STRUCTURE;



    private static final String CREATE_UCATS_TABLE = "CREATE TABLE " + TABLE_USER_DATA_CATS + TABLE_USER_DATA_CATS_STRUCTURE;
    private static final String CREATE_UDATA_TABLE = "CREATE TABLE " + TABLE_USER_DATA_ITEMS+ TABLE_USER_DATA_ITEMS_STRUCTURE;
    private static final String CREATE_UDC_TABLE = "CREATE TABLE " + TABLE_UCAT_UDATA + TABLE_UCAT_UDATA_STRUCTURE;


    private static final String CREATE_BOOKMARKS_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARKS_DATA + TABLE_BOOKMARK_STRUCTURE;

    private static final String CREATE_NOTES_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES_DATA + TABLE_NOTES_STRUCTURE;

    private static final String CREATE_TASKS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TASKS_DATA + TABLE_TASKS_STRUCTURE;


    private int data_mode = 0;
    private boolean speaking_mode;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        cntx = context;

        checkMode();

        speaking_mode = appSettings.getBoolean("set_speak", true);

    }

    public void checkMode() {
        appSettings = PreferenceManager.getDefaultSharedPreferences(cntx);
        String modeLevel = appSettings.getString(SET_DATA_MODE, cntx.getResources().getString(R.string.set_data_mode_default_value));
        data_mode = Integer.parseInt(modeLevel); ///TODO check for improvement
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        populateDB(db); //// Adding items and details

        db.execSQL(CREATE_CATDATA_TABLE);
        db.execSQL(CREATE_USER_ITEMS_TABLE);
        db.execSQL(CREATE_TESTS_TABLE);
        db.execSQL(CREATE_BOOKMARKS_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);

        db.execSQL(CREATE_UCATS_TABLE);
        db.execSQL(CREATE_UDATA_TABLE);
        db.execSQL(CREATE_UDC_TABLE);

        db.execSQL(CREATE_TASKS_TABLE);
        db.execSQL(CREATE_STATS_TABLE_IF_EXISTS);

        InfoNotesManager infoNotesManager = new InfoNotesManager(cntx);
        infoNotesManager.postStartNotes(DBHelper.this, db);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        populateDB(db);
        sanitizeDB(db);

        if (newVersion > 2) {
            db.execSQL(CREATE_TASKS_TABLE); // creates tables if not exist
            db.execSQL(CREATE_STATS_TABLE_IF_EXISTS);
        }

        InfoNotesManager infoNotesManager = new InfoNotesManager(cntx);
        infoNotesManager.postUpdateNotes(DBHelper.this, db, newVersion);

    }


    public void populateDB() {

        SQLiteDatabase db = this.getWritableDatabase();

        populateDB(db);

        db.close();

    }

    private void populateDB(SQLiteDatabase db) {

        DataFromJson dataFromJson= new DataFromJson(cntx);
        ArrayList<DataItem> allItems = dataFromJson.getAllData();


        DetailFromJson detailFromJson= new DetailFromJson(cntx);
        ArrayList<DetailItem> allDetails = detailFromJson.getAllData();


        ArrayList<QuestData> allQuests = detailFromJson.getAllQuests();


        db.beginTransaction();


        try {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_DATA);
            db.execSQL(CREATE_ITEMS_TABLE_IF_EXISTS);

            for (DataItem item: allItems) {



                String searchTerm = item.item + " " + item.info;

                String asciiName = Normalizer.normalize(searchTerm, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

                String removeAccents = searchTerm.replaceAll("[\u0301]", "");

                String formattedText= searchTerm + " " + asciiName + " " + removeAccents;

                ContentValues values = new ContentValues();
                values.put(KEY_ITEM_ID, item.id);
                values.put(KEY_ITEM_TITLE, item.item);
                values.put(KEY_ITEM_DESC, item.info);
                values.put(KEY_ITEM_IMAGE, item.image);
                values.put(KEY_ITEM_INFO_1, item.item_info_1);
                values.put(KEY_ITEM_PRONOUNCE, item.pronounce);
                values.put(KEY_ITEM_BASE, item.base);
                values.put(KEY_ITEM_GRAMMAR, item.grammar);
                values.put(KEY_ITEM_DIVIDER, item.divider);
                values.put(KEY_ITEM_FILTER, item.filter);
                values.put(KEY_ITEM_SEARCH, formattedText);
                values.put(KEY_ITEM_MODE, item.mode);
                values.put(KEY_ITEM_TRANS1, item.trans1);
                values.put(KEY_ITEM_TRANS2, item.trans2);
                db.insert(TABLE_ITEMS_DATA, null, values);
            }


            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }


        db.beginTransaction();

        try {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS_DATA);
            db.execSQL(CREATE_DETAILS_TABLE_IF_EXISTS);

            for (DetailItem item: allDetails) {
                ContentValues values = new ContentValues();
                values.put(KEY_DETAIL_ID, item.id);
                values.put(KEY_DETAIL_TITLE, item.title);
                values.put(KEY_DETAIL_DESC, item.desc);
                values.put(KEY_DETAIL_IMAGE, item.image);
                values.put(KEY_DETAIL_IMG_INFO, item.img_info);
                db.insert(TABLE_DETAILS_DATA, null, values);
            }


            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.beginTransaction();
        try {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEST_DATA);
            db.execSQL(CREATE_QUESTS_TABLE_IF_EXISTS);

            for (QuestData item: allQuests) {

                ContentValues values = new ContentValues();

                values.put(KEY_QUEST_ID, item.getId());
                values.put(KEY_QUEST_CAT_ID, item.getCategoryId() );
                values.put(KEY_QUEST_LEVEL, item.getLevel() );
                values.put(KEY_QUEST_LEVEL_GLOBAL, item.getLevelGlobal() );
                values.put(KEY_QUEST_MODE, item.getMode() );

                values.put(KEY_QUEST_QUEST, item.getQuest() );
                values.put(KEY_QUEST_TASK, item.getTask());
                values.put(KEY_QUEST_OPTIONS, item.getOptions());
                values.put(KEY_QUEST_CORRECT, item.getCorrect());
                values.put(KEY_QUEST_PRONOUNCE, item.getPronounce());
                values.put(KEY_QUEST_IMAGE, item.getImage() );
                values.put(KEY_QUEST_FILTER, item.getFilter() );

                db.insert(TABLE_QUEST_DATA, null, values);

            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }


        ArrayList<QuestData> allBuildQuests = detailFromJson.getAllBuildQuests();

        db.beginTransaction();
        try {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEST_BUILD);
            db.execSQL(CREATE_QUESTS_B_TABLE_IF_EXISTS);

            for (QuestData item: allBuildQuests) {

                ContentValues values = new ContentValues();

                values.put(KEY_QUEST_B_ID, item.getId());
                values.put(KEY_QUEST_B_CAT_ID, item.getCategoryId() );
                values.put(KEY_QUEST_B_LEVEL, item.getLevel() );
                values.put(KEY_QUEST_B_LEVEL_GLOBAL, item.getLevelGlobal() );
                values.put(KEY_QUEST_B_MODE, item.getMode() );

                values.put(KEY_QUEST_B_QUEST, item.getQuest() );
                values.put(KEY_QUEST_B_TASK, item.getTask());
                values.put(KEY_QUEST_B_OPTIONS, item.getOptions());
                values.put(KEY_QUEST_B_CORRECT, item.getCorrect());
                values.put(KEY_QUEST_B_PRONOUNCE, item.getPronounce());
                values.put(KEY_QUEST_B_IMAGE, item.getImage() );
                values.put(KEY_QUEST_B_PARAMS, item.getParams() );
                values.put(KEY_QUEST_B_FILTER, item.getFilter() );

                //Log.i("Quest", "quest: " + values.getAsString(KEY_QUEST_B_CORRECT));

                db.insert(TABLE_QUEST_BUILD, null, values);

            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }


    }


    public void getUpdatedTable(SQLiteDatabase db) {

        db.beginTransaction();

        try {
            db.execSQL(CREATE_USER_ITEMS_TABLE_IF_EXISTS);

            db.execSQL("DROP TABLE IF EXISTS temp_" + TABLE_USER_DATA);

            List<String> columns = GetColumns(db, TABLE_USER_DATA);

            db.execSQL("ALTER TABLE " + TABLE_USER_DATA + " RENAME TO temp_" + TABLE_USER_DATA);

            db.execSQL(CREATE_USER_ITEMS_TABLE);

            columns.retainAll(GetColumns(db, TABLE_USER_DATA));

            String cols = dbJoin(columns, ",");

            db.execSQL(String.format(
                    "INSERT INTO %s (%s) SELECT %s from temp_%s",
                    TABLE_USER_DATA, cols, cols, TABLE_USER_DATA));

            db.execSQL("DROP TABLE temp_" + TABLE_USER_DATA);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }

    private static List<String> GetColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;
        try {
            c = db.rawQuery("select * from " + tableName + " limit 1", null);
            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    private static String dbJoin(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append((String) list.get(i));
        }
        return buf.toString();
    }


    public void setTestResult(String cat_id, int ex_type, int result, Boolean forceSave) {
        SQLiteDatabase db = this.getWritableDatabase();
        String action = "nothing";

        String ex_id = cat_id+"_"+ex_type;

        if (ex_id.contains("revise_") || ex_id.contains("practice_")) {
            ex_id = cat_id;
        }

        if (result > 100) result = 100;


        Cursor cursor = db.query(TABLE_TESTS_DATA,  null, KEY_TEST_TAG +" = ?",
                new String[] { ex_id }, null, null, null);

        if ( cursor.moveToFirst() ) {
            cursor.moveToFirst();
            int exSavedData = cursor.getInt(cursor.getColumnIndex(KEY_TEST_PROGRESS));
            if (result > exSavedData || forceSave) {
                action = "update";
            }
        } else {
            action = "insert";
        }
        ContentValues values = new ContentValues();
        values.put(KEY_TEST_TAG, ex_id);
        values.put(KEY_TEST_PROGRESS, result);
        values.put(KEY_TEST_TIME, System.currentTimeMillis() );

        if ( action.equals("update")) {
            db.update(TABLE_TESTS_DATA, values, KEY_TEST_TAG + " = ?", new String[]{ex_id});
        } else if (action.equals("insert")){
            db.insert(TABLE_TESTS_DATA, null, values);
        }

        //Toast.makeText(cntx, "Action: "+action + "; id: "+ ex_id + "; r: "+ result, Toast.LENGTH_SHORT).show();

        cursor.close();

        db.close();
    }


    public int setBookmark(String bookmark, String parent, String param) {

        SQLiteDatabase db = this.getWritableDatabase();
        String action;

        int status = OUTCOME_NONE;

        Cursor cursor = db.query(TABLE_BOOKMARKS_DATA,  null,
                KEY_BOOKMARK_ITEM +" = ? AND " + KEY_BOOKMARK_PARENT + " = ?",
                new String[] { bookmark, parent }, null, null, null);

        if ( cursor.moveToFirst() ) {
            action = ACTION_DELETE;
        } else {
            action = ACTION_CREATE;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_BOOKMARK_ITEM, bookmark);
        values.put(KEY_BOOKMARK_PARENT, parent);
        values.put(KEY_BOOKMARK_TIME, System.currentTimeMillis() );

        if (action.equals(ACTION_DELETE)) {
            db.delete(TABLE_BOOKMARKS_DATA, KEY_BOOKMARK_ITEM +" = ? AND " + KEY_BOOKMARK_PARENT + " = ?",    new String[]{bookmark, parent});
            status = OUTCOME_REMOVED;

        } else if (action.equals(ACTION_CREATE)){

            if (param.equals(PARAM_LIMIT_REACHED)) {
                status = OUTCOME_LIMIT;
            } else {
                db.insert(TABLE_BOOKMARKS_DATA, null, values);
                setStarredTab(1);
                status = OUTCOME_ADDED;
            }

        }


        cursor.close();
        db.close();

        return status;
    }



    public String[] createUCat(String catTitle, String desc) {

        SQLiteDatabase db = this.getWritableDatabase();

        long time = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_UCAT_TITLE, catTitle);
        values.put(KEY_UCAT_DESC, desc);
        values.put(KEY_UCAT_PARAMS, "");
        values.put(KEY_UCAT_PARENT, "");
        values.put(KEY_UCAT_CREATED, time );
        values.put(KEY_UCAT_UPDATED, time );
        values.put(KEY_UCAT_UPDATED_SORT, time );

       long cat_id  = db.insert(TABLE_USER_DATA_CATS, null, values);


        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_PRIMARY_ID +" = ?",

                new String[] { String.valueOf(cat_id)}, null, null, null);


        if (cursor.moveToFirst() ) {

            values = new ContentValues();
            values.put(KEY_UCAT_ID, UC_PREFIX + cat_id);


            db.update(TABLE_USER_DATA_CATS, values,
                    KEY_UCAT_PRIMARY_ID +" = ?",
                    new String[] { String.valueOf(cat_id)});

        }

        cursor.close();
        db.close();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = sdf.format(new Date(time));

        return new String[] { UC_PREFIX + cat_id , date };
    }


    public String[] createGroup(DataObject groupObject) {

        SQLiteDatabase db = this.getWritableDatabase();

        long time = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_UCAT_TITLE, groupObject.title);
        values.put(KEY_UCAT_DESC, groupObject.desc);
        values.put(KEY_UCAT_ICON, groupObject.image);
        values.put(KEY_UCAT_PARAMS, "");
        values.put(KEY_UCAT_PARENT, "");
        values.put(KEY_UCAT_TYPE, PARAM_GROUP);
        values.put(KEY_UCAT_CREATED, time );
        values.put(KEY_UCAT_UPDATED, time );
        values.put(KEY_UCAT_UPDATED_SORT, time );

        long cat_id  = db.insert(TABLE_USER_DATA_CATS, null, values);


        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_PRIMARY_ID +" = ?",

                new String[] { String.valueOf(cat_id)}, null, null, null);


        if (cursor.moveToFirst() ) {

            values = new ContentValues();
            values.put(KEY_UCAT_ID, UC_PREFIX + cat_id);


            db.update(TABLE_USER_DATA_CATS, values,
                    KEY_UCAT_PRIMARY_ID +" = ?",
                    new String[] { String.valueOf(cat_id)});

        }

        cursor.close();
        db.close();


        return new String[] { UC_PREFIX + cat_id };
    }


    public void updateGroup(DataObject groupObject) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UCAT_TITLE, groupObject.title);
        values.put(KEY_UCAT_DESC, groupObject.desc);
        values.put(KEY_UCAT_ICON, groupObject.image);


        db.update(TABLE_USER_DATA_CATS, values,
                KEY_UCAT_ID +" = ?",
                new String[] { String.valueOf(groupObject.id)});


        db.close();


    }

    public int insertUserDataItems(ArrayList<DataItem> list) {

        int n = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {

            for (int i = 0; i < list.size(); i ++) {
                DataItem dataItem = list.get(i);
                createUData(db, dataItem, i);
            }

            n = list.size();
            db.setTransactionSuccessful();


        } finally {
            db.endTransaction();
        }


        db.close();

        return n;
    }


    public void createUData(DataItem dataItem) {

        SQLiteDatabase db = this.getWritableDatabase();

        createUData(db, dataItem, 0);

        db.close();

    }


    public void createUData( SQLiteDatabase db, DataItem dataItem, int timeFix) {

        long time = System.currentTimeMillis() + timeFix;

        ContentValues values = new ContentValues();

        values.put(KEY_UDATA_TEXT, dataItem.item);
        values.put(KEY_UDATA_TRANSLATE, dataItem.info);
        values.put(KEY_UDATA_TRANSCRIPT, dataItem.trans1);
        values.put(KEY_UDATA_SOUND, dataItem.sound);
        values.put(KEY_UDATA_GRAMMAR, dataItem.grammar);
        values.put(KEY_UDATA_INFO, dataItem.item_info_1);

        values.put(KEY_UDATA_CREATED, time );
        values.put(KEY_UDATA_UPDATED, time );
        values.put(KEY_UDATA_UPDATED_SORT, time );

        long cat_id  = db.insert(TABLE_USER_DATA_ITEMS, null, values);

        Cursor cursor = db.query(TABLE_USER_DATA_ITEMS,  null,
                KEY_UDATA_PRIMARY_ID +" = ?",

                new String[] { String.valueOf(cat_id)}, null, null, null);


        if (cursor.moveToFirst() ) {

            values = new ContentValues();
            values.put(KEY_UDATA_ID, "ud_"+cat_id);

            db.update(TABLE_USER_DATA_ITEMS, values,
                    KEY_UDATA_PRIMARY_ID +" = ?",
                    new String[] { String.valueOf(cat_id)});

            ContentValues udcValues = new ContentValues();

            udcValues.put(KEY_UDC_UDATA_ID, "ud_"+cat_id);
            udcValues.put(KEY_UDC_UCAT_ID, dataItem.cat);

            db.insert(TABLE_UCAT_UDATA, null, udcValues);

        }

        cursor.close();





    }


    public DataItem updateUData(DataItem dataItem) {

        SQLiteDatabase db = this.getWritableDatabase();

        dataItem.time = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_UDATA_TEXT, dataItem.item);
        values.put(KEY_UDATA_TRANSLATE, dataItem.info);
        values.put(KEY_UDATA_TRANSCRIPT, dataItem.trans1);
        values.put(KEY_UDATA_SOUND, dataItem.sound);
        values.put(KEY_UDATA_GRAMMAR, dataItem.grammar);
        values.put(KEY_UDATA_INFO, dataItem.item_info_1);
        values.put(KEY_UDATA_UPDATED, dataItem.time );

        Cursor cursor = db.query(TABLE_USER_DATA_ITEMS,  null,
                KEY_UDATA_ID +" = ?",
                new String[] { dataItem.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            db.update(TABLE_USER_DATA_ITEMS, values,
                    KEY_UDATA_ID +" = ?",
                    new String[] { dataItem.id });

        }

        cursor.close();
        db.close();

        return dataItem;
    }


    public DataObject updateUCatTitle(DataObject dataObject) {

        SQLiteDatabase db = this.getWritableDatabase();

        dataObject.time_updated = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_UCAT_TITLE, dataObject.title);
        values.put(KEY_UCAT_DESC, dataObject.desc);
        values.put(KEY_UCAT_UPDATED, dataObject.time_updated );

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",
                new String[] { dataObject.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            db.update(TABLE_USER_DATA_CATS, values,
                    KEY_UCAT_ID +" = ?",
                    new String[] { dataObject.id });

        }

        cursor.close();
        db.close();

        return dataObject;
    }

    public DataObject updateUCatSortTime(DataObject dataObject) {

        SQLiteDatabase db = this.getWritableDatabase();

        dataObject.time_updated = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_UCAT_UPDATED_SORT, dataObject.time_updated );

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",
                new String[] { dataObject.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            db.update(TABLE_USER_DATA_CATS, values,
                    KEY_UCAT_ID +" = ?",
                    new String[] { dataObject.id });

        }

        cursor.close();
        db.close();

        return dataObject;
    }

    public void updateUDataSortTime(String udata_id, long time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_UDATA_UPDATED_SORT, time );

        db.update(TABLE_USER_DATA_ITEMS, values,
                    KEY_UDATA_ID +" = ?",
                    new String[] { udata_id });

        db.close();

    }



    public void updateNoteSortTime(NoteData dataObject) {

        SQLiteDatabase db = this.getWritableDatabase();

        dataObject.time_updated = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_NOTE_UPDATED_SORT, dataObject.time_updated );

        Cursor cursor = db.query(TABLE_NOTES_DATA,  null,
                KEY_NOTE_ID +" = ?",
                new String[] { dataObject.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            db.update(TABLE_NOTES_DATA, values,
                    KEY_NOTE_ID +" = ?",
                    new String[] { dataObject.id });

            //Toast.makeText(cntx, "Time: " + dataObject.time_updated, Toast.LENGTH_SHORT).show();
        }



        cursor.close();
        db.close();

    }


    public DataObject archiveUCat(DataObject dataObject) {
       return parentUCat(dataObject, PARAM_UCAT_ARCHIVE);
    }

    public DataObject unarchiveUCat(DataObject dataObject) {
        return parentUCat(dataObject, PARAM_UCAT_ROOT);
    }

    public DataObject parentUCat(DataObject dataObject, String parent) {

        SQLiteDatabase db = this.getWritableDatabase();

        dataObject.parent = parent;
        dataObject.time_updated = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(KEY_UCAT_PARENT, dataObject.parent);
        values.put(KEY_UCAT_UPDATED_SORT, dataObject.time_updated );

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",
                new String[] { dataObject.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            db.update(TABLE_USER_DATA_CATS, values,
                    KEY_UCAT_ID +" = ?",
                    new String[] { dataObject.id });

        }

        cursor.close();
        db.close();

        return dataObject;
    }


    public void parentNote(String noteId, String parent) {

        SQLiteDatabase db = this.getWritableDatabase();

         long time = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_PARENT, parent);
       // values.put(KEY_NOTE_UPDATED, dataObject.time_updated);
        values.put(KEY_NOTE_UPDATED_SORT, time );


        int n = db.update(TABLE_NOTES_DATA, values,
                    KEY_NOTE_ID +" = ?",
                    new String[] { noteId });


        //Toast.makeText(cntx, "N " + n, Toast.LENGTH_SHORT).show();

        db.close();


    }





    public DataObject getUCatParams(DataObject dataObject) {

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",
                new String[] { dataObject.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            dataObject.params = cursor.getString(cursor.getColumnIndex(KEY_UCAT_PARAMS));

        }

        cursor.close();
        db.close();

        return dataObject;
    }


    public void updateUCatParams(DataObject dataObject) {

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put(KEY_UCAT_PARAMS, dataObject.params);

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",
                new String[] { dataObject.id }, null, null, null);

        if (cursor.moveToFirst() ) {

            db.update(TABLE_USER_DATA_CATS, values,
                    KEY_UCAT_ID +" = ?",
                    new String[] { dataObject.id });

        }

        cursor.close();
        db.close();

    }





    public int deleteUCat(String id) {

        int deleted = 0;

        SQLiteDatabase db = this.getWritableDatabase();


        /// delete item from ucats
        ///// delete all items from udata
        ////// delete all items from ucats - udata

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",
                new String[] { id }, null, null, null);

        if (cursor.moveToFirst() ) {
            db.delete(TABLE_USER_DATA_CATS, KEY_UCAT_ID +" = ?",  new String[] { id });
            deleted = deleteUDataFromUcat(db, id);
        }

        cursor.close();

        String idPrefix = id+"_%";

        db.delete(TABLE_TESTS_DATA, KEY_TEST_TAG +" LIKE ?", new String[]{idPrefix});

        db.delete(TABLE_UCAT_UDATA, KEY_UDC_UCAT_ID +" = ?",  new String[] { id });


        cleanUDataFromUser(db);

        db.close();

        return deleted;
    }





    private int deleteUDataFromUcat(SQLiteDatabase db, String ucat_id) {

        int num = 0;

        String query = "SELECT * FROM "
                +TABLE_UCAT_UDATA +" a INNER JOIN " + TABLE_USER_DATA_ITEMS
                +" b ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID
                +" WHERE a." + KEY_UDC_UCAT_ID + " = ? ";


        Cursor cursor = db.rawQuery(query, new String[]{ucat_id});

        //Toast.makeText(cntx, "Found: " + cursor.getCount(), Toast.LENGTH_SHORT).show();


        try {
            while (cursor.moveToNext()) {

               String ud_id = cursor.getString(cursor.getColumnIndex(KEY_UDATA_ID));

               int deleted = db.delete(TABLE_USER_DATA_ITEMS, KEY_UDATA_ID +" = ?",  new String[] { ud_id });

               //Toast.makeText(cntx, "Deleted: " + ud_id + " - " + deleted, Toast.LENGTH_SHORT).show();

               num = num + deleted;

            }

        } finally {
            cursor.close();
        }



        ///Toast.makeText(cntx, "Del: " + del, Toast.LENGTH_SHORT).show();

        return num;
    }




    public int deleteData(String id) {

        int deleted = 0;

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query(TABLE_USER_DATA_ITEMS,  null,
                KEY_UDATA_ID +" = ?",
                new String[] { id }, null, null, null);

        if (cursor.moveToFirst() ) {

            deleted = db.delete(TABLE_USER_DATA_ITEMS, KEY_UDATA_ID +" = ?",  new String[] { id });

            db.delete(TABLE_UCAT_UDATA, KEY_UDATA_ID +" = ?",  new String[] { id });

        }

        cursor.close();


        cleanUDataFromUser(db);


        db.close();

        return deleted;
    }


    private void cleanUDataFromUser(SQLiteDatabase db) {

        String query = "SELECT * FROM "  +TABLE_USER_DATA
                +" a LEFT JOIN " + TABLE_USER_DATA_ITEMS

                +" b ON a." + KEY_USER_ITEM_ID + " = b." + KEY_UDATA_ID
                +" WHERE (a." + KEY_USER_ITEM_ID + " LIKE '"+ UD_PREFIX +"%') AND ( b." + KEY_UDATA_ID + " IS NULL)";


        Cursor cursor = db.rawQuery(query, null);


        try {

            while (cursor.moveToNext()) {

                String id = cursor.getString(cursor.getColumnIndex(KEY_USER_ITEM_ID));

                db.delete(TABLE_USER_DATA, KEY_USER_ITEM_ID +" = ?",  new String[] { id });

            }

        } finally {
            cursor.close();
        }


    }




    public DataItem getUData(String id) {

        DataItem dataItem = new DataItem();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USER_DATA_ITEMS,  null,
                KEY_UDATA_ID +" = ?",

                new String[] { id }, null, null, null);

        if (cursor.moveToFirst() ) {
            dataItem = getDataItemFromUDATA(cursor);
        }

        cursor.close();
        db.close();

        return dataItem;
    }



    public DataItem getUDataAllInfo(String id) {

        DataItem dataItem = new DataItem();

        SQLiteDatabase db = this.getWritableDatabase();


        String query = "SELECT * FROM "  + TABLE_USER_DATA_ITEMS  +" a "

                +" LEFT JOIN " + TABLE_USER_DATA +" b "
                +" ON a." + KEY_UDATA_ID + " = b." + KEY_USER_ITEM_ID

                +" WHERE a." + KEY_UDATA_ID + " = ?";

        Cursor cursor = db.rawQuery(query,  new String[] { id });


        if (cursor.moveToFirst() ) {
            dataItem = getAllInfoUdataFromDB(cursor);
        }

        cursor.close();
        db.close();

        return dataItem;
    }


    public DataObject getUCat(String id) {

        DataObject dataObject = new DataObject();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null,
                KEY_UCAT_ID +" = ?",

                new String[] { id }, null, null, null);

        if (cursor.moveToFirst() ) {
            dataObject = getDataFromUCAT(cursor);
        }

        cursor.close();
        db.close();

        return dataObject;
    }



    public ArrayList<DataItem> getUDataList(String ucat_id) {
        return  getUDataList(ucat_id, "");
    }



    public ArrayList<DataItem> getUDataList(String ucat_id, String sort) {

        String sorting = "DESC";

        if (sort.equals(UCAT_PARAM_SORT_ASC)) sorting = "ASC";


        ArrayList<DataItem> dataItems = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " +TABLE_UCAT_UDATA +" a "

                + " INNER JOIN " + TABLE_USER_DATA_ITEMS + " b "
                + " ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID

                + " LEFT JOIN " + TABLE_USER_DATA + " c "
                + " ON b." + KEY_UDATA_ID + " = c." + KEY_USER_ITEM_ID

                + " WHERE a." + KEY_UDC_UCAT_ID + " = ? ORDER BY b." + KEY_UDATA_UPDATED_SORT + " " + sorting;

        Cursor cursor = db.rawQuery(query, new String[]{ucat_id});

        try {
            while (cursor.moveToNext()) {

                DataItem dataItem = getAllInfoUdataFromDB(cursor);
                dataItem.cat = ucat_id;


                dataItems.add(dataItem);

            }

        } finally {
            cursor.close();
        }

        db.close();

        return dataItems;
    }


    public int checkUcaDataListSize(String id) {


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_UCAT_UDATA,  null,
                KEY_UDC_UCAT_ID +" = ? ",
                new String[] { id }, null, null, null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }


    public String[] getUCatsCounts() {


        ArrayList<DataObject> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  new String[] {KEY_UCAT_ID , KEY_UCAT_TYPE},
                null, null, null, null, KEY_UCAT_UPDATED_SORT + " DESC");

        int catCount = cursor.getCount();

        int itemsCount = 0;

        try {
            while (cursor.moveToNext()) {

                DataObject category = new DataObject();
                category.id = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ID));
                category.type = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TYPE));
                if (category.type == null) category.type = "";

                if (!category.type.contains(PARAM_GROUP))
                    categories.add(category);
            }

        } finally {
            cursor.close();
        }


        String query = "SELECT * FROM "
                + TABLE_UCAT_UDATA +" a INNER JOIN " + TABLE_USER_DATA_ITEMS
                +" b ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID
                +" WHERE a." + KEY_UDC_UCAT_ID + " = ?";


        for (DataObject category: categories) {

            Cursor itemsCursor = db.rawQuery(query, new String[]{category.id});

            itemsCount += itemsCursor.getCount();

            itemsCursor.close();

        }

        db.close();



        return new String[] {String.valueOf(categories.size()), String.valueOf(itemsCount)};
    }


    public ArrayList<DataObject> getUCatsListUnpaid(int limit) {

        ArrayList<DataObject> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null, null,
                null, null, null,
                KEY_UCAT_UPDATED_SORT + " DESC");

        int topicCount = 0;
        int groupCount = 0;

        try {
            while (cursor.moveToNext()) {

                DataObject category = new DataObject();
                category.id = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ID));
                category.title = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TITLE));
                category.desc = cursor.getString(cursor.getColumnIndex(KEY_UCAT_DESC));
                category.parent = cursor.getString(cursor.getColumnIndex(KEY_UCAT_PARENT));

                category.time_created = cursor.getLong(cursor.getColumnIndex(KEY_UCAT_CREATED));

                category.type = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TYPE));
                if (category.type == null) category.type = "";

                category.image = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ICON));
                if (category.image == null) category.image = "";

                if (category.type.equals(PARAM_GROUP) ) {
                    if (groupCount < GROUPS_UNPAID_LIMIT) {
                        categories.add(category);
                        groupCount++;
                    }
                } else {
                    if (topicCount < UCATS_UNPAID_LIMIT) {
                        categories.add(category);
                        topicCount++;
                    }
                }

            }

        } finally {
            cursor.close();
        }

        db.close();

        return categories;
    }


    public ArrayList<DataObject> getUCatsList() {

        ArrayList<DataObject> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null, null, null, null, null, KEY_UCAT_UPDATED_SORT + " DESC");

        try {
            while (cursor.moveToNext()) {

                DataObject category = new DataObject();
                category.id = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ID));
                category.title = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TITLE));
                category.desc = cursor.getString(cursor.getColumnIndex(KEY_UCAT_DESC));
                category.parent = cursor.getString(cursor.getColumnIndex(KEY_UCAT_PARENT));

                category.time_created = cursor.getLong(cursor.getColumnIndex(KEY_UCAT_CREATED));

                category.type = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TYPE));
                if (category.type == null) category.type = "";

                category.image = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ICON));
                if (category.image == null) category.image = "";

                if (!category.parent.contains(UC_PREFIX)) // disable to display all cats and groups
                    categories.add(category);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return categories;
    }

    public ArrayList<NoteData> getNotesListForSet(String set_id) {


        ArrayList<NoteData> notes = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NOTES_DATA,  null, KEY_NOTE_PARENT +" = ?", new String[]{set_id},
                null, null, KEY_NOTE_UPDATED_SORT + " DESC");

        try {
            while (cursor.moveToNext()) {

                NoteData note = getNoteFromCursor(cursor);

                notes.add(note);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return notes;
    }


    public ArrayList<DataObject> getUCatsListForSet(String set_id) {


        ArrayList<DataObject> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USER_DATA_CATS,  null, KEY_UCAT_PARENT +" = ?", new String[]{set_id}, null, null, KEY_UCAT_UPDATED_SORT + " DESC");

        try {
            while (cursor.moveToNext()) {

                DataObject category = new DataObject();
                category.id = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ID));
                category.title = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TITLE));
                category.desc = cursor.getString(cursor.getColumnIndex(KEY_UCAT_DESC));
                category.parent = cursor.getString(cursor.getColumnIndex(KEY_UCAT_PARENT));
                category.time_created = cursor.getLong(cursor.getColumnIndex(KEY_UCAT_CREATED));

                category.type = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TYPE));
                if (category.type == null) category.type = "";

                category.image = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ICON));
                if (category.image == null) category.image = "";

                categories.add(category);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return categories;
    }

    public ArrayList<DataObject> getUGroupsListForSet(String set_id) {


        ArrayList<DataObject> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;


        cursor = db.query(TABLE_USER_DATA_CATS,  null, KEY_UCAT_TYPE +" = ?",
                    new String[]{"group"}, null, null, KEY_UCAT_UPDATED_SORT + " DESC");


        try {
            while (cursor.moveToNext()) {

                DataObject category = new DataObject();
                category.id = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ID));
                category.title = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TITLE));
                category.desc = cursor.getString(cursor.getColumnIndex(KEY_UCAT_DESC));
                category.parent = cursor.getString(cursor.getColumnIndex(KEY_UCAT_PARENT));
                category.time_created = cursor.getLong(cursor.getColumnIndex(KEY_UCAT_CREATED));

                category.type = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TYPE));
                if (category.type == null) category.type = "";

                categories.add(category);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return categories;
    }

    public int getGroupsCount() {


        int count = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;

        cursor = db.query(TABLE_USER_DATA_CATS,  null, KEY_UCAT_TYPE +" = ?",
                new String[]{PARAM_GROUP}, null, null, null);


        count = cursor.getCount();

        cursor.close();

        db.close();

        return count;
    }

    public ArrayList<DataObject> getUCatsListItemsCount(ArrayList<DataObject> categories) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM "
                + TABLE_UCAT_UDATA +" a INNER JOIN " + TABLE_USER_DATA_ITEMS
                +" b ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID
                +" WHERE a." + KEY_UDC_UCAT_ID + " = ?";


        String progressQuery = "SELECT * FROM " + TABLE_UCAT_UDATA +" a "

                +"INNER JOIN " + TABLE_USER_DATA_ITEMS +" b ON "
                +"a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID

                +" LEFT JOIN " + TABLE_USER_DATA +" c ON "
                +" b." + KEY_UDATA_ID + " = c." + KEY_USER_ITEM_ID

                +" WHERE (a." + KEY_UDC_UCAT_ID + " = ?) AND (c." +KEY_ITEM_SCORE + " > 2) "; //TODO find score

        String familiarQuery = "SELECT * FROM " + TABLE_UCAT_UDATA +" a "

                +"INNER JOIN " + TABLE_USER_DATA_ITEMS +" b ON "
                +"a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID

                +" LEFT JOIN " + TABLE_USER_DATA +" c ON "
                +" b." + KEY_UDATA_ID + " = c." + KEY_USER_ITEM_ID

                +" WHERE (a." + KEY_UDC_UCAT_ID + " = ?) AND (c." +KEY_ITEM_SCORE + " > 0) ";



        for (DataObject category: categories) {

            Cursor cursor = db.rawQuery(query, new String[]{category.id});
            category.count = cursor.getCount();

            if (category.count > 0) {

                Cursor progressCursor = db.rawQuery(progressQuery, new String[]{category.id});
                category.progress = progressCursor.getCount();

                Cursor familiarCursor = db.rawQuery(familiarQuery, new String[]{category.id});
                category.progress_1 = familiarCursor.getCount();

                progressCursor.close();
                familiarCursor.close();
            }

            if (category.type.equals(PARAM_GROUP) ) {

                cursor = db.query(TABLE_USER_DATA_CATS,  null, KEY_UCAT_PARENT +" = ?", new String[]{category.id}, null, null, KEY_UCAT_UPDATED_SORT + " DESC");
                category.count = cursor.getCount();
            }



            boolean bookmark = checkBookmark(db, category.id, PARAM_UCAT_PARENT);

            if (bookmark) {
                category.info =  category.info + UCAT_PARAM_BOOKMARK_ON;
            } else {
                category.info =  category.info + UCAT_PARAM_BOOKMARK_OFF;
            }

            cursor.close();


        }


        db.close();

        return categories;
    }


    public void createNote(NoteData note) {

        SQLiteDatabase db = this.getWritableDatabase();

        createNote(db, note);

        db.close();
    }

    public void createNote(SQLiteDatabase db, NoteData note) {

        long time = System.currentTimeMillis();

        ContentValues values = new ContentValues();

        values.put(KEY_NOTE_TITLE, note.title);
        values.put(KEY_NOTE_TEXT, note.content);
        values.put(KEY_NOTE_ICON, note.image);
        values.put(KEY_NOTE_STATUS, note.status);
        values.put(KEY_NOTE_PARAMS, note.params);
        values.put(KEY_NOTE_CREATED, time );
        values.put(KEY_NOTE_UPDATED, time );
        values.put(KEY_NOTE_UPDATED_SORT, time );

        long primary_id = db.insert(TABLE_NOTES_DATA, null, values);

        Cursor cursor = db.query(TABLE_NOTES_DATA,  null,
                KEY_NOTE_PRIMARY_ID +" = ? " ,

                new String[] { String.valueOf(primary_id)  }, null, null, null);



        if (cursor.moveToNext()) {

            ContentValues newValues = new ContentValues();

            newValues.put(KEY_NOTE_ID, "note_" + primary_id);

            db.update(TABLE_NOTES_DATA, newValues,
                    KEY_NOTE_PRIMARY_ID +" = ? ",
                    new String[] { String.valueOf(primary_id) });

        }

        cursor.close();
    }

    public void setPracticeTask(String w_id, int type, String result) {

        SQLiteDatabase db = this.getWritableDatabase();

        long time = System.currentTimeMillis();


        Cursor cursor = db.query(TABLE_QSTATS_DATA, null,
                KEY_QSTATS_ID + " = ? ",
                new String[]{ w_id }, null, null, null);

        int countTR = 0;
        int countAudio = 0;

        if (cursor.moveToNext()) {

            countTR = cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_COUNT_TR));
            countAudio = cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_COUNT_AUDIO));

            int correct = cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_CORRECT)) + 1;

            if (type == EX_AUDIO_TYPE) countAudio++;
            else countTR++;

            ContentValues newValues = new ContentValues();

            newValues.put(KEY_QSTATS_COUNT_TR, countTR);
            newValues.put(KEY_QSTATS_COUNT_AUDIO, countAudio);
            newValues.put(KEY_QSTATS_CORRECT, correct);
            newValues.put(KEY_QSTATS_CORRECT_TIME, time);


            db.update(TABLE_QSTATS_DATA, newValues,
                    KEY_QSTATS_ID + " = ? ",
                    new String[]{ w_id});

        } else {

            if (type == EX_AUDIO_TYPE) countAudio++;
            else countTR++;

            ContentValues values = new ContentValues();
            values.put(KEY_QSTATS_ID, w_id);
            values.put(KEY_QSTATS_COUNT_TR, countTR);
            values.put(KEY_QSTATS_COUNT_AUDIO, countAudio);
            values.put(KEY_QSTATS_CORRECT, 1);
            values.put(KEY_QSTATS_CORRECT_TIME, time);

            db.insert(TABLE_QSTATS_DATA, null, values);

        }

        cursor.close();


        db.close();

    }


    public void saveTask(String taskId, int priority) {
        SQLiteDatabase db = this.getWritableDatabase();
        saveTask(db, taskId, priority);
        db.close();
    }

    public void saveTask(SQLiteDatabase db, String taskId, int priority) {

        long time = System.currentTimeMillis();

        if (priority == -5) {
            time = 0;
        }

        Cursor cursor = db.query(TABLE_TASKS_DATA, null,
                KEY_TASK_ID + " = ? ",
                new String[]{ taskId }, null, null, null);

        if (cursor.moveToNext()) {


            ContentValues newValues = new ContentValues();

            newValues.put(KEY_TASK_SKIPPED_TIME, time);
            newValues.put(KEY_TASK_UPDATED, time);
            newValues.put(KEY_TASK_PRIORITY, priority);

            db.update(TABLE_TASKS_DATA, newValues,
                    KEY_TASK_ID + " = ? ",
                    new String[]{ taskId });

        } else {

            ContentValues values = new ContentValues();
            values.put(KEY_TASK_ID, taskId);
            values.put(KEY_TASK_SKIPPED_TIME, time);
            values.put(KEY_TASK_UPDATED, time);
            values.put(KEY_TASK_PRIORITY, priority);

            db.insert(TABLE_TASKS_DATA, null, values);

        }

        cursor.close();

    }


    public int deleteAllTasks() {

        SQLiteDatabase db = this.getWritableDatabase();

        int t = 0;

        db.beginTransaction();

        try {

            // db.execSQL("delete from "+ TABLE_TASKS_DATA);
            t = db.delete(TABLE_TASKS_DATA, null,null);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();

        return  t;
    }

    public void deleteTasksWithId(String taskId) {

        SQLiteDatabase db = this.getWritableDatabase();

       // Log.i("task", "Task id : " + taskId);

        db.beginTransaction();

        try {

            db.delete(TABLE_TASKS_DATA, KEY_TASK_ID + " LIKE ? OR " + KEY_TASK_ID + " LIKE ?",

                    new String[]{taskId + "%", "%_" + taskId + "%"} );

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();

    }


    public ArrayList<TaskItem> getTasksStats(ArrayList<TaskItem> tasks) {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<TaskItem> taskItems = new ArrayList<>();

        for (TaskItem taskItem: tasks) {



            //// getting completed time from tests
            Cursor cursor = db.query(TABLE_TESTS_DATA,  null,
                    KEY_TEST_TAG +" LIKE ?", new String[] { taskItem.getId()+"%" },
                    null, null, KEY_TEST_TIME + " DESC");

            if (cursor.moveToFirst()) {
                long time = cursor.getLong(cursor.getColumnIndex(KEY_TEST_TIME));
                taskItem.setTimeCompleted(  time   );

                if (taskItem.getId().contains("revise")) {
                    int progress = cursor.getInt(cursor.getColumnIndex(KEY_TEST_PROGRESS));
                    taskItem.setProgress(progress);
                    //Log.i("task", "Task id : " + taskItem.getId() + " - " + taskItem.getTimeCompleted() );
                }

            }


            cursor.close();

            /// getting stats from tasks database

            Cursor statsCursor = db.query(TABLE_TASKS_DATA, null,
                    KEY_TASK_ID + " = ? ",
                    new String[]{String.valueOf(taskItem.getId())}, null, null, null);

            if (statsCursor.moveToNext()) {
                taskItem.setPriority(statsCursor.getInt(statsCursor.getColumnIndex(KEY_TASK_PRIORITY)));
                taskItem.setSkippedTime( statsCursor.getLong(statsCursor.getColumnIndex(KEY_TASK_SKIPPED_TIME)) );
            }
            statsCursor.close();

            String sectionId = taskItem.getSectionStats().getSectionId();

            Cursor priorityCursor = db.query(TABLE_TASKS_DATA, null,
                    KEY_TASK_ID + " = ? ",
                    new String[]{sectionId}, null, null, null);

            if (priorityCursor.moveToNext()) {
                taskItem.setPriority(priorityCursor.getInt(statsCursor.getColumnIndex(KEY_TASK_PRIORITY)));
            }

            priorityCursor.close();


            taskItems.add(taskItem);

        }

        db.close();

        return taskItems;

    }

    public ArrayList<TaskItem> getTasksTimeByTests(ArrayList<TaskItem> tasks) {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<TaskItem> taskItems = new ArrayList<>();

        for (TaskItem taskItem: tasks) {

            Cursor cursor = db.query(TABLE_TESTS_DATA,  null,
                    KEY_TEST_TAG +" LIKE ?", new String[] { taskItem.getId()+"%" },
                    null, null, KEY_TEST_TIME + " DESC");

            if (cursor.moveToFirst()) {
                long time = cursor.getLong(cursor.getColumnIndex(KEY_TEST_TIME));
                taskItem.setTimeCompleted(  time   );
            }

            taskItems.add(taskItem);

            cursor.close();
        }


        db.close();

        return taskItems;

    }


    public void updateNote(NoteData note) {

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query(TABLE_NOTES_DATA,  null,
                KEY_NOTE_ID +" = ?",
                new String[] { note.id}, null, null, null);


        if (cursor.moveToFirst() ) {

           // Toast.makeText(cntx, "FOUND", Toast.LENGTH_SHORT).show();

            ContentValues values = new ContentValues();

            values.put(KEY_NOTE_TITLE, note.title);
            values.put(KEY_NOTE_TEXT, note.content);
            values.put(KEY_NOTE_ICON, note.image);
            values.put(KEY_NOTE_UPDATED, System.currentTimeMillis());

            db.update(TABLE_NOTES_DATA, values, KEY_NOTE_ID +" = ?", new String[] { note.id });
        } else {
           // Toast.makeText(cntx, "NOT FOUND: "+ note.id, Toast.LENGTH_SHORT).show();
        }

        cursor.close();


        db.close();
    }



    public void deleteNote(NoteData note) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NOTES_DATA,  null,
                KEY_NOTE_ID +" = ?",
                new String[] { note.id}, null, null, null);


        if (cursor.moveToFirst() ) {
            db.delete(TABLE_NOTES_DATA, KEY_NOTE_ID + " = ?", new String[]{note.id});
        }

        cursor.close();

        db.close();

    }


    public ArrayList<NoteData> getNotes() {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<NoteData> items = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NOTES_DATA,  null, null, null,
                null, null, KEY_NOTE_UPDATED_SORT + " DESC");

        try {
            while (cursor.moveToNext()) {

                NoteData note  = getNoteFromCursor(cursor);

                if (!note.parent.contains(NOTE_ARCHIVE))

                items.add(note);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return  items;
    }

    public NoteData getNote(String id) {

        SQLiteDatabase db = this.getWritableDatabase();

        NoteData note = new NoteData();
        note.title = "";
        note.status = "not_found";

        //Toast.makeText(cntx, "ID: " + id, Toast.LENGTH_SHORT).show();

        Cursor cursor = db.query(TABLE_NOTES_DATA,  null,
                KEY_NOTE_ID +" = ?",
                new String[] { id}, null, null, null);


        if (cursor.moveToFirst() ) {
            note.status = "";
            note  = getNoteFromCursor(cursor);

        }

        cursor.close();

        db.close();

        return note;
    }

    private NoteData getNoteFromCursor(Cursor cursor) {

        NoteData note  = new NoteData();

        note.id = cursor.getString(cursor.getColumnIndex(KEY_NOTE_ID));
        note.title = cursor.getString(cursor.getColumnIndex(KEY_NOTE_TITLE));
        note.content = cursor.getString(cursor.getColumnIndex(KEY_NOTE_TEXT));
        note.image = cursor.getString(cursor.getColumnIndex(KEY_NOTE_ICON));

        note.time_updated = cursor.getLong(cursor.getColumnIndex(KEY_NOTE_UPDATED));
        note.time_created = cursor.getLong(cursor.getColumnIndex(KEY_NOTE_CREATED));
        note.time_updated_sort = cursor.getLong(cursor.getColumnIndex(KEY_NOTE_UPDATED_SORT));

        note.parent = cursor.getString(cursor.getColumnIndex(KEY_NOTE_PARENT));

        if (note.parent == null) note.parent = "";

        return note;
    }





    public ArrayList<BookmarkItem> getBookmarks() {  ///

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<BookmarkItem> items = new ArrayList<>();

        Cursor cursor = db.query(TABLE_BOOKMARKS_DATA,  null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                String bookmark = cursor.getString(cursor.getColumnIndex(KEY_BOOKMARK_ITEM));
                String section = cursor.getString(cursor.getColumnIndex(KEY_BOOKMARK_PARENT));

                BookmarkItem bookmarkItem  = new BookmarkItem(bookmark, section);
                bookmarkItem.time = cursor.getLong(cursor.getColumnIndex(KEY_BOOKMARK_TIME));
                items.add(bookmarkItem);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return  items;
    }


    public ArrayList<BookmarkItem> getUCatsBookmarks() {

        ArrayList<BookmarkItem> bookmarks = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();


        String query = "SELECT * FROM " + TABLE_BOOKMARKS_DATA +" a "
                +"INNER JOIN " + TABLE_USER_DATA_CATS +" b "
                +"ON a." + KEY_BOOKMARK_ITEM + " = b." + KEY_UCAT_ID;


        Cursor cursor = db.rawQuery(query, null);


        try {
            while (cursor.moveToNext()) {

                BookmarkItem bookmark = new BookmarkItem();

                bookmark.item = cursor.getString(cursor.getColumnIndex(KEY_BOOKMARK_ITEM));
                bookmark.parent = cursor.getString(cursor.getColumnIndex(KEY_BOOKMARK_PARENT));
                bookmark.time = cursor.getLong(cursor.getColumnIndex(KEY_BOOKMARK_TIME));

                bookmark.title = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TITLE));
                bookmark.desc = cntx.getString(R.string.bookmarks_section_my_voc);
                bookmark.image = "cat/account.png";
                bookmark.navCategory = new NavCategory();

                int catListSize = checkUcatDataSize(db, bookmark.item);

                if (catListSize > 0) bookmarks.add(bookmark);
            }

        } finally {
            cursor.close();
        }

        db.close();

        return bookmarks;
    }

    private int checkUcatDataSize(SQLiteDatabase db, String id) {

        Cursor cursor = db.query(TABLE_UCAT_UDATA,  null,
                KEY_UDC_UCAT_ID +" = ? ",
                new String[] { id }, null, null, null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }







    public boolean checkBookmark(String bookmark, String parent) {

        SQLiteDatabase db = this.getWritableDatabase();

        boolean  bookmarked = checkBookmark(db, bookmark, parent);

        db.close();

        return  bookmarked ;
    }

    private boolean checkBookmark(SQLiteDatabase db, String bookmark, String parent) {


        boolean bookmarked = false;

        Cursor cursor = db.query(TABLE_BOOKMARKS_DATA,  null,
                KEY_BOOKMARK_ITEM +" = ? AND " + KEY_BOOKMARK_PARENT + " = ?",
                new String[] { bookmark, parent }, null, null, null);

        if (cursor.moveToFirst() ) {
            bookmarked = true;
        }

        cursor.close();

        return  bookmarked ;
    }



    public void updateCatResult(String cat_id, int tests_num) {
        SQLiteDatabase db = this.getWritableDatabase();
        setCatResult(db, cat_id, tests_num);
        db.close();
    }


    private void setCatResult(SQLiteDatabase db, String cat_id, int tests_num) {

        int progress = 0;
        String idPrefix = cat_id+"_%";

        Cursor cursor = db.query(TABLE_TESTS_DATA,  null,
                KEY_TEST_TAG +" LIKE ?", new String[] { idPrefix },
                null, null, null);
        try {
            while (cursor.moveToNext()) {
                int catProgress = cursor.getInt(cursor.getColumnIndex(KEY_TEST_PROGRESS));
                progress += catProgress;
            }

            if (cursor.getCount() > 0) progress = progress / tests_num;

        } finally {
            cursor.close();
        }

        if (progress > 0) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_CAT_ID, cat_id);
            initialValues.put(KEY_CAT_PROGRESS, progress);



            Boolean exist = false;

            Cursor checkCursor = db.query(TABLE_CAT_DATA,  null,
                    KEY_CAT_ID +" = ?", new String[] { cat_id },
                    null, null, null);

            if (checkCursor.getCount() > 0 ) exist= true;

            checkCursor.close();

            if (exist) {
                db.update(TABLE_CAT_DATA, initialValues, KEY_CAT_ID+"=?", new String[] {cat_id});
            } else {
                if (!cat_id.contains(UC_PREFIX))
                db.insert(TABLE_CAT_DATA, null, initialValues);
            }

        }
    }



    public int getTestResult(String exId) {
        int catResult = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_TESTS_DATA, new String[] { KEY_TEST_PROGRESS },
                KEY_TEST_TAG + " = ?", new String[]{exId}, null, null, null);
        if ( cursor.moveToFirst() ) {
            catResult = cursor.getInt(cursor.getColumnIndex(KEY_TEST_PROGRESS));
        }

        cursor.close();
        db.close();
        return catResult;
    }



    public void setWordResult(String w_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String action = "nothing";

        String table = TABLE_USER_DATA;

        Cursor cursor = db.query(TABLE_USER_DATA,  null, KEY_USER_ITEM_ID +" = ?",
                new String[] { w_id}, null, null, null);

        int w_progress = 1;
        int w_score = 1;
        long time = System.currentTimeMillis();
        int errors = 0;

        if ( cursor.moveToFirst() ) {

            cursor.moveToFirst();
            w_progress = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_PROGRESS));
            w_score = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SCORE));
            w_score = countScore(w_score, 1);
            w_progress++;
            action = "update";

            errors = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ERRORS));

            if (errors > 0) errors--;

            //Toast.makeText(cntx, "Error: " + errors, Toast.LENGTH_SHORT).show();


        } else {
            action = "insert";
        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ITEM_ID, w_id);
        values.put(KEY_ITEM_PROGRESS, w_progress);
        values.put(KEY_ITEM_SCORE, w_score);
        values.put(KEY_ITEM_TIME, time);

        values.put(KEY_ITEM_ERRORS, errors);


        if (w_score >= MAX_SCORE) values.put(KEY_ITEM_ERRORS, 0);

        if ( action.equals("update")) {
            db.update(table, values, KEY_USER_ITEM_ID +" = ?", new String[]{w_id});
        } else if (action.equals("insert")){
            db.insert(table, null, values);
        }

        cursor.close();
        db.close();
    }



    public void setError(String w_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String action = "nothing";

        String table = TABLE_USER_DATA;

        Cursor cursor = db.query(table,  null, KEY_USER_ITEM_ID +" = ?",
                new String[] { w_id}, null, null, null);


        int w_error = 1;
        int w_progress = 0;
        int w_score = 0;
        long time = System.currentTimeMillis();

        if ( cursor.moveToFirst() ) {
            cursor.moveToFirst();
            w_error = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ERRORS));
            w_progress = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_PROGRESS));
            w_error++;
            w_score = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SCORE));
            w_score = countScore(w_score, -1);
            action = "update";
        } else {
            action = "insert";
        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ITEM_ID, w_id);
        values.put(KEY_ITEM_PROGRESS, w_progress);
        values.put(KEY_ITEM_ERRORS, w_error);
        values.put(KEY_ITEM_SCORE, w_score);
        values.put(KEY_ITEM_TIME, time);
        values.put(KEY_ITEM_TIME_ERROR, time);

        if ( action.equals("update")) {
            db.update(table, values, KEY_USER_ITEM_ID +" = ? ", new String[]{w_id});
        } else if (action.equals("insert")){
            db.insert(table, null, values);
        }

        cursor.close();
        db.close();
    }

    public int deleteCatResult(String catId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int num = 0;

        String idPrefix = catId+"_%";

        db.delete(TABLE_TESTS_DATA, KEY_TEST_TAG +" LIKE ?", new String[]{idPrefix});

        // int num = db.delete(TABLE_USER_DATA, KEY_USER_ITEM_ID +" LIKE ?", new String[]{idPrefix});

        ArrayList<String> ids = new ArrayList<>();

        Cursor cursor;

        if (catId.contains(UC_PREFIX)) {

            String query = "SELECT * FROM "
                    + TABLE_UCAT_UDATA +" a INNER JOIN " + TABLE_USER_DATA_ITEMS
                    +" b ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID
                    +" WHERE a." + KEY_UDC_UCAT_ID + " = ?";

            cursor = db.rawQuery(query, new String[]{catId});


            while (cursor.moveToNext()) {
                String id = cursor.getString( (cursor.getColumnIndex(KEY_UDATA_ID)));
                ids.add(id);
            }

        } else {

            cursor = db.query(TABLE_USER_DATA,  null, KEY_USER_ITEM_ID +" LIKE ?",
                    new String[]{idPrefix}, null, null, null);


            while (cursor.moveToNext()) {
                String id = cursor.getString( (cursor.getColumnIndex(KEY_USER_ITEM_ID)));
                ids.add(id);
            }
        }







        cursor.close();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_PROGRESS, 0);
        values.put(KEY_ITEM_ERRORS, 0);
        values.put(KEY_ITEM_SCORE, 0);
        values.put(KEY_ITEM_TIME, "");
        values.put(KEY_ITEM_TIME_ERROR, "");

        db.beginTransaction();

        try {

            for (String id: ids) {
                db.update(TABLE_USER_DATA, values, KEY_USER_ITEM_ID +" = ?", new String[]{id});
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }


        db.close();

        return num;
    }

    public int deleteDataListResult(String catId, SQLiteDatabase db) {


        String idPrefix = catId+"_%";

        db.delete(TABLE_TESTS_DATA, KEY_TEST_TAG +" LIKE ?", new String[]{idPrefix});
        int num = db.delete(TABLE_USER_DATA, KEY_USER_ITEM_ID +" LIKE ?", new String[]{idPrefix});

        db.close();

        return num;
    }


    private void setStarredTab(int type) {

        String tab = TAB_ITEMS;
        if (type == 1) tab = TAB_GALLERY;

        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(STARRED_TAB_ACTIVE, tab);
        editor.apply();

    }

    public int setStarred(String w_id, Boolean star) {

       return setStarred(w_id, star, "");
    }


    public int setStarred(String w_id, Boolean star, String _info) {
        SQLiteDatabase db = this.getWritableDatabase();
        String action = "nothing";
        int status = 1;

        int check = starredGroupSize(db, w_id, _info);

        // Toast.makeText(cntx, "Starred: "+ check, Toast.LENGTH_SHORT).show();


        if ( check < Constants.STARRED_LIMIT || !star) {

            Cursor cursor = db.query(TABLE_USER_DATA, null, KEY_USER_ITEM_ID + " = ?",
                    new String[]{w_id}, null, null, null);

            int w_progress = 0;
            int starred = 0;

            long time = System.currentTimeMillis();

            if (star) {
                starred = 1;
                if (_info.contains(GALLERY_TAG)) setStarredTab(1);
                else setStarredTab(0);
            }

            String info = _info;
            if (!star) info = "";

            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                w_progress = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_PROGRESS));
                action = "update";
            } else {
                action = "insert";
            }

            ContentValues values = new ContentValues();
            values.put(KEY_USER_ITEM_ID, w_id);
            values.put(KEY_ITEM_INFO, info);
            values.put(KEY_ITEM_PROGRESS, w_progress);
            values.put(KEY_ITEM_STARRED, starred);
            values.put(KEY_ITEM_TIME_STARRED, time);

            if (action.equals("update")) {
                db.update(TABLE_USER_DATA, values, KEY_USER_ITEM_ID + " = ? ", new String[]{w_id});
            } else if (action.equals("insert")) {
                db.insert(TABLE_USER_DATA, null, values);
            }
            cursor.close();

        } else {
            status = 0;
            //Toast.makeText(cntx, "Starred limit", Toast.LENGTH_SHORT).show();
        }
        db.close();

        return status;
    }





    private int starredGroupSize(SQLiteDatabase db, String id, String _filter) {


        DataManager dataManager = new DataManager(cntx, 1);


        StringBuilder conditionLike = new StringBuilder("");

        for (int i = 0; i < dataManager.navCategories.size(); i++) {

            String like = "a."+KEY_USER_ITEM_ID + " LIKE '" + dataManager.navCategories.get(i).id + "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }


        String query = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"

                +" WHERE ("+conditionLike+") AND a." + KEY_ITEM_STARRED +" > ? AND a." + KEY_ITEM_INFO +" NOT LIKE ?";

        Cursor checkCursor = db.rawQuery(query, new String[]{"0", GALLERY_TAG});


        /*
        Cursor checkCursor = db.query(TABLE_USER_DATA,  null,
                KEY_USER_ITEM_ID + " LIKE ? AND " + KEY_ITEM_STARRED +" > ? AND " + KEY_ITEM_INFO +" NOT LIKE ?",
                new String[] {idPrefix, "0", GALLERY_TAG}, null, null, null);
*/


        if (_filter.contains(GALLERY_TAG)) {

            query = "SELECT * FROM "
                    +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                    +" b ON a.user_item_id=b.item_id"

                    +" WHERE a."+KEY_ITEM_STARRED +" > ? AND a." + KEY_ITEM_INFO +" LIKE ?";

            checkCursor = db.rawQuery(query, new String[]{"0", GALLERY_TAG});


            /*
            checkCursor = db.query(TABLE_USER_DATA,  null,
                    KEY_USER_ITEM_ID + " LIKE ? AND " + KEY_ITEM_STARRED +" > ? AND " + KEY_ITEM_INFO +" LIKE ?",
                    new String[] {idPrefix, "0", GALLERY_TAG}, null, null, null);
            */

        }


        int size = checkCursor.getCount();
        checkCursor.close();


        return size;
}

    public Boolean checkStarred(String word_id) {
        Boolean status = false;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USER_DATA,  null, KEY_USER_ITEM_ID +" = ? ",
                new String[] {word_id}, null, null, null);

        if (cursor.moveToFirst() ) {
            cursor.moveToFirst();
            int current = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_STARRED));
            if (current > 0) status = true;
        }

        cursor.close();
        db.close();
        return status;
    }



    public ArrayList<DataItem> searchData(ArrayList<NavCategory> navCategories, String searchTerm) {
        SQLiteDatabase db = getReadableDatabase();


        StringBuilder conditionLike = new StringBuilder("");

        for (int i = 0; i < navCategories.size(); i++) {
            String like = KEY_ITEM_ID + " LIKE '" + navCategories.get(i).id + "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }

        ArrayList<DataItem> items = new ArrayList<>();


        String query = "SELECT * FROM " + TABLE_ITEMS_DATA
                +" WHERE ("+conditionLike +")"
                +" AND " + "( " +KEY_ITEM_SEARCH+" LIKE '%" + searchTerm+"%' ) LIMIT 200";


        Cursor cursor = db.rawQuery(query, null);


        try {
            while (cursor.moveToNext()) {
                items.add(getSimpleItemFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        String notesQuery = "SELECT * FROM " +TABLE_NOTES_DATA
                +" WHERE  ("+KEY_NOTE_TITLE+" LIKE '%"+searchTerm+"%' OR "+KEY_NOTE_TEXT+" LIKE '%" + searchTerm+"%')"
                +"ORDER BY "+KEY_NOTE_UPDATED_SORT + " DESC";


        Cursor notesCursor = db.rawQuery(notesQuery, null);

        try {
            while (notesCursor.moveToNext()) {
                items.add(getSimpleItemFromNoteCursor(notesCursor));
            }
        } finally {
            notesCursor.close();
        }


        String udataQuery = "SELECT * FROM " + TABLE_UCAT_UDATA + " a"

                + " INNER JOIN " + TABLE_USER_DATA_ITEMS + " b "
                + " ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID

              //  + ' WHERE lower(column) GLOB '*addTildeOptions(searchTerm)*'";

               // + " WHERE lower(" + KEY_UDATA_TEXT +") GLOB '*addTildeOptions(%"+searchTerm+"%)*' ";

                +" WHERE  ("+KEY_UDATA_TEXT+" LIKE '%"+searchTerm+"%' OR "+KEY_UDATA_TRANSLATE+" LIKE '%" + searchTerm+"%')";




        Cursor udataCursor = db.rawQuery(udataQuery, null);

        try {
            while (udataCursor.moveToNext()) {

                items.add(getDataItemFromUDATA(udataCursor));
            }
        } finally {
            udataCursor.close();
        }


        db.close();

        return items;

    }


     private String addTildeOptions(String searchText) {
        return searchText.toLowerCase()
                .replaceAll("[aáàäâã]", "\\[aáàäâã\\]")
                .replaceAll("[eéèëê]", "\\[eéèëê\\]")
                .replaceAll("[iíìî]", "\\[iíìî\\]")
                .replaceAll("[oóòöôõ]", "\\[oóòöôõ\\]")
                .replaceAll("[uúùüû]", "\\[uúùüû\\]")
                .replace("*", "[*]")
                .replace("?", "[?]");
    }



    public ArrayList<DataItem> getAllDataItems(ArrayList<NavCategory> navCategories) {
        ArrayList<DataItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();


        StringBuilder conditionLike = new StringBuilder("");

        for (int i = 0; i < navCategories.size(); i++) {

            String like = KEY_ITEM_ID + " LIKE '" + navCategories.get(i).id + "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }

        String query = "SELECT * FROM " +TABLE_ITEMS_DATA
                +" WHERE "+conditionLike;


        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                items.add(getSimpleItemFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        db.close();

        return items;
    }



    public ArrayList<DataItem> getDataItemsByCats(ArrayList<NavCategory> navCategories) {


        ArrayList<DataItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();


        StringBuilder conditionLike = new StringBuilder("");


        for (int i = 0; i < navCategories.size(); i++) {

            String like = "a."+KEY_ITEM_ID + " LIKE '" + navCategories.get(i).id + "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }


        String query = "SELECT * FROM "
                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA +" b ON a.item_id=b.user_item_id"

                +" WHERE ("+ conditionLike +") "
                + " AND (a."+KEY_ITEM_MODE+" < "+data_mode+")" ;


        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                items.add(getItemFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        db.close();

        return items;
    }



    public ArrayList<DataItem> getTestDataByIds(ArrayList<DataItem> dataItems) {
        ArrayList<DataItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        for (DataItem item: dataItems) {


            if (item.id.contains(UD_PREFIX)) {

                Cursor cursor = db.query(TABLE_USER_DATA_ITEMS,  null, KEY_UDATA_ID +" = ?",
                        new String[] { item.id}, null, null, null);

                try {
                    while (cursor.moveToNext()) {

                        DataItem data = getDataItemFromUDATA(cursor);
                        data.testError = item.testError;
                        items.add(data);
                    }

                } finally {
                    cursor.close();
                }

            } else {

                Cursor cursor = db.query(TABLE_ITEMS_DATA,  null, KEY_ITEM_ID +" = ?",
                        new String[] { item.id}, null, null, null);

                try {
                    while (cursor.moveToNext()) {

                        DataItem data = getSimpleItemFromCursor(cursor);
                        data.testError = item.testError;
                        items.add(data);
                    }

                } finally {
                    cursor.close();
                }

            }


        }

        return items;
    }


    public ArrayList<DataItem> getDataItemsByCatIds(ArrayList<String> catIds) {

        ArrayList<DataItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();


        String limit = "";

        if (catIds.size() > TEST_CATS_MAX_FOR_BEST) {
            limit = "ORDER BY RANDOM() LIMIT 15";
        }




        // db.beginTransaction();

        for (int i = 0; i < catIds.size(); i++) {

            if (catIds.get(i).contains(UC_PREFIX)) {

                String id = catIds.get(i);

                String query = "SELECT * FROM " +TABLE_UCAT_UDATA +" a "

                        + " INNER JOIN " + TABLE_USER_DATA_ITEMS + " b "
                        + " ON a." + KEY_UDC_UDATA_ID + " = b." + KEY_UDATA_ID

                        + " WHERE a." + KEY_UDC_UCAT_ID + " = ?";

                Cursor cursor = db.rawQuery(query, new String[]{catIds.get(i)});

                //Toast.makeText(cntx, "Cursor: " + cursor.getCount(), Toast.LENGTH_SHORT).show();

                try {
                    while (cursor.moveToNext()) {

                       // DataItem dataItem = getAllInfoUdataFromDB(cursor); //TODO

                        DataItem dataItem = new DataItem("text " + id, "info");

                        dataItem.id = cursor.getString(cursor.getColumnIndex(KEY_UDATA_ID));
                        dataItem.item = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TEXT));
                        dataItem.info = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TRANSLATE));
                        dataItem.item_info_1 = cursor.getString(cursor.getColumnIndex(KEY_UDATA_INFO));

                        dataItem.cat = catIds.get(i);

                        items.add(dataItem);

                    }

                } finally {
                    cursor.close();
                }


            } else {

                String columns = KEY_ITEM_ID +", "+ KEY_ITEM_TITLE + ", "
                        + KEY_ITEM_DESC+ ", "+ KEY_ITEM_INFO_1+ ", "+ KEY_ITEM_PRONOUNCE+ ", "+ KEY_ITEM_BASE
                        + ", "+ KEY_ITEM_FILTER+ ", "+ KEY_ITEM_GRAMMAR;

                String catQuery = "SELECT "+columns+" FROM " +TABLE_ITEMS_DATA
                        +" WHERE ("+KEY_ITEM_ID + " LIKE '" + catIds.get(i) + "%') "
                        + "AND ("+KEY_ITEM_MODE+" < "+data_mode+") "
                        +limit;

                Cursor cursor = db.rawQuery(catQuery, null);

                try {
                    while (cursor.moveToNext()) {

                        DataItem dataItem = new DataItem();
                        dataItem.id = cursor.getString(cursor.getColumnIndex(KEY_ITEM_ID));
                        dataItem.item = cursor.getString(cursor.getColumnIndex(KEY_ITEM_TITLE));
                        dataItem.info = cursor.getString(cursor.getColumnIndex(KEY_ITEM_DESC));
                        dataItem.pronounce = cursor.getString(cursor.getColumnIndex(KEY_ITEM_PRONOUNCE));
                        dataItem.base = cursor.getString(cursor.getColumnIndex(KEY_ITEM_BASE));
                        dataItem.filter = cursor.getString(cursor.getColumnIndex(KEY_ITEM_FILTER));
                        dataItem.grammar = cursor.getString(cursor.getColumnIndex(KEY_ITEM_GRAMMAR));
                        dataItem.item_info_1 = cursor.getString(cursor.getColumnIndex(KEY_ITEM_INFO_1));

                        items.add(dataItem);
                    }
                } finally {
                    cursor.close();
                }


            }

        }

       // db.endTransaction();

        db.close();

        //Toast.makeText(cntx, "Items: " + items.size(), Toast.LENGTH_SHORT).show();

        return items;
    }



    public ArrayList<DataItem> selectSimpleDataItemsByIds(SQLiteDatabase db, ArrayList<String> conditions) {

        ArrayList<DataItem> items = new ArrayList<>();

        StringBuilder conditionLike = new StringBuilder("");

        for (int i = 0; i < conditions.size(); i++) {
            String like = KEY_ITEM_ID + " LIKE '" + conditions.get(i) + "%' ";
            if (i != 0)  like = "OR " + like;
            conditionLike.append(like);
        }

        String query = "SELECT * FROM " +TABLE_ITEMS_DATA
                +" WHERE ("+conditionLike+") AND ("+KEY_ITEM_MODE+" < "+data_mode+") ORDER BY RANDOM()";

        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                items.add(getSimpleItemFromCursor(cursor));
            }

        } finally {
            cursor.close();
        }

        return items;
    }



    public Section selectSectionDataFromDB(SQLiteDatabase db, Section section) {

        int studiedDataCount;
        int knownDataCount;
        int allDataCount;
        int errorsCount;

        StringBuilder conditionLike = new StringBuilder("");
        StringBuilder conditionItemLike = new StringBuilder("WHERE ");


        for (int i = 0; i < section.catIds.size(); i++) {
            String like = KEY_USER_ITEM_ID + " LIKE '" + section.catIds.get(i) + "%' ";
            String itemLike = KEY_ITEM_ID + " LIKE '" + section.catIds.get(i) + "%' ";

            if (i != 0) {
                like = "OR " + like;
                itemLike = "OR " +  itemLike;
            }

            conditionLike.append(like);
            conditionItemLike.append(itemLike);
        }


        String dataItemsQuery = "SELECT * FROM " + TABLE_USER_DATA + " WHERE (" + conditionLike +") AND (" +
                KEY_ITEM_SCORE + " > 0 AND " + KEY_ITEM_SCORE + "< 3)";

        Cursor knownItemsCursor = db.rawQuery(dataItemsQuery , null);

        knownDataCount = knownItemsCursor.getCount();

        knownItemsCursor.close();


        dataItemsQuery = "SELECT * FROM " + TABLE_USER_DATA + " WHERE (" + conditionLike +") AND " +
                KEY_ITEM_SCORE + " > 2";

        Cursor studiedItemsCursor = db.rawQuery(dataItemsQuery , null);

        studiedDataCount = studiedItemsCursor.getCount();

        studiedItemsCursor.close();

        dataItemsQuery = "SELECT * FROM " + TABLE_USER_DATA + " WHERE (" + conditionLike +") AND " +
                KEY_ITEM_ERRORS + " > 0";

        Cursor errorsItemsCursor = db.rawQuery(dataItemsQuery , null);

        errorsCount = errorsItemsCursor.getCount();

        errorsItemsCursor.close();


        dataItemsQuery = "SELECT * FROM " + TABLE_ITEMS_DATA + " " + conditionItemLike;

        Cursor allItemsCursor = db.rawQuery(dataItemsQuery , null);
        allDataCount = allItemsCursor.getCount();
        allItemsCursor.close();

        section.studiedDataCount = studiedDataCount;
        section.knownDataCount = knownDataCount;
        section.allDataCount = allDataCount;
        section.errorsCount = errorsCount;

        return section;
    }


    public ArrayList<DataItem> getSectionListDataItems(ArrayList<NavCategory> navCategories) {
        ArrayList<DataItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();


        String query =  "SELECT * FROM "
                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA
                +" b ON a.item_id=b.user_item_id"
                +" WHERE (a."+KEY_ITEM_ID + " LIKE ?) AND (a."+KEY_ITEM_MODE+" < "+data_mode+") ORDER BY a.id";

        NavCategory navCategory = new NavCategory();

        for (int i = 0; i < navCategories.size(); i++) {

            navCategory = navCategories.get(i);


            if (navCategory.review) {

                Cursor cursor = db.rawQuery(query, new String[]{navCategory.id + "%"});

                try {
                    while (cursor.moveToNext()) {

                        if (cursor.isFirst()) {

                            DataItem dataItem = new DataItem();
                            dataItem.type = "group_title";
                            dataItem.item = navCategory.title;
                            items.add(dataItem);
                        }

                        items.add(getItemFromCursor(cursor));

                    }
                } finally {
                    cursor.close();
                }
            }

        }


        db.close();

        return items;
    }

    public ArrayList<ArrayList<DataItem>> getSectionGroupsDataItems(ArrayList<NavCategory> navCategories) {

        ArrayList<ArrayList<DataItem>> groups = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String query =  "SELECT * FROM "
                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA
                +" b ON a.item_id=b.user_item_id"
                +" WHERE (a."+KEY_ITEM_ID + " LIKE ?) AND (a."+KEY_ITEM_MODE+" <= "+SECTION_REVIEW_MAX_MODE+") ORDER BY a.id";

        for (int i = 0; i < navCategories.size(); i++) {

            NavCategory navCategory = navCategories.get(i);
            ArrayList<DataItem> items = new ArrayList<>();

            if (navCategory.review) {

                Cursor cursor = db.rawQuery(query, new String[]{navCategory.id + "%"});

                try {
                    while (cursor.moveToNext()) {

                        DataItem item = getItemFromCursor(cursor);
                        item.cat = navCategory.id;
                        items.add(item);


                    }
                } finally {
                    cursor.close();
                }
            }

            groups.add(items);
        }

        db.close();

        return groups;
    }



    public UserStats getAllDataItemsStats(UserStats stats) {  /// TODO fix it

        int studiedCount = 0;
        int knownCount = 0;
        int allCount = 0;


        SQLiteDatabase db = this.getWritableDatabase();


        StringBuilder conditionLike = new StringBuilder("");
        StringBuilder userConditionLike = new StringBuilder("");

        for (int i = 0; i < stats.userStatsData.idsToCheck.size(); i++) {
            String like = KEY_ITEM_ID + " LIKE '" + stats.userStatsData.idsToCheck.get(i) + "%' ";
            String userLike = KEY_USER_ITEM_ID + " LIKE '" + stats.userStatsData.idsToCheck.get(i) + "%' ";

            if (i != 0) {
                like = "OR " + like;
                userLike = "OR " + userLike;
            }
            conditionLike.append(like);
            userConditionLike.append(userLike);
        }


        String query = "SELECT * FROM " +TABLE_ITEMS_DATA
                +" WHERE ("+conditionLike +")"
                + "AND ("+KEY_ITEM_MODE+" < "+data_mode+")";

        Cursor cursor = db.rawQuery(query, null);

        allCount = cursor.getCount();

        query = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"

                +" WHERE ("+userConditionLike+") "
                +" AND (b."+KEY_ITEM_MODE+" < "+data_mode+") "

                +" AND (a."+KEY_ITEM_SCORE +" > 0) ";

        cursor = db.rawQuery(query, null);
        knownCount = cursor.getCount();

        query = "SELECT * FROM "

                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"
                +" WHERE ("+userConditionLike+") "
                +" AND (b."+KEY_ITEM_MODE+" < "+data_mode+") "

                +" AND (a."+KEY_ITEM_SCORE +" > 2) ";

        cursor = db.rawQuery(query, null);
        studiedCount = cursor.getCount();

        cursor.close();

        stats.userStatsData.allDataCount = allCount;
        stats.userStatsData.studiedDataCount = studiedCount;
        stats.userStatsData.familiarDataCount = knownCount;

        db.close();

        return stats;
    }



    public ArrayList<DataItem> getCatByTag(String cat) {  //// mode for cat

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<DataItem> items = getDataItemsByCatId(db, cat);

        db.close();

        return items;
    }



    public ArrayList<DataItem> getDataItemsByCatId(SQLiteDatabase db, String cat) {

        ArrayList<DataItem> items = new ArrayList<>();

        String idPrefix = cat + "%";

        String query = "SELECT * FROM "
                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA
                +" b ON a.item_id=b.user_item_id"
                +" WHERE (a.item_id LIKE ?) AND (a.item_mode < "+data_mode+") ORDER BY a.id";

        Cursor cursor = db.rawQuery(query, new String[]{idPrefix});

        try {
            while (cursor.moveToNext()) {
                items.add(getItemFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        return items;
    }


    public int[] getDataItemsCountsByCatId(SQLiteDatabase db, String cat) {

        int studiedItems = 0;
        int totalItems = 0;

        String idPrefix = cat + "%";

        String query = "SELECT * FROM "
                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA
                +" b ON a.item_id=b.user_item_id"
                +" WHERE (a.item_id LIKE ?) AND (a.item_mode < "+data_mode+") ORDER BY a.id";

        Cursor cursor = db.rawQuery(query, new String[]{idPrefix});
        totalItems  = cursor.getCount();

        try {
            while (cursor.moveToNext()) {

                int rate = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SCORE));
                if (rate > 0 ) studiedItems ++;
            }
        } finally {
            cursor.close();
        }

        return new int[]{studiedItems, totalItems};
    }





    public ArrayList<DataItem> getStarredFromDB(ArrayList<NavCategory> navCategories) {
        return getStarredFromDB(1, navCategories);
    }

    public ArrayList<DataItem> getStarredFromDB(int type, ArrayList<NavCategory> navCategories) {

        StringBuilder conditionLike = new StringBuilder("");

        for (int i = 0; i < navCategories.size(); i++) {

            String like = "a."+KEY_USER_ITEM_ID + " LIKE '" + navCategories.get(i).id + "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }


        ArrayList<DataItem> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"
                +" WHERE ("+conditionLike+") AND a."+KEY_ITEM_STARRED +" > ? ORDER BY b.id";

        Cursor cursor = db.rawQuery(query, new String[]{"0"});

        try {
            while (cursor.moveToNext()) {

                DataItem item = getItemFromCursor(cursor);
                if (item.db_filter == null) item.db_filter = "";
                if (type == 2) {
                    if (item.db_filter.contains(GALLERY_TAG)) items.add(item);
                } else {
                    if (!item.db_filter.contains(GALLERY_TAG)) items.add(item);
                }
            }
        } finally {
            cursor.close();
        }


        query = "SELECT * FROM " +TABLE_USER_DATA +" a "
                +"INNER JOIN "+TABLE_USER_DATA_ITEMS +" b "
                +"ON a." + KEY_USER_ITEM_ID + " = b."+KEY_UDATA_ID

                +" LEFT JOIN "+TABLE_UCAT_UDATA +" c "
                +"ON b." + KEY_UDATA_ID + " = c."+KEY_UDC_UDATA_ID

                + " WHERE a."+KEY_ITEM_STARRED +" > ? ORDER BY b."+KEY_UDATA_CREATED;


        Cursor uCursor = db.rawQuery(query, new String[]{"0"});

        try {
            while (uCursor.moveToNext()) {

                DataItem item = getAllInfoUdataFromDB(uCursor);
                item.cat = uCursor.getString(uCursor.getColumnIndex(KEY_UDC_UCAT_ID));

                items.add(item);

            }
        } finally {
            uCursor.close();
        }

        db.close();


        return items;
    }


    public DataItem getDataItemById(String detail_id) {

        DataItem dataItem = new DataItem();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS_DATA,  null, KEY_ITEM_ID +" = ?",
                new String[] { detail_id}, null, null, null);

        if ( cursor.moveToFirst() ) {
            dataItem = getSimpleItemFromCursor(cursor);
        } else {
            dataItem.item = "not found";
            dataItem.info = "This entry doesn't exist";
        }

        cursor.close();
        db.close();

        return  dataItem;
    }

    public DataItem getDataItemDBById(String detail_id) {

        DataItem dataItem = new DataItem();
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM "
                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA +" b ON a.item_id=b.user_item_id"
                +" WHERE a."+ KEY_ITEM_ID +" = ? " ;


        Cursor cursor = db.rawQuery(query, new String[] {detail_id});


        if ( cursor.moveToFirst() ) {
            dataItem = getItemFromCursor(cursor);
        } else {
            dataItem.item = "not found";
            dataItem.info = "This entry doesn't exist";
        }

        cursor.close();
        db.close();

        return  dataItem;
    }




    public DetailItem getDetailById(String detail_id) {

        DetailItem detailItem = new DetailItem();

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query(TABLE_DETAILS_DATA,  null, KEY_DETAIL_ID +" = ?",
                new String[] { detail_id}, null, null, null);

        if ( cursor.moveToFirst() ) {
            detailItem = getDetailFromCursor(cursor);
        } else {
            detailItem.title = "not found";
        }

        cursor.close();
        db.close();

        return  detailItem;
    }


    private DetailItem getDetailFromCursor(Cursor cursor) {
        DetailItem detailItem = new DetailItem();
        detailItem.id = cursor.getString(cursor.getColumnIndex(KEY_DETAIL_ID));
        detailItem.title = cursor.getString(cursor.getColumnIndex(KEY_DETAIL_TITLE));
        detailItem.desc = cursor.getString(cursor.getColumnIndex(KEY_DETAIL_DESC));
        detailItem.image = cursor.getString(cursor.getColumnIndex(KEY_DETAIL_IMAGE));
        detailItem.img_info = cursor.getString(cursor.getColumnIndex(KEY_DETAIL_IMG_INFO));

        return detailItem;
    }


    public Map<String, String> checkCatProgressDB(ArrayList<String> catIds ) {

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder conditions = new StringBuilder("WHERE ");
        for (int i = 0; i < catIds.size(); i++) {
            String condition = KEY_CAT_ID+ " = '" + catIds.get(i) + "' ";
            if (i != 0) condition = "OR " + condition;
            conditions.append(condition);
        }

        String query = "SELECT * FROM " + TABLE_CAT_DATA + " " + conditions;

        Cursor cursor = db.rawQuery(query, null);

        Map<String,String> myMap = new HashMap<>();

        try {
            while (cursor.moveToNext()) {
                myMap.put(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_CAT_PROGRESS)));

            }
        } finally {
            cursor.close();
        }

        db.close();


        // Toast.makeText(cntx, "" + res, Toast.LENGTH_LONG).show();

        return myMap;

    }

    public Map<String, String> getTestsByCatId(ArrayList<String> catIds ) {

        SQLiteDatabase db = this.getWritableDatabase();

        Map<String, String> testsByCatId = getTestsByCatId(catIds, db );

        db.close();


        return testsByCatId;



    }


    public Map<String, String> getTestsByCatId(ArrayList<String> catIds, SQLiteDatabase db ) {

        Map<String,String> myMap = new HashMap<>();

        if (catIds.size() > 0) {

            StringBuilder conditions = new StringBuilder("WHERE ");  /// build request for all required tests

            for (int i = 0; i < catIds.size(); i++) {
                String condition = KEY_TEST_TAG + " LIKE '" + catIds.get(i) + "%' ";
                if (i != 0) condition = "OR " + condition;
                conditions.append(condition);
            }

            String query = "SELECT * FROM " + TABLE_TESTS_DATA + " " + conditions;
            Cursor cursor = db.rawQuery(query, null);


            try {
                while (cursor.moveToNext()) {
                    myMap.put(cursor.getString(cursor.getColumnIndex(KEY_TEST_TAG)),
                            cursor.getString(cursor.getColumnIndex(KEY_TEST_PROGRESS)));

                }
            } finally {
                cursor.close();
            }

        }


        return myMap;

    }

    public String[] getTestByTestId(SQLiteDatabase db, String testId) {

            String[] testData = new String[] {testId, "0", "0"};

            Cursor cursor = db.query(TABLE_TESTS_DATA,  null,
                     KEY_TEST_TAG +" = ?", new String[] { testId },
                     null, null, null);

            try {
                while (cursor.moveToNext()) {

                    testData[1] = cursor.getString(cursor.getColumnIndex(KEY_TEST_PROGRESS));
                    testData[2] = String.valueOf(cursor.getLong(cursor.getColumnIndex(KEY_TEST_TIME)));

                }
            } finally {
                cursor.close();
            }

        return testData;

    }




    public UserStatsData getErrorsData(UserStatsData userStatsData) {  /// TODO add cat selection

        SQLiteDatabase db = this.getWritableDatabase();

        userStatsData.errorsWords = new ArrayList<>();
        userStatsData.mostErrorsWords = new ArrayList<>();

        String query = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"
                +" WHERE (a."+KEY_ITEM_ERRORS +" > ?) "
                // + " AND (b."+KEY_ITEM_MODE+" < "+data_mode+")"

                +"ORDER BY a."+KEY_ITEM_TIME_ERROR+" DESC LIMIT 31";

        Cursor errorsCursor = db.rawQuery(query, new String[]{"0"});


        try {
            while (errorsCursor.moveToNext()) {
                userStatsData.errorsWords.add(getItemFromCursor(errorsCursor));
            }
        } finally {
            errorsCursor.close();
        }

        String mostQuery = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"
                +" WHERE (a."+KEY_ITEM_ERRORS +" > ?) "
                // + " AND (b."+KEY_ITEM_MODE+" < "+data_mode+")"


                +"ORDER BY a."+KEY_ITEM_TIME_ERROR+" DESC, a."+ KEY_ITEM_ERRORS +" DESC LIMIT 30";


        Cursor mostErrorsCursor = db.rawQuery(mostQuery, new String[]{"0"});

        try {
            while (mostErrorsCursor.moveToNext()) {

                userStatsData.mostErrorsWords.add(getItemFromCursor(mostErrorsCursor));
            }
        } finally {
            mostErrorsCursor.close();
        }

        db.close();


        return userStatsData;
    }


    public ArrayList<DataItem> getOldestFromDB(ArrayList<String> checkCatIds) {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<DataItem> oldestData = getOldestData(db, checkCatIds);

        db.close();

        return oldestData;
    }



    private ArrayList<DataItem> getOldestData(SQLiteDatabase db, ArrayList<String> checkCatIds) {

        ArrayList<DataItem> items = new ArrayList<>();

        StringBuilder conditionLike = new StringBuilder("");


        for (int i = 0; i < checkCatIds.size(); i++) {
            String like = "a."+KEY_USER_ITEM_ID + " LIKE '" + checkCatIds.get(i)+ "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }


        String query = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"
                +" WHERE ("+conditionLike+") AND a."+KEY_ITEM_SCORE +" > ? "
                +"ORDER BY a."+ KEY_ITEM_TIME +" ASC LIMIT "+String.valueOf(Constants.REVISE_NUM);

        Cursor cursor = db.rawQuery(query, new String[] { "0"});

        try {
            while (cursor.moveToNext()) {
                items.add(getItemFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return  items;
    }


    public ArrayList<DataItem> getDataItemsByCatIds(SQLiteDatabase db, ArrayList<String> catIds) {

        ArrayList<DataItem> items = new ArrayList<>();

        if (catIds.size() == 0) return  items;

        StringBuilder conditionLike = new StringBuilder("");


        for (int i = 0; i < catIds.size(); i++) {
            String like = "a."+KEY_ITEM_ID + " LIKE '" + catIds.get(i)+ "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }


        String query = "SELECT * FROM "
                //+TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA  +" b ON a.user_item_id=b.item_id"

                +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA +" b ON a.item_id = b.user_item_id"

                +" WHERE ("+conditionLike+") AND (a."+KEY_ITEM_MODE+" < "+data_mode+")";


        /// TODO add filter my mode

        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                items.add(getItemFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return  items;
    }


    public UserStatsData checkAppStatsDB(UserStatsData userStatsData) {

        SQLiteDatabase db = this.getWritableDatabase();

        userStatsData.sectionsDataList = checkSectionListStatsDB(db, userStatsData.sectionsDataList);

        db.close();
        return userStatsData;
    }



    public Section checkSectionStatsDB(Section section) {

        SQLiteDatabase db = this.getWritableDatabase();
        section = checkSectionStatsDB(db, section);
        db.close();

        return section;
    }


    private ArrayList<Section> checkSectionListStatsDB(SQLiteDatabase db, ArrayList<Section> sections) {
        for (Section section: sections) {
            section = checkSectionStatsDB(db, section);
        }
        return sections;
    }


    private Section checkSectionStatsDB(SQLiteDatabase db, Section section) {  //// TODO results for section

        section = getSectionTestsResults(db, section);
        section = checkSectionDataStatus(db, section);

        section.calculateProgress();

        return section;
    }


    private Section getSectionTestsResults(SQLiteDatabase db, Section section) {




        //section.testResults = getSectionCatsTestsResults(db, section);

        section.testResults = getSectionCatsExResults(db, section);


        String  query = "SELECT * FROM " + TABLE_TESTS_DATA + " WHERE "+KEY_TEST_TAG
                +" LIKE '"+Constants.SECTION_TEST_PREFIX + section.id + "%'";

        Cursor tcursor = db.rawQuery(query, null);

        int allResult = 0;

        Map<String,String> myMap = new HashMap<>();

        long lastTime = 0;

        try {
            while (tcursor.moveToNext()) {
                int result = Integer.valueOf(tcursor.getString(tcursor.getColumnIndex(KEY_TEST_PROGRESS)));

                //long testTime = tcursor.getLong(tcursor.getColumnIndex(KEY_TEST_TIME));
                //if (testTime > lastTime) lastTime = testTime;

                myMap.put(
                        tcursor.getString(tcursor.getColumnIndex(KEY_TEST_TAG)),
                        tcursor.getString(tcursor.getColumnIndex(KEY_TEST_PROGRESS)) );




                allResult += result;
            }
        } finally {
            tcursor.close();
        }

        section.lastTime = lastTime;
        section.controlMap = myMap;
        section.controlTests = allResult / Constants.SECTION_TESTS_NUM;


        return section;
    }

    private int getSectionCatsTestsResults(SQLiteDatabase db, Section section) {

        int testsResults = 0;
        section.stadiedCatsCount = 0;

        StringBuilder conditions = new StringBuilder("WHERE ");

        for (int i = 0; i < section.checkCatIds.size(); i++) {
            String condition = KEY_CAT_ID+ " = '" + section.checkCatIds.get(i) + "' ";
            if (i != 0) condition = "OR " + condition;
            conditions.append(condition);
        }

        //// getting section tests results

        String query = "SELECT * FROM " + TABLE_CAT_DATA + " " + conditions;

        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                int result = Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_CAT_PROGRESS)));
                testsResults += result;
                if (result > 50) section.stadiedCatsCount ++;
            }
        } finally {
            cursor.close();
        }

        return  testsResults / section.checkCatIds.size() ;

    }



    private int getSectionCatsExResults(SQLiteDatabase db, Section section) {


        ///  get section average result
        // - get section cats average result list
        /// - SUM cats results list
        //// - divide SUM by cats size

        Computer computer = new Computer();
        int testsResults = 0;
        ArrayList<String> checkCatIds = section.checkCatIds;
        boolean speaking = speaking_mode;

        // getting section all tests results from db
        Map<String, ArrayList<String>> exResults = computer.getCatExResults(checkCatIds, getTestsByCatId(checkCatIds, db));

        ///
        Map<String, String> catsProgressMap = computer.getCatProgress(checkCatIds, speaking, exResults);

        for (String catId: checkCatIds) {

            int catProgress = 0;

            if (catsProgressMap.containsKey(catId) ) {
                catProgress = Integer.parseInt(catsProgressMap.get(catId));
            }

            testsResults += catProgress;

        }



        return  testsResults / checkCatIds.size() ;

    }





    private Section checkSectionDataStatus(SQLiteDatabase db, Section section) {

        int studiedDataCount = 0;
        int knownDataCount = 0;
        int allDataCount = 0;

        int errorsCount = 0;

        StringBuilder conditionLike = new StringBuilder("");
        StringBuilder conditionItemLike = new StringBuilder("");


        for (int i = 0; i < section.catIds.size(); i++) {
            String like = "a."+KEY_USER_ITEM_ID + " LIKE '" + section.catIds.get(i) + "%' ";
            String itemLike = KEY_ITEM_ID + " LIKE '" + section.catIds.get(i) + "%' ";
            if (i != 0) {
                like = "OR " + like;
                itemLike = "OR " +  itemLike;
            }
            conditionLike.append(like);
            conditionItemLike.append(itemLike);
        }

        StringBuilder conditionAllCatLike = new StringBuilder("");

        for (int i = 0; i < section.allCatIds.size(); i++) {
            String like = "a."+KEY_USER_ITEM_ID + " LIKE '" + section.allCatIds.get(i) + "%' ";
            if (i != 0)  like = "OR " + like;
            conditionAllCatLike.append(like);
        }

                        //TODO CHECK INNER
        String dataItemsQuery = "SELECT * FROM "

                +TABLE_USER_DATA +" a INNER JOIN "+ TABLE_ITEMS_DATA +" b ON a.user_item_id=b.item_id"

                + " WHERE (" + conditionLike +") "

                + "AND (a." + KEY_ITEM_SCORE + " > 0 AND a." + KEY_ITEM_SCORE + "< 3) "

                + "AND (b."+KEY_ITEM_MODE+" < "+data_mode+")";

        Cursor knownItemsCursor = db.rawQuery(dataItemsQuery , null);

        knownDataCount = knownItemsCursor.getCount();

        knownItemsCursor.close();


        dataItemsQuery = "SELECT * FROM "

                + TABLE_USER_DATA +" a INNER JOIN "+ TABLE_ITEMS_DATA +" b ON a.user_item_id=b.item_id"
                + " WHERE (" + conditionLike +") "

                + "AND (a." + KEY_ITEM_SCORE + " > 2) "

                + "AND (b."+KEY_ITEM_MODE+" < "+data_mode+")";

        Cursor studiedItemsCursor = db.rawQuery(dataItemsQuery , null);

        studiedDataCount = studiedItemsCursor.getCount();

        studiedItemsCursor.close();


        dataItemsQuery = "SELECT * FROM "       /// TODO check mode for errors

                + TABLE_USER_DATA +" a INNER JOIN "+ TABLE_ITEMS_DATA +" b ON a.user_item_id=b.item_id"

                + " WHERE (" + conditionAllCatLike +")"
                // + "AND (b."+KEY_ITEM_MODE+" < "+data_mode+")"

                + "AND (a." + KEY_ITEM_ERRORS + " > 0)";

        Cursor errorsItemsCursor = db.rawQuery(dataItemsQuery , null);

        errorsCount = errorsItemsCursor.getCount();

        errorsItemsCursor.close();


        dataItemsQuery = "SELECT * FROM " + TABLE_ITEMS_DATA
                + " WHERE  (" + conditionItemLike +")"
                +" AND ("+KEY_ITEM_MODE+" < "+data_mode+")";

        Cursor allItemsCursor = db.rawQuery(dataItemsQuery , null);
        allDataCount = allItemsCursor.getCount();
        allItemsCursor.close();

        section.studiedDataCount = studiedDataCount;
        section.knownDataCount = knownDataCount;
        section.allDataCount = allDataCount;
        section.errorsCount = errorsCount;


        return section;
    }


    public Section getSectionCatItemsStats(Section section) {

        SQLiteDatabase db = this.getWritableDatabase();

        section = checkSectionDataStatus(db, section);

        section = getSectionCatItemsStatsDB(db, section);

        section.calculateProgress();

        db.close();

        return section;
    }



    private Section getSectionCatItemsStatsDB(SQLiteDatabase db, Section section) {

        for (Category category: section.categories) {

            String idPrefix = category.id+"%";

            String query = "SELECT * FROM "
                    +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA
                    +" b ON a.item_id=b.user_item_id"

                    +" WHERE (a."+KEY_ITEM_ID+" LIKE ?) "
                    +" AND(a."+KEY_ITEM_MODE+" < "+data_mode+") "

                    +" ORDER BY a.id";


            Cursor cursor = db.rawQuery(query , new String[]{idPrefix});

            category.allDataCount = cursor.getCount();


            query = "SELECT * FROM "

                    +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA +" b ON a.item_id=b.user_item_id"

                    +" WHERE (a." + KEY_ITEM_ID + " LIKE ?) "
                    +" AND(a."+KEY_ITEM_MODE+" < "+data_mode+") "

                    +" AND (b."+KEY_ITEM_SCORE +" >0 AND b."+KEY_ITEM_SCORE +" <3)";

            cursor = db.rawQuery(query , new String[]{idPrefix});

            category.knownDataCount = cursor.getCount();


            query = "SELECT * FROM "

                    +TABLE_ITEMS_DATA +" a LEFT JOIN "+TABLE_USER_DATA +" b ON a.item_id=b.user_item_id"
                    +" WHERE (a." + KEY_ITEM_ID + " LIKE ?) "
                    +" AND(a."+KEY_ITEM_MODE+" < "+data_mode+") "

                    +"AND (b."+KEY_ITEM_SCORE +" > 2)";


            cursor = db.rawQuery(query , new String[]{idPrefix});

            category.studiedDataCount = cursor.getCount();


            category.calculateDataCount();


            cursor.close();

        }



        return section;
    }





    public ArrayList<Section> checkSectionsStats(ArrayList<Section> sectionsDataList) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (Section section: sectionsDataList) {

            String idPrefix = section.id+"%";
            int testResult = 0;
            String testId = section.id + "_1";


            Cursor cursor = db.query(TABLE_USER_DATA, new String[] {KEY_USER_ITEM_ID, KEY_ITEM_SCORE},
                    KEY_USER_ITEM_ID + " LIKE ? AND " + KEY_ITEM_SCORE + " > ? AND "+ KEY_ITEM_SCORE + " < ?",
                    new String[]{idPrefix, "0", "3"}, null, null, null);

            section.knownDataCount = cursor.getCount();


            cursor = db.query(TABLE_USER_DATA, new String[] {KEY_USER_ITEM_ID, KEY_ITEM_SCORE},
                    KEY_USER_ITEM_ID + " LIKE ? AND " + KEY_ITEM_SCORE + " > ? ",
                    new String[]{idPrefix, "2"}, null, null, null);

            section.studiedDataCount = cursor.getCount();


            cursor = db.query(TABLE_ITEMS_DATA, new String[] {KEY_ITEM_ID},
                    KEY_ITEM_ID + " LIKE ?",
                    new String[]{idPrefix}, null, null, null);

            section.allDataCount = cursor.getCount();


            Cursor testCursor = db.query(TABLE_TESTS_DATA, new String[] { KEY_TEST_TAG, KEY_TEST_PROGRESS },
                    KEY_TEST_TAG + " = ?", new String[]{ testId }, null, null, null);


            if ( testCursor.moveToFirst() ) {
                testResult = testCursor.getInt(testCursor.getColumnIndex(KEY_TEST_PROGRESS));
            }

            section.testResults = testResult;

            section.calculateProgress();

            testCursor.close();
            cursor.close();
        }


        return sectionsDataList;
    }



    public ArrayList<DataItem> getSectionErrorsData(Section section) { // TODO check order

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<DataItem> errors = new ArrayList<>();

        StringBuilder conditionLike = new StringBuilder("");


        for (int i = 0; i < section.allCatIds.size(); i++) {
            String like = "a."+KEY_USER_ITEM_ID + " LIKE '" + section.allCatIds.get(i) + "%' ";

            if (i != 0) {
                like = "OR " + like;
            }
            conditionLike.append(like);
        }


        String mostQuery = "SELECT * FROM "
                +TABLE_USER_DATA +" a INNER JOIN "+TABLE_ITEMS_DATA
                +" b ON a.user_item_id=b.item_id"
                +" WHERE ("+conditionLike +") "
                // +" AND (b." + KEY_ITEM_MODE + " < " + data_mode + ") "

                +"AND (a."+KEY_ITEM_ERRORS +" > ?) "
                +"ORDER BY a."+KEY_ITEM_TIME_ERROR+" DESC, a."+ KEY_ITEM_ERRORS +" DESC LIMIT 30";


        Cursor mostErrorsCursor = db.rawQuery(mostQuery, new String[]{"0"});

        try {
            while (mostErrorsCursor.moveToNext()) {
                errors.add(getItemFromCursor(mostErrorsCursor));
            }
        } finally {
            mostErrorsCursor.close();
        }

        db.close();


        return errors;
    }




    private DataItem getSimpleItemFromCursor(Cursor cursor) {
        DataItem dataItem = new DataItem();

        dataItem.id = cursor.getString(cursor.getColumnIndex(KEY_ITEM_ID));
        dataItem.item = cursor.getString(cursor.getColumnIndex(KEY_ITEM_TITLE));
        dataItem.info = cursor.getString(cursor.getColumnIndex(KEY_ITEM_DESC));
        dataItem.image = cursor.getString(cursor.getColumnIndex(KEY_ITEM_IMAGE));

        dataItem.grammar = cursor.getString(cursor.getColumnIndex(KEY_ITEM_GRAMMAR));
        dataItem.pronounce = cursor.getString(cursor.getColumnIndex(KEY_ITEM_PRONOUNCE));
        dataItem.base = cursor.getString(cursor.getColumnIndex(KEY_ITEM_BASE));

        dataItem.trans1 = cursor.getString(cursor.getColumnIndex(KEY_ITEM_TRANS1));
        dataItem.trans2= cursor.getString(cursor.getColumnIndex(KEY_ITEM_TRANS2));

        dataItem.item_info_1 = cursor.getString(cursor.getColumnIndex(KEY_ITEM_INFO_1));
        dataItem.divider = cursor.getString(cursor.getColumnIndex(KEY_ITEM_DIVIDER));
        dataItem.filter = cursor.getString(cursor.getColumnIndex(KEY_ITEM_FILTER));

        return dataItem;
    }


    private DataItem getItemFromCursor(Cursor cursor) {

        DataItem dataItem = getSimpleItemFromCursor(cursor);

        dataItem.starred = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_STARRED));
        dataItem.rate = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SCORE));
        dataItem.errors = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ERRORS));
        dataItem.mode = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_MODE));

        dataItem.db_filter = cursor.getString(cursor.getColumnIndex(KEY_ITEM_INFO));

        dataItem.starred_time = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_TIME_STARRED));
        dataItem.time = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_TIME));
        dataItem.time_errors = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_TIME_ERROR));

        return dataItem;
    }

    private DataItem getSimpleItemFromNoteCursor(Cursor cursor) {

        DataItem dataItem = new DataItem();

        dataItem.id = cursor.getString(cursor.getColumnIndex(KEY_NOTE_ID));
        dataItem.item = cursor.getString(cursor.getColumnIndex(KEY_NOTE_TITLE));
        dataItem.info = cursor.getString(cursor.getColumnIndex(KEY_NOTE_TEXT));
        dataItem.image = cursor.getString(cursor.getColumnIndex(KEY_NOTE_ICON));

        dataItem.db_filter = NOTE_TAG;
        dataItem.filter = NOTE_TAG;

        return dataItem;
    }



    private DataItem getDataItemFromUDATA(Cursor cursor) {

        DataItem dataItem = new DataItem();

        dataItem.id = cursor.getString(cursor.getColumnIndex(KEY_UDATA_ID));
        dataItem.item = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TEXT));
        dataItem.info = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TRANSLATE));
        dataItem.trans1 = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TRANSCRIPT));
        dataItem.pronounce = dataItem.item;
        dataItem.grammar = cursor.getString(cursor.getColumnIndex(KEY_UDATA_GRAMMAR));
        dataItem.sound = cursor.getString(cursor.getColumnIndex(KEY_UDATA_SOUND));
        dataItem.item_info_1 = cursor.getString(cursor.getColumnIndex(KEY_UDATA_INFO));

        return dataItem;
    }


    private DataObject getDataFromUCAT(Cursor cursor) {

        DataObject dataObject = new DataObject();
        dataObject.id = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ID));
        dataObject.title = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TITLE));
        dataObject.desc = cursor.getString(cursor.getColumnIndex(KEY_UCAT_DESC));
        dataObject.params = cursor.getString(cursor.getColumnIndex(KEY_UCAT_PARAMS));

        dataObject.time_created = cursor.getLong(cursor.getColumnIndex(KEY_UCAT_CREATED));
        dataObject.time_updated = cursor.getLong(cursor.getColumnIndex(KEY_UCAT_UPDATED_SORT));

        dataObject.type = cursor.getString(cursor.getColumnIndex(KEY_UCAT_TYPE));
        if (dataObject.type == null) dataObject.type = "";

        dataObject.image = cursor.getString(cursor.getColumnIndex(KEY_UCAT_ICON));
        if (dataObject.image == null) dataObject.image = "";

        return dataObject;
    }


    private DataItem getAllInfoUdataFromDB(Cursor cursor) {

        DataItem dataItem = new DataItem();

        dataItem.id = cursor.getString(cursor.getColumnIndex(KEY_UDATA_ID));
        dataItem.item = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TEXT));

        //long i = cursor.getLong(cursor.getColumnIndex(KEY_UDATA_UPDATED_SORT));
        dataItem.info = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TRANSLATE)); // TODO fix
        dataItem.trans1 = cursor.getString(cursor.getColumnIndex(KEY_UDATA_TRANSCRIPT));
        dataItem.pronounce = dataItem.item;
        dataItem.grammar = cursor.getString(cursor.getColumnIndex(KEY_UDATA_GRAMMAR));
        dataItem.sound = cursor.getString(cursor.getColumnIndex(KEY_UDATA_SOUND));
        dataItem.item_info_1 = cursor.getString(cursor.getColumnIndex(KEY_UDATA_INFO));

        dataItem.starred = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_STARRED));
        dataItem.rate = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SCORE));
        dataItem.errors = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ERRORS));

        dataItem.starred_time = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_TIME_STARRED));
        dataItem.time = cursor.getLong(cursor.getColumnIndex(KEY_UDATA_UPDATED_SORT));
        dataItem.time_errors = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_TIME_ERROR));

        return dataItem;
    }



    public ArrayList<DataItem> checkStarredList(ArrayList<DataItem> words) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (DataItem word: words) {
            Cursor cursor = db.query(TABLE_USER_DATA, null, KEY_USER_ITEM_ID + " = ? ",
                    new String[]{word.id}, null, null, null);

            if (cursor.moveToFirst()) {
                cursor.moveToFirst();

                word.starred = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_STARRED));
                word.starred_time = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_TIME_STARRED));
                word.rate = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_SCORE));
                word.errors = cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ERRORS));

            }
            cursor.close();
        }
        db.close();

        return words;
    }


    public void sanitizeDB() {

        SQLiteDatabase db = this.getWritableDatabase();

        sanitizeDB(db);

//        cleanUDataFromUser(db);

        db.close();
    }


    private void sanitizeDB(SQLiteDatabase db) {

        DataFromJson dataFromJson= new DataFromJson(cntx);
        ArrayList<DataItem> allItems = dataFromJson.getItemsFromAllFiles();

        Cursor cursor = db.query(TABLE_USER_DATA, new String[]{KEY_USER_ITEM_ID}, null, null, null, null, null);

        int del = 0;


        try {
            while (cursor.moveToNext()) {
                String txt = cursor.getString(cursor.getColumnIndex(KEY_USER_ITEM_ID));
                boolean found = false;

                if (txt.contains(UD_PREFIX)) found = true;

                DataItem foundW = new DataItem();
                for (DataItem word: allItems) {
                    if (word.id.equals(txt)) {
                        foundW = word;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    allItems.remove(foundW);
                } else {

                    db.delete(TABLE_USER_DATA, KEY_USER_ITEM_ID +" = ?", new String[]{txt});
                    del++;
                }
            }
        } finally {
            cursor.close();
        }
    }


    public int deleteExData(String[] cat_tag) {
        int count = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        for (String aCat_tag : cat_tag) {
            int t = db.delete(TABLE_TESTS_DATA, KEY_TEST_TAG+" = ?", new String[]{aCat_tag});
            count = count + t;

        }
        db.close();

        return count;
    }


    private int countScore(int currentScore, int result) {
        int score = currentScore + result;
        if (score < 0) score = 0;
        if (score > MAX_SCORE) score = MAX_SCORE;
        return score;
    }


    public void importUserData(SQLiteDatabase db, List<DBImport.UserItemData> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA);
        db.execSQL(CREATE_USER_ITEMS_TABLE_IF_EXISTS);

        db.beginTransaction();

        try {

            for (DBImport.UserItemData item: list) {
                insertImportedUserItem(db, item);
            }

            db.setTransactionSuccessful();
            Toast.makeText(cntx, String.format(cntx.getString(R.string.entries_updated), list.size()), Toast.LENGTH_SHORT).show();

        } finally {
            db.endTransaction();
        }

    }




    private void insertImportedUserItem(SQLiteDatabase db, DBImport.UserItemData userItem) {

        /// TODO check for type if needed

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ITEM_ID, userItem.id);
        values.put(KEY_ITEM_INFO, userItem.itemInfo);
        values.put(KEY_ITEM_PROGRESS, userItem.itemProgress);
        values.put(KEY_ITEM_ERRORS, userItem.itemErrors);
        values.put(KEY_ITEM_SCORE, userItem.itemScore);
        values.put(KEY_ITEM_STATUS, userItem.itemStatus);
        values.put(KEY_ITEM_STARRED, userItem.itemStarred);
        values.put(KEY_ITEM_TIME, userItem.itemTime);
        values.put(KEY_ITEM_TIME_STARRED, userItem.itemTimeStarred);
        values.put(KEY_ITEM_TIME_ERROR, userItem.itemTimeError);

        db.insert(TABLE_USER_DATA, null, values);
    }


    public void importCatData (SQLiteDatabase db, List<DBImport.CatDataLine> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT_DATA);
        db.execSQL(CREATE_CATDATA_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.CatDataLine item: list) {
                insertImportedCatData(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }


    private void insertImportedCatData(SQLiteDatabase db, DBImport.CatDataLine catData) {

        /// TODO check for type if needed

        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, catData.catId);
        values.put(KEY_CAT_PROGRESS, catData.progress);

        db.insert(TABLE_CAT_DATA, null, values);
    }


    public void importTestsData (SQLiteDatabase db, List<DBImport.TestData> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS_DATA);
        db.execSQL(CREATE_TESTS_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.TestData item: list) {
                insertImportedTestsData(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }


    private void insertImportedTestsData(SQLiteDatabase db, DBImport.TestData testData) {

        /// TODO check for type if needed

        ContentValues values = new ContentValues();
        values.put(KEY_TEST_TAG, testData.tag);
        values.put(KEY_TEST_PROGRESS, testData.progress);
        values.put(KEY_TEST_TIME, testData.testTime);

        db.insert(TABLE_TESTS_DATA, null, values);
    }



    public void importBookmarksData (SQLiteDatabase db, List<DBImport.BookmarkData> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS_DATA);
        db.execSQL(CREATE_BOOKMARKS_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.BookmarkData item: list) {
                insertImportedBookmarkData(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }


    public void importNotesData (SQLiteDatabase db, List<DBImport.NoteDataDB> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES_DATA);
        db.execSQL(CREATE_NOTES_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.NoteDataDB item: list) {
                insertImportedNoteData(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }




    private void insertImportedBookmarkData(SQLiteDatabase db, DBImport.BookmarkData bookmarkData) {

        ContentValues values = new ContentValues();
        values.put(KEY_BOOKMARK_ITEM, bookmarkData.bookmarkItem);
        values.put(KEY_BOOKMARK_PARENT, bookmarkData.bookmarkParent);
        values.put(KEY_BOOKMARK_TIME, bookmarkData.bookmarkTime);
        values.put(KEY_BOOKMARK_TYPE, bookmarkData.bookmarkType);
        values.put(KEY_BOOKMARK_INFO, bookmarkData.bookmarkInfo);
        values.put(KEY_BOOKMARK_FILTER, bookmarkData.bookmarkFilter);

        db.insert(TABLE_BOOKMARKS_DATA, null, values);
    }

    private void insertImportedNoteData(SQLiteDatabase db, DBImport.NoteDataDB noteData) {

        ContentValues values = new ContentValues();

        values.put(KEY_NOTE_PRIMARY_ID, noteData.notePrimaryKey);
        values.put(KEY_NOTE_ID, noteData.noteId);
        values.put(KEY_NOTE_TITLE, noteData.noteTitle);
        values.put(KEY_NOTE_TEXT, noteData.noteContent);
        values.put(KEY_NOTE_ICON, noteData.noteIcon);
        values.put(KEY_NOTE_INFO, noteData.noteInfo);
        values.put(KEY_NOTE_STATUS, noteData.noteStatus);
        values.put(KEY_NOTE_TYPE, noteData.noteType);
        values.put(KEY_NOTE_PARAMS, noteData.noteParams);
        values.put(KEY_NOTE_FILTER, noteData.noteFilter);
        values.put(KEY_NOTE_PARENT, noteData.noteParent);
        values.put(KEY_NOTE_ORDER, noteData.noteOrder);
        values.put(KEY_NOTE_CREATED, noteData.noteCreated);
        values.put(KEY_NOTE_UPDATED, noteData.noteUpdated);
        values.put(KEY_NOTE_UPDATED_SORT, noteData.noteUpdatedSort);

        db.insert(TABLE_NOTES_DATA, null, values);
    }



    public void importUserDataItems(SQLiteDatabase db, List<DBImport.UserDataItem> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA_ITEMS);
        db.execSQL(CREATE_UDATA_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.UserDataItem item: list) {
                insertImportedUserDataItems(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }

    private void insertImportedUserDataItems(SQLiteDatabase db, DBImport.UserDataItem uDataItem) {

        ContentValues values = new ContentValues();

        values.put(KEY_UDATA_PRIMARY_ID, uDataItem.udataPrimaryId);
        values.put(KEY_UDATA_ID, uDataItem.udataId);
        values.put(KEY_UDATA_TEXT, uDataItem.udataText);
        values.put(KEY_UDATA_TRANSLATE, uDataItem.udataTranslate);
        values.put(KEY_UDATA_TRANSCRIPT, uDataItem.udataTranscript);
        values.put(KEY_UDATA_GRAMMAR, uDataItem.udataGrammar);
        values.put(KEY_UDATA_SOUND, uDataItem.udataSound);
        values.put(KEY_UDATA_INFO, uDataItem.udataInfo);
        values.put(KEY_UDATA_IMAGE, uDataItem.udataImage);
        values.put(KEY_UDATA_STATUS, uDataItem.udataStatus);
        values.put(KEY_UDATA_FILTER, uDataItem.udataFilter);
        values.put(KEY_UDATA_ORDER, uDataItem.udataOrder);
        values.put(KEY_UDATA_CREATED, uDataItem.udataCreated);
        values.put(KEY_UDATA_UPDATED, uDataItem.udataUpdated);
        values.put(KEY_UDATA_UPDATED_SORT, uDataItem.udataUpdatedSort);

        db.insert(TABLE_USER_DATA_ITEMS , null, values);
    }


    public void importUserDataCats(SQLiteDatabase db, List<DBImport.UserDataCat> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA_CATS);
        db.execSQL(CREATE_UCATS_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.UserDataCat item: list) {
                insertImportedUserDataCat(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }

    private void insertImportedUserDataCat(SQLiteDatabase db, DBImport.UserDataCat userDataCat) {

        ContentValues values = new ContentValues();

        values.put(KEY_UCAT_PRIMARY_ID, userDataCat.ucatPrimaryId);
        values.put(KEY_UCAT_ID, userDataCat.ucatId);
        values.put(KEY_UCAT_TITLE, userDataCat.ucatTitle);
        values.put(KEY_UCAT_DESC, userDataCat.ucatDesc);
        values.put(KEY_UCAT_ICON, userDataCat.ucatIcon);
        values.put(KEY_UCAT_INFO, userDataCat.ucatInfo);
        values.put(KEY_UCAT_STATUS, userDataCat.ucatStatus);
        values.put(KEY_UCAT_TYPE, userDataCat.ucatType);
        values.put(KEY_UCAT_FILTER, userDataCat.ucatFilter);
        values.put(KEY_UCAT_PARAMS, userDataCat.ucatParams);
        values.put(KEY_UCAT_PARENT, userDataCat.ucatParent);
        values.put(KEY_UCAT_ORDER, userDataCat.ucatCreated);
        values.put(KEY_UCAT_CREATED, userDataCat.ucatCreated);
        values.put(KEY_UCAT_UPDATED, userDataCat.ucatUpdated);
        values.put(KEY_UCAT_UPDATED_SORT, userDataCat.ucatUpdatedSort);

        db.insert(TABLE_USER_DATA_CATS , null, values);
    }


    public void importUcatUdata (SQLiteDatabase db, List<DBImport.UCatUData> list) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UCAT_UDATA);
        db.execSQL(CREATE_UDC_TABLE);

        db.beginTransaction();

        try {

            for (DBImport.UCatUData item: list) {
                insertImportedUcatUdata(db, item);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

    }

    private void insertImportedUcatUdata(SQLiteDatabase db, DBImport.UCatUData uCatUData) {

        ContentValues values = new ContentValues();

        values.put(KEY_UDC_UCAT_ID, uCatUData.udcUcatId);
        values.put(KEY_UDC_UDATA_ID, uCatUData.udcUdataId);


        db.insert(TABLE_UCAT_UDATA, null, values);
    }


    public ArrayList<QuestData> getQuestsByCatIds(ArrayList<String> catIds) {

        ArrayList<QuestData> items = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder conditionLike = new StringBuilder("");

        for (int i = 0; i < catIds.size(); i++) {

            String itemLike = KEY_QUEST_CAT_ID + " LIKE '" + catIds.get(i) + "%' ";

            if (i != 0) {
                itemLike = "OR " +  itemLike;
            }

            conditionLike.append(itemLike);
        }

        String questsQuery = "SELECT * FROM " + TABLE_QUEST_DATA + " WHERE (" + conditionLike +") ";

        Cursor cursor = db.rawQuery(questsQuery, null);

        try {
            while (cursor.moveToNext()) {


               // items.add(getSimpleItemFromCursor(cursor));

                items.add( getQuestFromCursor( cursor) );
            }
        } finally {
            cursor.close();
        }

        db.close();

        return items;
    }


    public ArrayList<ArrayList<QuestData>> getGroupedQuestsByCatIds(ArrayList<String> catIds, int level, int dataType) {

        ArrayList< ArrayList<QuestData>> groupedItemsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();


        //Toast.makeText(cntx, "Alert", Toast.LENGTH_SHORT).show();

        for (String catId : catIds) {

            ArrayList<QuestData> categoryQuest = new ArrayList<>();

            String questsQuery;

            if (dataType == TEST_BUILD) questsQuery = getBuildQuery(level, catId);
            else questsQuery = getMultichoiceQuery(level, catId);

            Cursor cursor = db.rawQuery(questsQuery, null);

            try {
                while (cursor.moveToNext()) {

                    QuestData quest;

                    if (dataType == TEST_BUILD) quest = getBuildQuestFromCursor( cursor);
                    else quest = getQuestFromCursor( cursor);

                    //Log.i("Quest", "quest: size " +  quest.getQuest() );

                    if (quest.getFilter().contains("#base") && !quest.getLevel().contains("l"+level+"l") ) {

                        if (questHasLowerLevel(quest, level)) categoryQuest.add(quest);

                    } else {

                        categoryQuest.add( quest );

                    }
                }
            } finally {
                cursor.close();
            }

           // Log.i("Quest", "group : size " +  + categoryQuest.size() );

            groupedItemsList.add( categoryQuest );

        }

        db.close();

        return groupedItemsList;
    }


    private String getMultichoiceQuery(int level, String catId) {

        return "SELECT * FROM " + TABLE_QUEST_DATA

                + " a LEFT JOIN " + TABLE_QSTATS_DATA

                + " b ON a." + KEY_QUEST_ID + " = b." + KEY_QSTATS_ID

                + " WHERE (a." + KEY_QUEST_CAT_ID + " LIKE '" + catId + "%' )"

                + " AND ( (a." + KEY_QUEST_LEVEL + " LIKE '%l" + level + "l%') "

                + " OR (a." + KEY_QUEST_FILTER + " LIKE '%#base%' ))";
                    //  +" AND (b." + KEY_QSTATS_CORRECT +" IS NULL OR b." + KEY_QSTATS_CORRECT +" < 6 ))) ";

    }

    private String getBuildQuery(int level, String catId) {

        return "SELECT * FROM " + TABLE_QUEST_BUILD

                + " a LEFT JOIN " + TABLE_QSTATS_DATA

                + " b ON a." + KEY_QUEST_B_ID + " = b." + KEY_QSTATS_ID

                + " WHERE (a." + KEY_QUEST_B_CAT_ID + " LIKE '" + catId + "%' )"

                + " AND ( (a." + KEY_QUEST_B_LEVEL + " LIKE '%l" + level + "l%') "

                + " OR (a." + KEY_QUEST_B_FILTER + " LIKE '%#base%' ))";

              //  +" AND (b." + KEY_QSTATS_CORRECT +" IS NULL OR b." + KEY_QSTATS_CORRECT +" < 2 ))) ";

    }

    private boolean questHasLowerLevel(QuestData quest, int level) {

        boolean hasLowerLevel = false;

        int levels = 10;

        for (int i = 0; i < levels; i++) {

            String lvlString = "l"+ i + "l";

            //Log.i("Quest", "lvl : " +  lvlString + ": "  + quest.getFilter().contains(lvlString) + " - " + i + " - " + level);

            if (quest.getLevel().contains(lvlString) && i < level) {
                hasLowerLevel = true;
                break;
            }

        }

        return hasLowerLevel ;
    }



    public ArrayList<ArrayList<QuestData>> getGroupedBuildQuestsByCatIds(ArrayList<String> catIds) {

        ArrayList< ArrayList<QuestData>> groupedItemsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        for (String catId : catIds) {

            ArrayList<QuestData> categoryQuest = new ArrayList<>();

            String questsQuery = "SELECT * FROM " + TABLE_QUEST_BUILD

                    +" a LEFT JOIN " + TABLE_QSTATS_DATA

                    +" b ON a." + KEY_QUEST_B_ID + " = b." + KEY_QSTATS_ID

                    + " WHERE " + KEY_QUEST_B_CAT_ID + " LIKE '" + catId + "%' ";


            Cursor cursor = db.rawQuery(questsQuery, null);

            try {

                while (cursor.moveToNext()) {
                    categoryQuest.add( getBuildQuestFromCursor( cursor) );
                }

            } finally {
                cursor.close();
            }

            groupedItemsList.add( categoryQuest );

        }

        db.close();

        return groupedItemsList;
    }

    private QuestData getBuildQuestFromCursor(Cursor cursor) {

        String quest = cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_QUEST));
        String correct = cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_CORRECT));
        String options = cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_OPTIONS));

        QuestData item = new QuestData(quest, correct, options);

        item.setId( cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_ID)) );
        item.setCategoryId( cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_CAT_ID)) );

        item.setLevel( cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_LEVEL)) );
        item.setLevelGlobal( cursor.getInt(cursor.getColumnIndex(KEY_QUEST_B_LEVEL_GLOBAL)) );
        item.setMode( cursor.getInt(cursor.getColumnIndex(KEY_QUEST_B_MODE)) );

        item.setPronounce( cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_PRONOUNCE)) );
        item.setParams( cursor.getString(cursor.getColumnIndex(KEY_QUEST_B_PARAMS)) );

        item.setCountTr( cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_COUNT_TR)) );
        item.setCountAudio( cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_COUNT_AUDIO)) );

        return item;
    }


    private QuestData getQuestFromCursor(Cursor cursor) {

        String quest = cursor.getString(cursor.getColumnIndex(KEY_QUEST_QUEST));
        String correct = cursor.getString(cursor.getColumnIndex(KEY_QUEST_CORRECT));
        String options = cursor.getString(cursor.getColumnIndex(KEY_QUEST_OPTIONS));

        QuestData item = new QuestData(quest, correct, options);

        item.setId( cursor.getString(cursor.getColumnIndex(KEY_QUEST_ID)) );
        item.setCategoryId( cursor.getString(cursor.getColumnIndex(KEY_QUEST_CAT_ID)) );

        item.setLevel( cursor.getString(cursor.getColumnIndex(KEY_QUEST_LEVEL)) );
        item.setLevelGlobal( cursor.getInt(cursor.getColumnIndex(KEY_QUEST_LEVEL_GLOBAL)) );
        item.setMode( cursor.getInt(cursor.getColumnIndex(KEY_QUEST_MODE)) );

        item.setPronounce( cursor.getString(cursor.getColumnIndex(KEY_QUEST_PRONOUNCE)) );

        item.setCountTr( cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_COUNT_TR)) );
        item.setCountAudio( cursor.getInt(cursor.getColumnIndex(KEY_QSTATS_COUNT_AUDIO)) );

        item.setFilter(cursor.getString(cursor.getColumnIndex(KEY_QUEST_FILTER)) );

        return item;
    }


    public int deleteQuestsStast() {

        SQLiteDatabase db = this.getWritableDatabase();

        int t = 0;

        db.beginTransaction();

        try {


            t = db.delete(TABLE_QSTATS_DATA, null,null);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();

        return  t;
    }


    public int checkSectionPracticeLevelProgress(SQLiteDatabase db, ArrayList<String> sectionCatIds, int level) {


            StringBuilder idsCondition = new StringBuilder("");

        for (int i = 0; i < sectionCatIds.size(); i++) {

            String like = "a."+ KEY_QUEST_CAT_ID + " = " + sectionCatIds.get(i) + " ";

            if (i != 0) {
                like = "OR " + like;
            }
            idsCondition.append(like);
        }


            String totalCountQuery = "SELECT * FROM " + TABLE_QUEST_DATA

                    + " a WHERE (" + idsCondition + " ) "

                    + "AND (" + KEY_QUEST_LEVEL + " LIKE '%l" + level + "l%') ";


            //Log.i("Quest", "query " + questsQuery);

            Cursor cursorTotal = db.rawQuery(totalCountQuery, null);


        String studiedQuery = "SELECT * FROM " + TABLE_QUEST_DATA

                +" a LEFT JOIN " + TABLE_QSTATS_DATA

                +" b ON a." + KEY_QUEST_ID + " = b." + KEY_QSTATS_ID

                + " WHERE (" + idsCondition + " ) "

                + "AND (a." + KEY_QUEST_LEVEL+ " LIKE '%l" + level + "l%') "

                + "AND (b." + KEY_QSTATS_CORRECT + " > 1 ) ";


        Cursor cursorStudied = db.rawQuery(studiedQuery, null);


        int total = cursorTotal.getCount();
        int studied = cursorStudied.getCount();

        int progress = 0;

        if (total > 0 ) {

            progress = studied * 100 / total;

        } else {
            progress = - 1;
        }

        //Log.i("Quests", "query: lvl "+ level + ": " + studied + " / " + total + " -> " + progress);

        cursorTotal.close();
        cursorStudied.close();


        return progress;
    }
}
