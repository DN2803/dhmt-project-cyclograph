package com.cg76.drawingapp
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.cg76.drawingapp.MainActivity.Companion.addLayerButton
import com.cg76.drawingapp.MainActivity.Companion.currentUserData
import com.cg76.drawingapp.Shape.*
import com.cg76.drawingapp.Shape.ActionType.*


class GLESSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    companion object{
        lateinit var renderer: GLESRenderer
        val factory = BuilderFactory()
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

        if (currentUserData.isDrawAction) {
            drawShape(e, currentUserData.shapeType)
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
                queueEvent { renderer.initShapeListAt(currentUserData.sampleCount)}
                startPoint = floatArrayOf(e.x, e.y)
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointerCount == 1) {
                    endPoint = floatArrayOf(e.x, e.y)

                    val builder = factory.select(type)
                    val shape = builder?.build(startPoint,endPoint, currentUserData.color, currentUserData.stroke)

                    queueEvent {
                        renderer.addShapeAt(currentUserData.sampleCount, shape, true)
                    }
                    requestRender()
                }
            }

            MotionEvent.ACTION_UP -> {
                endPoint = floatArrayOf(e.x, e.y)

                if (!endPoint.contentEquals(startPoint)) {
                    val builder = factory.select(type)
                    val shape = builder?.build(startPoint,endPoint, currentUserData.color, currentUserData.stroke)

                    queueEvent { renderer.addShapeAt(currentUserData.sampleCount, shape) }
                    requestRender()
                    addLayerButton()
                    currentUserData.sampleCount++
                }
            }
        }
    }

    fun requestRender(actionType: ActionType){
        when(actionType){
            COLOR -> colorShape()
            SCALE -> scaleShape()
            ROTATE -> rotateShape()
            TRANSLATE -> translateShape()
            MIRROR -> mirrorShape()
            SHEAR -> shearShape()
            GENCYCLO -> genCycloGraph()
            else -> ""
        }
    }

    private fun genCycloGraph() {
        for (i in 0..<currentUserData.isSelectedList.size){
            if (currentUserData.isSelectedList[i])
                queueEvent {
                    renderer.generateCyclograph(i)
                }
        }

        requestRender()
    }

    private fun colorShape(){
        for (i in 0..<currentUserData.isSelectedList.size) {
            if (currentUserData.isSelectedList[i]) {
                queueEvent {
                    renderer.colorShape(i)
                }
                requestRender()
            }
        }
    }

    private fun scaleShape(){
        for (i in 0..<currentUserData.isSelectedList.size) {
            if (currentUserData.isSelectedList[i]) {
                queueEvent {
                    renderer.scaleShape(i)
                }
                requestRender()
            }
        }
    }

    private fun rotateShape(){
        for (i in 0..<currentUserData.isSelectedList.size) {
            if (currentUserData.isSelectedList[i]) {
                queueEvent {
                    renderer.rotateShape(i)
                }
                requestRender()
            }
        }
    }

    private fun translateShape(){
        if(currentUserData.hShift == 0f && currentUserData.vShift == 0f) return
        for (i in 0..<currentUserData.isSelectedList.size)
            if (currentUserData.isSelectedList[i]) {
                queueEvent {
                    renderer.translateShape(i)
                }
                requestRender()
            }

    }

    private fun mirrorShape(){
        queueEvent{
            renderer.mirrorShape()
        }
        requestRender()
    }
    private fun shearShape() {
        if(currentUserData.hShift == 0f && currentUserData.vShift == 0f) return
        for (i in 0..<currentUserData.isSelectedList.size)
            if (currentUserData.isSelectedList[i]) {
                queueEvent {
                    renderer.shearShape(i)
                }
            }
        requestRender()

    }


}