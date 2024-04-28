package com.online.languages.study.lang.utils;

import android.content.Context;
import android.os.Vibrator;

public class Vibration {
    private final Vibrator vibrator;

    public Vibration(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void doOnStatus(int status) {
        int vibLen = 30;
        if (status == 0) {
            vibLen = 300;
        }

        if (vibrator != null) {
            vibrator.vibrate(vibLen);
        }
    }

    public void vibrate(int duration) {
        if (vibrator != null) {
            vibrator.vibrate(duration);
        }
    }
}
