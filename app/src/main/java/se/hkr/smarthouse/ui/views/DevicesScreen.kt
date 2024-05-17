package se.hkr.smarthouse.ui.views

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import se.hkr.smarthouse.network.WSHelper
import se.hkr.smarthouse.ui.composables.DevicesComposables

@Composable
fun DevicesScreen(navController: NavHostController, focusManager: FocusManager, scrollState: ScrollState, LCDText: MutableState<String>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween // Keeps the bottom navigation at the bottom
    ) {
        // Main content of the activity
        Column(
            modifier = Modifier.fillMaxHeight(0.9f), // Leave room for the navigation bar
            verticalArrangement = Arrangement.spacedBy(15.dp),

            ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier
                        .verticalScroll(state = scrollState)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    val mainScreenComposables = DevicesComposables()
                    mainScreenComposables.TextInputCard(
                        LCDText,
                        WSHelper.LCDmessages
                    )

                    WSHelper.devices.forEach { device ->
                        mainScreenComposables.DeviceCard(device = device)
                    }
                }
            }
        }

    }
}