package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import com.cg76.drawingapp.GLESRenderer
import kotlin.math.abs
import kotlin.math.min

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
fun Put4Pixel (centerX:Float, centerY: Float, vertices: MutableList<Vertex>):MutableList<Vertex> {
    var res = mutableListOf<Vertex>()

// Vòng lặp theo chiều thuận kim đồng hồ
    for (i in 0 until  vertices.size) {
        putPixel(centerX + vertices[i].x, centerY + vertices[i].y, res)
    }

    for (i in vertices.size - 1 downTo 0) {
        putPixel(centerX + vertices[i].x, centerY - vertices[i].y, res)
    }

    for (i in 0 until  vertices.size) {
        putPixel(centerX - vertices[i].x, centerY - vertices[i].y, res)
    }

    for (i in vertices.size - 1 downTo 0) {
        putPixel(centerX - vertices[i].x, centerY + vertices[i].y, res)
    }

// Vẽ điểm đầu tiên để hoàn thành vòng tròn
//    putPixel(centerX + vertices.first().x, centerY + vertices.first().y, res)


// Vẽ điểm cuối cùng để hoàn thành vòng tròn
    //putPixel(centerX + vertices.last().x, centerY + vertices.last().y, res)



    return res
}
fun setPosition (centerX: Float, centerY: Float, A: Float, B: Float): MutableList<Vertex>{


    var vertices = mutableListOf<Vertex>()
    var temp_vertices = mutableListOf<Vertex>()

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
    temp_vertices.add(Vertex(x , y))
    while (x<=MaxX)
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

        temp_vertices.add(Vertex(x , y))
    }
    p      = (A2-A*B2+B2/4);
    Const1 = 2*A2;
    Const2 = 2*B2;
    x      = A;
    y      = 0f;
    Delta1 = A2*(2*y+3);
    Delta2 = 2*B2*(1-x)+A2*(2*y+3);
    temp_vertices.add(Vertex(x , y))
    while (y<=MaxY)
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
        temp_vertices.add(Vertex(x , y))
    }
    temp_vertices.sortByDescending {it.y}
    //temp_vertices.sortBy { it.x }
    vertices = Put4Pixel(centerX, centerY, temp_vertices)
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

        var vertices = setPosition(x1, y1, rx, rx)
        var res = Circle(vertices.size, vertices, color, size)
        res.centerPoint = Vertex(x1, y1)
        res.startPoint = Vertex(x1 - rx, y1 - rx)
        res.endPoint = Vertex(x1 + rx, y1 + rx)
        return res

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

        var vertices = setPosition(x1, y1, rx, ry)
        var res = Circle(vertices.size, vertices, color, size)
        res.centerPoint = Vertex(x1, y1)
        res.startPoint = Vertex(x1 - rx, y1 - ry)
        res.endPoint = Vertex(x1 + rx, y1 + ry)
        return res


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