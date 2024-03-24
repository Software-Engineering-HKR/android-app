@file:OptIn(ExperimentalMaterial3Api::class)

package se.hkr.smarthouse.ui.composables

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.hkr.smarthouse.data.Device
import se.hkr.smarthouse.network.WSHelper


class DevicesComposables() {
    @Composable
    fun TextInputCard(LCDText: MutableState<String>) {
        ElevatedCard(
            Modifier
                .fillMaxWidth()
                .padding(0.4.dp),
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
                Column(
                ) {
                    OutlinedTextField(LCDText)
                    LCDButton(message = LCDText)
                }
            }
        }
    }
}

@Composable
fun OutlinedTextField(text: MutableState<String>) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text.value,
        onValueChange = { text.value = it },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        label = { Text("Your message here") }
    )
}

@Composable
fun LCDButton(message: MutableState<String>) {
    Button(onClick = { WSHelper.sendMessage(message.value, "LCD", "message") }, modifier = Modifier
        .padding(top = 16.dp)
        .fillMaxWidth()) {
        Text("Set new message")
    }
}

@Composable
fun DeviceSwitch(device: Device) {
    Switch(
        checked = device.status.value,
        onCheckedChange = { isChecked ->
            WSHelper.toggleDevice(device.status, device.endpoint)
            device.status.value = isChecked
        })
}

@Composable
fun DeviceCard(device: Device) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(0.4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = device.icon, contentDescription = null,
                        modifier = Modifier
                            .size(54.dp))
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp)
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
                modifier = Modifier
                .padding(end = 8.dp),
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
            .padding(horizontal = 0.dp, vertical = 8.dp)
            .clickable {
                context.startActivity(Intent(context, navigateTo))
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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