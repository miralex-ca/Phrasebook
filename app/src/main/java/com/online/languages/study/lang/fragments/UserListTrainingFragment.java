package com.online.languages.study.lang.fragments;


import android.os.Handler;
import android.os.Looper;

import com.online.languages.study.lang.UserListActivity;

import static com.online.languages.study.lang.Constants.STARRED_CAT_TAG;

public class UserListTrainingFragment extends TrainingFragment {

    @Override
    public void getCategoryId() {
        categoryID = STARRED_CAT_TAG;
    }

    @Override
    public void openExercise(final int position) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ((UserListActivity) getActivity()).nextPage(position);
        }, 30);
    }

}
