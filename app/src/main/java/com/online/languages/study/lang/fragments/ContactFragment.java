package com.online.languages.study.lang.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.online.languages.study.lang.BuildConfig;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.databinding.FragmentContactBinding;
import com.online.languages.study.lang.tools.ContactAction;

import static com.online.languages.study.lang.Constants.PARAM_EMPTY;

public class ContactFragment extends Fragment {

    public static final int SEND_MAIL_TYPE_MSG = 0;
    public static final int SEND_MAIL_TYPE_REPORT = 1;
    public static final String PARAM_APP_EN = "en";
    public static final String PARAM_APP_FR = "fr";
    public static final String PARAM_APP_JP = "jp";
    public static final String PARAM_APP_RU = "ru";
    public static final String PARAM_APP_HIST = "hist";

    private FragmentContactBinding binding;
    private ContactAction contactAction;

    public ContactFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContactBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        contactAction = new ContactAction(getActivity());

        setOnClicks();

        return view;
    }


    private void setOnClicks() {

        binding.list.sendFeedback.setOnClickListener(v -> sendMail(SEND_MAIL_TYPE_MSG));
        binding.list.sendReport.setOnClickListener(v -> sendMail(SEND_MAIL_TYPE_REPORT));
        binding.list.contactShare.setOnClickListener(v -> contactAction.share(getActivity()));
        binding.list.rateAppLink.setOnClickListener(v -> contactAction.rate());
        binding.list.appEnglishLink.setOnClickListener(v -> openAppLink(PARAM_APP_EN));
        binding.list.appFrenchLink.setOnClickListener(v -> openAppLink(PARAM_APP_FR));
        binding.list.appRussianLink.setOnClickListener(v -> openAppLink(PARAM_APP_RU));
        binding.list.appJapaneseLink.setOnClickListener(v -> openAppLink(PARAM_APP_JP));
        binding.list.appHistoryLink.setOnClickListener(v -> openAppLink(PARAM_APP_HIST));

    }


    private void sendMail(int type) {

        String email = getString(R.string.mail_address);

        String subject = getString(R.string.msg_mail_subject);

        if (type == SEND_MAIL_TYPE_REPORT) subject = getString(R.string.msg_mail_subject_error);

        String mailSubject = subject + " " + contactAction.getAppVersionName();

        contactAction.sendEmail(email, mailSubject, PARAM_EMPTY);

    }


    private void openAppLink(String app) {

        String appLink = getString(R.string.app_market_link);

        switch (app) {
            case PARAM_APP_EN:
                appLink = getString(R.string.en_market_link);
                break;
            case PARAM_APP_FR:
                appLink = getString(R.string.fr_market_link);
                break;
            case PARAM_APP_RU:
                appLink = getString(R.string.ru_market_link);
                break;
            case PARAM_APP_HIST:
                appLink = getString(R.string.hist_market_link);
                break;
            case PARAM_APP_JP:
                appLink = getString(R.string.jp_market_link);
                break;
        }

        contactAction.goToPlayStore(appLink);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
