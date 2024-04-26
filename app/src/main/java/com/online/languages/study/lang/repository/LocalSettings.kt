package com.online.languages.study.lang.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.online.languages.study.lang.Constants

import com.online.languages.study.lang.repository.LocalSettings.Companion.SECTION_LIST_VIEW


class LocalSettings (val context: Context) {
    val settings: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    companion object {
        const val SECTION_LIST_VIEW = "section_list_layout"
    }
}

fun LocalSettings.getSectionCompact() : Boolean {
    val layout = settings.getString(SECTION_LIST_VIEW, Constants.CAT_LIST_VIEW_COMPACT)
    return layout == Constants.CAT_LIST_VIEW_COMPACT
}

fun LocalSettings.setSectionLayout(layout: String)  {
    with (settings.edit()) {
        putString(SECTION_LIST_VIEW, layout)
        apply()
    }
}


fun LocalSettings.getVocabCompact() = settings.getBoolean("vocab_compact", false)

fun LocalSettings.getTranscriptShow() = settings.getBoolean("transcript_show", true)
fun LocalSettings.getTranscriptShowVoc() = settings.getBoolean("transcript_show_voc", true)
fun LocalSettings.getShowHomeResults() = settings.getBoolean("set_home_results", true)



