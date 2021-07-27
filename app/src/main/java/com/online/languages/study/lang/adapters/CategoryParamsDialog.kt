package com.online.languages.study.lang.adapters

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
import com.online.languages.study.lang.databinding.DialogCategoryParamsBinding
import com.online.languages.study.lang.databinding.DialogPracticeParamsBinding
import com.online.languages.study.lang.practice.PracticeParamsDialogCallback
import java.util.*


open class CategoryParamsDialog(var context: Context) : PracticeParamsDialogCallback {


    companion object {
        const val LEVEL_UNKNOWN = "unknown"
        const val CATEGORY_RESULT_DISPLAY = "cat_result"

        const val TRANSCRIPTION_SETTING =  "set_transript"

    }

    var sectionId = ""

    var binding: DialogCategoryParamsBinding? = null

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
        val content = inflater.inflate(R.layout.dialog_category_params, null)

        binding = DialogCategoryParamsBinding.bind(content)

        initResultDisplayStatus()

        val statusSpinner = binding!!.statusSpinner
        initStatusSpinner(statusSpinner)

        val transcriptionSpinner = binding!!.transcriptionSpinner
        initTranscriptionSpinner(transcriptionSpinner)


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


    private fun initResultDisplayStatus() {

        checkDisplayResultStatus()

        binding!!.checkboxDisplayResults.setOnCheckedChangeListener { buttonView, isChecked ->
            saveCategoryResult(isChecked)
            resultDisplayStatus(isChecked)
        }
    }

    private fun checkDisplayResultStatus() {

        val resultDisplay = appSettings!!.getBoolean(CATEGORY_RESULT_DISPLAY, true)

        binding!!.checkboxDisplayResults.isChecked = resultDisplay

        resultDisplayStatus(resultDisplay)
    }

    private fun resultDisplayStatus(checked: Boolean) {
       // TODO check the text
    }

    private fun saveCategoryResult(value: Boolean) {
        val editor = appSettings!!.edit()
        editor.putBoolean(CATEGORY_RESULT_DISPLAY, value)
        editor.apply()
    }

    ///// status settings
    private fun initStatusSpinner(spinner: Spinner) {

        ArrayAdapter.createFromResource(
            context,
            R.array.set_show_status_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.practice_limit_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = StatusOnItemSelectedListener()

        val limit =  appSettings!!.getString("show_status", "1")

        val values: Array<String>  = context.resources.getStringArray(R.array.set_show_status_values)
        val position = getPositionFromArrayByValue( limit!! , values)
        spinner.setSelection(position)

        checkStatusDisplayDesc(position)

    }

    private fun saveStatusParam(pos: Int) {
        val values = context.resources.getStringArray(R.array.set_show_status_values)
        saveStatus(values[pos])
        checkStatusDisplayDesc(pos)
    }

    private fun saveStatus(value: String) {
        val editor = appSettings!!.edit()
        editor.putString("show_status", value)
        editor.apply()
    }

    private fun checkStatusDisplayDesc(position: Int) {
       // TODO("Not yet implemented")
    }

    //// transcription settings
    private fun initTranscriptionSpinner(spinner: Spinner) {

        ArrayAdapter.createFromResource(
            context,
            R.array.set_transcript_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.practice_limit_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = TranscriptionOnItemSelectedListener()

        val default = context.resources.getString(R.string.set_transcript_default)

        val settingSavedValue =  appSettings!!.getString(TRANSCRIPTION_SETTING, default)

        val values: Array<String>  = context.resources.getStringArray(R.array.set_transcript_values)
        val position = getPositionFromArrayByValue( settingSavedValue!! , values)
        spinner.setSelection(position)

        checkTranscriptionStatusDesc(position)

    }

    fun saveTranscriptionParam(pos: Int) {
        val values = context.resources.getStringArray(R.array.set_transcript_values)
        saveTranscription(values[pos])
        checkTranscriptionStatusDesc(pos)
    }

    fun saveTranscription(value: String) {
        val editor = appSettings!!.edit()
        editor.putString(TRANSCRIPTION_SETTING, value)
        editor.apply()
    }

    private fun checkTranscriptionStatusDesc(position: Int) {
        // TODO("Not yet implemented")
    }

    inner class TranscriptionOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            saveTranscriptionParam(pos)
        }

        override fun onNothingSelected(arg0: AdapterView<*>?) {
        }
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



    inner class StatusOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            saveStatusParam(pos)
        }

        override fun onNothingSelected(arg0: AdapterView<*>?) {
        }
    }




    override fun practiceDialogCloseCallback() {

    }

/*
    private fun initSettingSpinner(spinner: Spinner) {  // change the method name

        ArrayAdapter.createFromResource(
            context,
            R.array.set_show_status_list,  // change array list with titles
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.practice_limit_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = SettingOnItemSelectedListener() /// create a new listener with saveSettingParam method

        val setting_saved_value =  appSettings!!.getString(setting_name, default)  /// change setting name

        val values: Array<String>  = context.resources.getStringArray(R.array.set_setting_values) // insert values array name
        val position = getPositionFromArrayByValue( setting_saved_value!! , values)
        spinner.setSelection(position)

    }


    fun saveSettingParam(pos: Int) {
        val values = context.resources.getStringArray(R.array.set_setting_values) // insert values array name
        saveSetting(values[pos])
    }

    fun saveSetting(value: String) {    /// change setting name
        val editor = appSettings!!.edit()
        editor.putString(setting_name, value) /// change setting name
        editor.apply()
    }


    */


}