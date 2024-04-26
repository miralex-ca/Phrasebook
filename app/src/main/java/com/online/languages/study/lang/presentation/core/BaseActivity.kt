package com.online.languages.study.lang.presentation.core

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.online.languages.study.lang.App
import com.online.languages.study.lang.AppContainer
import com.online.languages.study.lang.utils.OpenActivity
import com.online.languages.study.lang.repository.getAppSettings
import com.online.languages.study.lang.utils.LocaleChangedReceiver

open class BaseActivity : AppCompatActivity() {
    lateinit var openActivity: OpenActivity
    lateinit var appSettings: SharedPreferences
    lateinit var appContainer: AppContainer
    private var br: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        br = LocaleChangedReceiver()
        val filter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        registerReceiver(br, filter)

        appContainer = (application as App).appContainer
        openActivity = appContainer.getOpenActivity(this)
        appSettings = appContainer.repository.getAppSettings()
    }

    override fun onDestroy() {
        unregisterReceiver(br)
        super.onDestroy()
    }
}