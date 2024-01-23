package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import com.cg76.drawingapp.GLESRenderer
import java.lang.Long.max
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Circle: Shape{
    constructor(vertexCount: Int, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super (vertexCount, vertices, color, size)


    override var _drawMode = GLES20.GL_POINTS
    override var _type = ShapeType.CIRCLE
}
fun putPixel (intX: Float, intY: Float, vertices: MutableList<Vertex>) {
    var x = (intX / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
    var y = (1 - 2 * (intY / GLESRenderer.viewHeight))
    var z = 0f;
    vertices.add(Vertex(x, y, z))
    return
}
fun Put4Pixel (centerX:Float, centerY: Float, rX: Float, rY: Float, vertices: MutableList<Vertex>) {
    putPixel(centerX + rX, centerY+ rY, vertices)
    putPixel(centerX - rX, centerY - rY, vertices)
    putPixel(centerX + rX, centerY - rY, vertices)
    putPixel(centerX - rX, centerY + rY, vertices)
    return
}
fun setPossition (centerX: Float, centerY: Float, A: Float, B: Float): MutableList<Vertex>{


    var vertices = mutableListOf<Vertex>()
//    val angleIncrement = 2 * Math.PI / 360
//
//    var angle = 0.0 + angleIncrement
//
//
//    for (i in 0..360) {
//        var x = (centerX + rx * cos(angle)).toFloat();
//        var y = (centerY + ry * sin(angle)).toFloat();
//        var z = 0f;
//        angle += angleIncrement
//        vertices.add(Vertex(x, y, z));
//    }
//    return vertices

    var A2: Float
    var B2: Float
    var p: Float
    var Const1: Float
    var Const2: Float
    var Delta1: Float
    var Delta2: Float
    var x: Float
    var y: Float
    var MaxX: Float
    var MaxY: Float

    A2     = A*A;
    B2     = B*B;
    MaxY = (B2 / Math.sqrt((A2 + B2).toDouble())).toFloat()
    MaxX = (A2 / Math.sqrt((A2 + B2).toDouble())).toFloat()
    p      = (B2-A2*B+A2/4);
    Const1 = 2*B2;
    Const2 = 2*A2;
    x      = 0f;
    y      = B;
    Delta1 = B2*(2*x+3);
    Delta2 = 2*A2*(1-y)+B2*(2*x+3);
    Put4Pixel(centerX, centerY, x, y, vertices);
    while (x<MaxX)
    {
        if (p>=0)
        {
            p      += Delta2;
            Delta2 += Const2;
            y--;
        }
        else
            p += Delta1;
        Delta2+=Const1;
        Delta1+=Const1;
        x++;

        Put4Pixel(centerX, centerY, x, y, vertices);
    }
    p      = (A2-A*B2+B2/4);
    Const1 = 2*A2;
    Const2 = 2*B2;
    x      = A;
    y      = 0f;
    Delta1 = A2*(2*y+3);
    Delta2 = 2*B2*(1-x)+A2*(2*y+3);
    Put4Pixel(centerX, centerY, x, y, vertices);
    while (y<MaxY)
    {
        if (p>=0)
        {
            p     +=Delta2;
            Delta2+=Const2;
            x--;
        }
        else
            p+=Delta1;

        Delta2+=Const1;
        Delta1+=Const1;
        y++;
        Put4Pixel(centerX, centerY, x, y, vertices);
    }
    return vertices
}
class CircleBuilder: ShapeBuilder{
    override fun build(startPoint: FloatArray, endPoint: FloatArray, color: FloatArray, size: Float): Circle {
//        // left
//        val x2 = (startPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
//        val y2 = 1 - 2 * (startPoint[1] / GLESRenderer.viewHeight)
//        // right
//        val x3 = (endPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
//        val y3 = 1 - 2 * (endPoint[1] / GLESRenderer.viewHeight)
        // left
        val x2 = startPoint[0]
        val y2 = startPoint[1]
        //right
        val x3 = endPoint[0]
        val y3 = endPoint[1]

        val rx = (min(abs(x3-x2)/2,abs(y3-y2)/2))
        val x1 = (x2+ (if (x3>x2) 1 else -1)*rx)
        val y1 = (y2+ (if (y3>y2) 1 else -1)*rx)

        var vertices = setPossition(x1, y1, rx, rx)
        return Circle(vertices.size, vertices, color, size)
    }

    override fun build(
        vertexCount: Int,
        vertices: MutableList<Vertex>,
        color: FloatArray,
        size: Float
    ): Shape {
        return Circle(vertexCount, vertices, color, size)
    }
}

class ElipseBuilder: ShapeBuilder{
    override fun build(startPoint: FloatArray, endPoint: FloatArray, color: FloatArray, size: Float): Circle {
//        // left
//        val x2 = (startPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
//        val y2 = 1 - 2 * (startPoint[1] / GLESRenderer.viewHeight)
//        // right
//        val x3 = (endPoint[0] / GLESRenderer.viewHeight) * 2 - GLESRenderer.maxXCoord
//        val y3 = 1 - 2 * (endPoint[1] / GLESRenderer.viewHeight)
//
//        val x1 = (x2 + x3)/2
//        val y1 = (y2 + y3)/2
//
//        var rx = (x2-x1).toLong()
//        var ry = (y2-y1).toLong()
        val x2 = startPoint[0]
        val y2 = startPoint[1]
        //right
        val x3 = endPoint[0]
        val y3 = endPoint[1]

        val rx = abs(x3-x2)/2
        val ry = abs(y3-y2)/2
        val x1 = (x2+ (if (x3>x2) 1 else -1)*rx)
        val y1 = (y2+ (if (y3>y2) 1 else -1)*ry)

        var vertices = setPossition(x1, y1, rx, ry)
        return Circle(vertices.size, vertices, color, size)


    }

    override fun build(
        vertexCount: Int,
        vertices: MutableList<Vertex>,
        color: FloatArray,
        size: Float
    ): Shape {
        return Circle(vertexCount, vertices, color, size)
    }
}