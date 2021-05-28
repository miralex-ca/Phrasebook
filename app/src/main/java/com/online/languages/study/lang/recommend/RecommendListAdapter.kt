package com.online.languages.study.lang.recommend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.RoundedCornersTransformation
import com.online.languages.study.lang.adapters.RoundedTransformation
import com.online.languages.study.lang.data.ViewCategory
import com.online.languages.study.lang.databinding.*
import com.online.languages.study.lang.recommend.RecommendListAdapter.MyViewHolder
import com.online.languages.study.lang.recommend.TaskFragment.*
import com.squareup.picasso.Picasso
import java.util.*


open class RecommendListAdapter(context: Context?, var list: ArrayList<TaskItem>) : OnTaskListItemActions,
        RecyclerView.Adapter<MyViewHolder>() {

    var context: Context? = null

    init {
        this.context = context
    }


    class MyViewHolder : RecyclerView.ViewHolder {
        var taskNormalBinding: TaskListItemBinding? = null
        var taskSectionBinging: TaskSectionsGroupBinding? = null
        var taskErrorsItemBinding: TaskErrorsItemBinding? = null
        var taskInfoItemBinding: TaskInfoItemBinding? = null

        var taskSectionItemBinding: TaskListItemSectionBinding? = null


        constructor(binding: TaskListItemBinding) : super(binding.root) {
            taskNormalBinding = binding
        }

        constructor(binding: TaskSectionsGroupBinding) : super(binding.root) {
            taskSectionBinging = binding
        }

        constructor(binding: TaskErrorsItemBinding) : super(binding.root) {
            taskErrorsItemBinding = binding
        }

        constructor(binding: TaskListItemSectionBinding) : super(binding.root) {
            taskSectionItemBinding = binding
        }

        constructor(binding: TaskInfoItemBinding) : super(binding.root) {
            taskInfoItemBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding

        when (viewType) {
            2 -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.task_sections_group, parent, false)
                return MyViewHolder(binding as TaskSectionsGroupBinding)
            }
            3 -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.task_errors_item, parent, false)
                return MyViewHolder(binding as TaskErrorsItemBinding)
            }
            4 -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.task_list_item_section, parent, false)
                return MyViewHolder(binding as TaskListItemSectionBinding)
            }

            5 -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.task_info_item, parent, false)
                return MyViewHolder(binding as TaskInfoItemBinding)
            }
            else -> {
                binding = DataBindingUtil.inflate(inflater, R.layout.task_list_item, parent, false)
                return MyViewHolder(binding as TaskListItemBinding)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        var type = 1

        if (list[position].name == "explore_sections") type = 2

        if (list[position].id == "all_errors") type = 3

        if (list[position].type == "section") type = 4

        if (list[position].id == "tasks_info") type = 5

        return type
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = list[position]

        when (holder.itemViewType) {

            2 -> addSectionsWidget(holder.taskSectionBinging, task)

            3 -> addErrorsWidget(holder.taskErrorsItemBinding, task, position)

            4 -> addSectionTask(holder.taskSectionItemBinding!!, task, position)

            5 -> addInfoWidget(holder.taskInfoItemBinding!!, task, position)

            else -> addTask(holder.taskNormalBinding!!, task, position)

        }

    }

    private fun addInfoWidget(container: TaskInfoItemBinding?, task: TaskItem, position: Int) {

        container!!.btnTasksDetails.setOnClickListener { clickOnTask(ACTION_OPEN_INFO, position) }
        container.btnClearData.setOnClickListener { clickOnTask(ACTION_CLEAR_ALL_DATA, position) }
    }

    private fun addErrorsWidget(container: TaskErrorsItemBinding?, task: TaskItem, position: Int) {

        val taskInfo = String.format(context!!.getString(R.string.task_error_info), task.progress)

        container!!.tvProgress.text = taskInfo


        container.tvText3.text = task.name
        container.cardBox.setOnClickListener { clickOnTask(ACTION_SECTION, position) }

    }

    private fun addSectionTask(container: TaskListItemSectionBinding, task: TaskItem, position: Int) {

        if (task.status == "changed") {
            container.llBox.alpha = .0f
            container.llBox.animate().alpha(1.0f).setDuration(700).interpolator = AccelerateInterpolator()
        }

        val title = task.name

        container.tvText2.text = title

        container.cardBox.setOnClickListener { clickOnTask(ACTION_SECTION, position) }
        container.taskEdit.setOnClickListener { clickOnMenu(position) }

        container.btnSectionStats.setOnClickListener { clickOnTask(ACTION_SECTION_STATS, position) }
        container.btnSectionLink.setOnClickListener { clickOnTask(ACTION_SECTION, position) }

        container.taskSuggest.setOnClickListener { clickOnTask(ACTION_SUGGEST, position) }

        container.viewCompleted.visibility = View.GONE

        container.viewCompleted.visibility = View.VISIBLE

        val taskInfo = String.format(context!!.getString(R.string.task_section_info),
                task.sectionStats.masteredPercent, task.sectionStats.mastered, task.sectionStats.itemsCount)
        container.tvProgress.text = taskInfo

        if (task.sectionStats.masteredPercent < 2) {
            container.tvTaskAction.text = context!!.getString(R.string.task_start_section)
        }

        if (task.sectionStats.masteredPercent > 96) {
            container.tvTaskAction.text = context!!.getString(R.string.review_mastered_section)
        }


        Picasso.with(context)
                .load("file:///android_asset/pics/" + task.viewCategory?.image)
                .fit()
                .centerCrop()
                .transform(RoundedTransformation(0, 0))
                //.transform(RoundedCornersTransformation(12, 5))
                .into(container.sectionIcon)

    }

    private fun addTask(container: TaskListItemBinding, task: TaskItem, position: Int) {

        if (task.status.equals("changed")) {
            container.llBox.alpha = .0f
            container.llBox.animate().alpha(1.0f).setDuration(700).interpolator = AccelerateInterpolator()
        }

        setTitles(task, container)

        setTags(container, position)

        container.viewNew.visibility = View.GONE
        container.viewProgress.visibility = View.GONE
        container.viewCompleted.visibility = View.GONE

        var taskInfo = String.format(context!!.getString(R.string.task_info), task.progress, task.sectionTitle)

        container.tvProgress.text = taskInfo

        if (task.progress < Task.PROGRESS_MINIMUM) {
            container.viewNew.visibility = View.VISIBLE
        } else if (task.progress >= Task.PROGRESS_MINIMUM && task.progress <= Task.PROGRESS_MAXIMUM) {
            container.viewProgress.visibility = View.VISIBLE
        } else {
            container.viewCompleted.visibility = View.VISIBLE

            taskInfo = String.format(context!!.getString(R.string.task_revise_info), task.sectionsData.size)

            container.tvProgress.text = taskInfo
        }
    }

    private fun setTitles(task: TaskItem, container: TaskListItemBinding) {
        val title = task.name
        container.tvText.text = title
        container.tvText1.text = title
        container.tvText2.text = title
    }

    private fun setTags(container: TaskListItemBinding, position: Int) {

        container.cardBox.setOnClickListener { clickOnTask(ACTION_SECTION, position) }
        container.taskEdit.setOnClickListener { clickOnMenu(position) }

    }

    private fun addSectionsWidget(card: TaskSectionsGroupBinding?, task: TaskItem) {


        task.sectionsData.forEachIndexed { index, viewCategory ->

            var container = card!!.boxSection1

            if (index == 1) container = card.boxSection2
            if (index == 2) container = card.boxSection3

            if (index < 3) addSectionView(viewCategory, container)

        }

    }

    private fun addSectionView(viewCategory: ViewCategory, container: View) {
        Picasso.with(context)
                .load("file:///android_asset/pics/" + viewCategory.image)
                .fit()
                .centerCrop()
                .transform(RoundedCornersTransformation(12, 5))
                .into(container.findViewById<ImageView>(R.id.section_image))

        container.findViewById<TextView>(R.id.tv_text_section).text = viewCategory.title

        container.findViewById<View>(R.id.section_box).setOnClickListener {
            clickOnSection(viewCategory.id.toString())
        }

        container.findViewById<View>(R.id.section_box).visibility = View.VISIBLE

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun clickOnTask(action: String, taskPosition: Int) {

    }

    override fun clickOnMenu(taskPosition: Int) {

    }

    override fun clickOnSection(sectionId: String) {

    }
}