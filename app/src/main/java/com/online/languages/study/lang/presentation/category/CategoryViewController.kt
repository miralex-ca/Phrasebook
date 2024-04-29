package com.online.languages.study.lang.presentation.category

import com.online.languages.study.lang.databinding.ActivityCatBinding

interface CategoryViewController {
    fun setup(categoryId: String)
    fun getTitle(categoryId: String) : String
}

interface CategoryViewActions {
}

class CategoryViewControllerImpl(
    private val binding: ActivityCatBinding,
    private val viewModel: CategoryViewModel,
    private val viewActions: CategoryViewActions
) : CategoryViewController {
    private var categoryId: String = ""


    override fun setup(categoryId: String) {
        this.categoryId = categoryId
    }

    override fun getTitle(categoryId: String) = viewModel.getTitle(categoryId)



}



