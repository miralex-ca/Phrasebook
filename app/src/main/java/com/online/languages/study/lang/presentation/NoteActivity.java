package com.online.languages.study.lang.presentation;

import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ACTION;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedCornersTransformation;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NoteData;
import com.online.languages.study.lang.databinding.ActivityNoteBinding;
import com.online.languages.study.lang.view_models.NoteViewModel;
import com.online.languages.study.lang.view_models.NoteViewModelFactory;
import com.squareup.picasso.Picasso;

public class NoteActivity extends ThemedActivity {

    OpenActivity openActivity;

    String source = "";

    NoteData note;

    ActivityNoteBinding binding;
    NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        source = getIntent().getStringExtra("source");
        if (source == null) {
            source = "";
        }

        String noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);

        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(this.getApplication(), noteId)).get(NoteViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_note);
        binding.setLifecycleOwner(this);
        binding.setNoteViewModel(noteViewModel);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.note_title_txt);

        setNote();

    }


    private void setNote() {

        // note = dataManager.dbHelper.getNote(noteId);

        note = noteViewModel.loadNote();

        manageTitleAndIcon();

        String imagePath = noteViewModel.imagePath();

        Picasso.with(this)
                .load(imagePath)
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(20, 0))
                .into(binding.noteIcon);

    }


    private void manageTitleAndIcon() {

        boolean emptyImage = noteViewModel.emptyImage();

        if (emptyImage) {
            binding.noteTitleNopic.setVisibility(View.VISIBLE);
            binding.noteIcon.setVisibility(View.GONE);
            binding.noteTitle.setVisibility(View.GONE);
        } else {
            binding.noteTitleNopic.setVisibility(View.GONE);
            binding.noteIcon.setVisibility(View.VISIBLE);
            binding.noteTitle.setVisibility(View.VISIBLE);
        }

        if (note.title.equals("") && emptyImage) {
            binding.titleWrap.setVisibility(View.GONE);
        } else {
            binding.titleWrap.setVisibility(View.VISIBLE);
        }
    }


    private void editNote() {
        Intent i = new Intent(this, NoteEditActivity.class);
        i.putExtra(EXTRA_NOTE_ID, note.id);
        i.putExtra(EXTRA_NOTE_ACTION, ACTION_UPDATE);
        startActivityForResult(i, 20);
    }


    private void deleteNote() {

        DataManager dataManager = new DataManager(this);
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

        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        } else if (id == R.id.edit_note) {
            editNote();
            return true;
        } else if (id == R.id.delete_note) {
            deleteConfirmDialog();
            return true;
        } else if (id == R.id.info_item) {
            showInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {

        if (source.equals("search")) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            returnIntent.putExtra("position", getIntent().getIntExtra("position", 0));
        }

        super.finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
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
        builder.setPositiveButton(R.string.continue_txt, (dialog, which) -> deleteNote());
        builder.setNegativeButton(R.string.cancel_txt, (dialog, which) -> {
        });

        builder.show();

    }

}
