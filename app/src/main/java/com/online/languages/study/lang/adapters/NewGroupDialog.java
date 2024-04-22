package com.online.languages.study.lang.adapters;


import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_EDIT_GROUP;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.presentation.usercategories.UCatsListActivity;


public class NewGroupDialog {

    Context context;

    private int itemCharMax;

    private int infoCharMax;


    private EditText itemEditText;

    private EditText infoEditText;


    private CheckBox soundCheck;

    private TextView itemCharCounter;

    private TextView infoCharCounter;

    String [] pics = new String[] {};
    ImgGroupPickerAdapter imgPickerAdapter;
    RecyclerView recyclerView;

    int picIndex = 0;


    private UCatsListActivity activity;


    public NewGroupDialog(Context _context, UCatsListActivity activity) {
        context = _context;
        this.activity = activity;


        itemCharMax = context.getResources().getInteger(R.integer.ucat_title_limit);
        infoCharMax = context.getResources().getInteger(R.integer.ucat_desc_limit);

        pics = context.getResources().getStringArray(R.array.group_pics_list);
    }

    public void showCustomDialog(String title) {
        showCustomDialog(title, ACTION_CREATE, new DataObject());
    }


    public void showCustomDialog(String title, final String action, final DataObject groupObject) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_edit_group, null);

        itemEditText = content.findViewById(R.id.editItem);
        infoEditText = content.findViewById(R.id.editAddInfo);

        itemCharCounter = content.findViewById(R.id.itemCharCounter);
        infoCharCounter = content.findViewById(R.id.addInfoCharCounter);

        TextView dialogTitle = content.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);

        initCounters();

        itemEditText.addTextChangedListener(itemEditorWatcher);
        infoEditText.addTextChangedListener(infoEditorWatcher);

        picIndex = 1;


        if (action.equals(ACTION_UPDATE) || action.equals(ACTION_EDIT_GROUP))  setData(groupObject);

        recyclerView = content.findViewById(R.id.recycler_view);

        int spanCount = context.getResources().getInteger(R.integer.img_group_picker_span);

        imgPickerAdapter = new ImgGroupPickerAdapter(context, pics, picIndex);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, spanCount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(imgPickerAdapter);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder
                //.setTitle(title)
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
                        DataObject newGroupObject = getDataFromForm();
                        newGroupObject.id  = groupObject.id;
                        activity.updateGroup(newGroupObject);

                    }  else if (action.equals(ACTION_EDIT_GROUP)) {
                        DataObject newGroupObject = getDataFromForm();
                        newGroupObject.id  = groupObject.id;

                        activity.updateGroupFromList(newGroupObject);

                    } else  {

                       activity.createNewGroup(getDataFromForm());

                    }

                    alert.dismiss();
                }
            }
        });

    }

    public void updateList(int t) {

        picIndex =  t;
        imgPickerAdapter = new ImgGroupPickerAdapter(context, pics, t);
        recyclerView.setAdapter(imgPickerAdapter);

    }




    private void initCounters () {

        String initTxt = "0/";
        itemCharCounter.setText(initTxt + itemCharMax);

        infoCharCounter.setText(initTxt + infoCharMax);

    }


    private void setData(DataObject group) {

        itemEditText.setText(group.title);
        infoEditText.setText(group.desc);
        picIndex = getIconIndex(group.image);

    }

    private int getIconIndex(String pic) {

        int index = 0;

        for (int i = 0; i < pics.length; i ++) {
            if (pics[i].equals(pic)) index = i;
        }
        return index;
    }



    private DataObject getDataFromForm() {

        DataObject groupObject= new DataObject();

        groupObject.title = textSanitizer(itemEditText.getText().toString());

        groupObject.desc = textSanitizer(infoEditText.getText().toString());

        groupObject.image = pics[picIndex];

        return groupObject;
    }



    private String textSanitizer(String text) {
        text = text.replace("\n", " ").replace("\r", " ");
        text = text.trim();

        return text;
    }

    private static int dpToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
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
