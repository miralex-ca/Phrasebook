package com.online.languages.study.lang.adapters;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.MyCatEditActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;

import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.VALUE_SOUND_OFF;


public class NewItemDialog {


    Context context;

    private int itemCharMax;
    private int translateCharMax;
    private int transcriptCharMax;
    private int grammarCharMax;
    private int infoCharMax;


    private EditText itemEditText;
    private EditText transcriptEditText;
    private EditText translateEditText;
    private EditText infoEditText;
    private EditText grammarEditText;

    private CheckBox soundCheck;

    private TextView itemCharCounter;
    private TextView transcriptCharCounter;
    private TextView translateCharCounter;
    private TextView infoCharCounter;
    private TextView grammarCharCounter;

    private Button openMore;
    private Button openLess;

    private View moreWrap;


    private MyCatEditActivity activity;


    public NewItemDialog(Context _context, MyCatEditActivity activity) {
        context = _context;
        this.activity = activity;


        itemCharMax = context.getResources().getInteger(R.integer.edit_text_length);
        translateCharMax = context.getResources().getInteger(R.integer.edit_translation_length);
        transcriptCharMax = context.getResources().getInteger(R.integer.edit_transcription_length);
        grammarCharMax = context.getResources().getInteger(R.integer.edit_grammar_length);
        infoCharMax = context.getResources().getInteger(R.integer.edit_info_length);


    }


    public void showCustomDialog(String title) {
        showCustomDialog(title, ACTION_CREATE, new DataItem());
    }

    public void showCustomDialog(String title, final String action, final DataItem dataItem) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_edit_item, null);

        final View moreView = content.findViewById(R.id.openMore);

        openMore = content.findViewById(R.id.moreBtn);
        openLess = content.findViewById(R.id.lessBtn);
        moreWrap = content.findViewById(R.id.moreWrap);

        itemEditText = content.findViewById(R.id.editItem);
        translateEditText = content.findViewById(R.id.editTranslate);
        transcriptEditText = content.findViewById(R.id.editTrans);
        grammarEditText = content.findViewById(R.id.editGrammar);
        infoEditText = content.findViewById(R.id.editAddInfo);
        soundCheck = content.findViewById(R.id.soundOff);

        itemCharCounter = content.findViewById(R.id.itemCharCounter);
        transcriptCharCounter = content.findViewById(R.id.transCharCounter);
        translateCharCounter = content.findViewById(R.id.translateCharCounter);
        infoCharCounter = content.findViewById(R.id.addInfoCharCounter);
        grammarCharCounter = content.findViewById(R.id.grammarCharCounter);


        initCounters();

        itemEditText.addTextChangedListener(itemEditorWatcher);
        translateEditText .addTextChangedListener(translateEditorWatcher);
        transcriptEditText.addTextChangedListener(transcriptEditorWatcher);
        grammarEditText.addTextChangedListener(grammarEditorWatcher);
        infoEditText.addTextChangedListener(infoEditorWatcher);

        View speakBtn = content.findViewById(R.id.speakBtn);


        if (action.equals(ACTION_UPDATE) )  setData(dataItem);


        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String text = textSanitizer(itemEditText.getText().toString());
                activity.speakText(text);
            }
        });

        openMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                expandView(moreView);
            }
        });

        openLess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                closeView(moreView);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setTitle(title)
                .setCancelable(true)


                .setNegativeButton(R.string.cancel_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(R.string.save_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();

                            }
                        })



                .setView(content);



        final AlertDialog alert = builder.create();
        alert.show();


        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.speakText("");
            }
        });


        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean wantToCloseDialog = false;

                if (itemEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(context, R.string.edit_item_enter_txt_msg, Toast.LENGTH_SHORT).show();
                } else {
                    wantToCloseDialog = true;
                }

                if (wantToCloseDialog) {

                    if (action.equals(ACTION_UPDATE)) {

                        DataItem dataFromForm = getDataFromForm();
                        dataFromForm.id  = dataItem.id;

                        activity.updateDataItem(dataFromForm);

                    }  else  {

                        activity.saveDataItem(getDataFromForm());
                    }


                    activity.speakText("");
                    alert.dismiss();
                }
            }
        });

    }


    private void initCounters () {

        String initTxt = "0/";
        itemCharCounter.setText(initTxt + itemCharMax);
        translateCharCounter.setText(initTxt + translateCharMax);
        transcriptCharCounter.setText(initTxt + transcriptCharMax);
        grammarCharCounter.setText(initTxt + grammarCharMax);
        infoCharCounter.setText(initTxt + infoCharMax);

    }


    private void setData(DataItem dataItem) {

        itemEditText.setText(dataItem.item);
        translateEditText.setText(dataItem.info);
        transcriptEditText.setText(dataItem.trans1);
        if (dataItem.sound.equals(VALUE_SOUND_OFF)) soundCheck.setChecked(true);
        grammarEditText.setText(dataItem.grammar);
        infoEditText.setText(dataItem.item_info_1);

    }



    private DataItem getDataFromForm() {

        DataItem dataItem = new DataItem();

        dataItem.item = textSanitizer(itemEditText.getText().toString());
        dataItem.info = textSanitizer(translateEditText.getText().toString());
        dataItem.trans1 = textSanitizer(transcriptEditText.getText().toString());
        dataItem.grammar = textSanitizer(grammarEditText.getText().toString());
        dataItem.item_info_1 = infoEditText.getText().toString();

        if (soundCheck.isChecked()) {
            dataItem.sound = VALUE_SOUND_OFF;
        }

        return dataItem;
    }


    private void expandView(View view) {

        openMore.setVisibility(View.GONE);
        openLess.setVisibility(View.VISIBLE);

        view.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                moreWrap.animate().alpha(1.0f).setDuration(250);

            }
        }, 200);

    }

    private void closeView(final View view) {

        openMore.setVisibility(View.VISIBLE);
        openLess.setVisibility(View.GONE);

        moreWrap.animate().alpha(0f).setDuration(250);

        view.setVisibility(View.GONE);

    }

    private String textSanitizer(String text) {
        text = text.replace("\n", " ").replace("\r", " ");
        text = text.trim();

        return text;
    }


    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private final TextWatcher itemEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = s.length() + "/" + itemCharMax;

            itemCharCounter.setText(str);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher translateEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = s.length() + "/" + translateCharMax;

            translateCharCounter.setText(str);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher transcriptEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = s.length() + "/" + transcriptCharMax;

            transcriptCharCounter.setText(str);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher grammarEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = s.length() + "/" + grammarCharMax;

            grammarCharCounter.setText(str);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher infoEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = s.length() + "/" + infoCharMax;

            infoCharCounter.setText(str);
        }

        public void afterTextChanged(Editable s) {
        }
    };




    }
