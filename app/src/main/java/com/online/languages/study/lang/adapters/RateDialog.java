package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.databinding.DialogRateBinding;
import com.online.languages.study.lang.tools.ContactAction;

import static com.online.languages.study.lang.Constants.SET_RATE_REQUEST;

public class RateDialog {

    Context context;
    AlertDialog dialog;
    SharedPreferences appSettings;
    ContactAction contactAction;

    View[] stars;
    int rating = 0;

    private DialogRateBinding binding;

    public RateDialog(Context _context) {
        context = _context;
        appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        contactAction = new ContactAction(context);
    }


    public void createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_rate, null);
        binding = DialogRateBinding.bind(content);

        builder.setCancelable(true);
        builder.setView(binding.getRoot());

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        setOnClicks();
        initStarViews();

    }

    private void setOnClicks() {

        binding.btnRateSubmit.setOnClickListener(view -> processRating());
        binding.btnRateLate.setOnClickListener(view -> dialog.dismiss());
        binding.btnRateSend.setOnClickListener(view -> {
            sendFeedback();
            dialog.dismiss();
        });
    }


    private void initStarViews() {
        stars = new View[5];
        stars[0] = binding.rateStar1;
        stars[1] = binding.rateStar2;
        stars[2] = binding.rateStar3;
        stars[3] = binding.rateStar4;
        stars[4] = binding.rateStar5;

        for (View star : stars) {
            star.setOnClickListener(this::checkRating);
        }
    }

    private void sendFeedback() {

        String email = context.getString(R.string.mail_address);

        String subject = binding.rateTitleSend.getText().toString();
        String mailSubject = subject + "   " + contactAction.getAppVersionName();

        String mailBody = binding.etRateFeedback.getText().toString();

        contactAction.sendEmail(email, mailSubject, mailBody);

    }

    private void processRating() {

        saveRateRequest();

        if (rating == 5) {
            goToPlayStore();
            dialog.dismiss();
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

        binding.rateValueWrap.setVisibility(View.GONE);
        binding.rateFeedbackWrap.setVisibility(View.VISIBLE);

        String title = context.getString(R.string.send_rate_title);
        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < rating; i++) stars.append("★");

        binding.rateTitleSend.setText(String.format("%s %s", title, stars));

    }


    private void goToPlayStore() {
        String pack = context.getString(R.string.app_market_link);
        contactAction.goToPlayStore(pack);
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


}
