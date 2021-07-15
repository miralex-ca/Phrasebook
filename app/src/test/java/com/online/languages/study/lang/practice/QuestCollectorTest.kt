package com.online.languages.study.lang.practice

import android.content.Context
import com.online.languages.study.lang.DBHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestCollectorTest {


    lateinit var dbHelper: DBHelper
    private var mockContext: Context? = null


    @Before
    fun setUp() {

        mockContext = RuntimeEnvironment.application.applicationContext
        dbHelper = DBHelper(mockContext)

    }

    @Test
    fun processData() {

        val studiedIds = arrayOf("100100010", "100100020")

        val SUT = QuestCollector(dbHelper, 0, 0)

        SUT.studiedIds = studiedIds

        SUT.processData()

       //  System.out.println("Size: ${SUT.mainList.size}")



    }
}