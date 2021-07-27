package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.databinding.FragmentTrainingBinding;

import java.util.ArrayList;

import static com.online.languages.study.lang.adapters.CategoryParamsDialog.CATEGORY_RESULT_DISPLAY;


public class TrainingFragment extends Fragment implements TrainingFragmentMethods {

    private FragmentTrainingBinding binding;

    Context activityContext;

    DataManager dataManager;

    SharedPreferences appSettings;

    public String categoryID = "";

    public TrainingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTrainingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        activityContext = getActivity();

        appSettings = PreferenceManager.getDefaultSharedPreferences(activityContext);

        dataManager = new DataManager(activityContext);

        getCategoryId();

        getData();

        setClicks();

        return view;
    }


    public void getCategoryId() {
        categoryID = CatActivity.categoryID;
    }

    private void setClicks() {

        binding.cardFlachcard.setOnClickListener(v -> openCards());
        binding.cardTestVocabulary.setOnClickListener(v -> openVocabularyTest());
        binding.cardTestPhrases.setOnClickListener(v -> openPhraseTest());
        binding.cardTestAudio.setOnClickListener(v -> openAudioTest());

    }

    @Override
    public void getData() {

        boolean resultDisplay = appSettings.getBoolean(CATEGORY_RESULT_DISPLAY, true);

        checkTestsDesc();
        if (resultDisplay) getTestsData();

    }

    private void checkTestsDesc() {

        binding.prVocabResult.setText(R.string.practice_vocab_test_desc);
        binding.prPhrasesResult.setText(R.string.practice_translate_test_desc);
        binding.prAudioResult.setText(R.string.practice_audio_test_desc);
    }


    private void getTestsData() {

        ArrayList<String[]> testsData = getTestsDataFromDB();

        replaceResultDesc(testsData.get(0), binding.prVocabResult);
        replaceResultDesc(testsData.get(1), binding.prPhrasesResult);
        replaceResultDesc(testsData.get(2), binding.prAudioResult);

    }


    @NonNull
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

    @Override
    public void openExercise(final int position) {
        new Handler().postDelayed(() -> ((CatActivity)getActivity()).nextPage(position), 30);
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



}
