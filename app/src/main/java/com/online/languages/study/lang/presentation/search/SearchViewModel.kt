package com.online.languages.study.lang.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.repository.Repository
import com.online.languages.study.lang.repository.changeStarred
import com.online.languages.study.lang.repository.checkStarred
import com.online.languages.study.lang.repository.searchData
import com.online.languages.study.lang.utils.safeSlice
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: Repository,
) : ViewModel() {

    private var searchJob: Job? = null
    private var allSearchItemsList: List<SearchItem> = emptyList()
    private var maxListSize = INIT_MAX_SIZE
    private var searchQuery: String? = null

    fun getListToDisplay(): List<SearchItem> {
        val listToDisplay = allSearchItemsList.safeSlice(maxListSize)
        if (allSearchItemsList.size > listToDisplay.size) {
            val item = createLastItem(listToDisplay.size)
            listToDisplay.add(item)
        }
        return listToDisplay
    }

    private fun createLastItem(listSize: Int): SearchItem {
        val remainingItemsCount = allSearchItemsList.size - listSize
        val moreSize = remainingItemsCount.coerceAtMost(PAGE_SIZE)
        return SearchItem(id = SEARCH_ITEM_LOAD_MORE_ID,
            DataItem(
                moreSize.toString(),
                remainingItemsCount.toString()
            ), false)
    }

    fun searchData(query: String, callback: (List<SearchItem>) -> Unit) {
        resetListMaxSize()
        searchJob?.cancel()
        this.searchQuery = query
        searchJob = viewModelScope.launch {
            val data: List<DataItem> = if (query.isNotEmpty()) {
                repository.searchData(query)
            } else {
                emptyList()
            }
            allSearchItemsList = data.toSearchItems()
            callback(allSearchItemsList)
        }
    }

    fun increaseMaxListSize(sizeOfCurrentList: Int) {
        if (maxListSize < sizeOfCurrentList + PAGE_SIZE)
            maxListSize += PAGE_SIZE
    }

    private fun resetListMaxSize() {
        maxListSize = INIT_MAX_SIZE
    }

    fun checkStarred(callback: () -> Unit) {
        viewModelScope.launch {
            val list = repository.checkStarred(allSearchItemsList.map { it.data })
            allSearchItemsList = list.toSearchItems()
            callback()
        }
    }

    fun refreshData(callback: (List<SearchItem>) -> Unit) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val query = searchQuery
            val data: List<DataItem> = if (!query.isNullOrEmpty()) {
                repository.searchData(query)
            } else {
                emptyList()
            }

            if (data.isNotEmpty()) {
                allSearchItemsList = data.toSearchItems()
                callback(allSearchItemsList)
            }
        }
    }

    fun changeStarred(item: DataItem, callback: (Int) -> Unit) {
        viewModelScope.launch {
            val status = repository.changeStarred(item)
            val list = repository.checkStarred(allSearchItemsList.map { it.data })
            allSearchItemsList = list.toSearchItems()
            callback(status)
        }
    }

    private fun List<DataItem>.toSearchItems() : List<SearchItem> {
        return map {
            SearchItem(id = it.item, it, it.starred > 0)
        }
    }

    companion object {
        private const val INIT_MAX_SIZE = 15
        private const val PAGE_SIZE = 10
        const val SEARCH_ITEM_LOAD_MORE_ID = "last_item"
    }
}

data class SearchItem(val id: String, val data: DataItem, val starred: Boolean)

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}