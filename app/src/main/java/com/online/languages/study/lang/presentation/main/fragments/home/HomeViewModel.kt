package com.online.languages.study.lang.presentation.main.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.online.languages.study.lang.data.NavSection
import com.online.languages.study.lang.repository.Repository
import com.online.languages.study.lang.repository.getNavSections
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
) : ViewModel() {

    private var homeSectionList: List<NavSection> = emptyList()

    fun getSections(callback: (List<NavSection>) -> Unit) {
        if (homeSectionList.isEmpty()) {
           viewModelScope.launch {
               homeSectionList = repository.getNavSections()
               callback(homeSectionList)
           }
        } else {
            callback(homeSectionList)
       }
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}