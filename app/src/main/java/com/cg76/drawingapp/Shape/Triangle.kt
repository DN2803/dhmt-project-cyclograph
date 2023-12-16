package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle: Shape {
    constructor(vertexCount: Int, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super(vertexCount, vertices, color, size)

    override var _drawMode = GLES20.GL_LINE_LOOP
}