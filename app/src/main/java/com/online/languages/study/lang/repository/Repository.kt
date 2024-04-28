package com.online.languages.study.lang.repository

import android.content.Context
import android.util.Log
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.DBHelper
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.DataManager
import com.online.languages.study.lang.data.NavCategory
import com.online.languages.study.lang.data.NavSection
import com.online.languages.study.lang.data.NavStructure
import com.online.languages.study.lang.tools.CheckPlusVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Repository(val appContext: Context) {
    val localSettings = LocalSettings(appContext)
    val dataManager = DataManager(appContext)
    val dbHelper: DBHelper = dataManager.dbHelper
    val appNavigation: AppNavigation = getAppNavigation(dataManager)

    val backgroundDispatcher = Dispatchers.IO

    var navSections: List<NavSection>? = null

    fun provideAppSettings() = localSettings.prefs
    fun provideCheckPlusVersion() = CheckPlusVersion(appContext)
}

private fun getAppNavigation(dataManager: DataManager) : AppNavigation {
    val structure = dataManager.provideNavStructure()
    return AppNavigation(
        structure = structure,
        categories = structure.getUniqueCats()
    )
}

class AppNavigation(
    val structure: NavStructure,
    val categories: List<NavCategory>
) {

}

fun Repository.getAppSettings() = localSettings.prefs

suspend fun Repository.getNavSections(): List<NavSection> = withContext(backgroundDispatcher) {
    val navStructure: NavStructure =  dataManager.getNavStructure()
    navStructure.sections
}

suspend fun Repository.getCategoryItems(categoryId: String): List<DataItem> =
    withContext(backgroundDispatcher) {
        synchronized(Constants.DB_LOCK) {
            val list = try {
                val resultList = dataManager.getCatDBList(categoryId)
                resultList
            } catch (e: Exception) {
                emptyList()
            }
            list
        }
    }

suspend fun Repository.getCategoryTestsData(categoryId: String): List<Array<String>> =
    withContext(backgroundDispatcher) {
        synchronized(Constants.DB_LOCK) {
            val list: List<Array<String>> = try {
                val testIds = arrayOf(categoryId + "_1", categoryId + "_2", categoryId + "_3")
                val results = dataManager.getCategoryTestsResult(testIds)
                results
            } catch (e: Exception) {
                emptyList()
            }
            list
        }
    }

suspend fun Repository.searchData(query: String): List<DataItem> =
    withContext(backgroundDispatcher) {
        val list = try {
            val navStructure = appNavigation.structure
            val data = dbHelper.searchData(navStructure.getUniqueCats(), query)
            val resultList = dbHelper.checkStarredList(data)
            resultList
        } catch (e: Exception) {
            emptyList()
        }

        list
    }

suspend fun Repository.checkStarred(list: List<DataItem>): List<DataItem> = withContext(backgroundDispatcher) {
    synchronized(Constants.DB_LOCK) {
        try {
            dbHelper.checkStarredList(ArrayList(list))
        } catch (e: Exception) {
            Log.e("Repo exception", "Repo error: ${e.message}")
            list
        }
    }
}

suspend fun Repository.changeStarred(item: DataItem): Int = withContext(backgroundDispatcher) {
    synchronized(Constants.DB_LOCK) {
        try {
            val starred = dbHelper.checkStarred(item.id)
            val status = dbHelper.setStarred(item.id, !starred)
            status
        } catch (e: Exception) {
            -1
        }
    }
}

fun Repository.getTranscription(dataItem: DataItem): String {
    return dataManager.getTranscriptFromData(dataItem)
}

fun Repository.updateTranscriptionParams() {
    dataManager.checkAlternativeTranscription()
}

