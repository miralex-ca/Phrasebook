package com.online.languages.study.lang.practice

import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.Constants.EX_AUDIO_TYPE
import com.online.languages.study.lang.Constants.EX_ORIG_TR
import kotlin.collections.ArrayList


class QuestManager(var listedQuestsGroup: ArrayList<ArrayList<QuestData>>) {

    companion object {
        const val PRACTICE_LIMIT = 20
        const val PRACTICE_COUNT_SHUFFLE = 0
    }

    var mainList = ArrayList<QuestData>()
    var exerciseType = 0



    var reviseGroupsList = ArrayList<ArrayList<QuestData>>()

    fun processData() {

        checkGroupsSort(listedQuestsGroup)

        sortGroupsList()

        collectMainList()

    }

    private fun sortGroupsList() {

        val groups = ArrayList<ArrayList<QuestData>>()

        for (group in listedQuestsGroup) {
            if (group.size > 0) groups.add(group)
        }

        groups.sortBy { getRequiredCounter(it[0]) }

        listedQuestsGroup.clear()
        listedQuestsGroup.addAll(groups)

    }


    private fun collectMainList() {


       val matrix = getGroupsCounts()


        for ((index, group) in listedQuestsGroup.withIndex()) {

            val items =  ArrayList(group.subList(0, matrix[index]))

            mainList.addAll( items.shuffled() )

        }



        addForReviseIfUnderLimit()

    }

    fun getGroupsCounts(): ArrayList<Int> {

        val listData: ArrayList<Int> = ArrayList()
        val listMatrix: ArrayList<Int> = ArrayList()

        listedQuestsGroup.forEach { item ->
            listData.add(item.size)
            listMatrix.add(0)
        }

        var i = 0
        while (true) {

            for (index  in listedQuestsGroup.indices ) {

                var size = listData[index]

                if (size > 10) size = 10

                listData[index] -= size
                listMatrix[index] += size

                checkRemainder(listData, listMatrix)

                if (totalQuestsCount(listMatrix) >= PRACTICE_LIMIT) break
            }

            if (totalQuestsCount(listMatrix) >= PRACTICE_LIMIT ) break
            if (totalQuestsCount(listData) <= 0 ) break

            i++
            if (i > 1000) break
        }


        return listMatrix

    }

    private fun totalQuestsCount(listMatrix: ArrayList<Int>): Int {
        var total = 0
        for (count in listMatrix) {
            total += count
        }
        return total
    }

    private fun checkRemainder(listData: ArrayList<Int>, listMatrix: ArrayList<Int>) {

        var remainder = PRACTICE_LIMIT - totalQuestsCount(listMatrix)

        if (remainder in 1..4) {

            for (index in listData.indices) {

                if (listData[index] > 0 && remainder > 0) {

                    var size = listData[index]
                    if (size > remainder)  size = remainder

                    listMatrix[index]  += size
                    listData[index] -=size

                    remainder -= size
                }

            }

        }
    }

    private fun collectMainList1() {

            var helperGroupsList = ArrayList<ArrayList<QuestData>>(listedQuestsGroup.size)

            for ((index, group) in listedQuestsGroup.withIndex()) {

                var size = 10

                if (group.size < size)  size = group.size

                val itemsFromGroup = ArrayList( group.subList(0, size))

                val remain = PRACTICE_LIMIT - (mainList.size + itemsFromGroup.size)


                if (remain in 1..5) {

                    group.subList(0, size).clear()

                    var addItemsCount = remain

                    if (remain > group.size) addItemsCount = group.size

                    val addItemsFromGroup = ArrayList( group.subList(0, addItemsCount))

                    itemsFromGroup.addAll(addItemsFromGroup)

                }

                mainList.addAll(itemsFromGroup.shuffled())

                if (mainList.size > PRACTICE_LIMIT) break

            }

        addForReviseIfUnderLimit()

    }

    private fun addForReviseIfUnderLimit() {
        if (mainList.size > PRACTICE_LIMIT) {
            mainList = ArrayList(mainList.subList(0, PRACTICE_LIMIT))
        } else {

            val remain = PRACTICE_LIMIT - mainList.size

            if (remain > 0) {
                mainList.addAll(collectMainListFromCompleted(reviseGroupsList, remain))
            }
        }
    }

    private fun collectMainListFromCompleted(groupsList: ArrayList<ArrayList<QuestData>>, remain: Int) : ArrayList<QuestData> {

        var finalList = ArrayList<QuestData>()

        var hasItems = true
        var counter = 0

        groupsList.shuffle()

        while (hasItems) {

            var items = "init"

            for (group in groupsList) {

                if (group.size > 0) {

                    val quest = group.removeFirst()
                    finalList.add(quest)

                    items = "has_items"

                } else {
                  if (items != "has_items")  items = "no_items"
                }
            }

            if (items == "no_items") hasItems = false

            counter ++
            if (counter > 1000 ) break
        }

        sortByExType(finalList)

        if (finalList.size > remain) finalList = ArrayList(finalList.subList(0, remain))

        finalList.shuffle()

        return  finalList

    }

    private fun checkLowestItemFromLists(firstRowQuests: ArrayList<QuestFromList>): Int {

        if (exerciseType == EX_ORIG_TR) firstRowQuests.sortBy { it.quest.countTr }
        if (exerciseType == EX_AUDIO_TYPE) firstRowQuests.sortBy { it.quest.countAudio }

       // Log.i("Quest", "quest: ${firstRowQuests[0].quest.correct}: ${firstRowQuests[0].quest.countTr} - ${firstRowQuests[0].quest.countAudio}")

        return firstRowQuests[0].listNum
    }


    private fun checkGroupsSort(listedQuestsGroup: ArrayList<ArrayList<QuestData>>?) {


        listedQuestsGroup?.forEach { list ->

           // Log.i("Quest", "quests list: " + list.size)

            if (list.size > 0 )  {

                val listMin = ArrayList<QuestData>()
                val listHigher = ArrayList<QuestData>()

                for (item in list) {

                    val count = getRequiredCounter(item)

                    if (count > PRACTICE_COUNT_SHUFFLE ) {
                        listHigher.add(item)
                    }
                    else {

                        listMin.add(item)
                    }

                }

                sortByExType(listMin)

                list.clear()
                list.addAll(listMin)


                reviseGroupsList.add(listHigher)


            }
        }
    }

    private fun sortByExType(list: java.util.ArrayList<QuestData>) {
        if (exerciseType == EX_ORIG_TR) list.sortBy { it.countTr }
        if (exerciseType == EX_AUDIO_TYPE) list.sortBy { it.countAudio }
    }

    private fun getRequiredCounter(quest:  QuestData) : Int {

        return if (exerciseType == EX_AUDIO_TYPE) quest.countAudio
               else quest.countTr
    }


    data class QuestFromList(val quest: QuestData, val listNum: Int)


}