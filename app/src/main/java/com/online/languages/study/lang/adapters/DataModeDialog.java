package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;

public class DataModeDialog implements OnModeSelector {

    Context context;
    SharedPreferences appSettings;



    public DataModeDialog(Context _context) {
        context = _context;

        appSettings = PreferenceManager.getDefaultSharedPreferences(context);
    }



    public void openDialog() {
        createDialog(
                context.getResources().getString(R.string.easy_mode_dialog_title),
                context.getResources().getString(R.string.easy_mode_info));
    }

    public void modeInfoDialog() {
        createDialog(
                context.getResources().getString(R.string.title_info_txt),
                context.getResources().getString(R.string.mode_info));
    }


    public void createDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        (dialog, id) -> dialog.cancel())
                .setMessage(text);
        AlertDialog alert = builder.create();
        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);
    }

    public void createDialog2(String title, String text, String text2) {
        createDialog2(title, text, text2, false, false);
    }

    public void createModeHint(String title, String text, String text2) {
        createDialog2(title, text, text2, true, false);
    }

    public void createEasyDialog(String title, String text, String text2) {
        createDialog2(title, text, text2, false, true);
    }


    public void createDialog2(String title, String text, String text2, boolean hintConfirm, boolean oneOption) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        String defaultModeValue = context.getResources().getString(R.string.set_data_mode_default_value);

        String modeValue = appSettings.getString(Constants.SET_DATA_MODE, defaultModeValue);

        int checkedItem = 0;

        String[] values = context.getResources().getStringArray(R.array.set_data_mode_values);

        for (int i = 0; i < values.length; i++) {
            if (modeValue.equals(values[i])) checkedItem= i;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_mode, null);

        ((TextView) content.findViewById(R.id.dialog_txt)).setText(text);
        ((TextView) content.findViewById(R.id.dialog_txt_btm)).setText( text2);
        if (text2.trim().length() == 0) content.findViewById(R.id.dialog_txt_btm).setVisibility(View.GONE);

        View btn1 = content.findViewById(R.id.btn1);
        View btn2 = content.findViewById(R.id.btn2);
        View btn3 = content.findViewById(R.id.btn3);
        RadioButton radioButton1 = btn1.findViewById(R.id.radio1);
        RadioButton radioButton2 = btn2.findViewById(R.id.radio2);
        RadioButton radioButton3 = btn3.findViewById(R.id.radio3);

        radioButton1.setChecked(false);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);

        if (oneOption) {
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
            btn3.setVisibility(View.VISIBLE);
        } else {
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn3.setVisibility(View.GONE);
        }

        if (checkedItem == 0 ) radioButton1.setChecked(true);
        if (checkedItem == 1 ) {
            radioButton2.setChecked(true);
            radioButton3.setChecked(true);
        }

        builder.setTitle(title)
                .setView(content)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        (dialog, id) -> dialog.cancel());


        if (hintConfirm) {
            builder.setPositiveButton(R.string.mode_hint_got,
                    (dialog, id) -> {
                        dialog.cancel();
                        callHint();
                    });
        }


        AlertDialog alert = builder.create();
        alert.show();

        btn1.setOnClickListener((View v) -> {
            radioButton2.setChecked(false);
            radioButton1.setChecked(true);
            new Handler().postDelayed(() -> {
                callMode(0);
                alert.cancel();
            }, 400);
        });


        btn2.setOnClickListener(v -> {
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
            new Handler().postDelayed(() -> {
                callMode(1);
                alert.cancel();
            }, 400);

        });

        btn3.setOnClickListener(v -> {
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
            radioButton3.setChecked(true);
            new Handler().postDelayed(() -> {
                callMode(1);
                alert.cancel();
            }, 400);

        });


    }


    public void listModeChoice(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String defaultModeValue = context.getResources().getString(R.string.set_data_mode_default_value);

        String modeValue = appSettings.getString(Constants.SET_DATA_MODE, defaultModeValue);

        int checkedItem = 0;

        String[] values = context.getResources().getStringArray(R.array.set_data_mode_values);

        for (int i = 0; i < values.length; i++) {
            if (modeValue.equals(values[i])) checkedItem= i;
        }

        builder.setTitle(title)

                .setSingleChoiceItems(R.array.set_data_mode_list, checkedItem, (dialog, which) -> {

                    new Handler().postDelayed(() -> {
                        callMode(which);
                        dialog.dismiss();
                    }, 400);
                })

                .setCancelable(true)

                .setNegativeButton(R.string.dialog_close_txt,
                        (dialog, id) -> dialog.cancel());


        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    public void callMode(int index) {

    }

    @Override
    public void callHint() {

    }


}
