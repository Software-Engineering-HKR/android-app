package se.hkr.smarthouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.hkr.smarthouse.ui.theme.SmartHouseTheme
import se.hkr.smarthouse.view.bottombar.BottomNavItem
import se.hkr.smarthouse.view.bottombar.BottomNavigation

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setContent {
                SmartHouseTheme {
                    val currentNavItem = remember { mutableStateOf(BottomNavItem.PROFILE) }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 25.dp, top = 60.dp, end = 25.dp, bottom = 0.dp )
                        ) {
                            ProfileCard(
                                userName = "User 1 ",
                                userEmail = "user1@example.com"
                            )
                            // Settings and logout
                            SettingsMenuComponent {
                                // Logout action
                                val intent = Intent(this@Profile, LoginActivity::class.java)
                                intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                )
                                startActivity(intent)
                                finish()
                            }
                        }



                        BottomNavigation(
                            currentNavItem = currentNavItem,
                            onNavItemClick = { item ->
                                when (item.route) {
                                    "home" -> startActivity(Intent(this@Profile, MainActivity::class.java))
                                    "sensors" -> startActivity(Intent(this@Profile, SensorScreen::class.java))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

@Composable
fun ProfileCard(
    userName: String,
    userEmail: String
) {
    Text(
        text = "Profile",
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        modifier = Modifier.padding(bottom = 30.dp) // Added bottom padding
    )
    Spacer(modifier = Modifier.height(80.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(30.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp) // Increased spacing
        ) {

            Text(
                text = "Name: $userName",
                modifier = Modifier.padding(bottom = 20.dp) // Added bottom padding
            )
            Text(
                text = "Email: $userEmail",
                modifier = Modifier.padding(bottom = 16.dp) // Added bottom padding
            )
        }
    }
}

@Composable
fun SettingsMenuComponent(onLogout: () -> Unit) {
    Spacer(modifier = Modifier.height(20.dp))
    Button(onClick = onLogout, modifier = Modifier
        .fillMaxWidth() // Set the width to match the parent
        .padding(horizontal = 25.dp)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log out",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(30f)
                )
                Icon(Icons.Rounded.ExitToApp, contentDescription = null)
            }
        }
    }

