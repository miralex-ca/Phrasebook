package com.online.languages.study.lang.practice

import com.google.common.truth.Truth.assertThat
import com.online.languages.study.lang.data.DataItem
import org.junit.Test
import java.lang.Exception

class VocCollectManagerTest {

    // assert that some items are added if studied topics are empty



    // if studied topics empty, but we have mastered unstudied topics,
    // those mastered topics must be included in unstudied

    @Test
    fun mixIsEnabled_processData_finalListIncludeUnstudiedWhenStudiedMastered() {

        val SUT = VocCollectManager(ArrayList(), ArrayList() )
        SUT.mixStudiedAndUnknown = true

        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 3, "1001"))
        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1002"))

        SUT.studiedIds = arrayOf("1001")
        SUT.unStudiedIds = arrayOf("1002")

        SUT.processData()

        val finalList = SUT.mainListDataItems

        println("Final list when mix enabled: size: ${finalList.size}")

        assertThat(finalList.size).isGreaterThan(10)
    }

    @Test
    fun mixIsEnabled_studiedGroupsNotMastered_finalListNotIncludeUnstudied() {

        val SUT = VocCollectManager(ArrayList(), ArrayList() )
        SUT.mixStudiedAndUnknown = true

        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 3, "1001"))
        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1003"))
        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1002"))

        SUT.studiedIds = arrayOf("1001", "1003")
        SUT.unStudiedIds = arrayOf("1002")

        SUT.processData()

        val finalList = SUT.mainListDataItems

        println("Final list when mix enabled: size: ${finalList.size}")

        assertThat(finalList.size).isLessThan(21)
    }

    @Test
    fun mixIsNotEnabled_processData_finalListNotIncludeUnstudiedWhenStudiedMastered() {

        val SUT = VocCollectManager(ArrayList(), ArrayList() )
        SUT.mixStudiedAndUnknown = false

        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 3, "1001"))
        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1002"))

        SUT.studiedIds = arrayOf("1001")
        SUT.unStudiedIds = arrayOf("1002")

        SUT.processData()

        val finalList = SUT.mainListDataItems

        println("Final list when mix disabled: size: ${finalList.size}")

        assertThat(finalList.size).isLessThan(11)
    }





    @Test
    fun emptyStudiedGroupsWithMasteredUnstudied_processData_finalListIncludeMastered() {

        val SUT = VocCollectManager(ArrayList(), ArrayList() )

        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1001"))
        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 4, "1002"))

        SUT.unStudiedIds = arrayOf("1001", "1002")

        SUT.processData()

        val finalList = SUT.mainListDataItems

        println("Empty studied groups: final list size: ${finalList.size}")

        assertThat(finalList.size).isGreaterThan(10)
    }


    @Test
    fun emptyStudiedGroups_processData_finalListNotEmpty() {

        val SUT = VocCollectManager(ArrayList(), ArrayList() )

        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 1, "1001"))
        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1002"))
        SUT.unstudiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1003"))

        SUT.unStudiedIds = arrayOf("1001", "1002", "1003")

        SUT.processData()

        val finalList = SUT.mainListDataItems

        println("Empty studied groups: final list size: ${finalList.size}")

        assertThat(finalList).isNotEmpty()

    }

    @Test
    fun emptyUnStudiedGroups_processData_finalListNotEmpty() {

        val SUT = VocCollectManager(ArrayList(), ArrayList() )

        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 1, "1001"))
        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1002"))
        SUT.studiedCatsItems.addAll(itemsListWithCountAndProgress(10, 0, "1003"))

        SUT.studiedIds = arrayOf("1001", "1002", "1003")

        SUT.processData()

        val finalList = SUT.mainListDataItems

        println("Empty unstudied groups: final list size: ${finalList.size}")

        assertThat(finalList).isNotEmpty()

    }




    /// helpers ------------------------------------------------------------

    private fun itemsListWithCountAndProgress(count: Int, progress: Int, id: String): ArrayList<DataItem> {

        val list = initItemsArrayWithCount(count, id)

        changeItemsListRate(list, progress)

        return list

    }


    private fun initItemsArrayWithCount(count: Int, groupId: String) : ArrayList<DataItem> {

        val list: ArrayList<DataItem> = ArrayList()

        val item = DataItem()

        for (i in 0 until count) {
            item.id  = groupId+"_${i}"
            list.add(item)
        }

        return list
    }


    private fun changeItemsListRate(list: ArrayList<DataItem>, rate: Int ) {

        for (item in list) {
            item.rate = rate
        }

    }



}