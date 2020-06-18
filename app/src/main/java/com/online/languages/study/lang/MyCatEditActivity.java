package com.online.languages.study.lang;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.ImgPickerAdapter;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.NewItemDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedCornersTransformation;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NoteData;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ACTION;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;
import static com.online.languages.study.lang.Constants.FOLDER_PICS;
import static com.online.languages.study.lang.Constants.NOTE_PIC_DEFAULT_INDEX;

public class MyCatEditActivity extends BaseActivity implements TextToSpeech.OnInitListener  {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;
    TextView title, content;
    ImageView noteIcon;

    private TextView titleCharCounter;
    private TextView itemCharCounter;

    private EditText titleEditText;
    private EditText itemEditText;

    int titleCharMax = 60;
    int itemCharMax = 200;
    RecyclerView recyclerView;

    DataManager dataManager;

    NewItemDialog newItemDialog;


    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;



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

        newItemDialog = new NewItemDialog(this, MyCatEditActivity.this);

        dataManager = new DataManager(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_toolbar);

        setTitle("");

        titleCharCounter = findViewById(R.id.titleCharCounter);

        titleCharCounter.setText("0/"+titleCharMax);

        titleEditText = findViewById(R.id.editTitle);

        titleEditText.addTextChangedListener(titleEditorWatcher);


        View newItem =  findViewById(R.id.newItemBtn);


        newItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                editItem();

            }
        });

        titleEditText.requestFocus();


        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }



    private void editItem() {
        newItemDialog.showCustomDialog("Новая запись");
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

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_info, menu);

        return true;
    }



    private final TextWatcher titleEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String str = s.length() + "/" + titleCharMax;
            titleCharCounter.setText(str);
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


}
