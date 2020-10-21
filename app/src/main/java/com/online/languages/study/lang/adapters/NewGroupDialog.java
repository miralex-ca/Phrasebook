package com.online.languages.study.lang.adapters;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.MyCatEditActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.UCatsListActivity;
import com.online.languages.study.lang.data.DataItem;

import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.VALUE_SOUND_OFF;


public class NewGroupDialog {


    Context context;

    private int itemCharMax;

    private int infoCharMax;


    private EditText itemEditText;

    private EditText infoEditText;


    private CheckBox soundCheck;

    private TextView itemCharCounter;

    private TextView infoCharCounter;


    private UCatsListActivity activity;


    public NewGroupDialog(Context _context, UCatsListActivity activity) {
        context = _context;
        this.activity = activity;


        itemCharMax = context.getResources().getInteger(R.integer.ucat_title_limit);
        infoCharMax = context.getResources().getInteger(R.integer.edit_info_length);


    }

    public void showCustomDialog(String title) {
        showCustomDialog(title, ACTION_CREATE, new DataItem());
    }


    public void showCustomDialog(String title, final String action, final DataItem dataItem) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_edit_group, null);

        final View moreView = content.findViewById(R.id.openMore);


        itemEditText = content.findViewById(R.id.editItem);

        infoEditText = content.findViewById(R.id.editAddInfo);


        itemCharCounter = content.findViewById(R.id.itemCharCounter);

        infoCharCounter = content.findViewById(R.id.addInfoCharCounter);



        initCounters();

        itemEditText.addTextChangedListener(itemEditorWatcher);

        infoEditText.addTextChangedListener(infoEditorWatcher);

        if (action.equals(ACTION_UPDATE) )  setData(dataItem);



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

                       activity.updateGroup(dataFromForm.item, dataFromForm.id);

                    }  else  {

                       activity.createNewGroup(getDataFromForm().item);

                    }

                    alert.dismiss();
                }
            }
        });

    }


    private void initCounters () {

        String initTxt = "0/";
        itemCharCounter.setText(initTxt + itemCharMax);

        infoCharCounter.setText(initTxt + infoCharMax);

    }


    private void setData(DataItem dataItem) {

        itemEditText.setText(dataItem.item);

        infoEditText.setText(dataItem.item_info_1);

    }



    private DataItem getDataFromForm() {

        DataItem dataItem = new DataItem();

        dataItem.item = textSanitizer(itemEditText.getText().toString());

        dataItem.item_info_1 = infoEditText.getText().toString();

        return dataItem;
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
