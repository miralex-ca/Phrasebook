package com.online.languages.study.lang.presentation.category.category_exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.databinding.FragmentTrainingBinding;
import com.online.languages.study.lang.presentation.category.CatActivity;
import com.online.languages.study.lang.presentation.category.CategoryViewModel;
import com.online.languages.study.lang.presentation.core.BaseFragment;

public class TrainingFragment extends BaseFragment implements CategoryExListViewActions {
    private FragmentTrainingBinding mBinding;
    private CategoryExListViewController viewController;
    public String categoryID;

    public TrainingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentTrainingBinding.inflate(inflater, container, false);
        categoryID = requireActivity().getIntent().getStringExtra(Constants.EXTRA_CAT_ID);
        initViewController();
        return mBinding.getRoot();
    }

    private void initViewController() {
        CategoryViewModel viewModel = appContainer.getModels().provideCategoryViewModel(requireActivity());
        viewController = new CategoryExListViewControllerImpl(mBinding, this, viewModel);
        viewController.setup(categoryID);
    }

    public void getData() {
        viewController.getExercisesData();
    }

    @Override
    public void openExercise(int position) {
        openExerciseFromActivity(position);
    }

    private void openExerciseFromActivity(final int position) {
        ((CatActivity) requireActivity()).nextPage(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
