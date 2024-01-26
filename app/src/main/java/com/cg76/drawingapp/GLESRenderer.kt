package com.cg76.drawingapp

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLException
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.cg76.drawingapp.MainActivity.Companion.currentUserData
import com.cg76.drawingapp.Shape.*
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class GLESRenderer: GLSurfaceView.Renderer {


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA)

        var xAxis = Line(
            2,
            mutableListOf(Vertex(-1f,0f), Vertex(1f,0f)),
            floatArrayOf(0f,0f,0f,0.4f),
            3f)
        var yAxis = Line(
            2,
            mutableListOf(Vertex(0f,-1f), Vertex(0f,1f)),
            floatArrayOf(0f,0f,0f,0.4f),
            3f)

        xAxis.createProgram()
        yAxis.createProgram()
        currentUserData.shapeLists.add(mutableListOf(xAxis,yAxis))

    }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private val rotationMatrix = FloatArray(16)

    private var angle: Float = 0f
    override fun onDrawFrame(unused: GL10) {
        val scratch = FloatArray(16)

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        for (i in 0 until currentUserData.shapeLists.size - 1){
            for (shape in currentUserData.shapeLists[i]){
                shape.draw(scratch, shape.drawMode)
            }
        }

        currentUserData.bitmap = createBitmapFromGLSurface()

        for (shape in currentUserData.shapeLists.last()){
            shape.draw(scratch, shape.drawMode)
        }


    }

    companion object {
        var maxXCoord: Float = 0f
        var viewWidth: Int = 0
        var viewHeight: Int = 0
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

        maxXCoord = ratio
        viewWidth = width
        viewHeight = height
    }

    private fun cleanShapesAt(index: Int) {
        var i = 0
        while (i < currentUserData.shapeLists[index].size) {
            if (currentUserData.shapeLists[index][i].isTemp){
                currentUserData.shapeLists[index].removeAt(i)
            }
            else {
                i++
            }
        }
    }

    fun initShapeListAt(index: Int){
        currentUserData.shapeLists.add(index, mutableListOf())
    }

    fun addShapeAt(index: Int, shape: Shape?, isTemp: Boolean=false){
        cleanShapesAt(index)

        if (shape != null){
            shape.createProgram()
            shape.isTemp = isTemp
            currentUserData.shapeLists[index].add(shape)
        }
    }

    private fun clearCloneShapesAt(index: Int) {
        while (1 < currentUserData.shapeLists[index].size){
            currentUserData.shapeLists[index].removeAt(1)
        }
    }
    private fun convertToWSD (vertices: MutableList<Vertex>): MutableList<Vertex> {
        var result = mutableListOf<Vertex>()
        for (i in 0..< vertices.size) {
            var x_new = (vertices[i].x + maxXCoord) /2 * viewHeight
            var y_new = (1 - vertices[i].y)/2 * viewHeight
            result.add(Vertex(x_new, y_new))
        }
        return result.toMutableList()
    }
    private fun convertToVSD (vertices: MutableList<Vertex>): MutableList<Vertex> {
        var result = mutableListOf<Vertex>()
        for (i in 0..< vertices.size) {
            var x_new = (vertices[i].x / viewHeight) * 2 - maxXCoord
            var y_new = 1 - 2 * (vertices[i].y / viewHeight)
            result.add(Vertex(x_new, y_new))
        }
        return result.toMutableList()
    }

    fun generateCyclograph(index: Int){
        clearCloneShapesAt(index)
        val delta = 2*PI.toFloat()/currentUserData.copies
        var angle = delta

        var cosTheta = cos(angle)
        var sinTheta = sin(angle)

        val sample = currentUserData.shapeLists[index][0]

        for (i in 1 until currentUserData.copies){

            var newVertices = mutableListOf<Vertex>()

            for (vertex in sample.vertices){
                val newX = cosTheta * vertex.x - sinTheta * vertex.y
                val newY = sinTheta * vertex.x + cosTheta * vertex.y
                newVertices.add(Vertex(newX,newY))
            }

            val builder = GLESSurfaceView.factory.select(sample.type)
            val newShape = builder?.build(
                sample.vertexCount,
                newVertices,
                sample.color,
                sample.size)

            if (newShape != null) {
                this.addShapeAt(index, newShape)
            }

            angle+=delta
            cosTheta = cos(angle)
            sinTheta = sin(angle)
        }

    }

    fun colorShape(index: Int){
//        for (i in 0..<activeList.size){
//            if (activeList[i]){
//                shapes[i].color = color
//            }
//        }
        currentUserData.shapeLists[index][0].color = currentUserData.color

    }


    fun scaleShape(index: Int) {
        val sample = currentUserData.shapeLists[index][0]
        var center = sample.centerPoint
        var newVertices = mutableListOf<Vertex>()
        if (sample.type == ShapeType.CIRCLE || sample.type == ShapeType.ELIPSE) {
            var end = sample.endPoint
            val rx = (end.x - center.x)*currentUserData.scale
            val ry = (end.y - center.y)*currentUserData.scale
            newVertices  =  setPosition(center.x, center.y, rx, ry)


        }
        else {
            for (i in 0..<sample.vertices.size) {
                val x  = (sample.vertices[i].x - center.x)* currentUserData.scale + center.x
                val y  = (sample.vertices[i].y - center.y)* currentUserData.scale + center.y
                newVertices.add(Vertex(x, y))
            }

        }
        val builder = GLESSurfaceView.factory.select(sample.type)
        val newShape = builder?.build(
            newVertices.size,
            newVertices,
            sample.color,
            sample.size
        )
        while (currentUserData.shapeLists[index].size > 1) {
            currentUserData.shapeLists[index].removeAt(1)
        }
        if (newShape != null) {
            newShape.createProgram()
            currentUserData.shapeLists[index].add(newShape)
        }
    }

    fun rotateShape(index: Int){
        var A = cos(PI.toFloat() *currentUserData.rotate/180);
        var B = sin(PI.toFloat() *currentUserData.rotate/180);
        val sample = currentUserData.shapeLists[index][0]
        var center = sample.centerPoint
        var newVertices = mutableListOf<Vertex>()
        var cx = 0f
        var cy = 0f
        if (sample.type == ShapeType.CIRCLE || sample.type == ShapeType.ELIPSE) {
            cx = (center.x / viewHeight) * 2 - maxXCoord
            cy =  1 - 2 * (center.y / viewHeight)
        }
        else {
            cx = center.x
            cy = center.y
        }

        for (i in 0..<sample.vertices.size) {

            val x  = A*(sample.vertices[i].x - cx) - B*(sample.vertices[i].y - cy) + cx
            val y  = B*(sample.vertices[i].x - cx) + A*(sample.vertices[i].y - cy) + cy
            newVertices.add(Vertex(x, y))
        }

        val builder = GLESSurfaceView.factory.select(sample.type)
        val newShape = builder?.build(
            sample.vertexCount,
            newVertices,
            sample.color,
            sample.size
        )

        while (currentUserData.shapeLists[index].size > 1) {
            currentUserData.shapeLists[index].removeAt(1)
        }
        if (newShape != null) {
            newShape.createProgram()
            currentUserData.shapeLists[index].add(newShape)
        }

    }

    fun translateShape(index: Int){
        val sample = currentUserData.shapeLists[index][0]
        var newVertices = mutableListOf<Vertex>()
        for (i in 0..<sample.vertices.size) {
            var x  = sample.vertices[i].x + currentUserData.hShift
            var y  = sample.vertices[i].y + currentUserData.vShift
            newVertices.add(Vertex(x, y))
        }

        val builder = GLESSurfaceView.factory.select(sample.type)
        val newShape = builder?.build(
            sample.vertexCount,
            newVertices,
            sample.color,
            sample.size)
        while (currentUserData.shapeLists[index].size > 1) {
            currentUserData.shapeLists[index].removeAt(1)
        }
        if (newShape != null) {
            newShape.createProgram()
            currentUserData.shapeLists[index].add(newShape)
        }
//        currentUserData.hShift = 0f
//        currentUserData.vShift = 0f
    }

    fun mirrorShape(){

    }

    fun shearShape(index: Int){
        val sample = currentUserData.shapeLists[index][0]
        var newVertices = mutableListOf<Vertex>()
        for (i in 0..<sample.vertices.size) {
            var x  = sample.vertices[i].x + (currentUserData.vSheer/45)*sample.vertices[i].y
            var y  = sample.vertices[i].y + (currentUserData.hSheer/45)*sample.vertices[i].x
            newVertices.add(Vertex(x, y))
        }

        val builder = GLESSurfaceView.factory.select(sample.type)
        val newShape = builder?.build(
            sample.vertexCount,
            newVertices,
            sample.color,
            sample.size)
        newShape?.drawMode = GLES20.GL_LINE_LOOP
        while (currentUserData.shapeLists[index].size > 1) {
            currentUserData.shapeLists[index].removeAt(1)
        }
        if (newShape != null) {
            newShape.createProgram()
            currentUserData.shapeLists[index].add(newShape)
        }
    }

    @Throws(OutOfMemoryError::class)
    private fun createBitmapFromGLSurface(): Bitmap? {
        val w = viewWidth
        val h = viewHeight
        val bitmapBuffer = IntArray(w * h)
        val bitmapSource = IntArray(w * h)
        val intBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        try {
            GLES20.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
            var offset1: Int
            var offset2: Int
            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w
                for (j in 0 until w) {
                    val texturePixel = bitmapBuffer[offset1 + j]
                    val blue = texturePixel shr 16 and 0xff
                    val red = texturePixel shl 16 and 0x00ff0000
                    val pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            return null
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
    }
}

fun loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)

    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    return shader
}
