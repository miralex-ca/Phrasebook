package com.online.languages.study.lang.recommend

import android.content.Context

import com.online.languages.study.lang.data.*
import com.online.languages.study.lang.recommend.Task.Companion.TASK_REVISED_PREFIX

class RecommendData(context: Context) {

    var navStructure: NavStructure? = null
    var userStats: UserStats? = null
    var context: Context? = null
    var dataManager: DataManager? = null

    init {
        navStructure = DataManager(context).navStructure
        userStats = UserStats(context, navStructure)
        dataManager = DataManager(context)

        this.context = context
    }


    fun getCategoriesTasksBySectionId(sectionTask: TaskItem): ArrayList<TaskItem> {

        var taskItems: ArrayList<TaskItem> = ArrayList()

        val categories = getCategoriesBySectionId(sectionTask.id!!)

        val adapter = TaskIAdapter()

        categories.forEach {

           if (!it.type.equals("set")  && !it.spec.equals("items_list")) {
               taskItems.add(adapter.adaptCategoryToTask(it))
           }
        }

        val reviseItem = TaskItem("revise", 0, 1000)
            reviseItem.id = TASK_REVISED_PREFIX  + sectionTask.id

        taskItems.add(reviseItem)

        taskItems = getTasksStats(taskItems);

        //taskItems = getTasksTime(taskItems)


        return taskItems
    }



    fun getSectionsTasks(): ArrayList<TaskItem> {

        val sectionsList: ArrayList<Section> = getSectionsData()

        /// convert received items to tasks // TODO
        val adapter = TaskIAdapter()
        var sectionTasksList: ArrayList<TaskItem> = ArrayList()

        sectionsList.forEach {
            sectionTasksList.add(adapter.adaptSectionToTask(it))
        }


        sectionTasksList = getTasksStats(sectionTasksList);



        //sectionTasksList = getTasksTime(sectionTasksList)


        return sectionTasksList
    }




    fun getErrorsCount(): Int {

        return  userStats!!.userStatsData.errorsWords.size

    }

    fun getErrorsList(): ArrayList<DataItem>? {
        return  userStats!!.userStatsData.errorsWords
    }


    /// first function to get stats
    private fun getTasksStats(taskItems: ArrayList<TaskItem>): ArrayList<TaskItem> {

        val tasksData = ArrayList<TaskItem>()

        tasksData.addAll(dataManager?.dbHelper?.getTasksStats(taskItems)!!)

        return tasksData
    }

    fun getTasksTime(tasks: ArrayList<TaskItem>): ArrayList<TaskItem> {

        val tasksData = ArrayList<TaskItem>()

        tasksData.addAll( dataManager!!.dbHelper.getTasksTimeByTests(tasks) )

        return tasksData
    }


    fun getSectionsData(): ArrayList<Section> {

        val sections: ArrayList<Section>

        userStats!!.updateData()

        sections = userStats!!.userStatsData.sectionsDataList


        return sections
    }


    fun getCategoriesBySectionId(sectionId: String): ArrayList<ViewCategory> {

        val navSection = navStructure?.getNavSectionByID(sectionId)

        val viewSection = ViewSection(context, navSection, "root")

       if (viewSection.categories.size > 0) viewSection.getProgress()



        return viewSection.categories
    }



}