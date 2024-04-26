package com.online.languages.study.lang.presentation.section;

import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.PremiumDialog;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.databinding.ActivitySectionReviewListBinding;
import com.online.languages.study.lang.presentation.category.CatActivity;
import com.online.languages.study.lang.presentation.core.ThemedActivity;
import com.online.languages.study.lang.presentation.details.ScrollingActivity;

import org.jetbrains.annotations.NotNull;

public class SectionReviewActivity extends ThemedActivity implements SectionViewActions {

    private SectionViewController viewController;
    private ActivitySectionReviewListBinding mBinding;
    String sectionId;

    private MenuItem changeLayoutBtn;
    private static final int DIALOG_OPEN = 1;
    private static final int DIALOG_CLOSED = 0;
    private int dialogStatus = DIALOG_CLOSED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySectionReviewListBinding.inflate(getLayoutInflater());
        openActivity.setOrientation();
        setContentView(mBinding.getRoot());
        setupToolbar();

        sectionId = getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        initViewController(sectionId);

        setTitle(viewController.getTitle());
        openView();
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewController(String tSectionID) {
        viewController = new SectionViewControllerImpl(
                mBinding,
                appContainer.getModels().provideSectionViewModel(this),
                this
        );
        viewController.setup(tSectionID);
    }

    private void openView() {
        mBinding.itemsList.setVisibility(View.VISIBLE);
    }

    public void refreshList(String data) {
        viewController.getSectionList(data);
    }

    private void openPremiumDialog() {
        PremiumDialog premiumDialog = new PremiumDialog(this);
        premiumDialog.createDialog(getString(R.string.plus_version_btn), getString(R.string.topic_access_in_plus_version));
    }

    private void openLayoutDialog() {
        viewController.showLayoutDialog();
    }

    private void openInfoMessage() {
        DataModeDialog dataModeDialog = new DataModeDialog(this);
        dataModeDialog.createDialog(getString(R.string.info_txt), getString(R.string.info_star_review_txt));
    }

    @Override
    public void openCategory(@NotNull NavCategory data) {
        openCategoryPage(data);
    }

    @Override
    public void openItem(@NonNull DataItem data) {
        showAlertDialog(data);
    }

    @Override
    public void openPremium() {
        openPremiumDialog();
    }

    @Override
    public void applyLayoutStatus(boolean isCompact) {
        if (isCompact) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);
        } else {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
        }
    }

    private void openCategoryPage(NavCategory category) {
        Intent i = new Intent(this, CatActivity.class);
        i.putExtra(EXTRA_SECTION_ID, sectionId);
        i.putExtra(EXTRA_CAT_ID, category.id);
        i.putExtra(Constants.EXTRA_CAT_SPEC, category.spec);
        i.putExtra("cat_title", category.title);
        startActivityForResult(i, 2);
        openActivity.pageTransition();
    }

    public void showAlertDialog(DataItem dataItem) {
        if (dialogStatus == DIALOG_OPEN) return;
        Intent intent = new Intent(this, ScrollingActivity.class);
        intent.putExtra("id", dataItem.id);
        intent.putExtra("position", 0);
        intent.putExtra("item", dataItem);
        intent.putExtra(EXTRA_CAT_ID, dataItem.cat);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.slide_in_down, 0);
        dialogStatus = DIALOG_OPEN;
        new Handler(Looper.getMainLooper()).postDelayed(() -> dialogStatus = DIALOG_CLOSED, 200);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_section_review, menu);
        changeLayoutBtn = menu.findItem(R.id.list_layout);
        viewController.showLayoutStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.info_from_menu) {
            openInfoMessage();
            return true;
        } else if (id == R.id.list_layout) {
            openLayoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        openActivity.pageBackTransition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialogStatus = DIALOG_CLOSED;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra(EXTRA_CAT_ID)) {
                    String cat = data.getStringExtra(EXTRA_CAT_ID);
                    refreshList(cat);
                }
            }
        }

        if (requestCode == 2) {
            if (data != null && data.hasExtra(EXTRA_CAT_ID) ) {
                String cat = data.getStringExtra(EXTRA_CAT_ID);
                refreshList(cat);
            }
        }
    }

}
