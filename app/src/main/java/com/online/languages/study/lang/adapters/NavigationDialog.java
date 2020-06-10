package com.online.languages.study.lang.adapters;



import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.MainActivity;
import com.online.languages.study.lang.R;


public class NavigationDialog {

    enum TextType {
        TEXT, HTML
    }

    Context context;
    TextType textType;
    Boolean small_height;
    Boolean setMaxHeight;

    AlertDialog alert;

    MainActivity act;


    public NavigationDialog(Context _context, MainActivity activity) {
        context = _context;

        textType = TextType.TEXT;
        small_height = context.getResources().getBoolean(R.bool.small_height);
        setMaxHeight = false;


        act  = activity;
    }



    public void openInfoDialog(String message) {
        createDialog(
                context.getResources().getString(R.string.info_txt),
                message);
    }




    private void createDialog(String title, String text) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.nav_dialog, null);


        View navItem1 = content.findViewById(R.id.navItem1);
        View navItem2 = content.findViewById(R.id.navItem2);
        View navItem3 = content.findViewById(R.id.navItem3);
        View navItem4 = content.findViewById(R.id.navItem4);

        navItem1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismissDialog(4);
            }
        });


        navItem2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismissDialog(5);
            }
        });


        navItem3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismissDialog(6);
            }
        });


        navItem4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismissDialog(7);
            }
        });

        //builder.setTitle(title);

        builder.setCancelable(true);
                //.setMessage(text);

        builder.setView(content);


        alert = builder.create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);

    }


    public void dismissDialog(final int order) {


        act.onMenuItemClicker(order);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (alert!= null) alert.dismiss();

            }
        }, 180);

    }



    private static int dpToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }



    }
