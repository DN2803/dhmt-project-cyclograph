package com.cg76.drawingapp

import android.graphics.Bitmap
import com.cg76.drawingapp.Shape.Shape
import com.cg76.drawingapp.Shape.ShapeType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class UserData(
    var shapeLists: MutableList<MutableList<Shape>> = mutableListOf(mutableListOf()),
    var isSelectedList: MutableList<Boolean> = mutableListOf(),
    var sampleCount: Int = 0,
    // data for drawing,
    var isDrawAction: Boolean = false,
    var shapeType: ShapeType = ShapeType.LINE,
    var stroke: Float = 7f,
    var color: FloatArray = floatArrayOf(0f, 0f, 0f, 1f),
    var copies: Int = 0,
    var bitmap: Bitmap? = null,
    // data for transformation,
    //var affinePreviewShape: Shape? = null,
    var vShift: Float = 0f,
    var hShift: Float = 0f,
    var scale: Float = 1f,
    var rotate: Float = 0f,
    var vSheer: Float = 0f,
    var hSheer: Float = 0f
) {

    fun removeSampleAt(index: Int){
        shapeLists.removeAt(index)
        isSelectedList.removeAt(index)
        sampleCount--
    }

    fun selectedSampleToString(): String {
        var selectedSamples = mutableListOf(mutableListOf<Shape>())
        for (i in 0 until isSelectedList.size){
            if (isSelectedList[i]){
                selectedSamples.add(shapeLists.first())
            }
        }

        return Gson().toJson(selectedSamples)
    }

    fun loadSamples(json: String) {
        val type = object : TypeToken<MutableList<MutableList<Shape>>>() {}.type
        shapeLists =  Gson().fromJson(json, type)
        sampleCount = shapeLists.size

        for (i in 0 until shapeLists.size){
            isSelectedList.add(false)
        }
    }

    fun loadSamples(data: UserData){
        shapeLists = data.shapeLists
        sampleCount = data.sampleCount
        isSelectedList = data.isSelectedList
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserData

        if (shapeLists != other.shapeLists) return false
        if (isSelectedList != other.isSelectedList) return false
        if (sampleCount != other.sampleCount) return false
        if (isDrawAction != other.isDrawAction) return false
        if (shapeType != other.shapeType) return false
        if (stroke != other.stroke) return false
        if (!color.contentEquals(other.color)) return false
        if (copies != other.copies) return false
        if (bitmap != other.bitmap) return false
        if (vShift != other.vShift) return false
        if (hShift != other.hShift) return false
        if (scale != other.scale) return false
        if (rotate != other.rotate) return false
        if (vSheer != other.vSheer) return false
        if (hSheer != other.hSheer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shapeLists.hashCode()
        result = 31 * result + isSelectedList.hashCode()
        result = 31 * result + sampleCount
        result = 31 * result + isDrawAction.hashCode()
        result = 31 * result + shapeType.hashCode()
        result = 31 * result + stroke.hashCode()
        result = 31 * result + color.contentHashCode()
        result = 31 * result + copies
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        result = 31 * result + vShift.hashCode()
        result = 31 * result + hShift.hashCode()
        result = 31 * result + scale.hashCode()
        result = 31 * result + rotate.hashCode()
        result = 31 * result + vSheer.hashCode()
        result = 31 * result + hSheer.hashCode()
        return result
    }
}