package com.online.languages.study.lang.practice


import android.util.Log
import android.widget.Toast
import com.online.languages.study.lang.App
import com.online.languages.study.lang.Constants.EX_AUDIO_TYPE
import com.online.languages.study.lang.Constants.EX_ORIG_TR
import com.online.languages.study.lang.R
import com.online.languages.study.lang.tools.Computer

/*

we get a list of quest groups collected by topics and level

==== checkGroupsSort ====
We leave in each group only unstudied items (below or equal PRACTICE_COUNT_SHUFFLE)
Other items go into the list for revision which collects items from all topics and shuffle them

====== sortGroupsList ====
Groups are sorted by count of completed tasks (excluding base level tasks)

if we have unstudied groups are sorted based on the completion progress (more than 50% excluding base tasks)

=== collectMainList ========

- Items are collected from groups by portions (based on count UNSTUDIED_GROUP_ITEMS_ADDED)
- If there are not enough of unstudied items, they are added from the revision list


 */


class QuestManager(var listedQuestsGroup: ArrayList<ArrayList<QuestData>>) {

    companion object {
        const val PRACTICE_LIMIT = 20
        const val UNSTUDIED_GROUP_ITEMS_ADDED = 10
        const val PRACTICE_COUNT_SHUFFLE = 0
        const val UNSTUDIED_GROUP_PROGRESS_MIN = 50
        const val FILTER_BASE = "#base"
        const val FILTER_EASY = "#easy"
    }

    var unstudiedItemsAdded = UNSTUDIED_GROUP_ITEMS_ADDED
    var testTaskLimit = PRACTICE_LIMIT
    var mainList = ArrayList<QuestData>()
    var exerciseType = 0

    var unstudiedWithProgressMap = HashMap<String, Int> ()

    var unstudied = false

    var requiredLevel = 0

    val questDataList = ArrayList<QuestGroupData>()
    var reviseGroupsList = ArrayList<ArrayList<QuestData>>()

    var addUnstudied = false


    fun processData() {

        unstudiedItemsAdded = testTaskLimit/2

        // we're getting quests with stats grouped by topics

        checkGroupsSort(listedQuestsGroup)

        sortGroupsList()

        collectMainList()

        //Log.i("Quest", "quest main list size: ${ mainList.size }")

    }


    private fun sortGroupsList() {

       // questDataList.sortBy { getRequiredCounter(it.quests[0]) }

        if (unstudied) {

            questDataList.sortBy {it.completedProgress}

            questDataList.sortByDescending {it.progressCat}


        } else {
            questDataList.sortBy {it.completedCount}
        }

        listedQuestsGroup.clear()


        if (reviseGroupsList.size == 0 && questDataList.size > 0) {

            listedQuestsGroup.add( questDataList[0].quests)

        } else {

            questDataList.forEach{
                listedQuestsGroup.add(it.quests)
            }

        }

    }


    private fun collectMainList() {

       val matrix = getGroupsCounts()

        for ((index, group) in listedQuestsGroup.withIndex()) {

            val items =  ArrayList(group.subList(0, matrix[index]))

            items.shuffle()

            mainList.addAll( moveBaseToFront(items) )

        }

        addForReviseIfUnderLimit()

    }

    private fun moveBaseToFront(items: ArrayList<QuestData>): ArrayList<QuestData> {

        val sortedItems = ArrayList<QuestData>()

        items.forEach { item ->

            if (item.filter.contains(FILTER_BASE)) sortedItems.add(0, item )
            else sortedItems.add(item)
        }

        return sortedItems
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

                if (size > unstudiedItemsAdded) size = unstudiedItemsAdded

                listData[index] -= size
                listMatrix[index] += size

                checkRemainder(listData, listMatrix)

                if (totalQuestsCount(listMatrix) >= testTaskLimit) break
            }

            if (totalQuestsCount(listMatrix) >= testTaskLimit ) break
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

        var remainder = testTaskLimit - totalQuestsCount(listMatrix)

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

                val remain = testTaskLimit - (mainList.size + itemsFromGroup.size)


                if (remain in 1..5) {

                    group.subList(0, size).clear()

                    var addItemsCount = remain

                    if (remain > group.size) addItemsCount = group.size

                    val addItemsFromGroup = ArrayList( group.subList(0, addItemsCount))

                    itemsFromGroup.addAll(addItemsFromGroup)

                }

                mainList.addAll(itemsFromGroup.shuffled())

                if (mainList.size > testTaskLimit) break

            }

        addForReviseIfUnderLimit()

    }

    private fun addForReviseIfUnderLimit() {

        if (mainList.size < 5) {
            mixStudiedWithUnstudied()
        }


        if (mainList.size > testTaskLimit) {

            mainList = ArrayList(mainList.subList(0, testTaskLimit))

        } else {

            val remain = testTaskLimit - mainList.size

            if (remain > 0) {
                mainList.addAll(collectMainListFromCompleted(reviseGroupsList, remain))
            }
        }


    }

    private fun mixStudiedWithUnstudied() {

        if (!unstudied) addUnstudied = true

       // Toast.makeText(App.getAppContext(), "Mix with unstudied", Toast.LENGTH_LONG).show()
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
    /// sort by count in groups

        val questDataArray = ArrayList<QuestGroupData>()

        listedQuestsGroup?.forEach { list ->

            //Log.i("Quest", "quests list: " + list.size)



            if (list.size > 0 )  {

                val listMin = ArrayList<QuestData>()
                val listHigher = ArrayList<QuestData>()

                var countNotBaseTasks = 0
                var countNotBaseTasksCompleted = 0
                var completedCount = 0

                for (item in list) {



                    val count = getRequiredCounter(item)

                   if (!item.filter.contains(FILTER_BASE)) {

                       completedCount += count
                       countNotBaseTasks ++

                       if (count > 0) countNotBaseTasksCompleted ++

                   }

                    if (count > PRACTICE_COUNT_SHUFFLE ) {

                        if (levelToRevise(item))

                            listHigher.add(item)

                    } else {

                        listMin.add(item)


                    }
                }

               // listMin = checkListForLevel(listMin)

                sortByExType(listMin)

                list.clear()
                list.addAll(listMin)

                if (listMin.size > 0) {
                    val groupData = QuestGroupData(listMin, completedCount)
                    groupData.id = listMin[0].categoryId
                    groupData.progressCat = unstudiedWithProgressMap.getOrElse(groupData.id) {0}

                    val progress = Computer.calculatePercent(countNotBaseTasksCompleted, countNotBaseTasks )
                    if (progress > UNSTUDIED_GROUP_PROGRESS_MIN) groupData.completedProgress = progress

                    questDataArray.add(groupData)
                }

               if (listHigher.size > 0) reviseGroupsList.add(listHigher)

            }
        }

        questDataList.clear()
        questDataList.addAll(questDataArray)

    }

    private fun levelToRevise(item: QuestData): Boolean {

        var revise = true

        val lowerLevel = requiredLevel-2



        if (! item.level.contains("l" + requiredLevel + "l")) {

            if  (item.filter.contentEquals(FILTER_EASY) && getRequiredCounter(item) > 1 ) revise = false

            //Log.i("Quest", "quest ${item.quest} - ${item.level} + $requiredLevel")

          // if (requiredLevel > 2 && (item.levelGlobal == 1120) ) revise = false
          // if ( (item.levelGlobal == 1100 ) && getRequiredCounter(item) > 2 ) revise = false


        }

        return revise
    }


    private fun checkListForLevel(questList: ArrayList<QuestData>): ArrayList<QuestData> {

        val cutByLevel = ArrayList<QuestData>()

        questList.forEach {

            val matchesLevel = checkQuestLevel(it)

            if (matchesLevel) {
                cutByLevel.add(it)
            }
        }

        return cutByLevel
    }


    private fun checkQuestLevel(quest: QuestData): Boolean {

        val levels = getRequiredLevels()

        var matchesLevel = false

        levels.forEach{ level ->

            //Log.i("Quests", "level $level")
            //Log.i("Quests", "level qu ${quest.levelGlobal}")

            if ( quest.levelGlobal in level) {

                matchesLevel = true

            }
        }

        return matchesLevel
    }




    private fun sortByExType(list: java.util.ArrayList<QuestData>) {
        if (exerciseType == EX_ORIG_TR) list.sortBy { it.countTr }
        if (exerciseType == EX_AUDIO_TYPE) list.sortBy { it.countAudio }
    }

    private fun getRequiredCounter(quest:  QuestData) : Int {

        return if (exerciseType == EX_AUDIO_TYPE) quest.countAudio
               else quest.countTr
    }

    private fun getRequiredLevels(): ArrayList<IntRange> {

        val levels = ArrayList<IntRange>()

        when (requiredLevel) {

            0 -> {
                levels.add(0..10000)
            }

            1 -> {
                levels.add(1100..1150)
            }

            2 -> {
                levels.add(1120..1120)
                levels.add(1200..5000)
            }

            else -> levels.add(0..2000)

        }

        return levels
    }

    private fun printQuestGroupData(list: ArrayList<QuestGroupData>) {

        Log.i("Quest", "List")

        list.forEach{

            val id = it.quests[0].categoryId

            Log.i("Quest", "group $id size ${it.quests.size}, completed ${it.completedCount} progress ${it.completedProgress}%")

        }


    }


    data class QuestFromList(val quest: QuestData, val listNum: Int)

    data class QuestGroupData(val quests: ArrayList<QuestData>, var completedCount: Int) {

        var completedProgress = 0
        var progressCat = 0
        var id = ""
    }


}