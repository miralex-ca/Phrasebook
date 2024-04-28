package com.online.languages.study.lang.presentation.core

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.online.languages.study.lang.App
import com.online.languages.study.lang.AppContainer
import com.online.languages.study.lang.repository.getAppSettings
import com.online.languages.study.lang.utils.OpenActivity

open class BaseFragment : Fragment() {
    lateinit var appContainer: AppContainer
    lateinit var appSettings: SharedPreferences
    lateinit var openActivity: OpenActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myApplication: App = requireActivity().application as App
        appContainer  = myApplication.appContainer
        appSettings = appContainer.repository.getAppSettings()
        openActivity = appContainer.getOpenActivity(requireActivity())
    }

}