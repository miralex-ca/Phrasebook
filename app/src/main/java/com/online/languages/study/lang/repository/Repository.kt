package com.online.languages.study.lang.repository

import android.content.Context
import android.util.Log
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.DBHelper
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.DataManager
import com.online.languages.study.lang.data.NavSection
import com.online.languages.study.lang.data.NavStructure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(val appContext: Context) {
    val localSettings = LocalSettings(appContext)
    val dbHelper = DBHelper(appContext)
    val dataManager = DataManager(appContext)

    val backgroundDispatcher = Dispatchers.IO

    var navSections: List<NavSection>? = null

    fun provideAppSettings() = localSettings.settings
}

fun Repository.getAppSettings() = localSettings.settings

suspend fun Repository.getNavSections(): List<NavSection> = withContext(backgroundDispatcher) {
    val navStructure: NavStructure =  dataManager.getNavStructure()
    navStructure.sections
}

suspend fun Repository.searchData(query: String): List<DataItem> =
    withContext(backgroundDispatcher) {
        val list = try {
            val navStructure: NavStructure = dataManager.getNavStructure()
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