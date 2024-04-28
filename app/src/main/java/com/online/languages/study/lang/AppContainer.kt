package com.online.languages.study.lang

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.online.languages.study.lang.presentation.category.CategoryViewModel
import com.online.languages.study.lang.presentation.category.CategoryViewModelFactory
import com.online.languages.study.lang.utils.OpenActivity
import com.online.languages.study.lang.presentation.main.fragments.home.HomeViewModel
import com.online.languages.study.lang.presentation.main.fragments.home.HomeViewModelFactory
import com.online.languages.study.lang.presentation.section.SectionViewModel
import com.online.languages.study.lang.presentation.section.SectionViewModelFactory
import com.online.languages.study.lang.repository.Repository
import com.online.languages.study.lang.utils.Vibration

class AppContainer(val context: Context) {
    val repository = Repository(context)
    val vibration = Vibration(context)
    val models = ViewModelsHelper(this)

    fun getOpenActivity(context: Activity) =
        OpenActivity(context)
}

class ViewModelsHelper(private val appContainer: AppContainer) {

    private inline fun <reified VM : ViewModel> provideViewModel(owner: ViewModelStoreOwner, factory: ViewModelProvider.Factory): VM {
        return ViewModelProvider(owner, factory)[VM::class.java]
    }

    fun provideHomeViewModel(owner: ViewModelStoreOwner): HomeViewModel {
        val factory = HomeViewModelFactory(appContainer.repository)
        return provideViewModel(owner, factory)
    }

    fun provideSectionViewModel(owner: ViewModelStoreOwner): SectionViewModel {
        val factory = SectionViewModelFactory(appContainer.repository)
        return provideViewModel(owner, factory)
    }

    fun provideCategoryViewModel(owner: ViewModelStoreOwner): CategoryViewModel {
        val factory = CategoryViewModelFactory(appContainer.repository)
        return provideViewModel(owner, factory)
    }

//
//    fun provideExerciseViewModel(owner: ViewModelStoreOwner): ExerciseViewModel {
//        val factory = ExerciseViewModelFactory(appContainer.repository)
//        return provideViewModel(owner, factory)
//    }
}