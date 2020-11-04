package com.online.languages.study.lang.adapters;



import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.GetPremium;
import com.online.languages.study.lang.R;


public class PremiumDialog {

    enum TextType {
        TEXT, HTML
    }

    Context context;
    TextType textType;
    Boolean small_height;
    Boolean setMaxHeight;




    public PremiumDialog(Context _context) {
        context = _context;
        textType = TextType.TEXT;
        small_height = context.getResources().getBoolean(R.bool.small_height);
        setMaxHeight = false;
    }



    public void openInfoDialog(String message) {
        createDialog(
                context.getResources().getString(R.string.info_txt),
                message);
    }


    public void openInfoHtmlDialog(String message, Boolean _setMaxHeight) {
        textType = TextType.HTML;
        setMaxHeight = _setMaxHeight;
        createDialog(
                context.getResources().getString(R.string.info_txt),
                message);
    }



    public void createDialog(String title, String text) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(true)

                .setNeutralButton(R.string.plus_version_btn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openPremium();
                            }
                        })


                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });




        if (textType == TextType.HTML) {
            builder.setMessage(Html.fromHtml(text));
        } else {
            builder.setMessage(text);
        }


        final AlertDialog alert = builder.create();

        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);


        if (setMaxHeight) {
            if (!small_height) {

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                int dialogWidth = lp.width;
                alert.getWindow().setLayout(dialogWidth, dpToPixels(context, 500));

            }
        }

    }


    public void openPremium() {

         Intent i = new Intent(context, GetPremium.class);
        ((Activity)context).startActivityForResult(i, 1);


    }




    public void showCustomDialog(String title, String text) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_info, null);


        TextView txt = content.findViewById(R.id.dialog_txt);


        if (textType == TextType.HTML) {
            txt.setText(Html.fromHtml(text));
        } else {
            txt.setText(text);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_close_txt,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                ///.setMessage(Html.fromHtml(resultTxt))

                .setView(content);

        AlertDialog alert = builder.create();
        alert.show();

    }




    private static int dpToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }




    public void simpleDialog(String title, String text) {



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

    }






    }
