package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;

import android.preference.PreferenceManager;
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


        act = activity;

    }


    public void openInfoDialog(String message) {
        createDialog(
                context.getResources().getString(R.string.info_txt),
                message);
    }


    private void createDialog(String title, String text) {

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        String tasksNavSetting = appSettings.getString("set_tasks_nav", context.getString(R.string.set_tasks_nav_default));


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.nav_dialog, null);

        View navItemTasks = content.findViewById(R.id.navItemTasks);
        View navItem1 = content.findViewById(R.id.navItem1);
        View navItem2 = content.findViewById(R.id.navItem2);
        View navItem3 = content.findViewById(R.id.navItem3);
        View navItem4 = content.findViewById(R.id.navItem4);

        if (!tasksNavSetting.equals("menu")) {

            navItemTasks.setVisibility(View.GONE);
            View divider = content.findViewById(R.id.task_devider);
            divider.setVisibility(View.GONE);

        }


        navItemTasks.setOnClickListener(v -> dismissDialog(4));

        navItem1.setOnClickListener(v -> dismissDialog(5));

        navItem2.setOnClickListener(v -> dismissDialog(6));

        navItem3.setOnClickListener(v -> dismissDialog(7));

        navItem4.setOnClickListener(v -> dismissDialog(8));


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

        new Handler().postDelayed(() -> {

            if (alert != null) alert.dismiss();

        }, 180);

    }


}
