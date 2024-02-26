package se.hkr.smarthouse.data

import androidx.compose.runtime.MutableState
import se.hkr.smarthouse.network.WSHelper

data class Device(
    val name: String,
    val endpoint: String,
    val displayName: String,
    val status: MutableState<Boolean>,
    val statusMaskTrue: String = "On",
    val statusMaskFalse: String = "Off"
    )
