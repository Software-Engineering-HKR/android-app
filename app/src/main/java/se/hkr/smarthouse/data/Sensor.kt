package se.hkr.smarthouse.data

import androidx.compose.runtime.MutableState

    data class Sensor(
        val name: String,
        val displayName: String,
        val reading: MutableState<Int>, //changed from "value" to avoid confusion when calling sensor.reading.value
        val threshold: Int = 1,
        val low: String = "Off",
        val high: String = "On",
    )

