package com.online.languages.study.lang.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class LocalSettings (val context: Context) {
    val settings: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
}

fun LocalSettings.getVocabCompact() = settings.getBoolean("vocab_compact", false)
fun LocalSettings.getTranscriptShow() = settings.getBoolean("transcript_show", true)
fun LocalSettings.getTranscriptShowVoc() = settings.getBoolean("transcript_show_voc", true)
fun LocalSettings.getShowHomeResults() = settings.getBoolean("set_home_results", true)

