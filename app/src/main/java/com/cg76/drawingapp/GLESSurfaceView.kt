package com.cg76.drawingapp
import android.content.Context

import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.cg76.drawingapp.Shape.*

class GLESSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {
    private lateinit var renderer: GLESRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLESRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private val TOUCH_SCALE_FACTOR: Float = 0.1f

    private var previousX1: Float = 0f
    private var previousY1: Float = 0f
    private var previousX2: Float = 0f
    private var previousY2: Float = 0f

    private var vertices = mutableListOf<Vertex>(Vertex(), Vertex())
    companion object{ var maxXCoord: Float = 0f}

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val pointerCount = e.pointerCount

        when (e.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // Chạm một ngón tay
                previousX1 = e.getX(0)
                previousY1 = e.getY(0)

                // K = width/height
                // x' = 0-> x = -K
                // x' = width/2 -> x = 0
                // x' = width -> x = K

                // x = ((x')/height)*2 - width/height

                val x1 = (previousX1 / height) * 2 - maxXCoord
                val y1 = 1 - 2 * (previousY1 / this.height)

                vertices[0] = Vertex(x1,y1)
                performClick()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Chạm thêm một ngón tay (đa chạm)
                previousX2 = e.getX(1)
                previousY2 = e.getY(1)
            }

            MotionEvent.ACTION_MOVE -> {
//                if (pointerCount == 1) {
//
//                }
                // Di chuyển ngón tay
                if (pointerCount == 2) {
                    // Nếu có đủ hai ngón tay, tính toán sự thay đổi góc xoay
                    val x1: Float = e.getX(0)
                    val y1: Float = e.getY(0)
                    val x2: Float = e.getX(1)
                    val y2: Float = e.getY(1)

                    val dx1: Float = x1 - previousX1
                    val dy1: Float = y1 - previousY1
                    val dx2: Float = x2 - previousX2
                    val dy2: Float = y2 - previousY2

                    // Tính toán sự thay đổi góc xoay dựa trên vị trí của cả hai ngón tay
                    val deltaAngle = (dx1 + dy1 + dx2 + dy2) * TOUCH_SCALE_FACTOR
                    renderer.angle += deltaAngle

                    requestRender()

                    previousX1 = x1
                    previousY1 = y1
                    previousX2 = x2
                    previousY2 = y2
                }
            }

            MotionEvent.ACTION_UP -> {
                var x2 = e.getX(0)
                var y2 = e.getY(0)

                x2 = (x2 / height) * 2 - maxXCoord
                y2 = 1 - 2 * (y2 / this.height)

                vertices[1] = Vertex(x2,y2)
                val mLine = Line(2,vertices,BLACK,20f)

                queueEvent { renderer.addShape(mLine) }
                requestRender()
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

}