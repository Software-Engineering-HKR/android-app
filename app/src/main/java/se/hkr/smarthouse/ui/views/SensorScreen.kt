package se.hkr.smarthouse.ui.views

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import se.hkr.smarthouse.data.Sensor
import se.hkr.smarthouse.network.WSHelper

@Composable
fun SensorScreenContent(navController: NavHostController, scrollState: ScrollState) {
    Column(
        modifier = Modifier
            .verticalScroll(state = scrollState)
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp) ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sensors",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    //modifier = Modifier.padding(bottom = 24.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ){
                    WSHelper.sensors.forEach { sensor ->
                        SensorCard(sensor = sensor)
                    }
                }
            }
        }
    }
}

@Composable
fun SensorCard(sensor: Sensor) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(0.4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (sensor.reading.value < sensor.threshold) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (sensor.reading.value < sensor.threshold) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
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
                    Icon(imageVector = sensor.icon, contentDescription = null,
                        modifier = Modifier
                            .size(54.dp))
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = sensor.displayName.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.Start),
                    )

                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = if (sensor.reading.value < sensor.threshold) sensor.low.uppercase() else sensor.high.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start),
                    maxLines = 1
                )
            }
        }
    }
}