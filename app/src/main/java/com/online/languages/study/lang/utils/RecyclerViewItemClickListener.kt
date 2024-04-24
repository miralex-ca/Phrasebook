package com.online.languages.study.lang.utils

import android.view.View

interface RecyclerViewItemClickListener {
    fun onItemClick(view: View?, position: Int)
}

interface RecyclerViewItemClicksListener {
    fun onItemClick(view: View?, position: Int)
    fun onItemLongClick(view: View?, position: Int) { }
}

interface ListItemClicksListener {
    fun onItemClick(data: String, position: Int)
    fun onItemLongClick(data: String, position: Int) { }
}

enum class ClickAction {
    Click,
    LongClick,
    ClickToLoadMore
}

interface ListItemDataClickListener {
    fun onItemClick(data: String)
}