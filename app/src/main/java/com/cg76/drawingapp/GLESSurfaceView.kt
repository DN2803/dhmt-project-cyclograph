package com.cg76.drawingapp
import android.content.Context

import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.cg76.drawingapp.MainActivity.Companion.actionType
import com.cg76.drawingapp.MainActivity.Companion.shapeType
import com.cg76.drawingapp.MainActivity.Companion.shapeID
import com.cg76.drawingapp.Shape.*
import com.cg76.drawingapp.Shape.ActionType.*

class GLESSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    companion object{
        lateinit var renderer: GLESRenderer
        val factory = BuilderFactory()
        var color = floatArrayOf(0f, 0f, 0f, 0f)
        var size = 15f
        var beforeGenShapeCount = 0
    }

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLESRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY

        factory.registerWith(ShapeType.LINE, LineBuilder())
        factory.registerWith(ShapeType.TRIANGLE, TriangleBuilder())
        factory.registerWith(ShapeType.CIRCLE, CircleBuilder())
        factory.registerWith(ShapeType.ELIPSE, ElipseBuilder())

    }

    private lateinit var startPoint: FloatArray
    private lateinit var endPoint: FloatArray

    // factory here
    override fun onTouchEvent(e: MotionEvent): Boolean {

        when (actionType) {
            DRAW -> drawShape(e, shapeType)
            SCALE, ROTATE, TRANSLATE, MIRROR, SHEAR -> affineTrans(e, actionType, shapeID)
        }

        when (e.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun drawShape(e: MotionEvent, type: ShapeType){
        val pointerCount = e.pointerCount
        when (e.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // Chạm một ngón tay
                startPoint = floatArrayOf(e.x, e.y)
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointerCount == 1) {
                    endPoint = floatArrayOf(e.x, e.y)

                    var builder = factory.select(type)
                    var shape = builder?.build(startPoint,endPoint, color, size)

                    queueEvent {
                        renderer.addShape(shape, true)
                    }
                    requestRender()
                }
            }

            MotionEvent.ACTION_UP -> {
                endPoint = floatArrayOf(e.x, e.y)

                var builder = factory.select(type)
                var shape = builder?.build(startPoint,endPoint, color, size)

                queueEvent { renderer.addShape(shape) }
                requestRender()
                beforeGenShapeCount++
            }
        }
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    private fun affineTrans(e: MotionEvent, type: ActionType, shapeID: Int){
        //val transMatrixBuilder = TransMatrixBuilder()
        lateinit var transMatrix: FloatArray

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx: Float = x - previousX
                var dy: Float = y - previousY
                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }
                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }
                //transMatrix = transMatrixBuilder.build(ROTATE, dx, dy)

                //queueEvent() { renderer.updateShapeAt(transMatrix, shapeID)}

                requestRender()
            }
        }

        previousX = x
        previousY = y
    }

    fun genCycloGraph(copies: Int) {
        queueEvent {
            renderer.generateCyclograph(MainActivity.copies)
        }
        requestRender()
    }
}