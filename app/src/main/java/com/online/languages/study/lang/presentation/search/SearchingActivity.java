package com.online.languages.study.lang.presentation.search;


import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;
import static com.online.languages.study.lang.Constants.NOTE_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.databinding.SearchActivityBinding;
import com.online.languages.study.lang.presentation.core.BaseActivity;
import com.online.languages.study.lang.presentation.details.ScrollingActivity;
import com.online.languages.study.lang.presentation.notes.NoteActivity;
import com.online.languages.study.lang.utils.Vibration;

public class SearchingActivity extends BaseActivity implements SearchViewActions {
    SearchViewController viewController;
    SearchActivityBinding mBinding;
    Vibration vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = SearchActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        openActivity.setOrientation();
        vibration = appContainer.getVibration();

        initViewController();
        initSearchUI();
        setupToolbar();
    }

    private void initViewController() {
        SearchViewModelFactory viewModelFactory = new SearchViewModelFactory(appContainer.getRepository());
        SearchViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(SearchViewModel.class);

        viewController = new SearchViewControllerImpl(
                mBinding, this,
                viewModel,
                getLifecycle()
        );
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initSearchUI() {
        mBinding.searchView.onActionViewExpanded();
        mBinding.searchView.requestFocus();
        mBinding.recyclerView.setSelected(true);
        mBinding.container.setOnClickListener(v -> focusLayout());
        mBinding.containerScrollview.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (mBinding.searchView.hasFocus()) mBinding.searchView.clearFocus();
                });
    }

    @Override
    public void openDialog(DataItem dataItem) {
        mBinding.searchView.clearFocus();
        showItemDialog(dataItem);
    }

    @Override
    public void changeStarredCallback(int status) {
        if (status == 0) {
            showStarredLimitMessage();
        }
        vibration.doOnStatus(status);
    }

    private void showItemDialog(DataItem dataItem) {
        if (dataItem.filter.contains(NOTE_TAG)) {
            Intent i = new Intent(this, NoteActivity.class);
            i.putExtra(EXTRA_NOTE_ID, dataItem.id );
            i.putExtra("source", "search" );
            i.putExtra("position", 0);
            startActivityForResult(i, 2);
            openActivity.pageTransition();

            return;
        }

        Intent intent = new Intent(this, ScrollingActivity.class);
        intent.putExtra("id", dataItem.id );
        intent.putExtra("position", 0);
        intent.putExtra("item", dataItem);
        intent.putExtra("source", 1);

        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.slide_in_down, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            viewController.unlockOpenDialog();
            if (resultCode == SearchingActivity.RESULT_OK) {
                checkStarred();
            }
        }

        if (requestCode == 2) {
            viewController.unlockOpenDialog();
            if (resultCode == SearchingActivity.RESULT_OK) {
                refreshData();
            }
        }
    }

    private void showStarredLimitMessage() {
        String message = getString(R.string.starred_limit);
        Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void checkStarred() {
        viewController.checkStarred();
    }

    private void refreshData() {
        viewController.refreshData();
    }

    private void focusLayout() {
        mBinding.searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        openActivity.pageTransitionClose();
    }
}
