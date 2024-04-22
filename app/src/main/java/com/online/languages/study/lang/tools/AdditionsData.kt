package com.online.languages.study.lang.tools

import com.online.languages.study.lang.data.DataObject

class AdditionsData {

    private val resourcesMap = HashMap<String, DataObject>()
    var pagesTagsList: Array<String> = emptyArray()

    var additionsList =  ArrayList<DataObject>()

    init {
        prepareData()
    }


    fun processData() {
        pagesTagsList.forEach {
            val found = resourcesMap[it]

            if (found != null)
                additionsList.add(found)
        }
    }

    private fun getAddition(title:String, desc: String, image: String, tag: String): DataObject {

        val data = DataObject()
        data.id = tag
        data.title = title
        data.text = desc
        data.image = image


        return data
    }


    fun prepareData() {
        resourcesMap["example"] = getAddition("Title example", "Example description", "cat/house.png", "")
        resourcesMap["alphabet_es_ru"] = getAddition("Испанский язык",
            "Основная информация об испранском языке", "cat/notes/text.png", "alphabet_es_ru")
    }



}