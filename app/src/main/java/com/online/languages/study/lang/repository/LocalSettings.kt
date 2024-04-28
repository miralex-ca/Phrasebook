package com.online.languages.study.lang.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.online.languages.study.lang.Constants

import com.online.languages.study.lang.repository.LocalSettings.Companion.SECTION_LIST_VIEW

class LocalSettings (val context: Context) {
    val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    companion object {
        const val SECTION_LIST_VIEW = "section_list_layout"
        const val TRANSCRIPTION_KEY =  "set_transript"
    }
}

fun LocalSettings.getSectionCompact() : Boolean {
    val layout = prefs.getString(SECTION_LIST_VIEW, Constants.CAT_LIST_VIEW_COMPACT)
    return layout == Constants.CAT_LIST_VIEW_COMPACT
}

fun Repository.setSectionLayout(layout: String)  {
    with (localSettings.prefs.edit()) {
        putString(SECTION_LIST_VIEW, layout)
        apply()
    }
}

fun Repository.setCategoryLayout(layout: String)  {
    with (localSettings.prefs.edit()) {
        putString(Constants.CAT_LIST_VIEW, layout)
        apply()
    }
}

fun Repository.getSpeakingParam() : Boolean {
    return localSettings.prefs.getBoolean("set_speak", true)
}

fun Repository.getCategoryLayout() : String {
    val default = Constants.CAT_LIST_VIEW_DEFAULT
    return localSettings.prefs.getString(Constants.CAT_LIST_VIEW, default) ?: default
}

fun Repository.getStatusDisplay() : Int {
    val default = Constants.STATUS_SHOW_DEFAULT
    val status = localSettings.prefs.getString("show_status", default)
        ?: default
    return status.toInt()
}

fun Repository.setTranscription(value: String) {
    with (localSettings.prefs.edit()) {
        putString(LocalSettings.TRANSCRIPTION_KEY, value)
        apply()
    }
    dataManager.checkAlternativeTranscription()
}


fun LocalSettings.getTranscriptShow() = prefs.getBoolean("transcript_show", true)
fun LocalSettings.getTranscriptShowVoc() = prefs.getBoolean("transcript_show_voc", true)
fun LocalSettings.getShowHomeResults() = prefs.getBoolean("set_home_results", true)



