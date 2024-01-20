package com.cg76.drawingapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cg76.drawingapp.Shape.ActionType
import com.cg76.drawingapp.Shape.ShapeType
import com.cg76.drawingapp.databinding.AffinePopupBinding
import com.cg76.drawingapp.databinding.ColorPopupBinding
import com.cg76.drawingapp.databinding.GenerCycloPopupBinding
import com.cg76.drawingapp.databinding.ShapePopupBinding
import com.cg76.drawingapp.databinding.StrokePopupBinding
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.widget.GridLayout
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    companion object {
        var isDrawAction = false
        var shapeType = ShapeType.LINE
        var stroke: Float = 1f
        var color = floatArrayOf(0f, 0f, 0f, 1f)
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
            "Opacity",
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
            "\u25E6", affinePopupBinding.rotate.unitTxt,
            ActionType.ROTATE
        )
        setOnSeekbarAffine(
            "V-Sheer",
            affinePopupBinding.sheerx.typeTxt,
            affinePopupBinding.sheerx.seekBar,
            affinePopupBinding.sheerx.ValueTxt,
            -180f, 180f,
            "\u25E6", affinePopupBinding.sheerx.unitTxt,
            ActionType.SHEAR
        )
        setOnSeekbarAffine(
            "H-Sheer",
            affinePopupBinding.sheery.typeTxt,
            affinePopupBinding.sheery.seekBar,
            affinePopupBinding.sheery.ValueTxt,
            -180f, 180f,
            "\u25E6", affinePopupBinding.sheery.unitTxt,
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

        colorPopupBinding.style1.setOnClickListener {
            colorPopupBinding.palette.visibility = View.VISIBLE
            colorPopupBinding.rgbaSlider.visibility = View.GONE

            var colorGrid = colorPopupBinding.colorGrid

            // Clear existing color buttons if any
            colorGrid.removeAllViews()

            // Mảng chứa mã màu của các màu cơ bản
            val basicColors = arrayOf(
                "#FF0000",
                "#00FF00",
                "#0000FF",
                "#FFFFFF",
                "#000000",
                "#FFFF00",
                "#FFA500",
                "#FFC0CB",
                "#808080",
                "#A52A2A",
                "#800080",
                "#008000",
                "#000080",
                "#00FFFF",
                "#008080",
                "#008000",
                "#2E8B57",
                "#90EE90",
                "#006400",
                "#000080",
            )

            for (i in basicColors.indices) {
                val colorButton = Button(colorPopupBinding.buttonColor.context)
                colorButton.layoutParams = GridLayout.LayoutParams()
                colorButton.width = 50
                colorButton.height = 50
                colorButton.setBackgroundColor(Color.parseColor(basicColors[i]))
                colorButton.setOnClickListener { view ->
                    clearSelection(colorGrid)
                    applySelectionEffect(colorButton)
                    val selectedColor = Color.parseColor(basicColors[i])
                    color = colorToRGBA(selectedColor)
                    // TODO: Xử lý giá trị RGBA theo nhu cầu (ví dụ: hiển thị, sử dụng trong ứng dụng, ...)
                }
                colorGrid.addView(colorButton)
            }
        }

        colorPopupBinding.style2.setOnClickListener {
            colorPopupBinding.palette.visibility = View.GONE
            colorPopupBinding.rgbaSlider.visibility = View.VISIBLE
        }



        shapePickerButton.setOnClickListener {
            shapePopup.show()
            onButtonClicked(shapePickerButton)
            deselectAllLayer()
        }

        shapePopupBinding.root.setOnClickListener {
            setShape()
            isDrawAction = true
            shapePopup.dismiss()
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
    private fun colorToRGBA(color: Int): FloatArray {
        val r =  Color.red(color)/255.0f
        val g = Color.green(color)/255.0f
        val b = Color.blue(color)/255.0f
        val a = Color.alpha(color) / 255.0f // Normalize alpha to the range [0, 1]
        return floatArrayOf(r, g, b, a)
    }
    fun selectColor(view: View) {
        // Handle color selection if needed
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
    var red = 0
    var green = 0
    var blue = 0
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

                    var seekbar_color = Color.rgb(red, green, blue)
                    if (type == "R") {
                        red = progress

                        updateBackgroundColor(seekBar, progress, 1)
                        updateBackgroundColor(colorPopupBinding.greenLayout.seekBar, progress, 2)
                        updateBackgroundColor(colorPopupBinding.blueLayout.seekBar, progress, 3)


                    }
                    if (type == "G") {
                        green = progress
                        updateBackgroundColor(seekBar, progress, 2)
                        updateBackgroundColor(colorPopupBinding.redLayout.seekBar, progress, 1)
                        updateBackgroundColor(colorPopupBinding.blueLayout.seekBar, progress, 3)
                    }
                    if (type == "B") {
                        blue = progress
                        updateBackgroundColor(seekBar, progress, 3)
                        updateBackgroundColor(colorPopupBinding.redLayout.seekBar, progress, 1)
                        updateBackgroundColor(colorPopupBinding.greenLayout.seekBar, progress, 2)
                    }
                    if (type == "Opacity") {
                        var opacity = seekBar.progress*100 /255
                        colorTxt.text = opacity.toString() + " %"
                    }

                    // Set thumb color

                    colorPopupBinding.redLayout.seekBar.getThumb().setTint(seekbar_color)
                    colorPopupBinding.greenLayout.seekBar.getThumb().setTint(seekbar_color)
                    colorPopupBinding.blueLayout.seekBar.getThumb().setTint(seekbar_color)

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
    private fun updateBackgroundColor(seekBar: SeekBar, progress: Int, type: Int) {
        val layerDrawable = seekBar.progressDrawable as LayerDrawable
        val backgroundIndex = 0
        val trackIndex = 2

        // Disable gradient for the background
        layerDrawable.getDrawable(backgroundIndex).setTintMode(PorterDuff.Mode.SRC)

        val colorBackground = calculateBackgroundColor(progress, type)
        layerDrawable.getDrawable(backgroundIndex).setTint(colorBackground)

        val colorTrack = calculateTrackColor(progress,type)
        layerDrawable.getDrawable(trackIndex).setTint(colorTrack)

    }

    private fun calculateBackgroundColor(progress: Int, type: Int): Int {
        // Calculate the background color based on the progress
        // For example, you can create a gradient or use a specific color logic
        // Here, a simple example of changing color based on the progress is provided

        if (type == 1 ) {
            return android.graphics.Color.rgb(255, green, blue)

        }
       if (type == 2 ) {
           return android.graphics.Color.rgb(red, 255, blue)

       }else{
           return android.graphics.Color.rgb(red, green, 255)
       }

    }
    private fun calculateTrackColor(progress: Int, type: Int): Int {
        // Calculate the background color based on the progress
        // For example, you can create a gradient or use a specific color logic
        // Here, a simple example of changing color based on the progress is provided

        if (type == 1 ) {
            return android.graphics.Color.rgb(0, green, blue)

        }
        if (type == 2 ) {
            return android.graphics.Color.rgb(red, 0, blue)

        }else{
            return android.graphics.Color.rgb(red, green, 0)
        }

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


    private fun clearSelection(colorGrid: GridLayout) {
        for (i in 0 until colorGrid.childCount) {
            val colorButton = colorGrid.getChildAt(i) as Button
            colorButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun applySelectionEffect(colorButton: Button) {
        // Create a checkmark drawable
        val checkmarkDrawable = ContextCompat.getDrawable(this, android.R.drawable.checkbox_on_background)

        // Set the checkmark drawable as a compound drawable on the button
        colorButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, checkmarkDrawable, null)
    }

}

