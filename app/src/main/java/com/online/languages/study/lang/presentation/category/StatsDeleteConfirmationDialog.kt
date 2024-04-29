package com.online.languages.study.lang.presentation.category

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.online.languages.study.lang.R

class StatsDeleteConfirmationDialog(
    private val context: Context,
    private val callback: () -> Unit
) {
    fun show() {
        val alert = AlertDialog.Builder(context)
            .setTitle(R.string.confirmation_txt)
            .setMessage(R.string.delete_stats_confirm_text)
            .setCancelable(true)
            .setPositiveButton(R.string.continue_txt){ dialog, _ ->
                dialog.cancel()
                callback()
            }
            .setNegativeButton(R.string.dialog_close_txt) { dialog, _ -> dialog.cancel() }
            .create()

        alert.show()
    }

}