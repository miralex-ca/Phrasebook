package com.online.languages.study.lang.fragments;


import static com.online.languages.study.lang.Constants.STARRED_CAT_TAG;
import static com.online.languages.study.lang.presentation.category.CategoryParamsDialog.CATEGORY_RESULT_DISPLAY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.databinding.FragmentTrainingBinding;
import com.online.languages.study.lang.presentation.core.BaseFragment;
import com.online.languages.study.lang.presentation.usercategories.UserListActivity;

import java.util.ArrayList;

public class UserListTrainingFragment extends BaseFragment {

    private FragmentTrainingBinding mBinding;
    Context activityContext;
    DataManager dataManager;

    SharedPreferences appSettings;

    public String categoryID = "";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentTrainingBinding.inflate(inflater, container, false);

        getCategoryId();


        activityContext = getActivity();
        appSettings = PreferenceManager.getDefaultSharedPreferences(activityContext);
        dataManager = new DataManager(activityContext);

        checkAudioTestDisplay();

        getData();
        setClicks();

        return mBinding.getRoot();
    }


    public void getCategoryId() {
        categoryID = STARRED_CAT_TAG;
    }


    public void openExercise(final int position) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ((UserListActivity) requireActivity()).nextPage(position);
        }, 30);
    }

    private void setClicks() {
        mBinding.cardFlachcard.setOnClickListener(v -> openCards());
        mBinding.cardTestVocabulary.setOnClickListener(v -> openVocabularyTest());
        mBinding.cardTestPhrases.setOnClickListener(v -> openPhraseTest());
        mBinding.cardTestAudio.setOnClickListener(v -> openAudioTest());
    }

    public void getData() {
        boolean resultDisplay = appSettings.getBoolean(CATEGORY_RESULT_DISPLAY, true);
        checkTestsDesc();
        if (resultDisplay) getTestsData();

    }

    private void checkTestsDesc() {
        mBinding.prVocabResult.setText(R.string.practice_vocab_test_desc);
        mBinding.prPhrasesResult.setText(R.string.practice_translate_test_desc);
        mBinding.prAudioResult.setText(R.string.practice_audio_test_desc);
    }

    private void getTestsData() {
        ArrayList<String[]> testsData = getTestsDataFromDB();
        replaceResultDesc(testsData.get(0), mBinding.prVocabResult);
        replaceResultDesc(testsData.get(1), mBinding.prPhrasesResult);
        replaceResultDesc(testsData.get(2), mBinding.prAudioResult);

    }


    private ArrayList<String[]> getTestsDataFromDB() {
        String catId = categoryID;
        String[] testIds = new String[] {catId + "_1", catId + "_2", catId + "_3"};
        return dataManager.getCategoryTestsResult(testIds);
    }


    private void replaceResultDesc(String[] params, TextView textView) {
        if (params.length > 1 && params[2].equals("replace")) {
            textView.setText(params[1]);
        }
    }

    private void openCards() {
        openExercise(0);
    }

    private void openVocabularyTest() {
        openExercise(1);
    }

    private void openPhraseTest() {
        openExercise(2);
    }

    private void openAudioTest() {
        openExercise(3);
    }

    private void checkAudioTestDisplay() {
        boolean display = appSettings.getBoolean("set_speak", true);
        if (display) {
            mBinding.audioTestBox.setVisibility(View.VISIBLE);
        } else {
            mBinding.audioTestBox.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }





}
