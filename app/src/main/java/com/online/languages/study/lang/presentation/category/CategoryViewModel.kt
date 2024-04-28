package com.online.languages.study.lang.presentation.category

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.ColorProgress
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.presentation.category.category_list.ListType
import com.online.languages.study.lang.repository.*
import kotlinx.coroutines.launch


class CategoryViewModel(
    private val repository: Repository,
) : ViewModel() {
    private var allCategoryItemsList: List<CategoryUiItem> = emptyList()
    private var listParams: ListParams = ListParams.from(repository)

    fun getLayout() = repository.getCategoryLayout()

    fun getListToDisplay(): List<CategoryUiItem> {
        return allCategoryItemsList
    }

    fun checkSpeaking() = repository.getSpeakingParam()

    fun getData(categoryId: String, callback: () -> Unit) {
        listParams.update()
        viewModelScope.launch {
            val list = repository.getCategoryItems(categoryId)
            setAllItemsList(list.toCategoryUiItems())
            callback()
        }
    }

    fun changeStarred(item: CategoryUiItem, callback: (Int) -> Unit) {
        viewModelScope.launch {
            val status = repository.changeStarred(item.data)
            val list = repository.checkStarred(allCategoryItemsList.toDataItems())
            setAllItemsList(list.toCategoryUiItems())
            callback(status)
        }
    }

    private fun setAllItemsList(list: List<CategoryUiItem>) {
        allCategoryItemsList = list
    }

    fun displayTestResults() = repository.getResultDisplayParam()

    fun getExercisesData(categoryId: String, callback: (List<Array<String>>) -> Unit) {
        viewModelScope.launch {
            val results = repository.getCategoryTestsData(categoryId)
            callback(results)
        }
    }

    private fun ListParams?.update() {
        listParams = ListParams.from(repository)
    }

    private fun List<DataItem>.toCategoryUiItems(): List<CategoryUiItem> {
        return map { dataItem ->
            CategoryUiItem(
                id = dataItem.info,
                text = dataItem.item,
                info = dataItem.info,
                grammar = dataItem.formattedGrammar(),
                transcription = dataItem.verifiedTranscription(),
                errors = dataItem.verifiedErrors(),
                errorsCount = dataItem.verifiedErrorsCount(),
                isSpeaking = listParams.speaking,
                data = dataItem,
                isStarred = dataItem.starred > 0,
                isShowStats = dataItem.verifiedStatsDisplay()
            )
        }
    }

    private fun DataItem.verifiedTranscription() : String? {
        return repository.getTranscription(this).takeIf { it.isNotBlank() }
    }

    private fun DataItem.verifiedErrors() : String? {
        return if (errors > 0) {
            String.format(repository.appContext.resources.getString(R.string.errors_count), errors)
        } else null
    }

    private fun DataItem.verifiedErrorsCount() : String? {
        return if (errors > 0) {
            errors.toString()
        } else null
    }

    private fun DataItem.formattedGrammar() : String? {
        val grammarValue = grammar.replace("n. ", "").replace(".", "")
        return if (listParams.displayGrammar && grammarValue.isNotEmpty()
            && grammarValue.length < listParams.grammarCharLimit) {
            grammarValue
        } else {
            null
        }
    }

    private fun DataItem.verifiedStatsDisplay() : Boolean {
        return when (listParams.showStatus) {
            1 -> rate > 0 || errors > 0
            2 -> true
            else -> false
        }
    }

    fun setSectionLayout(type: ListType) {
        repository.setCategoryLayout(type.title)
    }

}

private data class ListParams (
    val speaking: Boolean,
    val displayGrammar: Boolean,
    val grammarCharLimit: Int,
    val showStatus: Int,
    val colorProgress: ColorProgress
) {
    companion object {
        fun from(repository: Repository) : ListParams {
            repository.updateTranscriptionParams()
            val resources = repository.appContext.resources
            return ListParams(
                speaking = repository.getSpeakingParam(),
                displayGrammar =  resources.getBoolean(R.bool.display_grammar),
                grammarCharLimit = resources.getInteger(R.integer.card_grammar_limit),
                colorProgress = ColorProgress(repository.appContext),
                showStatus = repository.getStatusDisplay()
            )
        }
    }
}

private fun List<CategoryUiItem>.toDataItems(): List<DataItem> {
    return map { it.data }
}

data class CategoryUiItem(
    val id: String,
    val text: String,
    val info: String,
    val transcription: String?,
    val grammar: String?,
    val isStarred: Boolean,
    val isSpeaking: Boolean,
    val isShowStats: Boolean,
    val errors: String?,
    val errorsCount: String?,
    val data: DataItem,
    val colorIndex: Int? = null,
    val colorInt: Int? = null,
)

data class CategoryExUiSchema(
    var exercises: List<CategoryExUiItem>? = null,
    var revision: List<CategoryExUiItem>? = null,
)

data class CategoryExUiItem(
    val id: String,
    @StringRes
    val title: Int,
    @StringRes
    val desc: Int,
    val data: Int? = null,
    val orderNum: Int,
)

@Suppress("UNCHECKED_CAST")
class CategoryViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}