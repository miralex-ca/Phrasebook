package com.online.languages.study.lang.presentation.category.category_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.databinding.CategoryListItemCompact2Binding
import com.online.languages.study.lang.databinding.CategoryListItemNormBinding
import com.online.languages.study.lang.presentation.category.CategoryUiItem
import com.online.languages.study.lang.utils.ClickAction
import com.online.languages.study.lang.utils.gone
import com.online.languages.study.lang.utils.invisible
import com.online.languages.study.lang.utils.visible

class CategoryListAdapter(
    private val callBack: (ClickAction, CategoryUiItem) -> Unit,
) : ListAdapter<CategoryUiItem, RecyclerView.ViewHolder>(ListDiffCallBack()) {
    private var layoutCompact = false

    fun setCompact(compact: Boolean) {
        layoutCompact = compact
    }

    class CompactViewHolder(binding: CategoryListItemCompact2Binding) : BaseViewHolder<CategoryListItemCompact2Binding>(binding) {
        override fun bind(item: CategoryUiItem, callBack: (ClickAction, CategoryUiItem) -> Unit) {
            binding.apply {
                itemText.text = item.data.item
                itemInfo.text = item.data.info

                if (item.isShowStats) {
                    statusWrap.visible()
                    updateStats(item,
                        statusKnown = statusKnown,
                        statusUnknown = statusUnknown,
                        statusStudied = statusStudied,
                        errorsCount = errorIcon
                    )
                } else {
                    statusWrap.gone()
                }
            }
            setOnClickListeners(callBack, item)
            checkStarred(item.data, binding.voclistStar)
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CategoryListItemCompact2Binding.inflate(layoutInflater, parent, false)
                return CompactViewHolder(binding)
            }
        }
    }

    class NormalViewHolder(binding: CategoryListItemNormBinding) : BaseViewHolder<CategoryListItemNormBinding>(binding) {
        override fun bind(item: CategoryUiItem, callBack: (ClickAction, CategoryUiItem) -> Unit) {
            binding.apply {
                itemText.text = item.data.item
                itemInfo.text = item.data.info
                itemGrammar.updateGrammar(item.grammar)

                if (item.isShowStats) {
                    statusWrap.visible()
                    item.errorsCount?.let { errorsCount.text = it }
                    updateStats(item,
                        statusKnown = statusKnown,
                        statusUnknown = statusUnknown,
                        statusStudied = statusStudied,
                        errorsCount = errorsCount
                    )
                } else {
                    statusWrap.gone()
                }
            }

            setOnClickListeners(callBack, item)
            checkStarred(item.data, binding.voclistStar)
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CategoryListItemNormBinding.inflate(layoutInflater, parent, false)
                return NormalViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
             COMPACT_VIEW_TYPE ->  CompactViewHolder.from(parent)
            else -> NormalViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is NormalViewHolder -> holder.bind(item, callBack)
            is CompactViewHolder -> holder.bind(item, callBack)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutCompact) {
             COMPACT_VIEW_TYPE
        } else {
             NORMAL_VIEW_TYPE
        }
    }

    companion object {
        private const val NORMAL_VIEW_TYPE = 1
        private const val COMPACT_VIEW_TYPE = 2
    }
}

class ListDiffCallBack : DiffUtil.ItemCallback<CategoryUiItem>() {
    override fun areItemsTheSame(oldItem: CategoryUiItem, newItem: CategoryUiItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CategoryUiItem, newItem: CategoryUiItem): Boolean {
        return oldItem == newItem
    }
}

abstract class BaseViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: CategoryUiItem, callBack: (ClickAction, CategoryUiItem) -> Unit)

    protected fun setOnClickListeners(
        callBack: (ClickAction, CategoryUiItem) -> Unit,
        item: CategoryUiItem
    ) {
        binding.root.setOnClickListener {
            callBack(ClickAction.Click, item)
        }
        binding.root.setOnLongClickListener {
            callBack(ClickAction.LongClick, item)
            true
        }
        binding.root.isHapticFeedbackEnabled = false
    }

    protected fun TextView.updateGrammar(grammar: String?) {
        if (grammar != null) {
            visible()
            text = grammar
        } else {
            gone()
        }
    }

    protected fun updateStats(
        item: CategoryUiItem,
        statusKnown: View,
        statusStudied: View,
        statusUnknown: View,
        errorsCount: View
    ) {
        statusKnown.gone()
        statusStudied.gone()
        statusUnknown.gone()

        if (item.isShowStats) {
            if (item.errors != null) {
                errorsCount.visible()
            } else {
                errorsCount.gone()
                if (item.data.rate > 2) {
                    statusStudied.visible()
                } else if (item.data.rate > 0) {
                    statusKnown.visible()
                } else {
                    statusUnknown.visible()
                }
            }
        }
    }

    protected fun checkStarred(dataItem: DataItem, starView: View) {
        if (dataItem.starred == 1) {
            starView.visible()
        } else {
            starView.invisible()
        }
    }
}