package com.online.languages.study.studymaster;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import java.util.Locale;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.online.languages.study.studymaster.App.getAppContext;

public class LocaleChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {

            Intent i = new Intent(context, AppStart.class);
            i.addFlags(FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }

        }
    }
}