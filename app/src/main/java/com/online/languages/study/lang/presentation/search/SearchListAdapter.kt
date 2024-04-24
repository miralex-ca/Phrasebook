package com.online.languages.study.lang.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.R
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.databinding.SearchItemBinding
import com.online.languages.study.lang.databinding.SearchItemMoreBinding
import com.online.languages.study.lang.utils.ClickAction
import com.online.languages.study.lang.utils.gone
import com.online.languages.study.lang.utils.invisible
import com.online.languages.study.lang.utils.loadImage
import com.online.languages.study.lang.utils.visible


class SearchListAdapter(
    private val callBack: (ClickAction, SearchItem) -> Unit,
) : ListAdapter<SearchItem, RecyclerView.ViewHolder>(ListDiffCallBack()) {

    class ViewHolder(private val binding: SearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem, callBack: (ClickAction, SearchItem) -> Unit) {
            binding.apply {
                title.text = item.data.item
                desc.text = item.data.info
                listStarIcon.visibility = if (item.starred) View.VISIBLE else View.INVISIBLE
            }

            binding.root.isHapticFeedbackEnabled = false
            binding.root.setOnClickListener { callBack(ClickAction.Click, item) }

            if (item.data.isNote()) {
                binding.noteIcon.visible()
                val picsNotesFolder = binding.root.context.getString(R.string.notes_pics_folder)
                val imagePath = Constants.FOLDER_PICS + picsNotesFolder + item.data.image
                binding.image.loadImage(imagePath)
                binding.image.visible()
            } else {
                binding.noteIcon.gone()
                binding.image.invisible()
            }

            if (item.data.isInfo()) {
                binding.iIcon.visible()
                binding.listStarIcon.invisible()
            } else {
                binding.iIcon.gone()
            }
        }

        private fun DataItem.isInfo() = filter.contains(Constants.INFO_TAG)
        private fun DataItem.isNote() = filter.contains(Constants.NOTE_TAG)

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class LastViewHolder(private val binding: SearchItemMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem, callBack: (ClickAction, SearchItem) -> Unit) {
            val resources = binding.root.context.resources
            val text = String.format(
                resources.getString(R.string.load_more),
                item.data.item.toInt(), item.data.info.toInt()
            )
            binding.openMoreTxt.text = text
            binding.openMore.setOnClickListener {
                callBack(ClickAction.ClickToLoadMore, item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchItemMoreBinding.inflate(layoutInflater, parent, false)
                return LastViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id == SearchViewModel.SEARCH_ITEM_LOAD_MORE_ID) {
            LAST_ITEM_VIEW_TYPE
        } else {
            NORMAL_ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LAST_ITEM_VIEW_TYPE -> LastViewHolder.from(parent)
            else -> ViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ViewHolder -> holder.bind(item, callBack)
            is LastViewHolder -> holder.bind(item, callBack)
        }
    }

    companion object {
        private const val NORMAL_ITEM_VIEW_TYPE = 1
        private const val LAST_ITEM_VIEW_TYPE = 2
    }
}

class ListDiffCallBack : DiffUtil.ItemCallback<SearchItem>() {
    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem == newItem
    }
}