package com.online.languages.study.lang.presentation.main.fragments.home

import com.online.languages.study.lang.data.NavSection
import com.online.languages.study.lang.databinding.FragmentHomeBinding
import com.online.languages.study.lang.utils.ListItemDataClickListener

interface HomeViewController {
    fun setup()
}

interface HomeViewActions {
    fun openSection(data: String)
}

class HomeViewControllerImpl(
    private val binding: FragmentHomeBinding,
    private val viewActions: HomeViewActions,
    private val viewModel: HomeViewModel,
) : HomeViewController, ListItemDataClickListener {

    private lateinit var listAdapter: HomeCardRecycleAdapter

    override fun setup() {
        initHomeList()
    }

    override fun onItemClick(data: String) {
        viewActions.openSection(data)
    }

    private fun initHomeList() {
         viewModel.getSections {
             submitListToAdapter(it.toHomeSectionUiItems())
        }
    }

    private fun submitListToAdapter(sections: List<HomeSectionUiItem>) {
        listAdapter = HomeCardRecycleAdapter(sections, this)
        binding.recyclerViewHome.adapter = listAdapter
    }
}

data class HomeSectionUiItem(
    val sectionId: String,
    val title: String,
    val image: String
)

private fun List<NavSection>.toHomeSectionUiItems(): List<HomeSectionUiItem> {
    return map {
        HomeSectionUiItem(
            sectionId = it.id,
            title = it.title,
            image = it.image
        )
    }
}

