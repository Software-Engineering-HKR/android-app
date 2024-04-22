package se.hkr.smarthouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Co2
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.Doorbell
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.RollerShades
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WindPower
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import se.hkr.smarthouse.data.Device
import se.hkr.smarthouse.data.Sensor
import se.hkr.smarthouse.network.WSHelper
import se.hkr.smarthouse.ui.composables.DevicesComposables
import se.hkr.smarthouse.ui.theme.SmartHouseTheme
import se.hkr.smarthouse.view.bottombar.BottomNavItem
import se.hkr.smarthouse.view.bottombar.BottomNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scrollState = rememberScrollState()
            val focusManager = LocalFocusManager.current

            WSHelper.devices.apply {
                add(Device(name = "led", endpoint = "led", displayName = "White Light", status = remember { mutableStateOf(false) }, icon = Icons.Outlined.Lightbulb))
                add(Device(name = "yellow-led", endpoint = "yellow-led", displayName = "Yellow Light", status = remember { mutableStateOf(false) }, icon = Icons.Outlined.Lightbulb))
                add(Device(name = "fan", endpoint = "fan", displayName = "Fan", status = remember { mutableStateOf(false) }, icon = Icons.Outlined.WindPower))
                add(Device(name = "door", endpoint = "door", displayName = "Door", status = remember { mutableStateOf(false) }, statusMaskTrue = "Open", statusMaskFalse = "Closed", icon = Icons.Outlined.DoorFront))
                add(Device(name = "window", endpoint = "window", displayName = "Window", status = remember { mutableStateOf(false) }, statusMaskTrue = "Open", statusMaskFalse = "Closed", icon = Icons.Outlined.RollerShades))
                add(Device(name = "lock", endpoint = "lock", displayName = "Lock door", status = remember { mutableStateOf(false) }, statusMaskTrue = "Locked", statusMaskFalse = "Unlocked", icon = Icons.Outlined.LockOpen))
            }
            WSHelper.sensors.apply {
                add(Sensor(name = "doorbell", displayName = "Doorbell", reading = mutableStateOf(0), threshold = 1, icon = Icons.Outlined.Doorbell))
                add(Sensor(name = "motion", displayName = "Motion", reading = mutableStateOf(0), icon = Icons.Outlined.Sensors))
                add(Sensor(name = "light", displayName = "Photocell", reading = mutableStateOf(0), threshold = 300, icon = Icons.Outlined.BrightnessMedium))
                add(Sensor(name = "gas", displayName = "Gas", reading = mutableStateOf(0), threshold = 100, icon = Icons.Outlined.Co2))
                add(Sensor(name = "steam", displayName = "Steam", reading = mutableStateOf(0), threshold = 100, icon = Icons.Outlined.Air))
                add(Sensor(name = "moisture", displayName = "Soil humidity", reading = mutableStateOf(0), threshold = 100, low = "Dry", high = "Moist", icon = Icons.Outlined.WaterDrop))
            }

            WSHelper.initConnection("ws://${BuildConfig.SERVER_IP}:8080")

            val LCDText = remember { mutableStateOf("")}

            SmartHouseTheme {
                val currentNavItem = remember { mutableStateOf(BottomNavItem.HOME) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween // Keeps the bottom navigation at the bottom
                ) {
                    // Main content of the activity
                    Column(
                        modifier = Modifier.fillMaxHeight(0.9f), // Leave room for the navigation bar
                        verticalArrangement = Arrangement.spacedBy(15.dp),

                        ) {
                        // A surface container using the 'background' color from the theme
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
                                /*Text(
                                    text = "Welcome, user!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.Black,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(horizontal = 16.dp)
                                )*/
    
                                val mainScreenComposables = DevicesComposables()
                                mainScreenComposables.TextInputCard(LCDText, WSHelper.LCDmessages)

                                WSHelper.devices.forEach { device ->
                                    mainScreenComposables.DeviceCard(device = device)
                                }
                                mainScreenComposables.SensorCard(
                                    text = "Sensors",
                                    navigateTo = SensorScreen::class.java
                                )
                            }
                        }
                    }
                    // Bottom navigation bar
                    BottomNavigation(
                        currentNavItem = currentNavItem,
                        onNavItemClick = { item ->
                            currentNavItem.value = item
                            when (item.route) {
                                "sensors" -> startActivity(Intent(this@MainActivity, SensorScreen::class.java))
                                "profile" -> startActivity(Intent(this@MainActivity, Profile::class.java))
                            }
                        }
                    )
                }
            }
        }
    }
            override fun onDestroy() {
                super.onDestroy()
                WSHelper.closeConnection()
            }
        }

