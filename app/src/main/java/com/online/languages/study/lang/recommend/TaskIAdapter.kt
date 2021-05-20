package com.online.languages.study.lang.recommend


import com.online.languages.study.lang.data.Section
import com.online.languages.study.lang.data.ViewCategory
import com.online.languages.study.lang.recommend.Task.Companion.TASK_TYPE_SECTION

class TaskIAdapter {

    fun adaptSectionToTask(section: Section): TaskItem {

        val task =  TaskItem(section.title, 0, section.testResults)

        task.type = TASK_TYPE_SECTION

        val viewCat = ViewCategory()
        viewCat.title = section.title
        viewCat.image = section.image
        viewCat.id = section.id

        task.viewCategory = viewCat
        task.id = section.id

       // Log.i("Task", "TaskAdapter: ${section.title_short} ${section.progress}%, words ${section.studiedDataCount} - ${section.allDataCount}")

        task.sectionTitle = section.title
        task.sectionStats = TaskSectionStats(section.id)
        task.sectionStats.generalProgress = section.progress
        task.sectionStats.testResult = section.testResults

        task.sectionStats.masteredPercent = section.studiedPart
        task.sectionStats.mastered = section.studiedDataCount
        task.sectionStats.itemsCount = section.allDataCount

        return task

    }


    fun adaptCategoryToTask(category: ViewCategory): TaskItem {

        val task = TaskItem(category.title, 0, category.progress)

        task.id = category.id
        task.viewCategory = category;

        task.sectionStats = TaskSectionStats( category.sectionId)
        task.sectionTitle = category.sectionTitle

        return task

    }


}