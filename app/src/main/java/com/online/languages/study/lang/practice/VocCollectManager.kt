package com.online.languages.study.lang.practice

import android.util.Log
import com.online.languages.study.lang.data.DataItem
import kotlin.collections.ArrayList


class VocCollectManager( var studiedCatsItems: ArrayList<DataItem>,
                         var unstudiedCatsItems: ArrayList<DataItem>
                         ) {

    companion object {
        const val PRACTICE_VOC_LIMIT = 30
        val PROGRESS_START_RANGE = 0..5
        const val PROGRESS_MASTERED_CAT = 85
        const val PROGRESS_MASTERED_CAT_STUDIED = 80

    }

    var mixStudiedAndUnknown = true

    var mainListDataItems = ArrayList<DataItem>()

    var studiedIds: Array<String> = emptyArray()
    var unStudiedIds: Array<String> = emptyArray()

    private var studiedSortedItems: ArrayList<DataItem> = ArrayList()
    private var unstudiedSortedItems: ArrayList<DataItem> = ArrayList()

    private var unstudiedSortedMasteredGroups: ArrayList<ItemsGroup> = ArrayList()
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

        val allCompleted =
            checkCatsForMasteredItems(catsIDs, catsItems) // check if some cats are not mastered

        //printList(studiedSortedItems)

        studiedSortedItems.addAll(studiedCatsItems)

        sortItemsListByDateAndScore(studiedSortedItems)

        return selectFinalList(allCompleted, studiedSortedItems)

    }

    private fun printList(list: ArrayList<DataItem>) {
        var i = 0
        list.forEach{
            i++
            Log.i("Quest", "item ${i}: ${it.item} - ${it.rate} - ${it.time}")
        }
    }

    private fun checkCatsForMasteredItems(
        catsIDs: Array<String>,
        catsItems: ArrayList<DataItem>
    ): Boolean {

        var allCompleted = true

        val groups: ArrayList<ItemsGroup> = sortItemsInGroupsByIds(catsIDs, catsItems)
        groups.forEach { group -> calculateGroupProgress(group) }

        groups.forEach {

            if ( it.progress < PROGRESS_MASTERED_CAT_STUDIED) allCompleted = false
        }

        return allCompleted
    }

    private fun selectFinalList(
        allCompleted: Boolean,
        sortedStudiedItemsArray: ArrayList<DataItem>
    ): ArrayList<DataItem> {

        var mixWithUnstudied = allCompleted

        if (!mixStudiedAndUnknown) mixWithUnstudied = false

        val finalLIst: ArrayList<DataItem> = ArrayList()

        if (mixWithUnstudied && (unstudiedSortedItems.size > 0)) {

            val mixedStudiedAndUnstudied = mixSortedStudiedAndUnstudied(sortedStudiedItemsArray)

            finalLIst.addAll(mixedStudiedAndUnstudied)

        } else {

            val shuffledSortedStudiedItems = cutListIfLimit(sortedStudiedItemsArray, PRACTICE_VOC_LIMIT).shuffled()

            finalLIst.addAll(shuffledSortedStudiedItems)
        }
        return finalLIst
    }

    private fun mixSortedStudiedAndUnstudied(sortedStudiedItemsArray: ArrayList<DataItem>): ArrayList<DataItem> {

        // happens when studied items are mastered, and unstudied are available

        var sortedStudiedItemsList = sortedStudiedItemsArray
        val halfTestLimit = PRACTICE_VOC_LIMIT / 2


        if (unstudiedSortedItems.size < halfTestLimit) {

            val limit = PRACTICE_VOC_LIMIT - unstudiedSortedItems.size
            sortedStudiedItemsList = cutListIfLimit(sortedStudiedItemsList, limit)

        } else {


            /// add mastered items from unstudied topics to studied items

            val masteredFromUnstudiedTopicList = masteredFromUnstudiedTopics()

            sortedStudiedItemsList.addAll( masteredFromUnstudiedTopicList )

            sortItemsListByDateAndScore(sortedStudiedItemsList)

            sortedStudiedItemsList = cutListIfLimit(sortedStudiedItemsList, halfTestLimit)


            /// add the rest from the list of unstudied

            val limit = PRACTICE_VOC_LIMIT - sortedStudiedItemsList.size


            unstudiedSortedItems = cutListIfLimit(unstudiedSortedItems, limit)

        }

        val finalLIst: ArrayList<DataItem> = ArrayList()

        finalLIst.addAll(sortedStudiedItemsList.shuffled())

        finalLIst.addAll(unstudiedSortedItems)

        return finalLIst
    }

    private fun masteredFromUnstudiedTopics(): ArrayList<DataItem> {

        val masteredFromUnstudiedTopics = ArrayList<DataItem>()

        unstudiedSortedMasteredGroups.forEach{ group ->
            masteredFromUnstudiedTopics.addAll(group.items)
        }


        return masteredFromUnstudiedTopics


    }

    private fun sortItemsListByDateAndScore(list: ArrayList<DataItem> ) {

        list.sortBy { it.id }
        list.sortBy { it.time }
        list.sortBy { it.rate }
    }


    private fun sortUnstudiedGroups() {

        val catsIDs = unStudiedIds
        val catsItems = unstudiedCatsItems
        val sortedItemsList = unstudiedSortedItems

        sortItemsByGroupsProgress(catsIDs, catsItems, sortedItemsList)


    }

    private fun sortItemsByGroupsProgress(
        catsIDs: Array<String>,
        catsItems: ArrayList<DataItem>,
        sortedItemsList: ArrayList<DataItem>
    ) {

        val groups: ArrayList<ItemsGroup> = sortItemsInGroupsByIds(catsIDs, catsItems)

        val sortedGroups: ArrayList<ItemsGroup> = sortGroupsByItemsProgress(groups)

        sortedGroups.forEach {

            if ( it.progress > 2) it.items = unknownToFrontAndShuffle(it.items)

            sortedItemsList.addAll(it.items)
        }
    }


    private fun unknownToFrontAndShuffle(list: ArrayList<DataItem>): ArrayList<DataItem> {

        val unknownList: ArrayList<DataItem> = ArrayList()
        val knownList: ArrayList<DataItem> = ArrayList()

        list.forEach {

            if (it.rate == 0) unknownList.add(it)
            else knownList.add(it)
        }

        unknownList.shuffle()

        unknownList.addAll(knownList.shuffled())

        return unknownList

    }

    private fun sortItemsInGroupsByIds(
        catsIDs: Array<String>,
        catsItems: ArrayList<DataItem>
    ): ArrayList<ItemsGroup> {

        val groups: ArrayList<ItemsGroup> = ArrayList()

        catsIDs.forEach { groups.add(ItemsGroup(ArrayList(), 0, it)) }
        sortItemsByGroup(groups, catsItems)

        return groups
    }

    private fun sortGroupsByItemsProgress(groups: ArrayList<ItemsGroup>): ArrayList<ItemsGroup> {

        val progressGroup: ArrayList<ItemsGroup> = ArrayList()
        val startProgressGroup: ArrayList<ItemsGroup> = ArrayList()
        val completedGroup: ArrayList<ItemsGroup> = ArrayList()

        for (group in groups) {

            calculateGroupProgress(group)

            when {
                group.progress in PROGRESS_START_RANGE -> startProgressGroup.add(group)
                group.progress > PROGRESS_MASTERED_CAT -> completedGroup.add(group)
                else -> progressGroup.add(group)
            }
        }

        val sortedGroups: ArrayList<ItemsGroup> = ArrayList()

        /// if this is the first cat to learn, leave just this cat
        if (progressGroup.size == 0 && completedGroup.size == 0 && startProgressGroup.size > 1) {

            sortedGroups.add(startProgressGroup[0])

        } else {

            sortedGroups.addAll(progressGroup.sortedByDescending { it.progress })
            sortedGroups.addAll(startProgressGroup)
            if (studiedCatsItems.size == 0) sortedGroups.addAll(completedGroup)

        }

        unstudiedSortedMasteredGroups.addAll(completedGroup)


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

        group.progress = progress

    }


    data class ItemsGroup(
        var items: ArrayList<DataItem>,
        var progress: Int,
        val id: String
    )


}