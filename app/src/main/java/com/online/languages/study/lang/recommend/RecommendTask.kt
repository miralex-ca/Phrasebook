package com.online.languages.study.lang.recommend

import android.content.Context
import com.online.languages.study.lang.R
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.recommend.Task.Companion.TASK_TYPE_SECTION

class RecommendTask(context: Context) {

    private var recommendData: RecommendData? = null
    var context: Context? = null

    var expectedTaskItem = TaskItem("-10", 0, 0, "-111")
    var expectedTaskUpdated = TaskItem("-20", 0, 0, "-222")

    init {
        recommendData = RecommendData(context)
        this.context = context
    }

    fun resetExpected() {
        expectedTaskItem = TaskItem("", 0, 0, "-111")
        expectedTaskUpdated = TaskItem("", 0, 0, "-222")
    }


    fun getRecommendations(): ArrayList<TaskItem> {

        expectedTaskUpdated = expectedTaskItem

        return getTasksListFromCategories()

    }

    private fun getSortedCategoriesTasks(taskList: ArrayList<TaskItem>, sectionTaskItem: TaskItem): ArrayList<TaskItem> {

        val task = Task()

        task.expectedTaskItem = expectedTaskItem

        val sortCategoriesTasks = task.sortCategoriesTasks(taskList, sectionTaskItem)

        if (expectedTaskItem.id!! == task.expectedTaskUpdated.id) {

            expectedTaskUpdated = task.expectedTaskUpdated

           // Log.i("Expect", "ExpectUpd (Rec):  ${expectedTaskUpdated.id} - ${expectedTaskUpdated.progress}")

        }

        return sortCategoriesTasks

    }


    private fun getTasksListFromCategories(): ArrayList<TaskItem> {

        val sections = getTargetSections()  //// get ids for target sections, and list of all other sections

        val tasks = ArrayList<TaskItem>()

        for (sectionTask in sections.targetSectionsTasks) {

            val taskList = recommendData!!.getCategoriesTasksBySectionId(sectionTask) // with stats data

            val sortedTasks = getSortedCategoriesTasks(taskList, sectionTask)

            tasks.addAll(sortedTasks)

            if (tasks.size > 5) break

        }

        checkErrorsWidget(tasks)

        checkSectionsWidget(tasks, sections)

        checkInfoWidget(tasks)


      //  tasks = recommendData!!.getCategoriesTasksBySectionId(sectionId)

        return tasks

    }

    private fun checkSectionsWidget(tasks: ArrayList<TaskItem>, sections: TargetSections) {

        val suggestSections = ArrayList<TaskItem>()

         for (section in sections.targetSectionsTasks) {

            var found = false

            for (task in tasks) {
                if (task.type == TASK_TYPE_SECTION && task.id!! == section.id) {
                    found = true
                }
            }

             if (!found) {

                 suggestSections.add(section)

                 if (sections.suggestSections1.size < 3) {
                     sections.suggestSections1.add(section)
                 } else {
                     sections.suggestSections2.add(section)
                 }
             }

         }


        if (tasks.size > 3) {

            if (sections.suggestSections1.size > 1) {
                tasks.add(3, getSectionsWidgetItem(sections.suggestSections1))
            }

            if (sections.suggestSections2.size > 1) tasks.add(getSectionsWidgetItem(sections.suggestSections2))

        } else {

            if (sections.suggestSections2.size > 1) tasks.add(getSectionsWidgetItem(sections.suggestSections2))
        }
    }

    private fun checkErrorsWidget(tasks: ArrayList<TaskItem>): ArrayList<TaskItem> {

        val errorsCount = recommendData!!.getErrorsCount()

        if (errorsCount  > 4) {

            val errorTask = TaskItem(context!!.getString(R.string.task_errors_title), 0, errorsCount)
            errorTask.id = "all_errors"

            tasks.add(3, errorTask)
        }

        if (errorsCount == 0 && expectedTaskItem.id!! == "all_errors") {
            expectedTaskUpdated.progress = 0
        }

        return tasks
    }

    private fun checkInfoWidget(tasks: ArrayList<TaskItem>): ArrayList<TaskItem> {


            val infoTask = TaskItem("Info", 0, 0)
            infoTask.id = "tasks_info"
            tasks.add(infoTask)


        return tasks
    }




    fun getErrorsList(): ArrayList<DataItem>? {
        return recommendData!!.getErrorsList()
    }


    private fun getSectionsWidgetItem(list: ArrayList<TaskItem>): TaskItem {

        val taskItem = TaskItem("explore_sections", 0, 0)

        list.forEach {
                 taskItem.sectionsData.add(it.viewCategory!!)
        }

        return taskItem
    }


    private fun getTargetSections(): TargetSections {

        val ids = ArrayList<TaskItem>()

        val suggestSectionStart = ArrayList<TaskItem>()
        val suggestSectionEnd = ArrayList<TaskItem>()


       // val id: String = getSortedSectionsTasks().get(0).name

        for (it in getSortedSectionsTasks()) {

            ids.add(it)

        }

       // Collections.shuffle(sectionsTasks)

        return TargetSections(ids, suggestSectionStart, suggestSectionEnd)

    }


    private fun getSortedSectionsTasks(): ArrayList<TaskItem> { //// get sections

        val task = Task()

        return task.processSectionsList(recommendData!!.getSectionsTasks())

    }


    /////////// test data


    data class TargetSections(val targetSectionsTasks: ArrayList<TaskItem>,
                              val suggestSections1: ArrayList<TaskItem>,
                              val suggestSections2: ArrayList<TaskItem>
    )



}