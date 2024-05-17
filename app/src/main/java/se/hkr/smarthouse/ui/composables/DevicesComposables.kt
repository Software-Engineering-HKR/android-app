@file:OptIn(ExperimentalMaterial3Api::class)

package se.hkr.smarthouse.ui.composables

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
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
    fun TextInputCard(LCDText: MutableState<String>, LCDmessages: SnapshotStateList<String>) {
        ElevatedCard(
            Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 6.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                ) {
                    Text(
                        text = "Set your message",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(horizontal = 4.dp)
                    )
                    if (!LCDmessages.isEmpty()){
                        Text(
                            text = "Current message: ${LCDmessages[LCDmessages.size-1]}", // TODO : display last message even if it didn't get added to LCDmessages
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 4.dp)
                                .alpha(0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LCDMessagesDropdown(LCDmessages, LCDText)
                    }
                    LCDTextInputField(LCDText)
                    LCDButton(message = LCDText, focusManager = LocalFocusManager.current)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LCDMessagesDropdown(LCDmessages: SnapshotStateList<String>, LCDText: MutableState<String>){
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf("Previous messages") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedOptionText,
                onValueChange = { },
                //label = { Text("Label") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                LCDmessages.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            LCDText.value = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LCDTextInputField(text: MutableState<String>) {
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
    fun LCDButton(message: MutableState<String>, focusManager: FocusManager) {
        Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                onClick = { WSHelper.sendMessage(message.value, "LCD", "message")
                            focusManager.clearFocus()
                          },
                modifier = Modifier
                    .padding(top = 6.dp)
                .fillMaxWidth()
        ) {
                Text("Set new message".uppercase())
        }
    }

    @Composable
    fun DeviceSwitch(device: Device) {
        Switch(
            checked = device.status.value,
            onCheckedChange = { isChecked ->
                WSHelper.toggleDevice(device.status, device.endpoint)
                device.status.value = isChecked
            }
        )
    }

    @Composable
    fun DeviceCard(device: Device) {
        ElevatedCard(
            Modifier
                .fillMaxWidth()
                .padding(0.4.dp)
                .alpha(if (device.status.value) 1.0f else 0.5f),
            colors = CardDefaults.cardColors(
                containerColor = if (!device.status.value) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (!device.status.value) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
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
                            style = MaterialTheme.typography.bodyMedium,
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
}
