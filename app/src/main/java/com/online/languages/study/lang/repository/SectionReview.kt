package com.online.languages.study.lang.repository

import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.NavCategory
import kotlinx.coroutines.withContext


suspend fun Repository.getCategoriesData(sectionId: String): List<CategoryReview> =
    withContext(backgroundDispatcher) {

        val navSection = appNavigation.structure.getNavSectionByID(sectionId)
        val uniqueCategories = navSection.uniqueCategories
        synchronized(Constants.DB_LOCK) {
            val groupsList = try {
                dbHelper.getSectionGroupsDataItems(navSection.uniqueCategories)
            } catch (e: Exception) {
                emptyList()
            }

            uniqueCategories.mapIndexed { index, category ->
                CategoryReview(category, groupsList[index])
            }
        }


    }

data class CategoryReview (
    val category: NavCategory,
    val dataItemsList:  List<DataItem>
)