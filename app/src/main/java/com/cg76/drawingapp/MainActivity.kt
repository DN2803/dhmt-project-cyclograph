package com.cg76.drawingapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import androidx.annotation.RequiresApi
import com.cg76.drawingapp.Shape.ActionType
import com.cg76.drawingapp.Shape.ShapeType
import com.cg76.drawingapp.databinding.AffinePopupBinding
import com.cg76.drawingapp.databinding.ColorPopupBinding
import com.cg76.drawingapp.databinding.GenerCycloPopupBinding
import com.cg76.drawingapp.databinding.ShapePopupBinding
import com.cg76.drawingapp.databinding.StrokePopupBinding



class MainActivity : AppCompatActivity() {

    companion object {
        var isDrawAction = false
        var shapeType = ShapeType.LINE
        var stroke: Float = 1f
        var color = floatArrayOf(0f, 0f, 0f, 0f)
        var copies: Int = 0
        lateinit var glSurfaceView: GLESSurfaceView

        var V_shift = 0
        var H_shift = 0
        var scale = 1f
        var rotate = 0f
        var V_sheer = 0f
        var H_sheer = 0f

        lateinit var layerList: LinearLayout
        var activeList = mutableListOf<Boolean>()
        var context: MainActivity? = null
        private var buttonCount = 0

        fun addLayerButton() {
            val newButton = Button(context)

            newButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT // height
            )

            activeList.add(false)

            newButton.id = buttonCount
            buttonCount++
            newButton.text = "Layer $buttonCount"
            newButton.setBackgroundColor(0xFFFFFFFF.toInt())

            newButton.setOnClickListener {
                val id = newButton.id
                activeList[id] = !activeList[id]

                if (activeList[id]) {
                    newButton.setBackgroundColor(0xFFA9A9A9.toInt())
                } else {
                    newButton.setBackgroundColor(0xFFFFFFFF.toInt())
                }
            }

            layerList.addView(newButton)
        }
    }

    private val actionButtons = mutableListOf<ImageButton>()

    private lateinit var colorPickerButton: ImageButton
    private lateinit var shapePickerButton: ImageButton
    private lateinit var strokePickerButton: ImageButton
    private lateinit var GenerateButton: ImageButton
    private lateinit var affinePickerButton: ImageButton

    private val colorPopupBinding: ColorPopupBinding by lazy {
        ColorPopupBinding.inflate(layoutInflater)
    }
    private val shapePopupBinding: ShapePopupBinding by lazy {
        ShapePopupBinding.inflate(layoutInflater)
    }
    private val strokePopupBinding: StrokePopupBinding by lazy {
        StrokePopupBinding.inflate(layoutInflater)
    }
    private val generCycloPopupBinding: GenerCycloPopupBinding by lazy {
        GenerCycloPopupBinding.inflate(layoutInflater)
    }
    private val affinePopupBinding: AffinePopupBinding by lazy {
        AffinePopupBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        glSurfaceView = findViewById(R.id.glSurfaceView)

        layerList = findViewById(R.id.layerList)

        var navigateBTN = findViewById<LinearLayout>(R.id.navigate)
        shapePickerButton = findViewById<ImageButton>(R.id.btn_draw)
        colorPickerButton = findViewById<ImageButton>(R.id.btn_colors)
        strokePickerButton = findViewById<ImageButton>(R.id.btn_stroke)
        GenerateButton = findViewById<ImageButton>(R.id.btn_cyclo)
        affinePickerButton = findViewById<ImageButton>(R.id.btn_affine)
        actionButtons.addAll(
            listOf(
                shapePickerButton,
                colorPickerButton,
                strokePickerButton,
                GenerateButton,
                affinePickerButton
            )
        )

        onButtonClicked(shapePickerButton)
        isDrawAction = true

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

        val generPopup = Dialog(this).apply {
            setContentView(generCycloPopupBinding.root)

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

        val affinePopup = Dialog(this).apply {
            setContentView(affinePopupBinding.root)

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

        setOnSeekbarColor(
            "R",
            colorPopupBinding.redLayout.typeTxt,
            colorPopupBinding.redLayout.seekBar,
            colorPopupBinding.redLayout.colorValueTxt,
            ActionType.COLOR
        )
        setOnSeekbarColor(
            "G",
            colorPopupBinding.greenLayout.typeTxt,
            colorPopupBinding.greenLayout.seekBar,
            colorPopupBinding.greenLayout.colorValueTxt,
            ActionType.COLOR
        )
        setOnSeekbarColor(
            "B",
            colorPopupBinding.blueLayout.typeTxt,
            colorPopupBinding.blueLayout.seekBar,
            colorPopupBinding.blueLayout.colorValueTxt,
            ActionType.COLOR
        )
        setOnSeekbarColor(
            "A",
            colorPopupBinding.alphaLayout.typeTxt,
            colorPopupBinding.alphaLayout.seekBar,
            colorPopupBinding.alphaLayout.colorValueTxt,
            ActionType.COLOR
        )
        setOnSeekbar(
            strokePopupBinding.seekBar,
            strokePopupBinding.ValueTxt,
            ActionType.STROKE
        )
        setOnSeekbar(
            generCycloPopupBinding.seekBar,
            generCycloPopupBinding.ValueTxt,
            ActionType.GENCYCLO
        )

        setOnSeekbarAffine(
            "V-Shift",
            affinePopupBinding.transx.typeTxt,
            affinePopupBinding.transx.seekBar,
            affinePopupBinding.transx.ValueTxt,
            -100f, 100f,
            "pixel", affinePopupBinding.transx.unitTxt,
            ActionType.TRANSLATE
        )
        setOnSeekbarAffine(
            "H-Shift",
            affinePopupBinding.transy.typeTxt,
            affinePopupBinding.transy.seekBar,
            affinePopupBinding.transy.ValueTxt,
            -100f, 100f,
            "pixel", affinePopupBinding.transy.unitTxt,
            ActionType.TRANSLATE
        )
        setOnSeekbarAffineScale(
            "Scale",
            affinePopupBinding.scale.typeTxt,
            affinePopupBinding.scale.seekBar,
            affinePopupBinding.scale.ValueTxt,
            0.1f, 10f,
            "times", affinePopupBinding.scale.unitTxt,
            ActionType.SCALE
        )
        setOnSeekbarAffine(
            "Rotate",
            affinePopupBinding.rotate.typeTxt,
            affinePopupBinding.rotate.seekBar,
            affinePopupBinding.rotate.ValueTxt,
            -180f, 180f,
            "degree", affinePopupBinding.rotate.unitTxt,
            ActionType.ROTATE
        )
        setOnSeekbarAffine(
            "V-Sheer",
            affinePopupBinding.sheerx.typeTxt,
            affinePopupBinding.sheerx.seekBar,
            affinePopupBinding.sheerx.ValueTxt,
            -180f, 180f,
            "degree", affinePopupBinding.sheerx.unitTxt,
            ActionType.SHEAR
        )
        setOnSeekbarAffine(
            "H-Sheer",
            affinePopupBinding.sheery.typeTxt,
            affinePopupBinding.sheery.seekBar,
            affinePopupBinding.sheery.ValueTxt,
            -180f, 180f,
            "degree", affinePopupBinding.sheery.unitTxt,
            ActionType.SHEAR
        )

        colorPopupBinding.done.setOnClickListener {
            //colorPopup.visibility = View.GONE
            colorPopup.dismiss()
            //val color = setRGBColor()
            println(color);
            navigateBTN.visibility = View.VISIBLE
        }
        colorPickerButton.setOnClickListener {
            //colorPopup.visibility = View.VISIBLE
            colorPopup.show()
            onButtonClicked(colorPickerButton)
        }
        shapePickerButton.setOnClickListener {
            shapePopup.show()
            onButtonClicked(shapePickerButton)
            deselectAllLayer()
        }

        shapePopupBinding.root.setOnClickListener {
            shapePopup.dismiss()
            setShape()
            isDrawAction = true
        }
        strokePickerButton.setOnClickListener {
            strokePopup.show()
            onButtonClicked(strokePickerButton)
        }
        strokePopupBinding.root.setOnClickListener {
            strokePopup.dismiss()
            setStroke()
        }
        GenerateButton.setOnClickListener {
            generPopup.show()
            onButtonClicked(GenerateButton)
        }
        generCycloPopupBinding.apply.setOnClickListener {

            copies = setCopies()
            // call redraw

            glSurfaceView.requestRender(ActionType.GENCYCLO)

            generPopup.dismiss()
        }
        affinePickerButton.setOnClickListener {
            affinePopup.show()
            onButtonClicked(affinePickerButton)
        }
        affinePopupBinding.root.setOnClickListener {
            generPopup.dismiss()
            setAffine()
        }
    }

    private fun setAffine() {

        V_shift = affinePopupBinding.transx.seekBar.progress.toInt()
        H_shift = affinePopupBinding.transy.seekBar.progress.toInt()
        scale = affinePopupBinding.scale.seekBar.progress.toFloat() * 0.1f + 0.1f
        rotate = affinePopupBinding.rotate.seekBar.progress.toFloat()
        V_sheer = affinePopupBinding.sheerx.seekBar.progress.toFloat()
        H_sheer = affinePopupBinding.sheery.seekBar.progress.toFloat()


//        return affines


    }

    private fun setCopies(): Int {
        val _copies = generCycloPopupBinding.seekBar.progress.toInt()
        return _copies
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
        shapePopupBinding.drawCricle.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.CIRCLE)
        }
        shapePopupBinding.drawPolygon.setOnTouchListener { view, motionEvent ->
            handleTouchEvent(view, motionEvent, ShapeType.TRIANGLE)
        }

    }

    private fun setOnSeekbarColor(
        type: String,
        typeTxt: TextView,
        seekBar: SeekBar,
        colorTxt: TextView,
        actionType: ActionType
    ) {

        typeTxt.text = type
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    colorTxt.text = seekBar.progress.toString()
                }
                setRGBColor()
                glSurfaceView.requestRender(ActionType.COLOR)
                //glSurfaceView.requestRender(actionType)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        colorTxt.text = seekBar.progress.toString()
    }

    private fun setRGBColor() {
        color = floatArrayOf(
            colorPopupBinding.redLayout.seekBar.progress.toFloat() / 255,
            colorPopupBinding.greenLayout.seekBar.progress.toFloat() / 255,
            colorPopupBinding.blueLayout.seekBar.progress.toFloat() / 255,
            colorPopupBinding.alphaLayout.seekBar.progress.toFloat() / 255
        )
        val hex = String.format(
            "#%02x%02x%02x%02x",
            colorPopupBinding.alphaLayout.seekBar.progress,
            colorPopupBinding.redLayout.seekBar.progress,
            colorPopupBinding.greenLayout.seekBar.progress,
            colorPopupBinding.blueLayout.seekBar.progress,

            )
        colorPopupBinding.viewColor.setBackgroundColor(Color.parseColor(hex))
        // goi ham chuyen mau o day
    }

    private fun setOnSeekbar(seekBar: SeekBar, value: TextView, actionType: ActionType) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    value.text = seekBar.progress.toString()
                }
                glSurfaceView.requestRender(actionType)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun setStroke() {
        stroke = strokePopupBinding.seekBar.progress.toFloat() / 100
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnSeekbarAffine(
        type: String, typeTxt: TextView, seekBar: SeekBar, valueTxt: TextView,
        min: Float, max: Float, unit: String, unitTxt: TextView, actionType: ActionType
    ) {
        typeTxt.text = type
        seekBar.min = min.toInt()
        seekBar.max = max.toInt()
        unitTxt.text = unit

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueTxt.text = seekBar?.progress.toString()
                glSurfaceView.requestRender(actionType)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        valueTxt.text = seekBar.progress.toString()
    }

    private fun setOnSeekbarAffineScale(
        type: String,
        typeTxt: TextView,
        seekBar: SeekBar,
        valueTxt: TextView,
        min: Float,
        max: Float,
        unit: String,
        unitTxt: TextView,
        actionType: ActionType
    ) {
        typeTxt.text = type
        unitTxt.text = unit
        val defaultScale = 1.0f
        val minScale = 0.1f
        val maxScale = max
        val seekBarMax = ((maxScale - minScale) / 0.1).toInt() // You can adjust the step size
        seekBar.max = seekBarMax
        seekBar.progress = (defaultScale).toInt()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    val scale = minScale + progress * 0.1
                    valueTxt.text = String.format("%.2f", scale)
                }

                glSurfaceView.requestRender(actionType)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        valueTxt.text = seekBar.progress.toString()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    private fun handleTouchEvent(view: View, motionEvent: MotionEvent, type: ShapeType): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                // Animation for touch down (scale up)
                scaleView(view, 1.0f, 1.2f)
                shapeType = type

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

    private fun onButtonClicked(clickedButton: ImageButton) {
        for (button in actionButtons) {
            button.setBackgroundColor(0xFFFFFFFF.toInt())
        }

        clickedButton.setBackgroundColor(0xFFA9A9A9.toInt())
    }

    private fun deselectAllLayer(){
        //TODO
        for (i in 0..<activeList.size){
            activeList[i] = false

        }
    }
}
