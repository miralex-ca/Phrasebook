package com.online.languages.study.lang.utils

import android.view.View
import androidx.core.text.HtmlCompat

fun <T> List<T>.safeSlice(maxSize: Int) : MutableList<T> {
    val limit = minOf(maxSize, size)
    return subList(0, limit).toMutableList()
}

fun String.asHTML() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun View.gone() {
    this.visibility = View.GONE
}
