package com.online.languages.study.lang.presentation.search

import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.R
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.databinding.SearchActivityBinding
import com.online.languages.study.lang.utils.ClickAction
import com.online.languages.study.lang.utils.asHTML
import com.online.languages.study.lang.utils.gone
import com.online.languages.study.lang.utils.visible
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface SearchViewController {
    fun unlockOpenDialog()
    fun checkStarred()
    fun refreshData()
}

interface SearchViewActions {
    fun openDialog(data: DataItem)
    fun changeStarredCallback(status: Int)
}

class SearchViewControllerImpl(
    private val binding: SearchActivityBinding,
    private val viewActions: SearchViewActions,
    private val viewModel: SearchViewModel,
    lifecycle: Lifecycle,
) : SearchViewController {
    private val coroutineScope = lifecycle.coroutineScope
    private var progressJob: Job? = null
    private lateinit var listAdapter: SearchListAdapter
    private var canOpenDialog = true

    companion object {
        private const val OPEN_DIALOG_LOCK_DURATION = 600L
        private const val PROGRESS_BAR_DURATION = 1000L
        private const val PROGRESS_BAR_EMPTY_QUERY_DURATION = 300L
        private const val MINIMUM_SEARCH_CHAR = 2
    }

    init {
        initSearchList()
        setSearchListener()
    }

    private fun setSearchListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length < MINIMUM_SEARCH_CHAR) {
                        clearListAndHideSearchCard()
                    } else {
                        searchByQuery(it)
                    }
                }
                return false
            }
        })
    }

    private fun clearListAndHideSearchCard() {
        listAdapter.submitList(emptyList())
        binding.searcTxt.gone()
        binding.card.gone()
    }

    private fun initSearchList() {
        listAdapter = SearchListAdapter { clickAction, searchItem ->
            when (clickAction) {
                ClickAction.LongClick -> listItemLongClicked(searchItem.data)
                ClickAction.ClickToLoadMore -> loadMore()
                else -> listItemClicked(searchItem.data)
            }
        }
        binding.recyclerView.adapter = listAdapter
    }

    private fun searchByQuery(query: String) {
        val delayLength =
            if (query.isEmpty()) PROGRESS_BAR_EMPTY_QUERY_DURATION else PROGRESS_BAR_DURATION
        showSearchProgress(delayLength)
        viewModel.searchData(query) { result ->
            displaySearchResult(query, result)
        }
    }

    private fun showSearchProgress(duration: Long) {
        binding.searchProgress.show()
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            delay(duration)
            hideSearchProgress()
        }
    }

    private fun hideSearchProgress() {
        binding.searchProgress.hide()
    }

    private fun displaySearchResult(query: String, result: List<SearchItem>) {
        submitListWithLimit()
        displaySearchCard(query, result.size)
    }

    private fun submitListWithLimit() {
        val list = viewModel.getListToDisplay()
        listAdapter.submitList(list)
    }

    private fun loadMore() {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        val sizeOfCurrentList = layoutManager.itemCount - 1
        viewModel.increaseMaxListSize(sizeOfCurrentList)
        submitListWithLimit()
    }

    private fun listItemClicked(item: DataItem) {
        if (canOpenDialog) {
            canOpenDialog = false
            coroutineScope.launch {
                delay(OPEN_DIALOG_LOCK_DURATION)
                unlockOpenDialog()
            }
            viewActions.openDialog(item)
        }
    }

    override fun unlockOpenDialog() {
        canOpenDialog = true
    }

    private fun listItemLongClicked(item: DataItem) {
        if (!Constants.PRO) return
        viewModel.changeStarred(item) {
            viewActions.changeStarredCallback(it)
            submitListWithLimit()
        }
    }

    override fun checkStarred() {
        viewModel.checkStarred {
            submitListWithLimit()
        }
    }

    override fun refreshData()  {
        viewModel.refreshData {
            submitListWithLimit()
        }
    }

    private fun displaySearchCard(query: String, size: Int) {
        if (size == 0) {
            val str: String = String.format(getText(R.string.no_search_result), query)
            binding.searcTxt.text = str.asHTML()
            binding.searcTxt.visible()
            binding.card.gone()
        } else {
            binding.searcTxt.gone()
            binding.card.visible()
        }
    }

    private fun getText(@StringRes textId: Int): String {
        val context = binding.root.context
        return context.resources.getString(textId)
    }
}

