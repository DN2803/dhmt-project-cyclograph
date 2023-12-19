package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import com.cg76.drawingapp.GLESRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle: Shape {
    constructor(vertexCount: Int, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super(vertexCount, vertices, color, size)

    override var _drawMode = GLES20.GL_LINE_LOOP
    override var _type = ShapeType.TRIANGLE
}

class TriangleBuilder: ShapeBuilder{
    override fun build(startPoint: FloatArray, endPoint: FloatArray, color: FloatArray, size: Float): Triangle {

        // Bottom Left
        val x2 = (startPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
        val y2 = 1 - 2 * (endPoint[1] / GLESRenderer.viewHeight)

        // Bottom Right
        val x3 = (endPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
        val y3 = 1 - 2 * (endPoint[1] / GLESRenderer.viewHeight)

        // Top Middle
        val x1 = (x2 + x3)/2
        val y1 = 1 - 2 * (startPoint[1] / GLESRenderer.viewHeight)

        val vertices = mutableListOf(Vertex(x1,y1), Vertex(x2,y2), Vertex(x3,y3))
        return Triangle(3, vertices, color, size)
    }

    override fun build(
        vertexCount: Int,
        vertices: MutableList<Vertex>,
        color: FloatArray,
        size: Float
    ): Shape {
        return Triangle(3, vertices, color, size)
    }
}