package com.online.languages.study.lang.practice

import android.util.Log
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.Constants.EX_AUDIO_TYPE
import com.online.languages.study.lang.Constants.EX_ORIG_TR
import java.util.*
import kotlin.collections.ArrayList


class QuestManager(var listedQuestsGroup: ArrayList<ArrayList<QuestData>>) {

    companion object {
        const val PRACTICE_LIMIT = 20
        const val PRACTICE_COUNT_SHUFFLE = 0
    }

    var mainList = ArrayList<QuestData>()
    var exerciseType = 0

    fun processData() {

        checkGroupsSort(listedQuestsGroup)

        collectMainList()

    }

    private fun collectMainList() {


        var hasItems = true
        var counter = 0

        while (hasItems) {

            var items = "init"

            for (group in listedQuestsGroup) {

                if (group.size > 0) {

                    val quest = group.removeFirst()
                    mainList.add(quest)

                    items = "has_items"

                } else {
                  if (items != "has_items")  items = "no_items"
                }
            }

            if (items == "no_items") hasItems = false

            counter ++
            if (counter > 1000 ) break
        }





        sortByExType(mainList)

        //mainList.sortBy { it.countTr }

        if (mainList.size > PRACTICE_LIMIT) mainList = ArrayList(mainList.subList(0, PRACTICE_LIMIT))

        if (Constants.DEBUG) for (quest in mainList) Log.i("Quest", "quest: ${quest.correct} ${quest.countTr} - ${quest.countAudio}")

        mainList.shuffle()

        //sort items by category
        // mainList.sortBy { it.categoryId }

    }


    private fun checkLowestItemFromLists(firstRowQuests: ArrayList<QuestFromList>): Int {

        if (exerciseType == EX_ORIG_TR) firstRowQuests.sortBy { it.quest.countTr }
        if (exerciseType == EX_AUDIO_TYPE) firstRowQuests.sortBy { it.quest.countAudio }

       // Log.i("Quest", "quest: ${firstRowQuests[0].quest.correct}: ${firstRowQuests[0].quest.countTr} - ${firstRowQuests[0].quest.countAudio}")

        return firstRowQuests[0].listNum
    }


    private fun checkGroupsSort(listedQuestsGroup: ArrayList<ArrayList<QuestData>>?) {




        listedQuestsGroup?.forEach { list ->

            Log.i("Quest", "quests list: " + list.size)

            if (list.size > 0 )  {

                val listMin = ArrayList<QuestData>()
                val listHigher = ArrayList<QuestData>()

                for (item in list) {

                    var count = list[0].countTr
                    if (exerciseType == EX_AUDIO_TYPE)  count = list[0].countAudio

                    if (count > PRACTICE_COUNT_SHUFFLE ) listHigher.add(item)
                    else listMin.add(item)
                }

                sortByExType(listMin)
                listHigher.shuffle()

                list.clear()
                list.addAll(listMin)
                list.addAll(listHigher)

            }
        }
    }

    private fun sortByExType(list: java.util.ArrayList<QuestData>) {
        if (exerciseType == EX_ORIG_TR) list.sortBy { it.countTr }
        if (exerciseType == EX_AUDIO_TYPE) list.sortBy { it.countAudio }
    }


    data class QuestFromList(val quest: QuestData, val listNum: Int)


}