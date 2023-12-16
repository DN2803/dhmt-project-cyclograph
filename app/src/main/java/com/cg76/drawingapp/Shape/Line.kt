package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Line: Shape {
    constructor(vertexCount: Int = 2, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super(vertexCount, vertices, color, size)

    override var _drawMode = GLES20.GL_LINES


    // Setter
    fun setStartPosition(x: Float, y: Float, z: Float) {
        _vertices[0].setPosition(x,y,z)
    }

    fun setEndPosition(x: Float, y: Float, z: Float) {
        _vertices[1].setPosition(x,y,z)
    }

}