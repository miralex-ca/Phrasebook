package com.online.languages.study.lang.recommend

import android.content.Context
import android.os.Handler
import android.view.View
import com.online.languages.study.lang.Constants.PARAM_EMPTY
import com.online.languages.study.lang.R
import com.online.languages.study.lang.recommend.Task.Companion.TASK_REVISED_MIN_RESULT
import com.online.languages.study.lang.adapters.RoundedTransformation
import com.online.languages.study.lang.databinding.FragmentTaskBinding
import com.squareup.picasso.Picasso

class TaskCompleteDialog(var context: Context, var binding: FragmentTaskBinding, var expected: TaskItem, val task: TaskItem) {


    companion object {
        const val ICON_DONE = "task_done.png"
        const val ICON_GOOD = "task_good.png"
        const val ICON_INFO = "info.png"

        const val DIALOG_DISPLAY_TIME: Long = 2000
    }


    fun checkTask() {

        if ( hasProgress() ) notifyTask()

    }

    fun showInfo() {
        infoTask()
        notifyTask()
    }

    private fun hasProgress(): Boolean {

        var hasProgress = false

        if (task.progress > expected.progress) {

            val diff = task.progress - expected.progress

            if (task.progress in 96..100)  {
                completedTask()
                hasProgress = true
            }

            if (task.progress in 4..95)   {

                if ( task.progress < 50 && diff > 20 ) {
                    hasProgress = true
                    improvedTask()
                }

                if ( task.progress >= 50 && diff > 10 ) {
                    hasProgress = true
                    improvedTask()
                }

                if ( task.progress >= 85 && diff > 2 ) {
                    hasProgress = true
                    improvedTask()
                }

            }
        }

        if (task.id!!.contains("revise_")) {

            if (task.progress > TASK_REVISED_MIN_RESULT) {

               // Log.i("Tasks", "Task time:  ${task.timeCompleted} - ${expected.timeCompleted}")

                if (task.timeCompleted!! > expected.timeCompleted!!) {

                    completedTask()

                    setMessage(context.getString(R.string.dialog_complete_revised), task.sectionTitle!!)

                    if (task.progress > 90) setMessage(context.getString(R.string.dialog_complete_revised_msg), PARAM_EMPTY)

                    hasProgress = true
                }

            }
        }

        if (task.id!!.contains("all_errors")) {

            if (task.progress == 0) {
                hasProgress = true
                completedTask()
                setMessage(context.getString(R.string.dialog_complete_errors_corrected), PARAM_EMPTY)
            } else {
                hasProgress = false
            }
        }


        return hasProgress
    }


    private fun notifyTask() {

       // setMessage("Прогресс: ${expected.progress} - ${task.progress}")

        Handler().postDelayed({
            showDialog()
        }, 200)
    }


    private fun showDialog() {

        binding.rlNotificationBox.visibility = View.VISIBLE
        binding.rlNotificationBox.alpha = 0.0f

        binding.rlNotificationBox.animate().alpha(1.0f)

        Handler().postDelayed({ dismissDialog() }, DIALOG_DISPLAY_TIME)
        binding.cardNotification.setOnClickListener { dismissDialog() }

    }

    private fun infoTask() {

        val title = context.getString(R.string.task_msg_info)
        val text = "Progress: ${expected.progress} - ${task.progress}"
        val icon = ICON_INFO

        prepareMessage(title, text, PARAM_EMPTY, icon)

    }

    private fun improvedTask() {

        val title = context.getString(R.string.dialog_complete_title)
        val text = context.getString(R.string.dialog_completed_progress)
        val icon = ICON_GOOD
        prepareMessage(title, text, task.viewCategory!!.title, icon)

    }


    private fun completedTask() {

        val title = context.getString(R.string.dialog_complete_title)
        val text = context.getString(R.string.dialog_completed_tests_done)
        val icon = ICON_DONE

        prepareMessage(title, text, task.viewCategory!!.title, icon)

    }



    private fun prepareMessage(title: String, text: String, detail: String, icon: String) {
        setTitle(title)
        setMessage(text, detail)
        setIcon(icon)
    }


    fun setTitle(text: String) {
        binding.tvMsgTitle.text = text
    }

    fun setMessage(text: String, section: String) {
        binding.tvMsgText.text = text
       if (section != PARAM_EMPTY) {
           binding.tvMsgTextDetail.visibility = View.VISIBLE
           binding.tvMsgTextDetail.text = section
       }
        else binding.tvMsgTextDetail.visibility = View.GONE
    }

    private fun setIcon(icon: String) {
        Picasso.with(context)
                .load("file:///android_asset/pics/task/$icon")
                .fit()
                .centerCrop()
                .transform( RoundedTransformation(0,0))
                //.transform(RoundedCornersTransformation(12, 5))
                .into(binding.iconMsg)
    }


    private fun dismissDialog() {

        binding.rlNotificationBox.animate().alpha(0.0f).duration = 200

        Handler().postDelayed({
            binding.rlNotificationBox.visibility = View.GONE
         }, 230)
    }
}