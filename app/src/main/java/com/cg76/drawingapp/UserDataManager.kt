package com.cg76.drawingapp

import android.icu.text.SimpleDateFormat
import android.os.Environment
import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.io.FilenameFilter
import java.nio.file.Paths
import java.util.Date

class UserDataManager private constructor() {
    private var userDataList: MutableList<UserData> = mutableListOf()

    private object Holder { val INSTANCE = UserDataManager() }

    companion object {
        private const val directoryPath = "CycloGraph App"
        private val directory = File(directoryPath)
        private const val counterPath = "read_this_first.txt"
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


    fun writeSampleToFile(sampleIndex: Int) {
        val json = userDataList[sampleIndex].selectedSampleToString()

        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        val filePath = "$directoryPath/$defaultFileName$timestamp$extension"
        File(filePath).writeText(json)
    }

    fun register(data: UserData){

    }

    fun writeSampleToFile(userData: UserData){
        val json = userData.selectedSampleToString()

        val timestamp = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
        val filePath = "$directoryPath/$defaultFileName$timestamp$extension"
        File(filePath).writeText(json)
    }

    fun readFile() {
        val fileList = directory.listFiles()

        if (fileList != null) {
            for (i in fileList.indices){
                val json = fileList[i].readText()
                var userData = UserData()
                userData.loadSamples(json)
                userDataList.add(userData)
            }
        }

    }


    fun select(index: Int): UserData? {
        return userDataList?.get(index) ?: null
    }
}
