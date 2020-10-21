package com.online.languages.study.lang;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.EditDataListAdapter;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.NewItemDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.PremiumDialog;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;

import java.util.ArrayList;
import java.util.Locale;

import static com.online.languages.study.lang.Constants.ACTION_DELETE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.ACTION_VIEW;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_SORT;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_SORT_ASC;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_SORT_DESC;
import static com.online.languages.study.lang.Constants.UC_PREFIX;
import static com.online.languages.study.lang.Constants.UDATA_LIMIT;
import static com.online.languages.study.lang.Constants.UDATA_LIMIT_UNPAID;

public class MyCatEditActivity extends BaseActivity implements TextToSpeech.OnInitListener  {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;
    TextView title, content;

    private TextView titleCharCounter;

    private EditText titleEditText;

    int titleCharMax = 50;

    DataManager dataManager;
    InfoDialog infoDialog;

    NewItemDialog newItemDialog;

    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;

    Button createBtn;
    Button updateCatBtn;

    View newItem;
    boolean catBtnAction;

    boolean newDataItemAction;

    ArrayList<DataItem> dataItems;
    EditDataListAdapter adapter;
    RecyclerView recyclerView;

    DataObject categoryObject;

    MenuItem deleteMenuItem;

    View listParams;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        setContentView(R.layout.activity_cat_edit);
        categoryObject = new DataObject();

        categoryObject.id = getIntent().getStringExtra(EXTRA_CAT_ID);

        if (savedInstanceState != null) {
            categoryObject = savedInstanceState.getParcelable("categoryObject");
        }


        newItemDialog = new NewItemDialog(this, MyCatEditActivity.this);

        dataManager = new DataManager(this);

        dataManager.plus_Version = dataManager.checkPlusVersion();

        infoDialog = new InfoDialog(this);

        catBtnAction = true;
        newDataItemAction = false;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_toolbar);

        setTitle("");

        titleCharMax = getResources().getInteger(R.integer.ucat_title_limit);

        titleCharCounter = findViewById(R.id.titleCharCounter);
        titleCharCounter.setText("0/"+titleCharMax);
        titleEditText = findViewById(R.id.editTitle);

        createBtn = findViewById(R.id.createBtn);
        updateCatBtn = findViewById(R.id.updateCatBtn);
        newItem =  findViewById(R.id.newItemBtn);
        listParams = findViewById(R.id.list_params);

        recyclerView = findViewById(R.id.recycler_view);

        newItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                editItem();
            }
        });

        titleEditText.addTextChangedListener(titleEditorWatcher);

        prepareCat();


        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);


    }

    public void editClick(final DataItem dataItem, String type) {

        if (type.equals(ACTION_UPDATE)) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    openDataItem(dataItem.id);

                }
            }, 50);

        }

        if (type.equals(ACTION_DELETE)) confirmDeletion(dataItem.id);

        if (type.equals(ACTION_VIEW)) viewItem(dataItem);

    }


    private void confirmDeletion(final String id) {


        String message = getString(R.string.ucat_delete_item_confirm);

        if (dataItems.size() == 1) message = getString(R.string.ucat_delete_item_confirm_last);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirmation_txt);

        builder.setMessage(message);

        builder.setCancelable(false);

        builder.setPositiveButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(id);
            }
        });

        builder.setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

    private void deleteItem(String id) {
        int num = dataManager.dbHelper.deleteData(id);
        if (num > 0) infoDialog.toast(getString(R.string.ucat_item_deleted_msg));
        updateItemsList();
    }


    public void prepareCat() {

        if (!categoryObject.id.contains(UC_PREFIX)) {

            titleEditText.requestFocus();
            createBtn.setVisibility(View.VISIBLE);
            newDataItemAction = false;
            newItem.setAlpha(0.3f);
            listParams.setVisibility(View.INVISIBLE);


        } else {

            categoryObject  = dataManager.dbHelper.getUCat(categoryObject.id);

            titleEditText.setText(categoryObject.title);

            TextView txt = findViewById(R.id.createdDate);


            txt.setText(String.format(getString(R.string.ucat_created_time), dataManager.formatTime(categoryObject.time_created)));
            newDataItemAction = true;
            updateItemsList();
            listParams.setVisibility(View.VISIBLE);

        }

    }



    private void showCreated(String[] catData) {

        categoryObject.id = catData[0];
        String time = catData[1];

        createBtn.animate()
                .alpha(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        createBtn.setVisibility(View.INVISIBLE);
                    }
                })
                .setDuration(450)
                .start();


        TextView txt = findViewById(R.id.createdDate);
        txt.setText(String.format(getString(R.string.ucat_created_time), time));

        newDataItemAction = true;

        newItem.setAlpha(1.0f);

        listParams.setAlpha(0);
        listParams.setVisibility(View.VISIBLE);
        listParams.animate().alpha(1.0f).start();

        titleEditText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

        checkDeleteItem();

    }


    public void openDataItem(String id) {
        DataItem dataItem = dataManager.dbHelper.getUData(id);
        newItemDialog.showCustomDialog(getString(R.string.ucat_edit_item_dialog), ACTION_UPDATE, dataItem);
        titleEditText.clearFocus();
    }


    public void createUCat(View view) {

        if (catBtnAction) {

            String title = textSanitizer(titleEditText.getText().toString());

            if (title.trim().equals("")) {

                infoDialog.simpleDialog(getString(R.string.ucat_saving_alert), getString(R.string.ucat_enter_title_alert));

            } else {
                catBtnAction = false;

                String[] catData = dataManager.dbHelper.createUCat(textSanitizer(title));

                categoryObject.title = textSanitizer(title);

                titleEditText.setText(categoryObject.title);

                showCreated(catData);

            }
        }
    }


    public void updateUCat(View view) {


            String title = titleEditText.getText().toString();

            if (title.trim().equals("")) {

                infoDialog.simpleDialog(getString(R.string.ucat_saving_alert), getString(R.string.ucat_enter_title_alert));

            } else {

                categoryObject.title = textSanitizer(title);
                categoryObject = dataManager.dbHelper.updateUCatTitle(categoryObject);

                titleEditText.setText(categoryObject.title);

                titleEditText.clearFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

                checkCatTitleChanges(title);

            }

    }


    private void checkCatTitleChanges(String str) {

        if (!categoryObject.id.contains(UC_PREFIX)) return;

        if (str.trim().equals(categoryObject.title)) {

            updateCatBtn.setVisibility(View.INVISIBLE);

        } else {
            updateCatBtn.setVisibility(View.VISIBLE);
        }
    };


    private void editItem() {
        if (newDataItemAction) {

            boolean limit = checkUcatLimits();

            if (limit) {

                if (!dataManager.plus_Version) {

                    PremiumDialog premiumDialog = new PremiumDialog(this);

                    premiumDialog.createDialog(
                            getString(R.string.udata_limit_dialog_title_unpaid),
                            String.format(getString(R.string.udata_limit_dialog_text_unpaid), String.valueOf(UDATA_LIMIT_UNPAID))

                    );

                } else {

                    infoDialog.simpleDialog(
                            getString(R.string.udata_limit_dialog_title),
                            String.format(getString(R.string.udata_limit_dialog_text), String.valueOf(UDATA_LIMIT))
                    );
                }

            } else {
                newItemDialog.showCustomDialog(getString(R.string.ucat_new_item_dialog));
                titleEditText.clearFocus();
            }

        }
    }


    private boolean checkUcatLimits() {

        boolean reachedLimit = true;

        int size = dataManager.checkUcatLimit(categoryObject.id);

        int limit = UDATA_LIMIT;

        if (!dataManager.plus_Version) {
            limit = UDATA_LIMIT_UNPAID;
        }

        reachedLimit = size >= limit;


        return reachedLimit;

    }


    public void saveDataItem (DataItem dataItem) {
        dataItem.cat = categoryObject.id;
        dataManager.dbHelper.createUData(dataItem);
        updateItemsList();
    }

    public void updateDataItem (DataItem dataItem) {
        dataManager.dbHelper.updateUData(dataItem);
        updateItemsList();
    }


    private void updateItemsList() {

        dataItems  = dataManager.getUDataList(categoryObject.id);
        adapter = new EditDataListAdapter(this, dataItems, MyCatEditActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }


    private void viewItem(DataItem dataItem) {

        Intent intent = new Intent(this, ScrollingActivity.class);

        intent.putExtra("starrable", true);
        intent.putExtra("id", dataItem.id );
        intent.putExtra("position", 0);

        startActivityForResult(intent,10);

        overridePendingTransition(R.anim.slide_in_down, 0);

    }


    private void deleteCurrentUcat() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirmation_txt);
        builder.setMessage(R.string.ucat_delete_confirm);

        builder.setCancelable(false);

        builder.setPositiveButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteUCat(categoryObject.id);
            }
        });

        builder.setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

    private void deleteUCat(String id) {

        int num =  dataManager.dbHelper.deleteUCat(id);

        infoDialog.toast(String.format(getString(R.string.ucat_deleted_items), num));

        setResult(50);

        finish();

    }


    private String textSanitizer(String text) {

        text = text.replace("\n", " ").replace("\r", " ");

        text = text.trim();
        return text;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.info_item:

                showInfoDialog();

                return true;

            case R.id.delete_ucat:

                deleteCurrentUcat();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showInfoDialog() {
        DataModeDialog dataModeDialog = new DataModeDialog(this);
        String info = getString(R.string.ucat_edit_info_txt); /// TODO check description
        dataModeDialog.createDialog(getString(R.string.info_txt), info);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ucat_edit, menu);

        deleteMenuItem = menu.findItem(R.id.delete_ucat);

        checkDeleteItem();

        return true;
    }

    private  void checkDeleteItem() {

        if (categoryObject.id.contains(UC_PREFIX)) {
            deleteMenuItem.setVisible(true);
        } else {
            deleteMenuItem.setVisible(false);
        }

    }


    private final TextWatcher titleEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String str = s.length() + "/" + titleCharMax;
            titleCharCounter.setText(str);

            checkCatTitleChanges(s.toString());
        }

        public void afterTextChanged(Editable s) {
        }
    };


    //// TTS integration


    public void speakText(String speech) {
        //speak straight away
        if (myTTS != null) myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

        //Toast.makeText(this, "Text: " + speech, Toast.LENGTH_LONG).show();
    }

    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {

            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    //setup TTS
    public void onInit(int initStatus) {

        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.ENGLISH)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.ENGLISH);
            //  speakBtn.setVisibility(View.VISIBLE);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            //Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }



    public interface ClickListener{
        void onClick(View view,int position);
        void onLongClick(View view,int position);
    }
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("categoryObject", categoryObject );

        super.onSaveInstanceState(outState);

    }



    public void sortDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        categoryObject = dataManager.getUcatParams(categoryObject);

        String paramSort = dataManager.readParam(categoryObject.params, UCAT_PARAM_SORT);

       // Toast.makeText(this, "S: " + paramSort, Toast.LENGTH_SHORT).show();

        int checkedItem = 0;

        if (paramSort.equals(UCAT_PARAM_SORT_ASC))  checkedItem = 1;

        builder.setTitle(getString(R.string.sort_udata_dialog_title))

                .setSingleChoiceItems(R.array.set_sort_ucat_list, checkedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        showSort(which);
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)

                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void showSort(int num) {

        String orderValue = UCAT_PARAM_SORT_DESC;
        if (num == 1) orderValue  = UCAT_PARAM_SORT_ASC;

        categoryObject.params =  dataManager.addValueToParams(
                    dataManager.getUcatParams(categoryObject).params,
                    UCAT_PARAM_SORT, orderValue);


        dataManager.saveUcatParams(categoryObject);

        updateItemsList();

    }



}
