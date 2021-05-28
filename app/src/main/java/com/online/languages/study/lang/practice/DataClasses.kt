package com.online.languages.study.lang.practice

import java.lang.Integer.parseInt


data class QuestData(val quest: String, val correct: String, val options: String) {


    constructor(quest: String,  correct: String, options: String,
                id: String, categoryId: String, level: String,
                task: String, pronounce: String, image: String, filter: String  ) : this(quest, correct, options) {

        this.id = id
        this.categoryId = categoryId
        this.level = parseInt(level)
        this.task = task
        this.pronounce = pronounce
        this.image = image
        this.filter = filter


    }

    var level = 0
    var levelGlobal = 0
    var mode = 0

    var id: String = ""
    var categoryId = ""
    var task = ""
    var pronounce = ""
    var image = ""
    var filter = ""

    var countTr = 0
    var countAudio = 0

}






