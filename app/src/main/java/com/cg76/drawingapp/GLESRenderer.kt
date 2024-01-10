package com.cg76.drawingapp
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.cg76.drawingapp.GLESSurfaceView.Companion.beforeGenShapeCount
import com.cg76.drawingapp.MainActivity.Companion.activeList
import com.cg76.drawingapp.MainActivity.Companion.color
import com.cg76.drawingapp.Shape.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class GLESRenderer: GLSurfaceView.Renderer {

    private var shapes = mutableListOf<Shape>()

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

//        var tVertices = mutableListOf<Vertex>()
//        tVertices.add(Vertex(0.0f, 1f, 0.0f))
//        tVertices.add(Vertex(-1080/1640f, -1f, 0.0f))
//        tVertices.add(Vertex(1080/1640f, -1f, 0.0f))
//        var mTriangle = Triangle(3,tVertices, RED,15f)
//
//        this.addShape(mTriangle)
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

        for(shape in shapes){
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

    private fun cleanShapes() {
        for (shape in shapes){
            if (shape.isTemp){
                shapes.remove(shape)
            }
        }
    }


    fun addShape(shape: Shape?, isTemp: Boolean=false, wasGenCyclo: Boolean=false){
        cleanShapes()

        if (shape != null){
            shape.createProgram()
            shape.isTemp = isTemp

            if (wasGenCyclo){
                for (i in 0..<shapes.size)
                    if (shapes[i].isClone) shapes.add(i,shape)
            }
            else
                shapes.add(shape)
        }
    }

    fun updateShapeAt(transMatrix: FloatArray, id: Int){
        if (id !in 0..<shapes.size) return


    }

//    private fun clearCloneShapeOf(index: Int) {
//        var i = index
//
//        while (i < shapes.size){
//            shapes.removeAt(index)
//            i += beforeGenShapeCount - 1
//        }
//    }

    fun generateCyclograph(copies: Int){
        val delta = 2*PI.toFloat()/copies
        var angle = delta

        var cosTheta = cos(angle)
        var sinTheta = sin(angle)

        for (i in 1..<copies){
            for (j in 0..<activeList.size){
                if (!activeList[j]){
                    continue
                }
                clearCloneShapeOf(j)
                var newVertices = mutableListOf<Vertex>()

                for (vertex in shapes[j].vertices){
                    val newX = cosTheta * vertex.x - sinTheta * vertex.y
                    val newY = sinTheta * vertex.x + cosTheta * vertex.y
                    newVertices.add(Vertex(newX,newY))
                }

                var builder = GLESSurfaceView.factory.select(shapes[j].type)
                var newShape = builder?.build(
                    shapes[j].vertexCount,
                    newVertices,
                    shapes[j].color,
                    shapes[j].size)
                if (newShape != null) {
                    newShape.isClone = true
                    this.addShape(newShape)
                }
            }

            angle+=delta
            cosTheta = cos(angle)
            sinTheta = sin(angle)
        }


        // draw
    }

    fun colorShape(){
        for (i in 0..<activeList.size){
            if (activeList[i]){
                shapes[i].color = color
            }
        }
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
}

fun loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)

    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    return shader
}
