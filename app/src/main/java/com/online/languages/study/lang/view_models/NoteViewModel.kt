package com.online.languages.study.lang.view_models

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.online.languages.study.lang.App.getAppContext
import com.online.languages.study.lang.Constants.FOLDER_PICS
import com.online.languages.study.lang.R
import com.online.languages.study.lang.data.DataManager
import com.online.languages.study.lang.data.NoteData
import kotlinx.coroutines.*
import java.util.*


class NoteViewModel(application: Application, _noteId: String) : ViewModel() {

    private var noteLiveData: MutableLiveData<NoteData>

    private var noteData: NoteData? = null
    private val dataManager: DataManager
    private var noteId = ""
    var noteIcons = arrayOf<String>()
    var notesPicsFolder: String


    init {
        dataManager = DataManager(application)
        noteId = _noteId
        noteLiveData = MutableLiveData<NoteData>()
        loadNote()

        noteIcons = application.resources.getStringArray(R.array.note_pics_list)
        notesPicsFolder = FOLDER_PICS + application.getString(R.string.notes_pics_folder)
    }

    ///// updating information
    fun loadNote(): NoteData? {
        noteData = getNote()
        getNoteLiveData()

        //upload()

        return noteData
    }

    fun upload() {

       CoroutineScope(Dispatchers.IO).launch {
           Log.i("Cor", "Hello from ${Thread.currentThread().name}")
       }

        CoroutineScope(Dispatchers.Main).launch {
            Log.i("Cor", "Hello from ${Thread.currentThread().name}")
        }

    }

    fun getNoteLiveData(): MutableLiveData<NoteData> {
        noteLiveData.value = noteData
        return noteLiveData
    }
    
    fun getNote() : NoteData? {
        return dataManager.dbHelper.getNote(noteId)
    }


    fun emptyImage(): Boolean {
        val picName = noteData!!.image
        var noImage = false
        if (picName == "none" || picName == "empty.png" || picName == "") {
            noImage = true
        }
        return noImage
    }

    fun imagePath(): String {
        return notesPicsFolder + checkImage()
    }

    private fun checkImage(): String {
        var picName = noteData!!.image

        var found = false

        if (picName == null) picName = ""

        var img = picName
        for (pic in noteIcons) {
            if (picName == pic) {
                found = true
                break
            }
        }

        if (!found) img = "none"
        return img
    }

}


class NoteViewModelFactory(private val mApplication: Application, private val mParam: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteViewModel(mApplication, mParam) as T
    }
}