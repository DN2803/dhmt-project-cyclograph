package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import kotlin.math.cos
import kotlin.math.sin

class Circle: Shape{
    constructor(vertexCount: Int = 364, vertices: MutableList<Vertex>, color: FloatArray,
                size: Float) : super (vertexCount, vertices, color, size)


    override var _drawMode = GLES20.GL_LINE_LOOP
    fun setPossition (centerX: Float, centerY: Float, rx: Float, ry: Float){


        //_vertices.add(Vertex(centerX, centerY, 0f));
        val angleIncrement = 2 * Math.PI / 364

        var angle = 0.0 + angleIncrement
        var index = 0

        for (i in 0..363) {
            var x = (centerX + rx * cos(angle)).toFloat();
            var y = (centerY + ry * sin(angle)).toFloat();
            var z = 0f;
            angle += angleIncrement
            _vertices.add(Vertex(x, y, z));
        }
    }






}