package com.online.languages.study.lang.practice

class QuestLevels {


    val levels = ArrayList< Array<IntRange>>()

    init {
        getLevelsList()
    }


    fun getQuestLevel(requestedLevel: Int) : Array<IntRange> {

        var level = requestedLevel

        if (level >= levels.size) level =0

        return levels[level]

    }

    private fun getLevelsList() {

        val level0 = arrayOf(1000..11100)

        val level1 = arrayOf(1000..1120, 1100..2000)

        val level2 = arrayOf(2000..2000, 1100..2000)


        val levelsList = ArrayList< Array<IntRange>>()

        levelsList.add(level0)
        levelsList.add(level1)
        levelsList.add(level2)


        levels.addAll(levelsList)

    }

}