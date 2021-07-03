package com.online.languages.study.lang.practice

import android.util.Log
import com.online.languages.study.lang.data.DataItem
import kotlin.collections.ArrayList


class VocCollectManager(var studiedCatsItems: ArrayList<DataItem>, var unstudiedCatsItems: ArrayList<DataItem>) {

    companion object {
        const val PRACTICE_VOC_LIMIT = 30
        val PROGRESS_START_RANGE = 0..5
        const val PROGRESS_MASTERED_CAT = 85
        const val PROGRESS_MASTERED_CAT_STUDIED = 80

    }

    private var mixStudiedAndUnknown = true

    var mainListDataItems = ArrayList<DataItem>()

    var studiedIds: Array<String> = emptyArray()
    var unStudiedIds: Array<String> = emptyArray()

    private var unstudiedSortedItems: ArrayList<DataItem> = ArrayList()
    private var studiedSortedItems: ArrayList<DataItem> = ArrayList()

    private var prepareList = ArrayList<DataItem>()


    fun processData() {

        sortUnstudiedGroups()

        if (studiedCatsItems.size < 1) {

            prepareList.addAll(unstudiedSortedItems)

        } else {
            prepareList = sortStudiedGroups()
        }

        mainListDataItems.addAll(prepareList)

        mainListDataItems = cutListIfLimit(mainListDataItems, PRACTICE_VOC_LIMIT)

    }

    private fun cutListIfLimit(list: ArrayList<DataItem>, limit: Int): ArrayList<DataItem> {

        return if (list.size > limit) ArrayList(list.subList(0, limit))
        else list
    }


    private fun sortStudiedGroups(): ArrayList<DataItem> {

        val catsIDs = studiedIds
        val catsItems = studiedCatsItems

        val allCompleted = checkCatsForMasteredItems(catsIDs, catsItems) // check if some cats are not mastered

        Log.i("Quest", "quests: ${allCompleted}")

        sortStudiedByDateAndScore()
        studiedSortedItems.addAll(studiedCatsItems)

        return selectFinalList(allCompleted, studiedSortedItems)

    }

    private fun checkCatsForMasteredItems(catsIDs: Array<String>, catsItems: ArrayList<DataItem>): Boolean {

        var allCompleted = true

        val groups: ArrayList<ItemsGroup> = sortItemsInGroupsByIds(catsIDs, catsItems)
        groups.forEach { group -> calculateGroupProgress(group) }

        groups.forEach { if (Integer.valueOf(it.stats) < PROGRESS_MASTERED_CAT_STUDIED) allCompleted = false }

        return allCompleted
    }

    private fun selectFinalList(allCompleted: Boolean, sortedItemsList: ArrayList<DataItem>): ArrayList<DataItem> {

        var mixWithUnstudied = allCompleted

        if (!mixStudiedAndUnknown) mixWithUnstudied = false

        var sortedStudiedItemsList = sortedItemsList

        val finalLIst: ArrayList<DataItem> = ArrayList()

        val halfTestLimit = PRACTICE_VOC_LIMIT / 2


        if (mixWithUnstudied && (unstudiedSortedItems.size > halfTestLimit)) {

            sortedStudiedItemsList = cutListIfLimit(sortedStudiedItemsList, halfTestLimit)

            unstudiedSortedItems = cutListIfLimit(unstudiedSortedItems, PRACTICE_VOC_LIMIT - sortedStudiedItemsList.size)

            finalLIst.addAll(sortedStudiedItemsList.shuffled())
            finalLIst.addAll(unstudiedSortedItems)

        } else {

            finalLIst.addAll(sortedStudiedItemsList)
        }
        return finalLIst
    }

    private fun sortStudiedByDateAndScore() {

        studiedCatsItems.sortBy { it.id }
        studiedCatsItems.sortBy { it.time }
        studiedCatsItems.sortBy { it.rate }
    }


    private fun sortUnstudiedGroups() {

        val catsIDs = unStudiedIds
        val catsItems = unstudiedCatsItems
        val sortedItemsList = unstudiedSortedItems

        sortItemsByGroupsProgress(catsIDs, catsItems, sortedItemsList)

    }

    private fun sortItemsByGroupsProgress(catsIDs: Array<String>,
                                          catsItems: ArrayList<DataItem>,
                                          sortedItemsList: ArrayList<DataItem>) {

        val groups: ArrayList<ItemsGroup> = sortItemsInGroupsByIds(catsIDs, catsItems)

        val sortedGroups: ArrayList<ItemsGroup> = sortGroupsByItemsProgress(groups)

        sortedGroups.forEach {

            if (Integer.valueOf(it.stats) > 2) it.items = unknownToFrontAndShuffle( it.items )

            sortedItemsList.addAll(it.items)

        }
    }


    private fun unknownToFrontAndShuffle( list : ArrayList<DataItem>) : ArrayList<DataItem> {

        val unknownList : ArrayList<DataItem> = ArrayList()
        val knownList : ArrayList<DataItem> = ArrayList()

        list.forEach{

            if (it.rate == 0) unknownList.add(it)
            else knownList.add(it)
        }

        unknownList.shuffle()

        unknownList.addAll(knownList.shuffled())

        return unknownList

    }

    private fun sortItemsInGroupsByIds(catsIDs: Array<String>, catsItems: ArrayList<DataItem>): ArrayList<ItemsGroup> {

        val groups: ArrayList<ItemsGroup> = ArrayList()

        catsIDs.forEach { groups.add(ItemsGroup(ArrayList(), "0", it)) }
        sortItemsByGroup(groups, catsItems)

        return groups
    }

    private fun sortGroupsByItemsProgress(groups: ArrayList<ItemsGroup>): ArrayList<ItemsGroup> {

        val progressGroup: ArrayList<ItemsGroup> = ArrayList()
        val startProgressGroup: ArrayList<ItemsGroup> = ArrayList()
        val completedGroup: ArrayList<ItemsGroup> = ArrayList()

        Log.i("Quest", "quests: progress ${groups.size} }")

        for (group in groups) {

            calculateGroupProgress(group)

            Log.i("Quest", "quests: gr ${group.id}  - ${group.stats}")

            when {
                Integer.valueOf(group.stats) in PROGRESS_START_RANGE -> startProgressGroup.add(group)
                Integer.valueOf(group.stats) > PROGRESS_MASTERED_CAT -> completedGroup.add(group)
                else -> progressGroup.add(group)
            }
        }

        val sortedGroups: ArrayList<ItemsGroup> = ArrayList()

        /// if this is the first cat to learn, leave just this cat

        Log.i("Quest", "quests: ${startProgressGroup.size} ; ${progressGroup.size}")

        if (progressGroup.size == 0 && completedGroup.size == 0 && startProgressGroup.size > 1) {


            sortedGroups.add(startProgressGroup[0])

        } else {

            sortedGroups.addAll(progressGroup.sortedByDescending { it.stats })
            sortedGroups.addAll(startProgressGroup)
            sortedGroups.addAll(completedGroup)
        }

        return sortedGroups
    }


    private fun sortItemsByGroup(groups: ArrayList<ItemsGroup>, catsItems: ArrayList<DataItem>) {
        for (item in catsItems) {
            for (group in groups) {
                if (item.id.matches("${group.id}.*".toRegex())) {
                    group.items.add(item)
                    break
                }
            }
        }
    }


    private fun calculateGroupProgress(group: ItemsGroup) {

        var progress = 0


        if (group.items.size != 0) {

            var totalCount = 0

            group.items.forEach { totalCount += it.rate }

            progress = (totalCount * 100) / (3 * group.items.size)

            if (progress > 100) progress = 100
        }


        group.stats = progress.toString()

    }


    data class ItemsGroup(var items: ArrayList<DataItem>,
                          var stats: String,
                          val id: String
    )


}