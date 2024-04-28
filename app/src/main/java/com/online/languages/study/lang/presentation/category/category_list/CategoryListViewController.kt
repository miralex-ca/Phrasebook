package com.online.languages.study.lang.presentation.category.category_list

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.databinding.FragmentCat1Binding
import com.online.languages.study.lang.presentation.category.CategoryLayoutDialog
import com.online.languages.study.lang.presentation.category.CategoryUiItem
import com.online.languages.study.lang.presentation.category.CategoryViewModel
import com.online.languages.study.lang.utils.ClickAction
import com.online.languages.study.lang.utils.gone
import com.online.languages.study.lang.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface CategoryListViewController {
    fun setup(categoryId: String)
    fun getDataList()
    fun unlockOpenDialog()
    fun showLayoutDialog()
    fun showLayoutStatus()
}

interface CategoryListViewActions {
    fun openDialog(data: String)
    fun changeStarredCallback(status: Int)
    fun applyLayoutStatus(type: ListType )
}

class CategoryListViewControllerImpl(
    private val binding: FragmentCat1Binding,
    private val viewActions: CategoryListViewActions,
    private val viewModel: CategoryViewModel,
    lifecycle: Lifecycle,
) : CategoryListViewController {
    private val coroutineScope = lifecycle.coroutineScope
    private lateinit var listAdapter: CategoryListAdapter
    private lateinit var listCardAdapter: CategoryCardListAdapter
    private var canOpenDialog = true
    private var categoryId: String = ""

    companion object {
        private const val OPEN_DIALOG_LOCK_DURATION = 600L
    }

    override fun setup(categoryId: String) {
        this.categoryId = categoryId
        initDataList()
    }

    private fun initDataList() {
        listAdapter = CategoryListAdapter(::onListItemClick)
        binding.myRecyclerView.adapter = listAdapter
        listCardAdapter = CategoryCardListAdapter(::onListItemClick)
        binding.myRecyclerViewCard.adapter = listCardAdapter
        getDataList()
    }

    override fun getDataList() {
        viewModel.getData(categoryId) {
            loadAndDisplayList()
        }
    }

    private fun loadAndDisplayList() {
        manageListDisplay()
        loadAndSubmitList()
    }

    private fun loadAndSubmitList() {
        if (listLayoutType() == ListType.CARD) {
            submitCardsList()
        } else {
            submitItemsList()
        }
    }

    private fun refreshList() = loadAndSubmitList()

    private fun manageListDisplay() {
        if (listLayoutType() == ListType.CARD) {
            binding.myRecyclerViewCard.visible()
            binding.myRecyclerView.gone()
        } else {
            binding.myRecyclerView.visible()
            binding.myRecyclerViewCard.gone()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun submitItemsList() {
        val isCompact = listLayoutType() == ListType.COMPACT

        val list = viewModel.getListToDisplay()
        listAdapter.apply {
            setCompact(isCompact)
        }

        listAdapter.submitList(list.applyUi())
        binding.myRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun submitCardsList() {
        val list = viewModel.getListToDisplay()
        listCardAdapter.submitList(list.applyUi())
    }

    private fun onListItemClick(
        clickAction: ClickAction,
        item: CategoryUiItem
    ) {
        when (clickAction) {
            ClickAction.ClickStar -> changeStarred(item)
            ClickAction.LongClick  -> changeStarredWithCallback(item)
            else -> listItemClicked(item)
        }
    }

    private fun listItemClicked(item: CategoryUiItem) {
        if (canOpenDialog) {
            lockOpenDialog()
            coroutineScope.launch {
                delay(OPEN_DIALOG_LOCK_DURATION)
                unlockOpenDialog()
            }
            viewActions.openDialog(item.data.id)
        }
    }

    private fun lockOpenDialog() {
        canOpenDialog = false
    }

    override fun unlockOpenDialog() {
        canOpenDialog = true
    }

    private fun listLayoutType() = ListType.from(viewModel.getLayout())

    private fun changeStarred(item: CategoryUiItem) {
        viewModel.changeStarred(item) {
            refreshList()
        }
    }

    private fun changeStarredWithCallback(item: CategoryUiItem) {
        viewModel.changeStarred(item) {
            viewActions.changeStarredCallback(it)
            refreshList()
        }
    }

    private fun List<CategoryUiItem>.applyUi(): List<CategoryUiItem> {
        return map { it  }
    }

    override fun showLayoutStatus() {
        viewActions.applyLayoutStatus(listLayoutType())
    }

    override fun showLayoutDialog() {
        val dialog = CategoryLayoutDialog(binding.root.context, listLayoutType()) {
            changeLayout(it)
        }
        dialog.show()
    }

    private fun changeLayout(type: ListType) {
        viewModel.setSectionLayout(type)
        loadAndDisplayList()
        showLayoutStatus()
    }
}

enum class ListType(val title: String) {
    NORMAL (Constants.CAT_LIST_VIEW_NORM),
    COMPACT(Constants.CAT_LIST_VIEW_COMPACT),
    CARD(Constants.CAT_LIST_VIEW_CARD);
    companion object {
        fun from(text: String) : ListType {
            return when (text) {
                COMPACT.title -> COMPACT
                CARD.title -> CARD
                else -> NORMAL
            }
        }
    }
}
