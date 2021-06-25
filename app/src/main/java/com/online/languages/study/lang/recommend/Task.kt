package com.online.languages.study.lang.recommend

import java.util.*

class Task {

    var expectedTaskItem = TaskItem("", 0, 0, "init_expected")
    var expectedTaskUpdated = TaskItem("", 0, 0, "init_expected_update")

    companion object {
        const val PROGRESS_MINIMUM = 2
        const val PROGRESS_MAXIMUM = 95  /// minimum completed
        const val TASK_TYPE_COMPLETED = "completed"
        const val TASK_TYPE_SECTION = "section"
        const val TIME_RECENTLY_SKIPPED = 12 * 60 // in minutes
        const val TIME_RECENTLY_REVISED = 24*60 // in minutes
        const val TASK_REVISED_MIN_RESULT = 80
        const val TASK_REVISED_PREFIX = "revise_"
    }

    fun processSectionsList(dataList: ArrayList<TaskItem>): ArrayList<TaskItem> {
        return sortSectionsData(dataList)
    }

    private fun sortSectionsData(dataList: ArrayList<TaskItem>): ArrayList<TaskItem> { // used for sections

        val listInProgress: ArrayList<TaskItem>  = ArrayList()
        val listNew: ArrayList<TaskItem> = ArrayList()
        val listCompleted: ArrayList<TaskItem> = ArrayList()

        val sortedTaskItems: ArrayList<TaskItem> = ArrayList()

        val latest = checkLastCompleted(dataList)
        sortedTaskItems.add(latest)

        dataList.forEach {

            when {

                it.progress in PROGRESS_MINIMUM .. PROGRESS_MAXIMUM -> {
                    listInProgress.add(it)
                }

                it.progress > PROGRESS_MAXIMUM -> {
                    listCompleted.add(it)
                }
                it.progress < PROGRESS_MINIMUM -> {
                    listNew.add(it)
                }
            }
        }



        sortedTaskItems.addAll(listInProgress.sortedByDescending { it.timeCompleted })
        sortedTaskItems.addAll(listNew.sortedByDescending { it.timeCompleted })
        sortedTaskItems.addAll(listCompleted.sortedByDescending { it.timeCompleted })

        sortedTaskItems.sortByDescending { it.priority }

        return sortedTaskItems
    }

    private fun checkLastCompleted(dataList: ArrayList<TaskItem>) : TaskItem {

        val tasks = ArrayList<TaskItem>()
        tasks.addAll(dataList)

        tasks.sortByDescending { it.timeCompleted }

        val latestTask = tasks[0]

        val index = dataList.indexOf(latestTask)
        dataList.removeAt(index)

        return latestTask
    }

    fun sortCategoriesTasks(dataList: ArrayList<TaskItem>, sectionTask: TaskItem): ArrayList<TaskItem> { // sort cats for just one section, we get the list with stats

        val listInProgress: ArrayList<TaskItem>  = ArrayList()
        val listNew: ArrayList<TaskItem> = ArrayList()
        val listCompleted: ArrayList<TaskItem> = ArrayList()

        var revisedTask = TaskItem("", 0, 100)

        dataList.forEach {


            if (it.id.equals(expectedTaskItem.id)) {
                expectedTaskUpdated = it

            }

            if (it.id!!.contains(TASK_REVISED_PREFIX)) {

                revisedTask = it

                //Log.i("Tasks", "Task rest:  ${revisedTask.id} ")

            } else {

                when {
                    it.progress in PROGRESS_MINIMUM..PROGRESS_MAXIMUM -> {
                        it.type ="progress"
                        if (isVisible(it)) listInProgress.add(it)
                    }

                    it.progress < PROGRESS_MINIMUM -> {
                        it.type ="new"
                        if (isVisible(it)) listNew.add(it)
                    }

                    it.progress > PROGRESS_MAXIMUM -> {
                        it.type = TASK_TYPE_COMPLETED
                        listCompleted.add(it)
                    }

                }

            }

        }


        listInProgress.sortWith(compareByDescending<TaskItem> { it.timeCompleted }.thenByDescending { it.priority })
        listNew.sortByDescending { it.priority }


        return combineCategoriesTasks(listInProgress, listNew, listCompleted, revisedTask, sectionTask)
    }

    private fun isVisible(task: TaskItem) : Boolean {

        var visible = true

        if (recentlySkipped(task.skippedTime!!))  visible = false // checking time ago skipped

        if (task.priority == -10 ) visible = false

        if ( task.timeCompleted!! > task.skippedTime!!)  visible = true


         //Log.i("Task", "Time visible: ${task.name} -  $visible")

        return visible
    }

    private fun recentlySkipped(time: Long) : Boolean {

        val currentTime = System.currentTimeMillis()

        val difTime = currentTime - time

        var recently = false

        if (difTime < TIME_RECENTLY_SKIPPED * 60 * 1000)  recently = true

        return recently

    }



    private fun combineCategoriesTasks(listInProgress: ArrayList<TaskItem>,
                                       listNew: ArrayList<TaskItem>,
                                       listCompleted: ArrayList<TaskItem>,
                                       revisedTaskItem: TaskItem,
                                       sectionTask: TaskItem
    ): ArrayList<TaskItem> {

        val sortedTaskItems: ArrayList<TaskItem> = ArrayList()
        val progress = ArrayList<TaskItem>()
        val news = ArrayList<TaskItem>()


        val categoryTasksSize = 3

        // check new and progress categories and add maximum to the main list
        if (listInProgress.size >= categoryTasksSize) {

            if (listNew.size > 0) {
                progress.addAll(ArrayList(listInProgress.subList(0, categoryTasksSize - 1)))
                news.add(listNew[0])
            } else {

                progress.addAll(ArrayList(listInProgress.subList(0, categoryTasksSize)))
            }
        } else {
            // add all available cats in progress
            progress.addAll(listInProgress)

            /// add new cats as the rest of available space
            val restSpace = categoryTasksSize - progress.size

            //Log.i("Taskss", "Task rest:  $restSpace - ${news.size}")

            if (listNew.size > restSpace) {

                news.addAll(ArrayList(listNew.subList(0, restSpace)))
            } else {

                news.addAll(listNew)
            }
        }

        sortedTaskItems.addAll(progress)
        sortedTaskItems.addAll(news)

        if (sortedTaskItems.size == 0) {

            if (!recentlySkipped(sectionTask.skippedTime!!)) sortedTaskItems.add(sectionTask)

        } else {

            if (!recentlySkipped(revisedTaskItem.skippedTime!!)) {

                if (!recentlyRevised(revisedTaskItem)) {

                    sortedTaskItems.addAll(checkListCompleted(listCompleted, revisedTaskItem, sectionTask))
                }
            }

        }

        /// if the the max is not reached add topics (now 2) to revise

        return sortedTaskItems
    }

    private fun recentlyRevised(revisedTask: TaskItem): Boolean {

        val currentTime = System.currentTimeMillis()

        val difTime = currentTime - revisedTask.timeCompleted!!

        var recently = false

        if ( (difTime < TIME_RECENTLY_REVISED * 60 * 1000) && (revisedTask.progress > TASK_REVISED_MIN_RESULT)  )
            recently = true

        return recently

    }

    private fun checkListCompleted(tasks: ArrayList<TaskItem>, revisedTaskItem: TaskItem, sectionTask: TaskItem): ArrayList<TaskItem> {

        val list = ArrayList<TaskItem>()

        if ( tasks.size >= 3) {

            val completedTask = TaskItem(tasks[0].viewCategory!!.sectionTitle, 0, 100)
            completedTask.id = TASK_REVISED_PREFIX + tasks[0].viewCategory!!.sectionId
            completedTask.priority = sectionTask.priority


            completedTask.sectionsData = ArrayList()
            completedTask.type = TASK_TYPE_COMPLETED
            completedTask.sectionTitle = sectionTask.sectionTitle

            completedTask.skippedTime = revisedTaskItem.skippedTime
            completedTask.timeCompleted = revisedTaskItem.timeCompleted

            completedTask.sectionStats = sectionTask.sectionStats

            tasks.forEach { task ->
                completedTask.sectionsData.add(task.viewCategory!!)
                if (completedTask.viewCategory == null) completedTask.viewCategory = task.viewCategory!!
            }

            //Log.i("tasks", "Tasks: ${completedTask.sectionsData?.size}")

            list.add(completedTask)

        }


        return list
    }






}