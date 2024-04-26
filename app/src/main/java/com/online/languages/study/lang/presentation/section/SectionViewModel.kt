package com.online.languages.study.lang.presentation.section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.repository.CategoryReview
import com.online.languages.study.lang.repository.Repository
import com.online.languages.study.lang.repository.getCategoriesData
import com.online.languages.study.lang.repository.getSectionCompact
import com.online.languages.study.lang.repository.setSectionLayout
import kotlinx.coroutines.launch

class SectionViewModel(
    private val repository: Repository,
) : ViewModel() {

    private var sectionId: String? = null
    private val checkPlusVersion = repository.provideCheckPlusVersion()

    fun isPlusVersion() = checkPlusVersion.isPlusVersion()

    fun isSectionCompact() = repository.localSettings.getSectionCompact()
    fun setSectionLayout(position: Int) {
        val layout = if (position == 0) {
            Constants.CAT_LIST_VIEW_NORM
        } else {
            Constants.CAT_LIST_VIEW_COMPACT
        }
        repository.localSettings.setSectionLayout(layout)
    }

    fun setSectionId(id: String) {
        sectionId = id
    }

    fun getTitle() : String {
        val section = repository.appNavigation.structure.getNavSectionByID(sectionId)
        return section.title
    }

    fun getCategoriesData(callback: (List<CategoryReview>) -> Unit ) {
        viewModelScope.launch {
            sectionId?.let {
               val categories = repository.getCategoriesData(it)
                callback(categories)
            } ?: callback(emptyList())
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SectionViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SectionViewModel::class.java)) {
            return SectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}