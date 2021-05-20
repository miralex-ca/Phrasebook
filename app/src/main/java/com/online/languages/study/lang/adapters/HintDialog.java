package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.online.languages.study.lang.R;


public class HintDialog {

    enum TextType {
        TEXT, HTML
    }

    Context context;
    TextType textType;
    Boolean small_height;
    Boolean setMaxHeight;


    public HintDialog(Context _context) {
        context = _context;
        textType = TextType.HTML;
        small_height = context.getResources().getBoolean(R.bool.small_height);
        setMaxHeight = false;
    }



    public void createDialog(String title, String text) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        (dialog, id) -> dialog.cancel());


        if (textType == TextType.HTML) {
            builder.setMessage(Html.fromHtml(text));
        } else {
            builder.setMessage(text);
        }


        final AlertDialog alert = builder.create();

        alert.show();
        TextView textView = alert.findViewById(android.R.id.message);

        textView.setTextSize(14);

    }



    public void showCustomDialog(String titleText, String text) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_hint, null);

        TextView title = content.findViewById(R.id.dialog_title);
        TextView txt = content.findViewById(R.id.dialog_txt);

        title.setText(titleText);


        if (textType == TextType.HTML) {
            txt.setText(Html.fromHtml(text));
        } else {
            txt.setText(text);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                //.setTitle(title)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        (dialog, id) -> dialog.cancel())

                ///.setMessage(Html.fromHtml(resultTxt))

                .setView(content);

        AlertDialog alert = builder.create();
        alert.show();




    }


    private static int dpToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }



}
