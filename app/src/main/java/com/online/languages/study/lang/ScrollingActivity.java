package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DetailItem;

import java.util.Locale;

import static com.online.languages.study.lang.Constants.GALLERY_TAG;
import static com.online.languages.study.lang.Constants.INFO_TAG;
import static com.online.languages.study.lang.Constants.UD_PREFIX;

public class ScrollingActivity extends BaseActivity implements TextToSpeech.OnInitListener {


    DetailItem detailItem;
    DataItem dataItem;

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    DBHelper dbHelper;
    int itemPostion = 0;

    Boolean starrable = false;
    Boolean starStatusChanged = false;
    int sourceType;

    FloatingActionButton floatingActionButton;

    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;
    View speakBtn;
    boolean speaking;
    MenuItem starMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, 2);
        themeAdapter.getTheme();

        itemPostion = getIntent().getIntExtra("position", 0);

        starrable = getIntent().getBooleanExtra("starrable", false);

        sourceType = getIntent().getIntExtra("source", 0); // 0 - list, 1 - search


        Drawable background = getWindow().getDecorView().getBackground();  background.setAlpha(0);


        if (appSettings.getBoolean(Constants.SET_VERSION_TXT, false)) starrable = true;

        starStatusChanged= false;

        setContentView(R.layout.activity_detail);

        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String tag = getIntent().getStringExtra("id");

        setTitle("");

        final DataManager dataManager = new DataManager(this);

        detailItem = dataManager.getDetailFromDB(tag);

        dataItem = dbHelper.getDataItemById(tag);

        if (tag.contains(UD_PREFIX)) {
            dataItem =  dbHelper.getUDataAllInfo(tag);
        }


        if (detailItem.title.equals("not found")) {
            detailItem  = new DetailItem(dataItem);
           // sourceType = 1; // the same height as in search
        }


        TextView infoT = findViewById(R.id.lbl_text);

        speaking = appSettings.getBoolean("set_speak", true);


        View appbar = findViewById(R.id.app_bar);
        View coordinator = findViewById(R.id.coordinator);


        if (sourceType==1) {
            int dialogHeight = getResources().getInteger(R.integer.search_dialog_height);
            int imgHeight = getResources().getInteger(R.integer.search_dialog_img_height);
            changeDialogSize(coordinator, dialogHeight );
        }


        //changeDialogSize(coordinator, 450 );


        floatingActionButton = findViewById(R.id.fab);

        final boolean inStarred = checkStarred(tag);


        if (starrable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if ( dataItem.filter.contains(INFO_TAG) && !inStarred ) { }
                    else  {
                            // TODO CHECK STAR
                    }
                }
            }, 350);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (speaking) floatingActionButton.show();
            }
        }, 350);


        itemPostion = getIntent().getIntExtra("position", 0);

        String  transcript = dataManager.getTranscriptFromData(dataItem);

        boolean transcription = transcript.trim().equals("");

        TextView txt = findViewById(R.id.itemTxt);

        txt.setText(detailItem.title);
        txt.setTextSize(getTextTxtSize( detailItem.title.length(), transcription ));

        infoT.setText(detailItem.desc);

        infoT.setTextSize(getInfoTxtSize(detailItem.desc.length()));

        TextView trans = findViewById(R.id.itemTxtTrans);



        if (transcription) trans.setVisibility(View.GONE);
        else trans.setVisibility(View.VISIBLE);

        trans.setText(String.format("[ %s ]", transcript));

        trans.setTextSize(getTranscriptTxtSize(transcript.length()));


        TextView grammar = findViewById(R.id.grammar);

        String gr = dataItem.grammar;

        //Toast.makeText(this, "Len: " + gr.trim().length(), Toast.LENGTH_SHORT ).show();

        View grammarBox = findViewById(R.id.grammarBox);

        if (gr.trim().length() > 0 ) {
            grammarBox.setVisibility(View.VISIBLE);
        } else {
            grammarBox.setVisibility(View.GONE);
        }

        grammar.setTextSize(getGrammarTxtSize(gr.length(), transcript.length()));
        grammar.setText(gr);


        TextView addInfo = findViewById(R.id.add_info);

        String info = dataItem.item_info_1;

        int bottomTextLen = detailItem.desc.length() + dataItem.item_info_1.length();

        if ( info.trim().length() > 0) {
            addInfo.setVisibility(View.VISIBLE);

            if (bottomTextLen > 150 && bottomTextLen <= 190) {
                changeDialogSize(coordinator, 390 );
            } else if (bottomTextLen > 190) {
                changeDialogSize(coordinator, 430 );
            }

        } else {
            addInfo.setVisibility(View.GONE);
        }

        addInfo.setText(dataItem.item_info_1);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakReading();
            }
        });

        //check for TTS data
        //speakBtn = findViewById(R.id.speakBtn);



        if (speaking) {
            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        }

        measureHeights();

        //enableScroll();

        View statusView = findViewById(R.id.status_wrap);

       if (!tag.contains(UD_PREFIX)) dataItem = dbHelper.getDataItemDBById(dataItem.id);

        int showStatus = Integer.valueOf(appSettings.getString("show_status", Constants.STATUS_SHOW_DEFAULT));

        statusInfoDisplay(showStatus, statusView, dataItem);


    }


    private int getTextTxtSize(int textLength, boolean transcript) {

        int size;

        if (transcript) {
            size = getTextSmallerTxtSize(textLength);
        } else {
            size = getTextTxtSize(textLength);
        }

        return size;
    }

    private int getTextTxtSize(int textLength) {

        int size = 25;

        if (textLength > 20) size = 24;
        if (textLength > 40) size = 23;
        if (textLength > 50) size = 22;
        if (textLength > 60) size = 21;
        if (textLength > 80) size = 20;

        return size;
    }

    private int getTextSmallerTxtSize(int textLength) {

        int size = 25;

        if (textLength > 20) size = 24;
        if (textLength > 40) size = 23;
        if (textLength > 50) size = 21;
        if (textLength > 60) size = 20;
        if (textLength > 80) size = 19;
        if (textLength > 90) size = 18;

        return size;
    }


    private int getInfoTxtSize(int textLength) {

        int size = 23;

        if (textLength > 20) size = 22;
        if (textLength > 40) size = 20;
        if (textLength > 60) size = 18;
        if (textLength > 80) size = 17;
        if (textLength > 90) size = 16;

        return size;
    }

    private int getTranscriptTxtSize(int textLength) {

        int size = 16;

        if (textLength > 40) size = 15;
        if (textLength > 60) size = 14;

        return size;
    }


    private int getGrammarTxtSize(int textLength, int transcript) {

        int size = 22;

        if (textLength > 8 || transcript > 60) size = 18;

        return size;
    }


    private void changeDialogSize(View coordinator, int coordHeight) {
        coordinator.getLayoutParams().height = convertDimen(coordHeight);
        coordinator.setLayoutParams(coordinator.getLayoutParams());

    }


    private void measureHeights() {

        final View appBar= findViewById(R.id.app_bar);
        final View infoBox= findViewById(R.id.infoBox);

        appBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                appBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h = appBar.getHeight();
                checkScrollByAppBar(h);
            }
        });

        infoBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                infoBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h = infoBox.getHeight();
                checkScrollByText(h);
            }
        });

    }

    private void checkScrollByAppBar(int height) {
        int minHeight = (int) (getResources().getDimension(R.dimen.detail_card_height) / getResources().getDisplayMetrics().density);
        minHeight = minHeight * 60/100;
        if ( pxToDp(height)  > minHeight)  enableScroll();
    }

    private void checkScrollByText(int height) {
        int minHeight = 130;
        if ( pxToDp(height)  > minHeight)  enableScroll();
    }

    private void enableScroll() {
        View collapsingToolbar = findViewById(R.id.toolbar_layout);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);

    }





    public int convertDimen(int dimen) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen, getResources().getDisplayMetrics());
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", itemPostion);

        if (starStatusChanged) {
            setResult(RESULT_OK,returnIntent);
        } else {
            setResult(RESULT_CANCELED,returnIntent);
        }

        speakWords("");

        super.finish();
        overridePendingTransition(0, R.anim.fade_out_2);
    }


    public void limitMessage() {
        Toast.makeText(this, R.string.starred_limit, Toast.LENGTH_SHORT).show();
        // Snackbar.make(mask, R.string.starred_limit, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    private void changeStarStatus(String tag) {

        Boolean starred = checkStarred(tag);

        String filter = "";
        if (dataItem.filter.contains(GALLERY_TAG)) filter = GALLERY_TAG;

        int status = dbHelper.setStarred(tag, !starred, filter); // id to id
        starStatusChanged = true;

        if (status == 0) {
            limitMessage();
        }

        checkStarStatus(tag);
    }


    private void checkStarStatus(String tag) {
        Boolean starred = checkStarred(tag);

        if (starred) {
            starMenuItem.setIcon(R.drawable.ic_star_detail);
        } else {
            starMenuItem.setIcon(R.drawable.ic_star_detail_inactive);
        }
    }


    private Boolean checkStarred(String text) {
        Boolean starred = dbHelper.checkStarred(text);
        return starred;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
        starMenuItem = menu.findItem(R.id.star);
        checkStarStatus(detailItem.id);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.star:
                changeStarStatus(detailItem.id);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //// TTS integration

    public void speakReading() {
        String text = dataItem.item;
        speakWords(text);
    }


    private void speakWords(String speech) {
        //speak straight away
       if (myTTS != null) myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
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

        //check for successful instantiation

        //Locale locale = new Locale("en", "US");

        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.ENGLISH)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.ENGLISH);
          //  speakBtn.setVisibility(View.VISIBLE);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            //Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    private void openStatus(Boolean show, View statusView, DataItem dataItem) {

        if (show) {
            statusView.setVisibility(View.VISIBLE);
            manageStatusView(statusView, dataItem.rate);
            manageErrorsView(statusView, dataItem.errors);

        } else {
            statusView.setVisibility(View.INVISIBLE);
        }
    }


    private void manageStatusView(View statusBox, int result) {
        View unknown = statusBox.findViewById(R.id.statusUnknown);
        View known = statusBox.findViewById(R.id.statusKnown);
        View studied = statusBox.findViewById(R.id.statusStudied);

        unknown.setVisibility(View.GONE);
        known.setVisibility(View.GONE);
        studied.setVisibility(View.GONE);

        if (result > 2) {
            studied.setVisibility(View.VISIBLE);
        } else if ( result > 0) {
            known.setVisibility(View.VISIBLE);
        } else {
            unknown.setVisibility(View.VISIBLE);
        }
    }

    private void manageErrorsView(View statusBox, int errorsCount) {
        TextView errorsTxt = statusBox.findViewById(R.id.errorsCount);
        errorsTxt.setText(String.format("Ошибки: %d", errorsCount));

        if (errorsCount > 0) {
            errorsTxt.setVisibility(View.VISIBLE);
        } else {
            errorsTxt.setVisibility(View.GONE);
        }
    }


    private void statusInfoDisplay(int displayStatus, View statusView, DataItem dataItem) {

        switch (displayStatus) {

            case (1): /// auto

                if (dataItem.rate > 0 || dataItem.errors > 0) {
                    openStatus(true, statusView, dataItem);
                } else {
                    openStatus(false, statusView, dataItem);
                }

                break;

            case (2):  /// always
                openStatus(true, statusView, dataItem);
                break;

            case (0):  /// never
                openStatus(false, statusView, dataItem);
                break;

        }

    }




}
