package com.online.languages.study.lang.presentation.category.category_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.databinding.CategoryListItemCardBinding
import com.online.languages.study.lang.presentation.category.CategoryUiItem
import com.online.languages.study.lang.utils.ClickAction
import com.online.languages.study.lang.utils.gone
import com.online.languages.study.lang.utils.invisible
import com.online.languages.study.lang.utils.visible

class CategoryCardListAdapter(
    private val callBack: (ClickAction, CategoryUiItem) -> Unit,
) : ListAdapter<CategoryUiItem, RecyclerView.ViewHolder>(ListDiffCallBack()) {

    class CardViewHolder(val binding: CategoryListItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryUiItem, callBack: (ClickAction, CategoryUiItem) -> Unit) {
            binding.apply {
                itemText.text = item.text
                itemInfo.text = item.info
                playButton.tag = item.data

                if (!item.isSpeaking) {
                    playHolder.gone()
                    val params = starHolder.layoutParams as? RelativeLayout.LayoutParams
                    params?.removeRule(RelativeLayout.ABOVE)
                }

                itemGrammar.updateGrammar(item.grammar)
                itemTrans.updateTranscription(item.transcription)

                if (item.isShowStats) {
                     statusWrap.visible()
                    updateStats(item)
                } else {
                     statusWrap.gone()
                }
            }


            setOnClickListeners(callBack, item)
            checkStarred(item.data)
        }

        @SuppressLint("SetTextI18n")
        private fun TextView.updateTranscription(transcription: String?) {
            if (transcription != null) {
                visible()
                text = "[ $transcription ]"
            } else {
                gone()
            }
        }

        private fun TextView.updateGrammar(grammar: String?) {
            if (grammar != null) {
                visible()
                text = grammar
            } else {
                gone()
            }
        }

        private fun updateStats(item: CategoryUiItem) {
            binding.apply {
                statusKnown.gone()
                statusStudied.gone()
                statusUnknown.gone()

                if (item.isShowStats) {
                    if (item.data.rate > 2) {
                        statusStudied.visible()
                    } else if (item.data.rate > 0) {
                        statusKnown.visible()
                    } else {
                        statusUnknown.visible()
                    }

                    if (item.errors != null) {
                        errorsCount.visible()
                        errorsCount.text = item.errors.toString()
                    } else {
                        errorsCount.gone()
                    }
                }
            }
        }

        private fun setOnClickListeners(
            callBack: (ClickAction, CategoryUiItem) -> Unit,
            item: CategoryUiItem
        ) {
            binding.itemWrap.setOnClickListener {
                callBack(ClickAction.Click, item)
            }
            binding.itemWrap.setOnLongClickListener {
                callBack(ClickAction.LongClick, item)
                true
            }

            binding.changeStar.setOnClickListener {
                callBack(ClickAction.ClickStar, item)
            }

            binding.itemWrap.isHapticFeedbackEnabled = false
        }

        private fun checkStarred( dataItem: DataItem) {
            if (dataItem.starred == 1) {
                binding.voclistStar.visible()
            } else {
                binding.voclistStar.invisible()
            }
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CategoryListItemCardBinding.inflate(layoutInflater, parent, false)
                return CardViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is CardViewHolder) holder.bind(item, callBack)
    }

}
