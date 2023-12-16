package com.cg76.drawingapp
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import com.cg76.drawingapp.Shape.*

class GLESRenderer: GLSurfaceView.Renderer {
    private var shapes = mutableListOf<Shape>()

    fun addShape(shape: Shape){
        shapes.add(shape)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

//        var lVertices = mutableListOf<Vertex>()
//        lVertices.add(Vertex(-0.5f, -0.6708927f, 0.0f))
//        lVertices.add(Vertex(0.5f, 0.603135f, 0.0f))//
//        var mLine = Line(2, lVertices, BLACK, 20f)

        var tVertices = mutableListOf<Vertex>()
        tVertices.add(Vertex(0.0f, 0.75f, 0.0f))
        tVertices.add(Vertex(-0.5f, -0.5f, 0.0f))
        tVertices.add(Vertex(0.5f, -0.5f, 0.0f))
        var mTriangle = Triangle(3,tVertices, RED,15f)

//        shapes.add(mLine)
        shapes.add(mTriangle)
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

//        // Create a rotation transformation for the triangle
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)
//
//        // Combine the rotation matrix with the projection and camera view
//        // Note that the vPMatrix factor *must be first* in order
//        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        for(shape in shapes){
            shape.draw(scratch, shape.drawMode)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}

fun loadShader(type: Int, shaderCode: String): Int {

    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    return GLES20.glCreateShader(type).also { shader ->

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
    }
}