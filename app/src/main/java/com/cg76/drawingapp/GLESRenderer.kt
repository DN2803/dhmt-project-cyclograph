package com.cg76.drawingapp

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLException
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.cg76.drawingapp.GLESSurfaceView.Companion.bitmap
import com.cg76.drawingapp.Shape.*
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class GLESRenderer: GLSurfaceView.Renderer {

    private var shapeLists = mutableListOf(mutableListOf<Shape>())
    private var xAxis = Line(
        2,
        mutableListOf(Vertex(-1f,0f), Vertex(1f,0f)),
        floatArrayOf(0f,0f,0f,0.4f),
        3f)
    private var yAxis = Line(
        2,
        mutableListOf(Vertex(0f,-1f), Vertex(0f,1f)),
        floatArrayOf(0f,0f,0f,0.4f),
        3f)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA)

        xAxis.createProgram()
        yAxis.createProgram()
        shapeLists.add(mutableListOf(xAxis,yAxis))
        shapeLists.removeFirst()

    }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private val rotationMatrix = FloatArray(16)

    var angle: Float = 0f
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

        for (i in 0 until shapeLists.size - 1){
            for (shape in shapeLists[i]){
                shape.draw(scratch, shape.drawMode)
            }
        }

        bitmap = createBitmapFromGLSurface()

        for (shape in shapeLists.last()){
            shape.draw(scratch, shape.drawMode)
        }



    }

    companion object{
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
        while (i < shapeLists[index].size) {
            if (shapeLists[index][i].isTemp){
                shapeLists[index].removeAt(i)
            }
            else {
                i++
            }
        }
    }

    fun initShapeListAt(index: Int){
        shapeLists.add(index, mutableListOf())
    }

    fun addShapeAt(index: Int, shape: Shape?, isTemp: Boolean=false){
        cleanShapesAt(index)

        if (shape != null){
            shape.createProgram()
            shape.isTemp = isTemp
            shapeLists[index].add(shape)
        }
    }

    private fun clearCloneShapesAt(index: Int) {
        while (1 < shapeLists[index].size){
            shapeLists[index].removeAt(1)
        }
    }

    fun generateCyclograph(index: Int, copies: Int){
        clearCloneShapesAt(index)
        val delta = 2*PI.toFloat()/copies
        var angle = delta

        var cosTheta = cos(angle)
        var sinTheta = sin(angle)

        val sample = shapeLists[index][0]

        for (i in 1..<copies){

            var newVertices = mutableListOf<Vertex>()

            for (vertex in sample.vertices){
                val newX = cosTheta * vertex.x - sinTheta * vertex.y
                val newY = sinTheta * vertex.x + cosTheta * vertex.y
                newVertices.add(Vertex(newX,newY))
            }

            var builder = GLESSurfaceView.factory.select(sample.type)
            var newShape = builder?.build(
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

    fun colorShape(){
//        for (i in 0..<activeList.size){
//            if (activeList[i]){
//                shapes[i].color = color
//            }
//        }
    }

    fun scaleShape(){

    }

    fun rotateShape(){

    }

    fun translateShape(){

    }

    fun mirrorShape(){

    }

    fun shearShape(){

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
