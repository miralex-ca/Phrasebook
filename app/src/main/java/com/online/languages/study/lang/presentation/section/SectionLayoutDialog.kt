package com.online.languages.study.lang.presentation.section


import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.online.languages.study.lang.R


class SectionLayoutDialog (
   private val context: Context,
   private val isCompact: Boolean,
   private val callback: (Int) -> Unit
) {

    fun show() {
        var checkedItem = 0
        if (isCompact) checkedItem = 1

        val alert = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.set_section_layout_title))
            .setSingleChoiceItems(R.array.set_section_layout_list, checkedItem) { dialog, which ->
                callback(which)
                dialog.dismiss()
            }
            .setCancelable(true)
            .setNegativeButton(R.string.dialog_close_txt) { dialog, _ -> dialog.cancel() }
            .create()

        alert.show()

    }

}