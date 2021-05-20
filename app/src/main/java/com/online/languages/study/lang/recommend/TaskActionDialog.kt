package com.online.languages.study.lang.recommend

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.online.languages.study.lang.R
import com.online.languages.study.lang.databinding.TaskActionDialogBinding
import com.online.languages.study.lang.recommend.Task.Companion.TASK_TYPE_COMPLETED
import com.online.languages.study.lang.recommend.Task.Companion.TASK_TYPE_SECTION
import com.online.languages.study.lang.recommend.TaskFragment.*

open class TaskActionDialog(var context: Context, var task: TaskItem, var position: Int) : OnTaskDialogItemActions {

    var dialog: AlertDialog? = null
    var binding: TaskActionDialogBinding? = null


    fun createDialog() {

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialog)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.task_action_dialog, null)
        binding = TaskActionDialogBinding.bind(content)

        checkItemsDisplay(binding!!, task)

        setDialogItemsClicks()
        setItemsTexts()

        dialogBuilder.setCancelable(true)
        dialogBuilder.setView(binding!!.root)

        dialog = dialogBuilder.create()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
    }

    private fun setItemsTexts() {
        val priorityMsg = String.format(context.getString(R.string.downgrade_priority_section), task.sectionTitle)
        binding!!.tvDowngrade.text = priorityMsg

        val clearMsg = String.format(context.getString(R.string.tasks_clear_section), task.sectionTitle)
        binding!!.tvClearSection.text = clearMsg
    }


    private fun setDialogItemsClicks() {
        binding!!.downgradeSection.setOnClickListener { dismissDialog(ACTION_DOWNGRADE) }
        binding!!.clearSectionData.setOnClickListener { dismissDialog(ACTION_CLEAR_DATA) }
        binding!!.blockTask.setOnClickListener { dismissDialog(ACTION_BLOCK) }
        binding!!.postponeTask.setOnClickListener { dismissDialog(ACTION_SKIP) }
    }

    private fun checkItemsDisplay(binding: TaskActionDialogBinding, task: TaskItem) {

        if (task.type == TASK_TYPE_COMPLETED) binding.blockTask.visibility = View.GONE

        if (task.priority == -5) binding.downgradeSection.visibility = View.GONE

        if (task.type == TASK_TYPE_SECTION) binding.blockTask.visibility = View.GONE
    }

    private fun dismissDialog(action: String) {
        Handler().postDelayed({ callBack(action, position) }, 230)
        Handler().postDelayed({ if (dialog != null) dialog!!.dismiss() }, 80)
    }

    override fun callBack(action: String, taskId: Int) {}
}