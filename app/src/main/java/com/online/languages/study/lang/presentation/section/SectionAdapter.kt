package com.online.languages.study.lang.presentation.section

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.online.languages.study.lang.R
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.databinding.SectionItemsListBinding
import com.online.languages.study.lang.databinding.SectionReviewListItemBiggerBinding
import com.online.languages.study.lang.databinding.SectionReviewListItemBinding
import com.online.languages.study.lang.tools.TopicIcons
import com.online.languages.study.lang.utils.gone
import com.online.languages.study.lang.utils.invisible
import com.online.languages.study.lang.utils.visible

class SectionReviewAdapter(
    private val items: MutableList<CategoryReviewUi>,
    private val isPlusVersion: Boolean = true,
    private val isCompact: Boolean = true,
    private val onEvent: (SectionUiEvent) -> Unit,
) : RecyclerView.Adapter<SectionReviewAdapter.ViewHolder>() {

    fun updateItem(position: Int, updatedItem: CategoryReviewUi) {
        items[position] = updatedItem
        notifyItemChanged(position)
    }

    inner class ViewHolder(val binding: SectionItemsListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind( category: CategoryReviewUi,  onEvent: (SectionUiEvent) -> Unit ) {
            binding.apply {
                 sectionListTitle.text = category.title

                if (!isPlusVersion && !category.data.unlocked) {
                    sectionListImg.setImageResource(R.drawable.ic_lock)
                    sectionListHeader.setOnClickListener {
                       onEvent(SectionUiEvent.OpenPremium)
                    }
                } else {
                    if (category.data.spec == "example") {
                         sectionListImg.gone()
                         sectionListHeader.background = null
                         sectionListTitle.setTypeface(null, Typeface.NORMAL)
                    } else {
                        sectionListImg.setImageResource(R.drawable.ic_arrow_forward)
                        sectionListHeader.setOnClickListener {
                            onEvent(SectionUiEvent.SectionClick(category.data))
                        }
                    }
                }

                val resourceId = TopicIcons().getIcon(category.data.image)
                if (category.data.image == "none") sectionListTitleIcon.gone()
                sectionListTitleIcon.setImageResource(resourceId)
            }

            val nestedAdapter = SectionNestedAdapter(category.items, isCompact, onEvent)
            binding.recyclerView.adapter = nestedAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SectionItemsListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onEvent)
    }

    override fun getItemCount(): Int = items.size
}




class SectionNestedAdapter(
    private val items: List<CategoryListDataUi>,
    private val isCompact: Boolean,
    private val onEvent: (SectionUiEvent) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class CompactViewHolder(val binding: SectionReviewListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryListDataUi, onEvent: (SectionUiEvent) -> Unit) {
            binding.itemText.text = category.item
            binding.itemInfo.text = category.info
            binding.catItemWrap.setOnClickListener {
                onEvent(SectionUiEvent.ListItemClick(category.data))
            }

            checkStarred(category.data)
        }

        private fun checkStarred( dataItem: DataItem ) {
            if (dataItem.starred == 1) {
                 binding.voclistStar.visible()
            } else {
                binding.voclistStar.invisible()
            }
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SectionReviewListItemBinding.inflate(layoutInflater, parent, false)
                return CompactViewHolder(binding)
            }
        }
    }

    class ViewHolder(val binding: SectionReviewListItemBiggerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryListDataUi, onEvent: (SectionUiEvent) -> Unit) {
            binding.itemText.text = category.item
            binding.itemInfo.text = category.info
            binding.catItemWrap.setOnClickListener {
                onEvent(SectionUiEvent.ListItemClick(category.data))
            }
            checkStarred(category.data)
        }

        private fun checkStarred( dataItem: DataItem ) {
            if (dataItem.starred == 1) {
                binding.voclistStar.visible()
            } else {
                binding.voclistStar.invisible()
            }
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SectionReviewListItemBiggerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COMPACT_VIEW_TYPE -> CompactViewHolder.from(parent)
            else -> ViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is  ViewHolder -> holder.bind(item, onEvent)
            is CompactViewHolder -> holder.bind(item, onEvent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isCompact) {
            COMPACT_VIEW_TYPE
        } else {
            NORMAL_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int = items.size

    companion object {
        private const val NORMAL_VIEW_TYPE = 1
        private const val COMPACT_VIEW_TYPE = 2
    }
}






//
//
//
//class NestedDataAdapter(private val data: List<String>) : RecyclerView.Adapter<NestedViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.nested_item_layout, parent, false)
//        return NestedViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
//        holder.bind(data[position])
//    }
//
//    override fun getItemCount(): Int = data.size
//}
//
//class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    private val textView: TextView = itemView.findViewById(R.id.nested_text_view)
//
//    fun bind(text: String) {
//        textView.text = text
//    }
//}