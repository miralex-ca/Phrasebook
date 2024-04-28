package com.online.languages.study.lang.presentation.category


import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.online.languages.study.lang.R
import com.online.languages.study.lang.presentation.category.category_list.ListType


class CategoryLayoutDialog (
   private val context: Context,
   private val listType: ListType,
   private val callback: (ListType) -> Unit
) {
    fun show() {
        val checkedItem = ListType.values().indexOf(listType)

        val alert = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.set_section_layout_title))
            .setSingleChoiceItems(R.array.set_category_layout_list, checkedItem) { dialog, which ->
                callback(ListType.values()[which])
                dialog.dismiss()
            }
            .setCancelable(true)
            .setNegativeButton(R.string.dialog_close_txt) { dialog, _ -> dialog.cancel() }
            .create()
        alert.show()
    }

}