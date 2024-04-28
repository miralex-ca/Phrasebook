package com.online.languages.study.lang.presentation.section

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.NavCategory
import com.online.languages.study.lang.databinding.ActivitySectionReviewListBinding
import com.online.languages.study.lang.repository.CategoryReview

interface SectionViewController {
    fun setup(sectionId: String)
    fun getTitle() : String
    fun getSectionList(data: String)
    fun showLayoutDialog()
    fun showLayoutStatus()
}

interface SectionViewActions {
    fun openCategory(data: NavCategory)
    fun openItem(data: DataItem)
    fun openPremium()
    fun applyLayoutStatus(isCompact: Boolean)
}

class SectionViewControllerImpl(
    private val binding: ActivitySectionReviewListBinding,
    private val viewModel: SectionViewModel,
    private val viewActions: SectionViewActions
) : SectionViewController {
    private var sectionId: String = ""
    private lateinit var sectionReviewAdapter: SectionReviewAdapter

    override fun setup(sectionId: String) {
        this.sectionId = sectionId
        viewModel.setSectionId(sectionId)
        initSectionList()
    }

    override fun getTitle() = viewModel.getTitle()

    private fun initSectionList() {
        viewModel.getCategoriesData {
            setupList(it.toSectionUi())
        }
    }

    private fun setupList(list: List<CategoryReviewUi>) {
        binding.categoriesList.itemAnimator?.changeDuration = 0
        submitListData(list)
    }

    private fun submitListData(list: List<CategoryReviewUi>) {
        sectionReviewAdapter = SectionReviewAdapter(
            items = list.toMutableList(),
            isPlusVersion = viewModel.isPlusVersion(),
            isCompact = viewModel.isSectionCompact(),
            onEvent = ::onListEvent
        )
        binding.categoriesList.adapter = sectionReviewAdapter
    }

    private fun onListEvent(event: SectionUiEvent) {
        when (event) {
            is SectionUiEvent.ListItemClick -> viewActions.openItem(event.item)
            SectionUiEvent.OpenPremium -> viewActions.openPremium()
            is SectionUiEvent.SectionClick -> viewActions.openCategory(event.category)
        }
    }

    override fun getSectionList(data: String) {
        viewModel.getCategoriesData { categoryReviews ->
            val list = categoryReviews.toSectionUi()
            list.find {
                it.id == data
            }?.let {
                val index = list.indexOf(it)
                sectionReviewAdapter.updateItem(index, it)
            }
        }
    }

    override fun showLayoutStatus() {
        viewActions.applyLayoutStatus(viewModel.isSectionCompact())
    }

    override fun showLayoutDialog() {
        val isCompact = viewModel.isSectionCompact()
        val dialog = SectionLayoutDialog(binding.root.context, isCompact) {
            changeLayout(it)
        }
        dialog.show()
    }

    private fun changeLayout(it: Int) {
        viewModel.setSectionLayout(it)
        binding.itemsList.animate().alpha(0.0F).setDuration(80).setListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    viewModel.getCategoriesData { list ->
                        setupList(list.toSectionUi())
                        binding.itemsList.alpha = 1.0F
                    }
                }
            }
        )
        showLayoutStatus()
    }
}

sealed interface SectionUiEvent {
    data class SectionClick(val category: NavCategory) : SectionUiEvent
    data class ListItemClick(val item: DataItem) : SectionUiEvent
    object OpenPremium : SectionUiEvent
}

data class CategoryReviewUi(
    val id: String,
    val title: String,
    val items: List<CategoryListDataUi>,
    val data: NavCategory
)

data class CategoryListDataUi(
    val id: String,
    val item: String,
    val info: String,
    val data: DataItem
)

private fun List<CategoryReview>.toSectionUi(): List<CategoryReviewUi> {
    return map {
        CategoryReviewUi(
            id = it.category.id,
            title = it.category.title,
            items = it.dataItemsList.toSectionUiItems(),
            data = it.category
        )
    }
}

private fun List<DataItem>.toSectionUiItems(): List<CategoryListDataUi> {
    return map {
        CategoryListDataUi(
            id = it.id,
            item = it.item,
            info = it.info,
            data = it
        )
    }
}

