package se.hkr.smarthouse.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector

data class Device(
    val name: String,
    val endpoint: String,
    val displayName: String,
    val status: MutableState<Boolean>,
    val statusMaskTrue: String = "On",
    val statusMaskFalse: String = "Off",
    val icon: ImageVector = Icons.Outlined.Home
    )
