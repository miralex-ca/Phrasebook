package com.online.languages.study.lang.presentation.main.fragments.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.databinding.FragmentHomeBinding;
import com.online.languages.study.lang.presentation.core.BaseFragment;
import com.online.languages.study.lang.presentation.main.MainActivity;

public class HomeFragment extends BaseFragment implements HomeViewActions {
    private FragmentHomeBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        setupHomeList();
        initViewController();
        return mBinding.getRoot();
    }

    private void initViewController() {
        HomeViewController viewController = new HomeViewControllerImpl(mBinding, this,
                appContainer.getModels().provideHomeViewModel(requireActivity()));
        viewController.setup();
    }

    private void setupHomeList() {
        boolean tablet = getResources().getBoolean(R.bool.tablet);
        int spanCount = 2;

        if (tablet) {
            spanCount = 3;
            if (getResources().getBoolean(R.bool.tablet_land)) {
                spanCount = 4;
            }
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), spanCount, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewHome.setLayoutManager(mLayoutManager);

    }

    @Override
    public void openSection(@NonNull String data) {
        onGridClick(data);
    }

    private void onGridClick(final String sectionId) {
        new Handler().postDelayed(() -> {
            ((MainActivity) requireActivity()).openCatActivity(sectionId);
        }, 80);
    }
}
