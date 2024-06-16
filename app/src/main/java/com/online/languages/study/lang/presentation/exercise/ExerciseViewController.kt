package com.online.languages.study.lang.presentation.exercise


import com.online.languages.study.lang.databinding.ActivityExerciseBinding

interface ExerciseViewController {
    fun setup(categoryId: String)
    fun getTitle(categoryId: String) : String
}

interface ExerciseViewActions {
}

class ExerciseViewControllerImpl(
    private val binding: ActivityExerciseBinding,
    private val viewModel: ExerciseViewModel,
    private val viewActions: ExerciseViewActions
) : ExerciseViewController {
    private var categoryId: String = ""


    override fun setup(categoryId: String) {
        this.categoryId = categoryId
    }

    override fun getTitle(categoryId: String) = viewModel.getTitle(categoryId)



}



