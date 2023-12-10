package com.cg76.drawingapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.opengl.GLSurfaceView


class GLESActivity : AppCompatActivity() {
    private lateinit var gLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = GLESSurfaceView(this)
        setContentView(gLView)

    }

    override fun onPause() {
        super.onPause()
        gLView.onPause()
    }

    override fun onResume() {
        super.onResume()
        gLView.onResume()
    }
}