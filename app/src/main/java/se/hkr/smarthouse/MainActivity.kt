package se.hkr.smarthouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.House
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.hkr.smarthouse.data.Device
import se.hkr.smarthouse.data.Sensor
import se.hkr.smarthouse.network.WSHelper
import se.hkr.smarthouse.ui.composables.Devices
import se.hkr.smarthouse.ui.theme.SmartHouseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scrollState = rememberScrollState()

            WSHelper.devices.apply {
                add(Device(name = "led", endpoint = "led", displayName = "White Light", status = remember { mutableStateOf(false) }))
                add(Device(name = "yellow-led", endpoint = "yellow-led", displayName = "Yellow Light", status = remember { mutableStateOf(false) }))
                add(Device(name = "fan", endpoint = "fan", displayName = "Fan", status = remember { mutableStateOf(false) }))
                add(Device(name = "door", endpoint = "door", displayName = "Door", status = remember { mutableStateOf(false) }, statusMaskTrue = "Open", statusMaskFalse = "Closed"))
                add(Device(name = "window", endpoint = "window", displayName = "Window", status = remember { mutableStateOf(false) }, statusMaskTrue = "Open", statusMaskFalse = "Closed"))
            }
            WSHelper.sensors.apply {
                add(Sensor(name = "motion", displayName = "Motion Sensor", status = mutableStateOf(0)))
                add(Sensor(name = "light", displayName = "Photocell Sensor", status = mutableStateOf(0)))
                add(Sensor(name = "gas", displayName = "Gas Sensor", status = mutableStateOf(0)))
                add(Sensor(name = "steam", displayName = "Steam Sensor", status = mutableStateOf(0)))
                add(Sensor(name = "moisture", displayName = "Soil humidity Sensor", status = mutableStateOf(0)))
            }

            WSHelper.initConnection("ws://${BuildConfig.SERVER_IP}:8080")

            val LCDText = remember { mutableStateOf("Your message here")}

            SmartHouseTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            //ScaffoldExample()
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .verticalScroll(state = scrollState)
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Welcome, user!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.Black,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                val Composables = Devices()
                                Composables.TextInputCard(LCDText)

                                WSHelper.devices.forEach { device ->
                                    DeviceCard(device = device)
                                }

                                SensorCard(
                                    text = "Sensor",
                                    navigateTo = SensorScreen::class.java
                                )
                                }
                            }
                        }
                    }
    }
    override fun onDestroy() {
        super.onDestroy()
        WSHelper.closeConnection()
    }
}

@Composable
fun DeviceSwitch(device: Device) {
    Switch(
        modifier = Modifier.semantics { contentDescription = "Demo" },
        checked = device.status.value,
        onCheckedChange = { isChecked ->
            // Assuming toggleDevice is now a static method in WSHelper
            WSHelper.toggleDevice(device.status, device.endpoint)
            device.status.value = isChecked // Optimistically update the UI
        })
}

@Composable
fun DeviceCard(device: Device) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    //modifier = Modifier
                        //.padding(16.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.House, contentDescription = null)
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    Text(
                        text = device.displayName.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.Start),
                    )
                    Text(
                        text = (if (device.status.value) device.statusMaskTrue else device.statusMaskFalse).uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                //modifier = Modifier
                    //.padding(16.dp),
            ) {
                DeviceSwitch(device)
            }
        }
    }
}

@Composable
fun SensorCard(
    text: String,
    navigateTo: Class<*>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 15.dp)
            .clickable {
                context.startActivity(Intent(context, navigateTo))
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontSize = 27.sp

            )
        }
    }
}