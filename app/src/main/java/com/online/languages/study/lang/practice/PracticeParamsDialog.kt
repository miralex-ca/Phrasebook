package com.online.languages.study.lang.practice

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import com.online.languages.study.lang.Constants.*
import com.online.languages.study.lang.R
import com.online.languages.study.lang.databinding.DialogPracticeParamsBinding
import java.util.*


open class PracticeParamsDialog(var context: Context) : PracticeParamsDialogCallback {


    companion object {
        const val LEVEL_UNKNOWN = "unknown"
    }

    var sectionId = ""

    var binding: DialogPracticeParamsBinding? = null

    var appSettings: SharedPreferences? = null

    var autoLevelValue = LEVEL_UNKNOWN

    var levelValues:Array<String> = emptyArray()
    var levelTitles:Array<String> = emptyArray()

    var sectionStudiedIds = ArrayList<Array<String>>()

    var sectionTopicsList = ArrayList<Array<String>>()


    init {

        appSettings = PreferenceManager.getDefaultSharedPreferences(context)

        levelValues = context.resources.getStringArray(R.array.practice_level_array_values)
        levelTitles = context.resources.getStringArray(R.array.practice_level_array)


    }

    fun showParams() {


        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.dialog_practice_params, null)

        binding = DialogPracticeParamsBinding.bind(content)

        val limitSpinner = binding!!.limitsSpinner
        initLimitSpinner(limitSpinner)

        initAutoLevelStatus()
        val levelSpinner = binding!!.levelSpinner
        initLevelSpinner(levelSpinner)


        val mixSpinner = binding!!.mixSpinner
        initMixSpinner(mixSpinner)

        initTopicsParams()

        initExcludedTopicsParams()


        val builder = AlertDialog.Builder(context)
        builder
            .setCancelable(true)
            .setView(content)
        val alert = builder.create()
        alert.show()

        alert.setOnCancelListener {
            practiceDialogCloseCallback()
        }

        binding!!.btnCloseDialog.setOnClickListener {
            alert.cancel()
        }
    }

    private fun initExcludedTopicsParams() {

        val savedList =  appSettings!!.getString(PRACTICE_EXCLUDED_SETTING + sectionId, "")

        val inflater =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        for (item in sectionTopicsList) {

            val itemView = inflater.inflate(R.layout.practice_dialog_exlude_topic, null);

            val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox_topic)

            checkBox.text = item[1]
            checkBox.tag = item[0]
            checkBox.isChecked = isExcluded(item[0], savedList!!)

            itemView.findViewById<CheckBox>(R.id.checkbox_topic).setOnCheckedChangeListener { _, _ ->
                checkExcludedTopics(checkBox)
            }

            binding!!.exludedTopicsList.addView(itemView)

        }
    }

    private fun isExcluded(section: String, savedList: String): Boolean {
        var excluded = false

        val list = savedList.split(",")

        list.forEach { if (it == section) excluded = true }

        return excluded
    }

    private fun checkExcludedTopics(checkBox: CompoundButton) {

        var list = ""

        val topics = binding!!.exludedTopicsList.children

        var checkedTopicsCount = 0

        for (topic in topics) {
            val item = topic as CheckBox
            if (item.isChecked ) {
                list += "," + item.tag.toString()
                checkedTopicsCount++
            }
        }

        if (checkedTopicsCount == sectionTopicsList.size) {

            checkBox.isChecked = false
            Toast.makeText(context, "Last topic", Toast.LENGTH_SHORT).show()


        } else {
            saveExcludedTopics(list)
        }



    }

    private fun initTopicsParams() {

        if (sectionStudiedIds.size > 0) {
            binding!!.tvZeroStudiedTopics.visibility = View.GONE
            binding!!.mixParamBox.visibility = View.VISIBLE
            binding!!.topicsListBox.visibility = View.VISIBLE

            displayCatsList()

        } else {
            binding!!.tvZeroStudiedTopics.visibility = View.VISIBLE
            binding!!.topicsListBox.visibility = View.GONE
            binding!!.mixParamBox.visibility = View.GONE
        }
    }

    private fun displayCatsList() {

        var topics = ""

        for ((index, it) in sectionStudiedIds.withIndex()) {
            var divider = ""
            if (index != 0) divider = "; "
            topics += divider + it[1]
        }

        binding!!.tvTopicsList.text = topics

    }


    private fun initAutoLevelStatus() {
        checkAutoLevelStatus()

        binding!!.checkboxLevelAuto.setOnCheckedChangeListener { buttonView, isChecked ->

            saveAutoLevel(isChecked)
            autoLevelStatus(isChecked)
        }
    }

    private fun checkAutoLevelStatus() {

        val autoLevel = appSettings!!.getBoolean(PRACTICE_AUTOLEVEL_SETTING+sectionId, true)
        binding!!.checkboxLevelAuto.isChecked = autoLevel

        autoLevelStatus(autoLevel)

    }

    private fun autoLevelStatus(checked: Boolean) {

        if (checked) {
            binding!!.levelSpinner.visibility = View.GONE

            if (autoLevelValue == LEVEL_UNKNOWN) {
                binding!!.levelValueBox.visibility = View.GONE
            } else {

                val valueIndexInTitles = getPositionFromArrayByValue(autoLevelValue, levelValues)


                if ( valueIndexInTitles in levelTitles.indices) {

                    val desc = context.getString(R.string.current_level) + levelTitles[valueIndexInTitles]

                    binding!!.tvLevelValueDesc.text = desc
                    binding!!.levelValueBox.visibility = View.VISIBLE
                    binding!!.tvLevelValueDesc.visibility = View.VISIBLE
                }
            }

        } else {
            binding!!.levelValueBox.visibility = View.VISIBLE
            binding!!.levelSpinner.visibility = View.VISIBLE
            binding!!.tvLevelValueDesc.visibility = View.GONE
        }
    }


    private fun initLimitSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            context,
            R.array.practice_limit_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.practice_limit_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = PracticeLimitOnItemSelectedListener()

        val limit =  appSettings!!.getString(PRACTICE_LIMIT_SETTING, PRACTICE_LIMIT_DEFAULT.toString())

        val values: Array<String>  = context.resources.getStringArray(R.array.practice_limit_array_values)
        val position = getPositionFromArrayByValue( limit!! , values)
        spinner.setSelection(position)

    }

    private fun initLevelSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            context,
            R.array.practice_level_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.practice_limit_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = PracticeLevelOnItemSelectedListener()

        val limit =  appSettings!!.getString(PRACTICE_LEVEL_SETTING + sectionId, 1.toString())

        val values: Array<String>  = context.resources.getStringArray(R.array.practice_level_array_values)
        val position = getPositionFromArrayByValue( limit!! , values)
        spinner.setSelection(position)

    }

    private fun initMixSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            context,
            R.array.practice_mix_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.practice_limit_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = PracticeMixOnItemSelectedListener()

        val limit =  appSettings!!.getString(PRACTICE_MIX_SETTING+sectionId, "mixed")

        val values: Array<String>  = context.resources.getStringArray(R.array.practice_mix_array_values)
        val position = getPositionFromArrayByValue( limit!! , values)
        spinner.setSelection(position)

    }


    private fun getPositionFromArrayByValue(value: String, array: Array<String>) : Int {

        var position = -1

        for ((index, it) in array.withIndex()) {
            if (it == value) {

                position = index
            }
        }
        return position
    }


    fun saveLimitParam(pos: Int) {
        val values = context.resources.getStringArray(R.array.practice_limit_array_values)
        saveLimits(values[pos])
    }

    fun saveLimits(value: String) {
        val editor = appSettings!!.edit()
        editor.putString(PRACTICE_LIMIT_SETTING, value)
        editor.apply()
    }

    fun saveLevelParam(pos: Int) {
        val values = context.resources.getStringArray(R.array.practice_level_array_values)
        saveMinLevel(values[pos])
    }

    private fun saveAutoLevel(value: Boolean) {
        val editor = appSettings!!.edit()
        editor.putBoolean(PRACTICE_AUTOLEVEL_SETTING+sectionId, value)
        editor.apply()
    }

    private fun saveMinLevel(value: String) {
        val editor = appSettings!!.edit()
        editor.putString(PRACTICE_LEVEL_SETTING+sectionId, value)
        editor.apply()
    }

    private fun saveExcludedTopics(value: String) {
        val editor = appSettings!!.edit()
        editor.putString(PRACTICE_EXCLUDED_SETTING + sectionId, value)
        editor.apply()
    }

    fun saveMixParam(pos: Int) {
        val values = context.resources.getStringArray(R.array.practice_mix_array_values)
        saveMixValue(values[pos])
    }

    private fun saveMixValue(value: String) {
        val editor = appSettings!!.edit()
        editor.putString(PRACTICE_MIX_SETTING+sectionId, value)
        editor.apply()
    }

    inner class PracticeLimitOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            saveLimitParam(pos)
        }

        override fun onNothingSelected(arg0: AdapterView<*>?) {
        }
    }

    inner class PracticeLevelOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            saveLevelParam(pos)
        }

        override fun onNothingSelected(arg0: AdapterView<*>?) {
        }
    }

    inner class PracticeMixOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            saveMixParam(pos)
        }

        override fun onNothingSelected(arg0: AdapterView<*>?) {
        }
    }

    override fun practiceDialogCloseCallback() {

    }


}