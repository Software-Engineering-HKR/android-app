package se.hkr.smarthouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import se.hkr.smarthouse.data.Sensor
import se.hkr.smarthouse.network.WSHelper

class SensorScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorScreenContent()
        }
    }
}

@Composable
fun SensorScreenContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        BackButton()
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ){ DisplaySensors()}

    }
}

@Composable
fun BackButton() {
    val context = LocalContext.current
    Button(onClick = { (context as? ComponentActivity)?.finish() }) {
        Text("Back")
    }
}

@Composable
fun DisplaySensors() {
    WSHelper.sensors.forEach { sensor ->
        SensorIndicator(sensor = sensor)
    }
}

@Composable
fun SensorIndicator(sensor: Sensor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = sensor.displayName, style = MaterialTheme.typography.bodyLarge)
            Text(
                //text = if (sensor.status.value) "ON" else "OFF",
                //color = if (sensor.status.value) Color.Blue else Color.Gray
                text = if (sensor.reading.value < sensor.threshold) sensor.low else sensor.high,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}