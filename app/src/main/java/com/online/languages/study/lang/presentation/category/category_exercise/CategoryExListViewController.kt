package com.online.languages.study.lang.presentation.category.category_exercise

import android.widget.TextView
import com.online.languages.study.lang.R
import com.online.languages.study.lang.databinding.FragmentTrainingBinding
import com.online.languages.study.lang.presentation.category.CategoryViewModel
import com.online.languages.study.lang.utils.visibleIf

interface CategoryExListViewController {
    fun setup(categoryId: String)
    fun getExercisesData()
}

interface CategoryExListViewActions {
    fun openExercise(position: Int)
}

class CategoryExListViewControllerImpl(
    private val binding: FragmentTrainingBinding,
    private val viewActions: CategoryExListViewActions,
    private val viewModel: CategoryViewModel
) : CategoryExListViewController {
    private var categoryId: String = ""

    override fun setup(categoryId: String) {
        this.categoryId = categoryId
        checkAudioTestDisplay()
        setClicks()
        getExercisesData()
    }

    override fun getExercisesData() {
        if (viewModel.displayTestResults()) {
            viewModel.getExercisesData(categoryId) {
                setTestsDesc()
                handleTestsData(it)
            }
        } else {
            setTestsDesc()
        }
    }

    private fun handleTestsData(testsData: List<Array<String>>) {
        replaceResultDesc(testsData[0], binding.prVocabResult)
        replaceResultDesc(testsData[1], binding.prPhrasesResult)
        replaceResultDesc(testsData[2], binding.prAudioResult)
    }

    private fun replaceResultDesc(params: Array<String>, textView: TextView) {
        if (params.size > 1 && params[2] == "replace") {
            textView.text = params[1]
        }
    }

    private fun setTestsDesc() {
        binding.apply {
            prVocabResult.setText(R.string.practice_vocab_test_desc)
            prPhrasesResult.setText(R.string.practice_translate_test_desc)
            prAudioResult.setText(R.string.practice_audio_test_desc)
        }
    }

    private fun checkAudioTestDisplay() {
        val displayAudioTest = viewModel.checkSpeaking()
        binding.audioTestBox.visibleIf(displayAudioTest)
    }

    private fun setClicks() {
        binding.apply {
            cardFlachcard.setOnClickListener { viewActions.openExercise(Exercises.Cards.ordinal) }
            cardTestVocabulary.setOnClickListener { viewActions.openExercise(Exercises.Translation.ordinal) }
            cardTestPhrases.setOnClickListener { viewActions.openExercise(Exercises.InverseTranslation.ordinal) }
            cardTestAudio.setOnClickListener { viewActions.openExercise(Exercises.Audio.ordinal) }
        }
    }
}

enum class Exercises {
    Cards,
    Translation,
    InverseTranslation,
    Audio
}


