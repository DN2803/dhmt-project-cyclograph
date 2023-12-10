package com.cg76.drawingapp
import android.content.Context
import android.opengl.GLSurfaceView
class GLESSurfaceView (context: Context) : GLSurfaceView(context) {

    private val renderer: GLESRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLESRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        // Render the view only when there is a change in the drawing data
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        setRenderMode(renderMode)
    }

}