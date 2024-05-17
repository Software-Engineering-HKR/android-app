package se.hkr.smarthouse.ui.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import se.hkr.smarthouse.network.WSHelper

@Composable
fun ProfileScreenContent(navController: NavHostController, scrollState: ScrollState, context: Context){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
            ProfileCard(
                userName = WSHelper.username.value
            )
            // Settings and logout
            SettingsMenuComponent {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                }
                context.startActivity(intent)
                // Assuming this composable is called from an Activity, you might want to finish it as well
                (context as? Activity)?.finish()
            }
        }
    }

@Composable
fun ProfileCard(userName: String) {
    Text(
        text = "Profile",
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 14.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Username: $userName",
                style = MaterialTheme.typography.bodyLarge,
                //color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun SettingsMenuComponent(onLogout: () -> Unit) {
    Button(onClick = onLogout, modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = "Log out",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(30f)
                )
                Icon(Icons.Rounded.ExitToApp, contentDescription = null)
            }
        }
    }

