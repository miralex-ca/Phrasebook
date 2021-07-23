package com.online.languages.study.lang.practice

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class QuestManagerFunTest {

    lateinit var SUT: QuestManager

    @Before
    fun setUp() {

        val list: ArrayList<ArrayList<QuestData>> = ArrayList()
        SUT = QuestManager(list)
        SUT.testTaskLimit  = 20
    }

    @Test
    fun checkGetGroupsCounts_standard_returnEqualCounts() {

        val list: ArrayList<ArrayList<QuestData>> = ArrayList()

        list.add(  initQuestArrayWithCount(3)  )
        list.add( initQuestArrayWithCount(10))

        SUT.listedQuestsGroup = list

        val matrix = SUT.getGroupsCounts()

        assertThat(matrix[0]).isEqualTo(3)
        assertThat(matrix[1]).isEqualTo(10)

    }


    @Test
    fun checkGetGroupsCounts_12_5_returnCounts() {

        val list: ArrayList<ArrayList<QuestData>> = ArrayList()

        list.add(  initQuestArrayWithCount(12)  )
        list.add( initQuestArrayWithCount(5))

        SUT.listedQuestsGroup = list

        val matrix = SUT.getGroupsCounts()

        assertThat(matrix[0]).isEqualTo(12)
        assertThat(matrix[1]).isEqualTo(5)


    }


    @Test
    fun checkGetGroupsCounts_12_4_5_returnCounts() {

        val list: ArrayList<ArrayList<QuestData>> = ArrayList()

        list.add(  initQuestArrayWithCount(18)  )
        list.add( initQuestArrayWithCount(9))
        list.add( initQuestArrayWithCount(4))

        SUT.listedQuestsGroup = list

        val matrix = SUT.getGroupsCounts()

        assertThat(matrix[0]).isEqualTo(11)
        assertThat(matrix[1]).isEqualTo(9)
        assertThat(matrix[2]).isEqualTo(0)

    }



    private fun initQuestArrayWithCount(count: Int) : ArrayList<QuestData> {

        val list: ArrayList<QuestData> = ArrayList()
        val quest = QuestData("", "", "")

        for (i in 0 until count) list.add(quest)

        return list
    }

}