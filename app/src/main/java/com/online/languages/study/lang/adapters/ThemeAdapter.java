package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;

import com.online.languages.study.lang.R;


public class ThemeAdapter {

    private Context context;

    public int styleTheme;

    private int colorListTxt;
    private Boolean transparentStatus = false;

    private Boolean dialog = false;

    SharedPreferences appSettings;

    public ThemeAdapter() {

    }

    public ThemeAdapter(Context _context, String _theme, int layout) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(_context);
        setTheme(_context, _theme, false, true);
    }


    public ThemeAdapter(Context _context, String _theme, Boolean transparent) {
        appSettings = PreferenceManager.getDefaultSharedPreferences(_context);
        setTheme(_context, _theme, transparent, false);

    }

    private void setTheme(Context _context, String _theme, Boolean transparent, Boolean _dialog) {

        context = _context;
        transparentStatus = transparent;
        appSettings = PreferenceManager.getDefaultSharedPreferences(_context);

        dialog = _dialog;

        styleTheme = getThemeStyleName(_theme);
        colorListTxt = getColorFromAttr(R.attr.colorListTxt);


       // Toast.makeText(context, "Dialog: "+ dialog, Toast.LENGTH_SHORT).show();




    }


    private int getThemeStyleName(String theme) {
        int themeStyle = R.style.DefaultTheme;

        boolean nightMode = appSettings.getBoolean("night_mode", false);

        switch (theme) {

            case "default":
                themeStyle =  R.style.DefaultTheme;
                if (transparentStatus) themeStyle =  R.style.DefaultTheme;
                if (dialog) themeStyle =  R.style.DefaultTheme_UserDialog;

                if (nightMode) {
                    themeStyle =  R.style.DefaultThemeModes;
                    if (transparentStatus) themeStyle =  R.style.DefaultThemeModes;
                    if (dialog) themeStyle =  R.style.DefaultThemeModes_Detail;
                }

                break;

            case "red":
                    themeStyle =  R.style.RedTheme;
                    if (transparentStatus) themeStyle =  R.style.RedTheme;
                    if (dialog) themeStyle =  R.style.RedTheme_Detail;

                    if (nightMode) {
                        themeStyle =  R.style.RedThemeModes;
                        if (transparentStatus) themeStyle =  R.style.RedThemeModes;
                        if (dialog) themeStyle =  R.style.RedThemeModes_Detail;
                    }

                    break;
            case "white":

                themeStyle =  R.style.WhiteTheme;
                if (transparentStatus) themeStyle = R.style.WhiteTheme;
                if (dialog) themeStyle =  R.style.WhiteTheme_Detail;

                if (nightMode) {
                    themeStyle =  R.style.WhiteThemeModes;
                    if (transparentStatus) themeStyle = R.style.WhiteThemeModes;
                    if (dialog) themeStyle =  R.style.WhiteThemeModes_Detail;
                }


                break;

            case "white_map":
                themeStyle =  R.style.WhiteThemeMap;
                break;

            case "dark":
                themeStyle = R.style.DarkTheme;
                if (transparentStatus) themeStyle = R.style.DarkTheme_TransparentStatus;
                if (dialog) themeStyle =  R.style.DarkTheme_UserDialog;
                break;
            case "smoky":
                themeStyle = R.style.SmokyTheme;
                if (transparentStatus) themeStyle = R.style.SmokyTheme_TransparentStatus;
                if (dialog) themeStyle =  R.style.SmokyTheme_UserDialog;
                break;
            case "westworld":
                themeStyle = R.style.WestworldTheme;
                if (transparentStatus) themeStyle = R.style.WestworldTheme_TransparentStatus;
                if (dialog) themeStyle =  R.style.WestworldTheme_UserDialog;
                break;
        }

        return themeStyle;
    }


    public void getTheme() {

        context.setTheme(styleTheme);








       // BroadcastReceiver br = new LocaleChangedReceiver();

        //IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
       // filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
       // context.registerReceiver(br, filter);

       // IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
       // context.registerReceiver(br, filter);





        /*
        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //RESTART APPLICATION


                if (intent.getAction (). compareTo (Intent.ACTION_LOCALE_CHANGED) == 0)
                {


                }


                Toast.makeText(getAppContext(), "Locale changed", Toast.LENGTH_SHORT).show();
            }
        };

            //register broadcastreceiver
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));



         */

        //Toast.makeText(context, "Theme: "+styleTheme, Toast.LENGTH_SHORT).show();
    }

    public int getColorFromAttr(int attr) {

        TypedArray a = context.getTheme().obtainStyledAttributes(styleTheme, new int[] {attr});

        int color = a.getResourceId(0, 0);
        a.recycle();
        return color;
    }



}
