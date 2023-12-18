package com.cg76.drawingapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AbsSeekBar
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.cg76.drawingapp.Shape.ShapeType
import com.cg76.drawingapp.databinding.ColorPopupBinding
import com.cg76.drawingapp.databinding.ShapePopupBinding
import com.cg76.drawingapp.databinding.StrokePopupBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLESSurfaceView
    private lateinit var colorPickerButton: ImageButton
    private lateinit var shapePickerButton: ImageButton
    private lateinit var strokePickerButton: ImageButton
    lateinit var shapeID: ShapeType
    var stroke: Float = 1f
    var color = floatArrayOf(0f, 0f, 0f, 0f)



    companion object{

    }

    private val colorPopupBinding : ColorPopupBinding by lazy {
        ColorPopupBinding.inflate(layoutInflater)
    }
    private val shapePopupBinding : ShapePopupBinding by lazy {
        ShapePopupBinding.inflate(layoutInflater)
    }
    private val strokePopupBinding : StrokePopupBinding by lazy {
        StrokePopupBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.glSurfaceView)


        var navigateBTN = findViewById<LinearLayout>(R.id.navigate)
        shapePickerButton =  findViewById<ImageButton>(R.id.btn_draw)
        // pick shape type
        val shapePopup = Dialog(this).apply {
            setContentView(shapePopupBinding.root)

            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT


            )
            // Set the X and Y position of the dialog
            val layoutParams = window!!.attributes
            layoutParams.gravity = Gravity.BOTTOM
            layoutParams.dimAmount = 0.0f
            window!!.setBackgroundDrawable(ColorDrawable(Color.argb(0, 0, 0, 0)))


            setCancelable(true)

        }


        colorPickerButton = findViewById<ImageButton>(R.id.btn_colors)

        // use to pick color
        val colorPopup = Dialog(this).apply {
            setContentView(colorPopupBinding.root)

            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT


            )
            // Set the X and Y position of the dialog
            val layoutParams = window!!.attributes
            layoutParams.gravity = Gravity.BOTTOM
            layoutParams.dimAmount = 0.0f
            window!!.setBackgroundDrawable(ColorDrawable(Color.argb(0, 0, 0, 0)))

            setCancelable(false)

        }

        strokePickerButton = findViewById<ImageButton>(R.id.btn_stroke)
        // use to pick stroke
        val strokePopup = Dialog(this).apply {
            setContentView(strokePopupBinding.root)

            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT


            )
            // Set the X and Y position of the dialog
            val layoutParams = window!!.attributes
            layoutParams.gravity = Gravity.BOTTOM
            layoutParams.dimAmount = 0.0f
            window!!.setBackgroundDrawable(ColorDrawable(Color.argb(0, 0, 0, 0)))


            setCancelable(true)

        }

        setOnSeekbar(
            "R",
            colorPopupBinding.redLayout.typeTxt,
            colorPopupBinding.redLayout.seekBar,
            colorPopupBinding.redLayout.colorValueTxt,
        )
        setOnSeekbar(
            "G",
            colorPopupBinding.greenLayout.typeTxt,
            colorPopupBinding.greenLayout.seekBar,
            colorPopupBinding.greenLayout.colorValueTxt,
        )
        setOnSeekbar(
            "B",
            colorPopupBinding.blueLayout.typeTxt,
            colorPopupBinding.blueLayout.seekBar,
            colorPopupBinding.blueLayout.colorValueTxt,
        )
        setOnSeekbar(
            "A",
            colorPopupBinding.alphaLayout.typeTxt,
            colorPopupBinding.alphaLayout.seekBar,
            colorPopupBinding.alphaLayout.colorValueTxt,
        )
        setOnSeekbar(
            strokePopupBinding.seekBar,
            strokePopupBinding.ValueTxt,
        )
        colorPopupBinding.done.setOnClickListener{
            //colorPopup.visibility = View.GONE
            colorPopup.dismiss()
            //val color = setRGBColor()
            println(color);
            navigateBTN.visibility = View.VISIBLE
        }
        colorPickerButton.setOnClickListener {
            //colorPopup.visibility = View.VISIBLE
            colorPopup.show()
            navigateBTN.visibility = View.GONE

        }
        shapePickerButton.setOnClickListener{


            shapePopup.show()
            //navigateBTN.visibility = View.GONE
        }

        shapePopupBinding.root.setOnClickListener{
            shapePopup.dismiss()
            setShape()
        }
        strokePickerButton.setOnClickListener{
            strokePopup.show()
        }
        strokePopupBinding.root.setOnClickListener{
            strokePopup.dismiss()
            setStroke()
            println(stroke)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setShape() {
        shapePopupBinding.drawBrush.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.BRUSH)
        }
        shapePopupBinding.drawLine.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.LINE)
        }
        shapePopupBinding.drawCurve.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.CURVE)
        }
        shapePopupBinding.drawElipse.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.ELIPSE)
        }
        shapePopupBinding.drawPolygon.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.TRIANGLE)
        }

    }

    private fun setOnSeekbar(type: String, typeTxt: TextView, seekBar: SeekBar, colorTxt:TextView) {

        typeTxt.text = type
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    colorTxt.text = seekBar.progress.toString()
                }
                setRGBColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        colorTxt.text = seekBar.progress.toString()
    }

    private fun setRGBColor(){
        color = floatArrayOf(
            colorPopupBinding.redLayout.seekBar.progress.toFloat()/255,
            colorPopupBinding.greenLayout.seekBar.progress.toFloat()/255,
            colorPopupBinding.blueLayout.seekBar.progress.toFloat()/255,
            colorPopupBinding.alphaLayout.seekBar.progress.toFloat()/255)
        val hex = String.format(
            "#%02x%02x%02x%02x",
            colorPopupBinding.alphaLayout.seekBar.progress,
            colorPopupBinding.redLayout.seekBar.progress,
            colorPopupBinding.greenLayout.seekBar.progress,
            colorPopupBinding.blueLayout.seekBar.progress,

        )
        colorPopupBinding.viewColor.setBackgroundColor(Color.parseColor(hex))

    }
    private fun setOnSeekbar(seekBar: SeekBar, value: TextView) {
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    value.text = seekBar.progress.toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
    private fun setStroke(){
        stroke = strokePopupBinding.seekBar.progress.toFloat()/100
    }
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    private fun handleTouchEvent(view: View, motionEvent: MotionEvent, shapeIdentify: ShapeType): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                // Animation for touch down (scale up)
                scaleView(view, 1.0f, 1.2f)
                this.shapeID = shapeIdentify


            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Animation for touch up or touch cancel (scale back to normal)
                scaleView(view, 1.2f, 1.0f)
            }
        }
        return true
    }

    private fun scaleView(view: View, fromScale: Float, toScale: Float) {
        val scaleAnimation = ScaleAnimation(
            fromScale, toScale, fromScale, toScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        scaleAnimation.duration = 200 // Set the duration of the animation in milliseconds

        // Set the animation listener to reset the view after the animation ends
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Reset the scale after the animation ends
                view.clearAnimation()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Start the animation
        view.startAnimation(scaleAnimation)
    }

}
