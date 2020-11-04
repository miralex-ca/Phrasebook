package com.online.languages.study.lang.adapters;



import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;

import com.online.languages.study.lang.R;

public class DataModeDialog {

    Context context;


    public DataModeDialog(Context _context) {
        context = _context;
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
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setMessage(text);
        AlertDialog alert = builder.create();
        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);
    }















    }
