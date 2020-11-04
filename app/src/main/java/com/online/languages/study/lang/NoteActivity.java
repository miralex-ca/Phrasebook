package com.online.languages.study.lang;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedCornersTransformation;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NoteData;
import com.squareup.picasso.Picasso;

import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ACTION;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;

public class NoteActivity extends BaseActivity {


    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;
    TextView title, titleNopic, content;
    ImageView noteIcon;
    View titleWrap;

    String source = "";

    String noteId;
    DataManager dataManager;
    NoteData note;

    String [] note_pics = new String[] {};
    String folder = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);

        source = getIntent().getStringExtra("source");
        if (source  == null) {
            source = "";
        }

        note_pics = getResources().getStringArray(R.array.note_pics_list);
        folder = getString(R.string.notes_pics_folder);


        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.note_title_txt);

        title = findViewById(R.id.noteTitle);
        titleNopic = findViewById(R.id.noteTitleNopic);
        titleWrap = findViewById(R.id.titleWrap);
        content = findViewById(R.id.noteContent);
        noteIcon = findViewById(R.id.noteIcon);


        dataManager = new DataManager(this);

        setNote();

    }



    private void setNote() {

        note = dataManager.dbHelper.getNote(noteId);

        String titleTxt = note.title;
        String titleContent = note.content;

        title.setText(titleTxt);
        titleNopic.setText(titleTxt);
        content.setText(titleContent);
        noteIcon.setVisibility(View.VISIBLE);


        String noteImage = checkImage(note.image);

        if (emptyImage(noteImage)) {
            titleNopic.setVisibility(View.VISIBLE);
            noteIcon.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        } else {
            titleNopic.setVisibility(View.GONE);
            noteIcon.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
        }

        if (note.title.equals("") && emptyImage(noteImage)) {
            titleWrap.setVisibility(View.GONE);
        } else {
            titleWrap.setVisibility(View.VISIBLE);
        }

        Picasso.with( this )
                .load("file:///android_asset/pics/" +folder + noteImage )
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(20,0))
                .into(noteIcon);

    }


    private String checkImage(String picName) {

        boolean found = false;
        if (picName == null) picName = "";

        String img = picName;

        for (String pic: note_pics) {

            if (picName.equals(pic)) {
                found = true;
                break;
            }
        }

        if (!found) img = "none";

        return img;
    }




    private boolean emptyImage(String picName) {

        boolean noImage = false;

        if (picName.equals("none") || picName.equals("empty.png") || picName.equals("")) {
            noImage = true;
        }

        return noImage;
    }


    private void editNote() {
        Intent i = new Intent(this, NoteEditActivity.class);
        i.putExtra(EXTRA_NOTE_ID, note.id );
        i.putExtra(EXTRA_NOTE_ACTION, ACTION_UPDATE );

        startActivityForResult(i, 20);
    }

    private void deleteNote() {
        dataManager.dbHelper.deleteNote(note);
        finish();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        setNote();

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
            case R.id.edit_note:
                editNote();
                return true;
            case R.id.delete_note:
                deleteConfirmDialog();
                return true;
            case R.id.info_item:
                showInfo();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {


        if (source.equals("search"))  {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);
            returnIntent.putExtra("position", getIntent().getIntExtra("position", 0));
        }

        super.finish();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);

        if (source.equals("search")) {
           // menu.findItem(R.id.edit_note).setVisible(false);
            //menu.findItem(R.id.delete_note).setVisible(false);
        }

        return true;
    }


    private void showInfo() {
        InfoDialog infoDialog = new InfoDialog(this);
        infoDialog.simpleDialog(getString(R.string.info_notes_title), getString(R.string.info_notes_text));

    }


    public void deleteConfirmDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation_txt);
        builder.setMessage(R.string.delete_note_confirm);
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();

            }
        });

        builder.setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }



}
