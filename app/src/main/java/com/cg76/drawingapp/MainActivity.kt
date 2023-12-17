package com.cg76.drawingapp

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
import com.cg76.drawingapp.databinding.ColorPopupBinding

import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLESSurfaceView
    private lateinit var colorPickerButton: ImageButton

    private val colorPopupBinding : ColorPopupBinding by lazy {
        ColorPopupBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.glSurfaceView)


        var navigateBTN = findViewById<LinearLayout>(R.id.navigate)
        colorPickerButton = findViewById<ImageButton>(R.id.btn_colors)


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
            window!!.setBackgroundDrawable(ColorDrawable(Color.argb(128, 0, 0, 0)))

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
        colorPopupBinding.done.setOnClickListener{
            //colorPopup.visibility = View.GONE
            colorPopup.dismiss()
            val color = setRGBColor()
            println(color);
            navigateBTN.visibility = View.VISIBLE
        }
        colorPickerButton.setOnClickListener {
            //colorPopup.visibility = View.VISIBLE
            colorPopup.show()
            navigateBTN.visibility = View.GONE

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

    private fun setRGBColor(): FloatArray {
        val color = floatArrayOf(colorPopupBinding.redLayout.seekBar.progress.toFloat()/256,
            colorPopupBinding.greenLayout.seekBar.progress.toFloat()/256,colorPopupBinding.blueLayout.seekBar.progress.toFloat()/256)
        val hex = String.format(
            "#%02x%02x%02x",
            colorPopupBinding.redLayout.seekBar.progress,
            colorPopupBinding.greenLayout.seekBar.progress,
            colorPopupBinding.blueLayout.seekBar.progress
        )
        colorPopupBinding.viewColor.setBackgroundColor(Color.parseColor(hex))
        return color
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }



}
