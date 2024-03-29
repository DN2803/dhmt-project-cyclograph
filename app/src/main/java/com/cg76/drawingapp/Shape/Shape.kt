package com.cg76.drawingapp.Shape

import android.opengl.GLES20
import com.cg76.drawingapp.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

val RED = floatArrayOf(1f,0f,0f,1f)
val GREEN = floatArrayOf(0f,1f,0f,1f)
val BLUE = floatArrayOf(0f,0f,1f,1f)
val BLACK = floatArrayOf(0f,0f,0f,1f)

enum class ShapeType{
    BRUSH, LINE, TRIANGLE, SQUARE, CIRCLE, ELIPSE, CURVE
}

enum class ActionType{
     STROKE, COLOR, SCALE, ROTATE, TRANSLATE, SHEAR, GENCYCLO
}

abstract class Shape(
    protected var _vertexCount: Int,
    protected var _vertices: MutableList<Vertex> = mutableListOf(),
    protected var _color: FloatArray = FloatArray(16),
    protected var _size: Float,
) : Drawable {
    protected abstract var _drawMode: Int
    protected abstract var _type: ShapeType
    private var _isTemp = false
    protected var _centerPoint = Vertex()
    protected var _startPoint = Vertex()
    protected var _endPoint = Vertex()


    var vertexCount: Int
        get() = _vertexCount
        set(vertexCount) {_vertexCount = vertexCount}

    var vertices: MutableList<Vertex>
        get() = _vertices
        set(vertices) {_vertices = vertices}

    var color: FloatArray
        get() = _color
        set(color){ _color = color}

    var size: Float
        get() = _size
        set(size) {_size = size}

    var drawMode: Int
        get() = _drawMode
        set(drawMode){ _drawMode = drawMode }

    var isTemp: Boolean
        get() = _isTemp
        set(isTemp) {_isTemp = isTemp}

    var type: ShapeType
        get() = _type
        set(type) {_type = type}

    var centerPoint: Vertex
        get() = _centerPoint
        set(centerPoint) {_centerPoint = centerPoint}
    var startPoint: Vertex
        get() = _startPoint
        set(startPoint) {_startPoint = startPoint}
    var endPoint: Vertex
        get() = _endPoint
        set(endPoint) {_endPoint = endPoint}
    
    companion object{
        private const val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute float a_PointSize;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  gl_PointSize = a_PointSize;" +
                    "}"

        private const val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"
    }

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    // Compile shader code and add them to a OpenGL ES program object
    // and then link the program
    private var mProgram: Int = 0

    fun createProgram() {   // call in GLESRenderer's methods
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {
            // add the vertex shader & fragment shader to program
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    // Create vertex buffer
    private fun createVertexBuffer(): FloatBuffer{
        var result: FloatBuffer =
            // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect( Vertex.COORDS_PER_VERTEX * _vertexCount * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    for (vertex in _vertices){
                        put(vertex.toFloatArray())
                    }
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }

        return result
    }

    // draw method
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var pointSizeHandle: Int = 0

    private val vertexStride: Int = Vertex.COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    override fun draw(mvpMatrix: FloatArray, mode: Int) {
        var vertexBuffer = createVertexBuffer()

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the shape vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the shape coordinate data
            GLES20.glVertexAttribPointer(
                it,
                Vertex.COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // Set color for drawing
                GLES20.glUniform4fv(colorHandle, 1, _color, 0)
            }

            pointSizeHandle= GLES20.glGetAttribLocation(mProgram, "a_PointSize")

            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

            // Draw the shape
            GLES20.glLineWidth(_size)
            GLES20.glVertexAttrib1f(pointSizeHandle, size)
            GLES20.glDrawArrays(mode, 0, _vertexCount)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}