package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import com.cg76.drawingapp.GLESRenderer
import com.cg76.drawingapp.GLESRenderer.Companion.maxXCoord
import com.cg76.drawingapp.GLESRenderer.Companion.viewHeight

class Line: Shape {
    constructor(vertexCount: Int = 2, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super(vertexCount, vertices, color, size)

    override var _drawMode = GLES20.GL_LINES

}

class LineBuilder: ShapeBuilder{
    override fun build(startPoint: FloatArray, endPoint: FloatArray, color: FloatArray, size: Float): Line {
        // K = width/height
        // x' = 0-> x = -K
        // x' = width/2 -> x = 0
        // x' = width -> x = K
        // -> x = ((x')/height)*2 - width/height

        val x1 = (startPoint[0] / viewHeight) * 2 - maxXCoord
        val y1 = 1 - 2 * (startPoint[1] / viewHeight)

        val x2 = (endPoint[0] / viewHeight) * 2 - maxXCoord
        val y2 = 1 - 2 * (endPoint[1] / viewHeight)

        val vertices = mutableListOf(Vertex(x1,y1), Vertex(x2, y2))
        return Line(2, vertices, color, size)
    }
}