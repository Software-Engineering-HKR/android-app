package se.hkr.interactivehouse.data

import androidx.compose.runtime.MutableState
import se.hkr.interactivehouse.network.WSHelper

data class Device (val name: String, val endpoint: String, val displayName: String, val status: MutableState<Boolean>) {
    val wsHelper : WSHelper = WSHelper(ledStatus = status)
}