package se.hkr.smarthouse

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class SensorScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 320f, resources.displayMetrics).toInt()

        val buttonHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 75f, resources.displayMetrics).toInt() // 60dp height

        val mainLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(17, 50, 17, 16)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP or Gravity.START
                topMargin = 17
                leftMargin = 25
            }
            text = "Sensor"
            textSize = 30f
            setTextColor(ContextCompat.getColor(this@SensorScreen, R.color.black))
        }

        val buttonSensorOn = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(sizeInPixels, buttonHeight).apply {
                gravity = Gravity.CENTER
                topMargin = 80
            }
            text = "Motion sensor: ON"
            setTextColor(Color.WHITE)
            background = ContextCompat.getDrawable(this@SensorScreen, R.drawable.on_button) // Ensure you have this drawable
            textSize = 18f
            setOnClickListener { // Handle the server
            }
        }
        val buttonSensorOff = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(sizeInPixels, buttonHeight).apply {
                gravity = Gravity.CENTER
                topMargin = 16
            }
            text = "Motion sensor: OFF"
            setTextColor(Color.WHITE)
            background = ContextCompat.getDrawable(this@SensorScreen, R.drawable.off_button) // Ensure you have this drawable
            textSize = 18f
            visibility = Button.VISIBLE // Initially hidden
            setOnClickListener {
                // Handle server
            }
        }
        mainLayout.addView(title)
        mainLayout.addView(buttonSensorOn)
        mainLayout.addView(buttonSensorOff)



        setContentView(mainLayout)


    }

}