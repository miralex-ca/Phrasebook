package com.online.languages.study.lang.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.AdditionsListAdapter.MyViewHolder
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.DataObject
import com.online.languages.study.lang.databinding.*
import com.squareup.picasso.Picasso
import java.util.*

open class AdditionsListAdapter(context: Context?, var list: ArrayList<DataObject>) : OnListItemCallback,
        RecyclerView.Adapter<MyViewHolder>() {

    var context: Context? = null

    init {
        this.context = context
    }

    class MyViewHolder : RecyclerView.ViewHolder {
        var taskNormalBinding: AdditionListItemBinding? = null

        constructor(binding: AdditionListItemBinding) : super(binding.root) {
            taskNormalBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding

        when (viewType) {
            2 -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.addition_list_item, parent, false)
                return MyViewHolder(binding as AdditionListItemBinding)
            }

            else -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.addition_list_item, parent, false)
                return MyViewHolder(binding as AdditionListItemBinding)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        var type = 1

        if (list[position].id == "explore_sections") type = 2

        return type
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val task = list[position]

        when (holder.itemViewType) {

            2 -> addTask(holder.taskNormalBinding!!, task, position)

            else -> addTask(holder.taskNormalBinding!!, task, position)

        }
    }


    private fun addTask(container: AdditionListItemBinding, addition: DataObject, position: Int) {

        container.additionTitle.text = addition.title
        container.additionDescription.text = addition.text

        Picasso.with(context)
            .load("file:///android_asset/pics/" + addition.image)
            .fit()
            .centerCrop()
            .transform(RoundedCornersTransformation(8, 6))
            .into(container.additionImage)

        val data = DataItem()
        data.id = addition.id
        data.item = addition.title

        container.additionCard.setOnClickListener{
            callBack(data)
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun callBack(item: DataItem?) {
        //TODO("Not yet implemented")
    }




}