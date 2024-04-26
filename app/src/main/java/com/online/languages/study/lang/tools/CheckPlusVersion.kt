package com.online.languages.study.lang.tools

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

import com.online.languages.study.lang.Constants

class CheckPlusVersion {
    private var plusVersion = false
    private var appSettings: SharedPreferences

    constructor(context: Context) : this(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
    )

    constructor(context: Context, appSettings: SharedPreferences) {
        this.appSettings = appSettings
        checkPlusVersion()
    }

    private fun checkPlusVersion() {
        plusVersion = appSettings.getBoolean(Constants.SET_VERSION_TXT, false)
        if (Constants.PRO) plusVersion = true
    }

    fun isPlusVersion(): Boolean {
        checkPlusVersion()
        return plusVersion
    }

    fun isNotPlusVersion(): Boolean {
        checkPlusVersion()
        return !plusVersion
    }


}