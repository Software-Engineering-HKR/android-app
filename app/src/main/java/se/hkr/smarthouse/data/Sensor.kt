package se.hkr.smarthouse.data

import androidx.compose.runtime.MutableState

    data class Sensor(
        val name: String,
        val endpoint: String,
        val displayName: String,
        val status: MutableState<Boolean>
    )

