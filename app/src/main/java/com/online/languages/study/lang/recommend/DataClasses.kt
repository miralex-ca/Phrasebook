package com.online.languages.study.lang.recommend

import com.online.languages.study.lang.data.ViewCategory


data class TaskItem(val name: String, val time: Int, var progress: Int) {

    constructor(name: String, time: Int, progress: Int, id: String) : this(name, time, progress) {
        this.id = id
    }

    var viewCategory: ViewCategory = ViewCategory()

    var type: String = ""

    var id: String? = ""

    var sectionsData = ArrayList<ViewCategory>()

    var skippedTime: Long? = 0
    var priority: Int = 0
    var timeCompleted: Long? = 0

    var status: String? = "unknown"
    var sectionTitle: String? = ""

    var sectionStats = TaskSectionStats("init_stats")

}

data class TaskSectionStats(val sectionId: String) {

    var generalProgress = 0
    var testResult = 0
    var masteredPercent = 0
    var mastered = 0
    var familiar = 0
    var unknown = 0
    var itemsCount = 0

}




