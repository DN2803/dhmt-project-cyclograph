package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import com.cg76.drawingapp.GLESRenderer
import kotlin.math.cos
import kotlin.math.sin

class Circle: Shape{
    constructor(vertexCount: Int = 364, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super (vertexCount, vertices, color, size)


    override var _drawMode = GLES20.GL_LINE_LOOP
    override var _type = ShapeType.CIRCLE
}
fun setPossition (centerX: Float, centerY: Float, rx: Float, ry: Float): MutableList<Vertex>{


    var vertices = mutableListOf<Vertex>()
    val angleIncrement = 2 * Math.PI / 360

    var angle = 0.0 + angleIncrement


    for (i in 0..363) {
        var x = (centerX + rx * cos(angle)).toFloat();
        var y = (centerY + ry * sin(angle)).toFloat();
        var z = 0f;
        angle += angleIncrement
        vertices.add(Vertex(x, y, z));
    }
    return vertices
}
class CircleBuilder: ShapeBuilder{
    override fun build(startPoint: FloatArray, endPoint: FloatArray, color: FloatArray, size: Float): Circle {
        // left
        val x2 = (startPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
        val y2 = 1 - 2 * (startPoint[1] / GLESRenderer.viewHeight)
        // right
        val x3 = (endPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
        val y3 = 1 - 2 * (endPoint[1] / GLESRenderer.viewHeight)

        val x1 = (x2 + x3)/2
        val y1 = (y2 + y3)/2

        var rx = x2-x1

        var vertices = setPossition(x1, y1, rx, rx)
        return Circle(364, vertices, color, size)
    }

    override fun build(
        vertexCount: Int,
        vertices: MutableList<Vertex>,
        color: FloatArray,
        size: Float
    ): Shape {
        return Circle(364, vertices, color, size)
    }
}

class ElipseBuilder: ShapeBuilder{
    override fun build(startPoint: FloatArray, endPoint: FloatArray, color: FloatArray, size: Float): Circle {
        // left
        val x2 = (startPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
        val y2 = 1 - 2 * (startPoint[1] / GLESRenderer.viewHeight)
        // right
        val x3 = (endPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
        val y3 = 1 - 2 * (endPoint[1] / GLESRenderer.viewHeight)

        val x1 = (x2 + x3)/2
        val y1 = (y2 + y3)/2

        var rx = x2-x1
        var ry = y2-y1
        var vertices = setPossition(x1, y1, rx, ry)

        return Circle(364, vertices, color, size)
    }

    override fun build(
        vertexCount: Int,
        vertices: MutableList<Vertex>,
        color: FloatArray,
        size: Float
    ): Shape {
        return Circle(364, vertices, color, size)
    }
}