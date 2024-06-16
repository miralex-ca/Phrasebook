package com.online.languages.study.lang.presentation.exercise

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.ColorProgress
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.presentation.category.category_list.ListType
import com.online.languages.study.lang.repository.*
import kotlinx.coroutines.launch


class ExerciseViewModel(
    private val repository: Repository,
) : ViewModel() {
//    private var allCategoryItemsList: List<CategoryUiItem> = emptyList()
//    private var listParams: ListParams = ListParams.from(repository)

    fun getTitle(sectionId: String) : String {

        return ""
    }









}









@Suppress("UNCHECKED_CAST")
class ExerciseViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}