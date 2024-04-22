package com.online.languages.study.lang.repository

import android.content.Context
import com.online.languages.study.lang.DBHelper

class Repository(val appContext: Context) {
    val localSettings = LocalSettings(appContext)
    val dbHelper = DBHelper(appContext)

    fun provideAppSettings() = localSettings.settings
}

fun Repository.getAppSettings() = localSettings.settings