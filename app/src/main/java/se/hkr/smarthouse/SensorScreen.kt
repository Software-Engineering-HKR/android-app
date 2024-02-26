package se.hkr.smarthouse

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import android.graphics.drawable.GradientDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import se.hkr.smarthouse.network.WSHelper

class SensorScreen : ComponentActivity() {

    private lateinit var motionSensorStateIndicator: TextView
    private lateinit var moistureSensorStateIndicator: TextView
    private var updateJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {

                topMargin = 15
                leftMargin = 10
                gravity = Gravity.START
            }
            setOnClickListener {
                onBackPressed()
            }
        }
        mainLayout.addView(backButton)

        val title = TextView(this).apply {
            text = "Sensor"
            textSize = 30f
            setTextColor(ContextCompat.getColor(this@SensorScreen, R.color.black))

            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.START
                setMargins(80, 90, 0, 0)
            }
        }
        mainLayout.addView(title)

        motionSensorStateIndicator = createSensorStateIndicator("Motion Sensor")
        moistureSensorStateIndicator = createSensorStateIndicator("Moisture Sensor")

        mainLayout.addView(motionSensorStateIndicator)
        mainLayout.addView(moistureSensorStateIndicator)

        setContentView(mainLayout)


        startObservingSensorStates()
    }
    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
    }

    private fun startObservingSensorStates() {
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {

                val motionSensor = WSHelper.devices.find { it.name == "motion_sensor" }
                val moistureSensor = WSHelper.devices.find { it.name == "moisture_sensor" }

                motionSensor?.let {
                    updateSensorState(motionSensorStateIndicator, it.status.value)
                }
                moistureSensor?.let {
                    updateSensorState(moistureSensorStateIndicator, it.status.value)
                }

                delay(5000)
            }
        }
    }


    private fun createSensorStateIndicator(initialText: String): TextView = TextView(this).apply {
        text = initialText
        textSize = 19f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)

        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            200 // Explicit height
        ).apply {
            topMargin = 50
            bottomMargin = 10
            leftMargin = 60
            rightMargin = 60

            // Create a GradientDrawable with rounded corners
        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.LTGRAY)
            cornerRadius = 80f
        }

        background = backgroundDrawable
    }
    }

    private fun updateSensorState(indicator: TextView, sensorOn: Boolean) {
        indicator.text =
            if (sensorOn) "${indicator.text.split(":")[0]}: ON" else "${indicator.text.split(":")[0]}: OFF"
        indicator.setBackgroundColor(if (sensorOn) Color.BLUE else Color.GRAY)
    }}
