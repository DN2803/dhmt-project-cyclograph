package com.cg76.drawingapp

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.google.gson.Gson
import java.io.File
import java.util.Date

class UserDataManager private constructor() {
    private var userDataList: MutableList<UserData> = mutableListOf()
    private lateinit var fileList: Array<File>
    private object Holder { val INSTANCE = UserDataManager() }

    companion object {
        private const val directoryPath = "CycloGraph App"
        private val directory = File(directoryPath)
        private const val defaultFileName = "data_"
        private var fileCount = 0
        private const val extension = ".json"
        @JvmStatic
        fun getInstance(): UserDataManager{
            return Holder.INSTANCE
        }
    }

    init {
        if (!directory.exists()){
            directory.mkdir()
        }
        readFile()
    }


    @SuppressLint("SimpleDateFormat")
    fun writeSampleToFile(userData: UserData){
        val json = userData.selectedSampleToString()

        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        val filePath = "$directoryPath/$defaultFileName$timestamp$extension"
        File(filePath).writeText(json)
    }

    fun readFile() {
        fileList = directory.listFiles()!!

        for (i in fileList.indices){
            val json = fileList[i].readText()
            var userData = UserData()
            userData.loadSamples(json)
            userDataList.add(userData)
        }
    }

    fun addSample(data: UserData){
        userDataList.add(data)
        writeSampleToFile(data)
    }

    fun removeSample(index: Int) {
        userDataList.removeAt(index)
        fileList[index].delete()
    }

    fun select(index: Int): UserData? {
        return userDataList[index]
    }
}
