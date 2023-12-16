package com.cg76.drawingapp.Shape

import android.opengl.GLES20

interface Drawable {

    fun draw(mvpMatrix: FloatArray, mode: Int) // pass in the calculated transformation matrix

//    fun translate(tx: Float, ty: Float, tz: Float)
//    fun rotate(angle: Float, rx: Float, ry: Float, rz: Float)
//    fun scale(sx: Float, sy: Float, sz: Float)
}