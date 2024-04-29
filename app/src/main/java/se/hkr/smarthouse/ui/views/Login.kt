package se.hkr.smarthouse.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import se.hkr.smarthouse.MainActivity
import se.hkr.smarthouse.network.WSHelper
import se.hkr.smarthouse.ui.theme.SmartHouseTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedBundle: Bundle?) {
        super.onCreate(savedBundle)
        setContent {
            SmartHouseTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                // Setting up NavHost with NavGraph
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreenContent(onLoginSuccess = {
                            // Navigate to the MainActivity on login success
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }) {
                            // Navigate to the registration screen when the registration button is clicked
                            navController.navigate("registration")
                        }
                    }
                    composable("registration") {
                        RegistrationScreenContent(onRegistrationSuccess = {
                            // After successful registration, navigate back to the login screen
                                navController.navigate("login")
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(onLoginSuccess: () -> Unit, onRegistrationClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                WSHelper.authenticate(username, password, isRegistration = false, onLoginSuccess) //TODO: display error message on invalid credentials
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Button(
            onClick = {
                onRegistrationClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}



