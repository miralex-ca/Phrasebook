package com.online.languages.study.lang.adapters;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.online.languages.study.lang.BuildConfig;
import com.online.languages.study.lang.MainActivity;
import com.online.languages.study.lang.R;

import static com.online.languages.study.lang.Constants.SET_RATE_REQUEST;


public class RateDialog {



    Context context;


    AlertDialog alert;

    MainActivity act;

    View rateValue;
    View rateFeedback;
    TextView rateFeedbackTitle;
    EditText editFeedback;
    SharedPreferences appSettings;

    View[] stars;
    int rating = 0;


    public RateDialog(Context _context) {
        context = _context;
        appSettings = PreferenceManager.getDefaultSharedPreferences(context);
    }



    public void createDialog(String title, String text) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.dialog_rate, null);

        stars = new View[5];
        stars[0] = content.findViewById(R.id.rate_star_1);
        stars[1] = content.findViewById(R.id.rate_star_2);
        stars[2] = content.findViewById(R.id.rate_star_3);
        stars[3] = content.findViewById(R.id.rate_star_4);
        stars[4] = content.findViewById(R.id.rate_star_5);

        for (View star: stars) {
                 star.setOnClickListener(this::checkRating );
        }

        Button btnRate = content.findViewById(R.id.btn_rate_submit);
        Button btnRateLater = content.findViewById(R.id.btn_rate_late);
        Button btnRateSend = content.findViewById(R.id.btn_rate_send);

        rateValue = content.findViewById(R.id.rate_value_wrap);
        rateFeedback = content.findViewById(R.id.rate_feedback_wrap);
        rateFeedbackTitle = content.findViewById(R.id.rate_title_send);
        editFeedback = content.findViewById(R.id.et_rate_feedback);

        builder.setCancelable(true);
                //.setMessage(text);
        builder.setView(content);

        alert = builder.create();

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(14);

        btnRate.setOnClickListener(view -> {
            processRating();
        });

        btnRateLater.setOnClickListener(view -> {
            alert.dismiss();
        });

        btnRateSend.setOnClickListener(view -> {
            sendFeedback();
            alert.dismiss();
        });

    }

    private void sendFeedback() {

        String recepientEmail = context.getString(R.string.mail_address);

        String subject = rateFeedbackTitle.getText().toString();
        String versionName = BuildConfig.VERSION_NAME;

        String mailBody = editFeedback.getText().toString();

        String version = String.format(context.getString(R.string.msg_version_name), context.getString(R.string.msg_version_abr), versionName);

        String mailSubject = subject + "   " + version;

        Intent i = new Intent(Intent.ACTION_SENDTO);

        String mailto = "mailto:" + recepientEmail +
                "?subject=" + Uri.encode(mailSubject) +
                "&body=" + Uri.encode(mailBody);

        i.setData(Uri.parse(mailto));

        try {
            context.startActivity(Intent.createChooser(i, context.getString(R.string.msg_sending_mail)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.msg_no_mail_client, Toast.LENGTH_SHORT).show();
        }



    }

    private void processRating() {

        saveRateRequest();

        if (rating == 5) {
            goToPlayStore();
            alert.dismiss();
        } else {
            askInfo();
        }

    }

    private void saveRateRequest() {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(SET_RATE_REQUEST, false);
        editor.apply();
    }

    private void askInfo() {

        rateValue.setVisibility(View.GONE);
        rateFeedback.setVisibility(View.VISIBLE);

        String title = context.getString(R.string.send_rate_title);
        String stars = "";

        for (int i=0; i < rating; i++) {
            stars += "★";
        }

        rateFeedbackTitle.setText(title +" " + stars);

    }

    private void goToPlayStore() {

        String pack = context.getString(R.string.app_market_link);

        //pack = context.getPackageName();
        Uri uri = Uri.parse("market://details?id=" + pack);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + pack)));

        }

    }


    private void checkRating(View view) {
       int rate = Integer.parseInt(view.getTag().toString());

       rating = rate;

        for (int i = 0; i < stars.length; i++) {
            View starWrap = stars[i];
            ImageView star = starWrap.findViewById(R.id.rate_star);
            star.setImageResource(R.drawable.ic_star_borded_inactive);
            if (rate > i) {
                star.setImageResource(R.drawable.ic_star_borded);
            }
        }
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





    }
