package com.online.languages.study.lang.recommend

interface OnTaskListItemActions {

    fun clickOnTask(action: String, taskPosition: Int)
    fun clickOnMenu(taskPosition: Int)
    fun clickOnSection(sectionId: String)

}