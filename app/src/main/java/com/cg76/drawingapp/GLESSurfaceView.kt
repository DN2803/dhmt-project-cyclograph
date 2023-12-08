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
    }

}