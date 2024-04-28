package com.online.languages.study.lang.presentation.category.category_list;

import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.google.android.material.snackbar.Snackbar;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.databinding.FragmentCat1Binding;
import com.online.languages.study.lang.presentation.core.BaseFragment;
import com.online.languages.study.lang.presentation.details.ScrollingActivity;
import com.online.languages.study.lang.utils.Vibration;

public class CatTabFragment1 extends BaseFragment implements CategoryListViewActions {
    private CategoryListViewController viewController;
    private FragmentCat1Binding mBinding;
    private Vibration vibration;

    String categoryID;
    private MenuItem changeLayoutBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentCat1Binding.inflate(getLayoutInflater(), container, false);
        vibration = appContainer.getVibration();

        categoryID = requireActivity().getIntent().getStringExtra(Constants.EXTRA_CAT_ID);

        SimpleItemAnimator listAnimator = ((SimpleItemAnimator) mBinding.myRecyclerView.getItemAnimator());
        if (listAnimator != null) {
            listAnimator.setSupportsChangeAnimations(false);
        }

        SimpleItemAnimator listCardAnimator = ((SimpleItemAnimator) mBinding.myRecyclerViewCard.getItemAnimator());
        if (listCardAnimator != null) {
            listCardAnimator.setSupportsChangeAnimations(false);
        }

        initViewController();

        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category_list_options, menu);
        changeLayoutBtn = menu.findItem(R.id.list_layout);
        viewController.showLayoutStatus();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.list_layout) {
            openLayoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void applyLayoutStatus(@NonNull ListType type) {
        if (type == ListType.CARD) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_card);
        } else if (type == ListType.COMPACT) {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_column);
        } else {
            changeLayoutBtn.setIcon(R.drawable.ic_view_list_big);
        }
    }

    private void openLayoutDialog() {
        viewController.showLayoutDialog();
    }

    private void initViewController() {
        viewController = new CategoryListViewControllerImpl(
                mBinding, this,
                appContainer.getModels().provideCategoryViewModel(requireActivity()),
                getLifecycle()
        );

        viewController.setup(categoryID);
    }

    public void updateList() {
        viewController.getDataList();
    }

    @Override
    public void openDialog(@NonNull String data) {
        showAlertDialog(data);
    }

    @Override
    public void changeStarredCallback(int status) {
        if (status == 0) {
            showStarredLimitMessage();
        }
        vibration.doOnStatus(status);
    }

    private void showStarredLimitMessage() {
        String message = getString(R.string.starred_limit);
        Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    public void showAlertDialog(String dataItemId) {
        Intent intent = new Intent(requireActivity(), ScrollingActivity.class);
        intent.putExtra("starrable", true);
        intent.putExtra("id", dataItemId);
        intent.putExtra("position", 0);
        startActivityForResult(intent,1);
        openActivity.detailOpenTransition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            viewController.unlockOpenDialog();
            if (resultCode == RESULT_OK) {
                viewController.getDataList();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void updateDataList() {   /// check all items
        updateList();
    }

}
