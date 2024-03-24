package se.hkr.smarthouse.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector

data class Sensor(
        val name: String,
        val displayName: String,
        val reading: MutableState<Int>, //changed from "value" to avoid confusion when calling sensor.reading.value
        val threshold: Int = 1,
        val low: String = "Off",
        val high: String = "On",
        val icon: ImageVector = Icons.Outlined.Sensors,
    )

