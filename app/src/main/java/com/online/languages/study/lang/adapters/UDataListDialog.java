package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.online.languages.study.lang.MyCatEditActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataObject;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_EDIT_GROUP;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;


public class UDataListDialog {


    Context context;

    private final int itemsMax;

    private EditText itemEditText;

    private CheckBox soundCheck;

    private TextView itemCharCounter;


    Spinner spin;
    final String[] dividers = { "#", "-", ":", "/" };

    TextView alert;

    private final int itemCharMax;
    private final int translateCharMax;
    private final int transcriptCharMax;

    private final MyCatEditActivity activity;

    public UDataListDialog(Context _context, MyCatEditActivity activity, int limit) {
        context = _context;
        this.activity = activity;

        itemsMax = Math.max(limit, 0);

        itemCharMax = context.getResources().getInteger(R.integer.edit_text_length);
        translateCharMax = context.getResources().getInteger(R.integer.edit_translation_length);
        transcriptCharMax = context.getResources().getInteger(R.integer.edit_transcription_length);

    }

    public void showCustomDialog(String title) {
        showCustomDialog(title, ACTION_CREATE, new DataObject());
    }


    public void showCustomDialog(String title, final String action, final DataObject groupObject) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_add_list, null);

        itemEditText = content.findViewById(R.id.editItem);
        alert = content.findViewById(R.id.alert);

        TextView addListText = content.findViewById(R.id.add_list_txt);

        String itemCountTxt = context.getResources().getQuantityString(R.plurals.item_plurals, itemsMax, itemsMax);

        String text = String.format(context.getString(R.string.add_list_dialog_text), "<b>"+itemCountTxt+"</b>");
        addListText.setText(Html.fromHtml(text));


        spin = content.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item_text, dividers);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString(); //this is your selected item
                String hint = String.format( context.getString( R.string.add_list_edit_hint), selectedItem );
                itemEditText.setHint(hint);

            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        itemCharCounter = content.findViewById(R.id.itemCharCounter);

        TextView dialogTitle = content.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);

        initCounters();

        itemEditText.addTextChangedListener(itemEditorWatcher);

        if (action.equals(ACTION_UPDATE) || action.equals(ACTION_EDIT_GROUP))  setData(groupObject);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder
                //.setTitle(title)
                .setCancelable(true)

                .setNegativeButton(R.string.cancel_txt,
                        (dialog, id) -> dialog.cancel())
                .setPositiveButton(R.string.save_txt,
                        (dialog, id) -> {

                            //Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();

                        })

                .setView(content);



        final AlertDialog alert = builder.create();
        alert.show();



        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            boolean wantToCloseDialog = false;

            if (itemEditText.getText().toString().trim().equals("")) {

                Toast.makeText(context, R.string.edit_item_enter_txt_msg, Toast.LENGTH_SHORT).show();
            } else {
                wantToCloseDialog = true;
            }

            if (wantToCloseDialog) {

                ArrayList<DataObject> objects = checkItems(getDataFromForm().text);

               if (objects.size() > itemsMax) objects = new ArrayList<>(objects.subList(0, itemsMax));;

                activity.addDataList(objects);

                alert.dismiss();
            }
        });

    }






    private void initCounters () {

        String initTxt = "0/";
        itemCharCounter.setText(initTxt + itemsMax);

    }


    private void setData(DataObject group) {

        itemEditText.setText(group.title);
    }


    private DataObject getDataFromForm() {

        DataObject dataObject = new DataObject();

        dataObject.text = textSanitizer(itemEditText.getText().toString());

        return dataObject;
    }



    private String textSanitizer(String text) {
        // text = text.replace("\n", " ").replace("\r", " ");
        text = text.trim();
        return text;
    }

    private ArrayList<DataObject> checkItems(String text) {

        String[] lines = textSanitizer(text).split("\n");

        ArrayList<DataObject> dataObjects = new ArrayList<>();

       String divider = spin.getSelectedItem().toString();

        for (String line: lines) {

            DataObject dataObject = new DataObject();
            String[] data = line.split(divider);
            if ((data.length>0) ) dataObject.text = getStringWithLimit(data[0], itemCharMax);
            if (data.length>1) dataObject.info = getStringWithLimit(data[1], translateCharMax);
            if (data.length>2) dataObject.desc = getStringWithLimit(data[2], transcriptCharMax);


            if (!textSanitizer(line).replace(divider, "").trim().equals(""))
                dataObjects.add(dataObject);
        }

        if (dataObjects.size() > itemsMax)  {

            String alertMsg = context.getString(R.string.limit_txt)+" <b>"+ dataObjects.size() + "</b>/"+ itemsMax ;

            alert.setVisibility(View.VISIBLE);
            alert.setText(Html.fromHtml(alertMsg));

        }


        else alert.setVisibility(View.GONE);

        return  dataObjects;
    }

    private String getStringWithLimit(String text, int limit) {

        String string = text.trim();

        if (string.length() > limit) {
            string = string.substring(0, limit);
        }

        return string;
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


            int itemsCount = checkItems( itemEditText.getText().toString() ).size() ;

            String str = itemsCount + "/" + itemsMax;

            itemCharCounter.setText(str);

        }

        public void afterTextChanged(Editable s) {
        }
    };









    }
