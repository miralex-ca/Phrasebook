package com.online.languages.study.lang.practice

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.online.languages.study.lang.Constants
import com.online.languages.study.lang.DBHelper
import com.online.languages.study.lang.data.DataItem
import com.online.languages.study.lang.data.ExerciseTask
import com.online.languages.study.lang.tools.Computer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class QuestCollector(val dbHelper: DBHelper, val testType: Int, val testLevel: Int) {

    companion object {
        const val TEST_MULTICHOICE = 1
        const val TEST_BUILD = 2
        const val PRACTICE_LIMIT = 20
    }

    var studiedIds = emptyArray<String>()
    var unStudiedIds = emptyArray<String>()

    var type = TEST_MULTICHOICE
    var mixWithUnstudied = true

    val mainList = ArrayList<ExerciseTask>()

    var taskLimit = PRACTICE_LIMIT

    fun processData() {

        var studiedIdsList = ArrayList(listOf(*studiedIds))
        val unStudiedIdsList = ArrayList(listOf(*unStudiedIds))

        if (studiedIds.isEmpty()) {
            studiedIdsList = unStudiedIdsList
        }

        var questsByCatIds = getQuestsByCatIds(studiedIdsList)


        if (questsByCatIds.size == 0) {
            questsByCatIds = getQuestsByCatIds(unStudiedIdsList)
        }

        val questManager = QuestManager(questsByCatIds)
        questManager.testTaskLimit = taskLimit
        questManager.requiredLevel = testLevel

        if (studiedIds.isEmpty()) {
            questManager.unstudied = true
        }

        if (mixWithUnstudied) {
            questManager.unstudiedWithProgressMap = checkUnstudiedProgress(unStudiedIdsList)
        }

        questManager.exerciseType = testType

        questManager.processData()

        var quests = questManager.mainList

        if (mixWithUnstudied && questManager.addUnstudied) {

            val unstudiedQuestsByCatIds = getQuestsByCatIds(unStudiedIdsList)

            val unstudiedQuestManager = QuestManager(unstudiedQuestsByCatIds)

            unstudiedQuestManager.testTaskLimit = questManager.testTaskLimit
            unstudiedQuestManager.requiredLevel = questManager.requiredLevel

            unstudiedQuestManager.unstudied = true
            unstudiedQuestManager.exerciseType = testType
            unstudiedQuestManager.unstudiedWithProgressMap = questManager.unstudiedWithProgressMap

            unstudiedQuestManager.processData()

            quests = joinStudiedAndUnstudied(quests, unstudiedQuestManager.mainList, questManager.testTaskLimit)

        }


        val tasks = ArrayList<ExerciseTask>()

        for (quest in quests) {

            val exerciseTask: ExerciseTask =
                if (type == TEST_BUILD) getBuildExerciseTaskFromQuest(quest)
                else getExerciseTaskFromQuest(quest)

            tasks.add(exerciseTask)
        }

        mainList.clear()
        mainList.addAll(tasks)

    }

    private fun joinStudiedAndUnstudied(
        studiedQuests: ArrayList<QuestData>,
        unStudiedQuests: ArrayList<QuestData>,
        limit: Int
    ) : ArrayList<QuestData> {


        val unstudiedQuestsList = if (unStudiedQuests.size > limit/2)
        {
            ArrayList(unStudiedQuests.subList(0, limit/2))

        } else {
            unStudiedQuests
        }

        val quests = if (studiedQuests.size > limit - unstudiedQuestsList.size) {
            ArrayList(studiedQuests.subList(0, limit - unstudiedQuestsList.size ))
        } else {
            studiedQuests
        }

        quests.addAll(unstudiedQuestsList)

        return quests

    }

    private fun checkUnstudiedProgress(unStudiedIdsList: ArrayList<String>): HashMap<String, Int> {

        val unstudiedWithProcessMap = hashMapOf<String, Int>()

        val db = dbHelper.writableDatabase

        unStudiedIdsList.forEach { id ->

           val catStats = dbHelper.getDataItemsCountsByCatId(db, id)
           val progress = Computer.calculatePercent(catStats[0], catStats[1])

           if (progress > 10) unstudiedWithProcessMap[id] = 1

        }

        db.close()

        //Log.i("Quest", "unstudied: ${unStudiedIdsList.size} => ${unstudiedWithProcessMap.size}")

        return unstudiedWithProcessMap
    }

    private fun getQuestsByCatIds(studiedIdsList: ArrayList<String>): ArrayList<ArrayList<QuestData>> {

        return dbHelper.getGroupedQuestsByCatIds(studiedIdsList, testLevel, type)

    }


    private fun getExerciseTaskFromQuest(quest: QuestData): ExerciseTask {

        val exerciseTask = ExerciseTask()

        exerciseTask.quest = quest.quest
        exerciseTask.questInfo = quest.correct
        exerciseTask.data = DataItem()
        exerciseTask.data.item = exerciseTask.quest

        exerciseTask.data.pronounce =  if (quest.pronounce != "") quest.pronounce else exerciseTask.quest

        exerciseTask.option = quest.options
        exerciseTask.options = java.util.ArrayList()
        exerciseTask.answers = java.util.ArrayList()

        val optString = quest.options
        val options = optString.split("\\|".toRegex()).toTypedArray()

        exerciseTask.options.addAll(options)
        exerciseTask.options.shuffle()

        val optNum = Constants.TEST_OPTIONS_NUM - 1

        if (exerciseTask.options.size > optNum) {
            exerciseTask.options = ArrayList(exerciseTask.options.subList(0, optNum))
        }

        exerciseTask.options.add(0, quest.correct)
        exerciseTask.savedInfo = quest.id
        val dataItem = DataItem(exerciseTask.quest, exerciseTask.questInfo)
        dataItem.id = exerciseTask.savedInfo


        return exerciseTask
    }

    private fun getBuildExerciseTaskFromQuest(quest: QuestData): ExerciseTask {

        val exerciseTask = ExerciseTask()
        exerciseTask.quest = quest.quest
        exerciseTask.questInfo = quest.correct
        exerciseTask.data = DataItem()
        exerciseTask.data.item = exerciseTask.quest
        exerciseTask.option = quest.options
        exerciseTask.params = quest.params
        exerciseTask.response = quest.correct
        exerciseTask.data.pronounce =
            if (quest.pronounce != "") quest.pronounce else exerciseTask.response
        exerciseTask.options = java.util.ArrayList()
        exerciseTask.answers = java.util.ArrayList()
        val answersString = quest.correct
        val answers = answersString.split("\\|".toRegex()).toTypedArray()
        if (answers.size > 1) {
            exerciseTask.response = answers[0]
        }
        Collections.addAll(exerciseTask.answers, *answers)
        exerciseTask.savedInfo = quest.id
        val dataItem = DataItem(exerciseTask.quest, exerciseTask.questInfo)
        dataItem.id = exerciseTask.savedInfo
        return exerciseTask
    }




}