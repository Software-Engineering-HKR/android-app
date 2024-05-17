package se.hkr.smarthouse.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import se.hkr.smarthouse.MainActivity
import se.hkr.smarthouse.R
import se.hkr.smarthouse.network.WSHelper
import se.hkr.smarthouse.ui.theme.SmartHouseTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedBundle: Bundle?) {
        super.onCreate(savedBundle)
        setContent {
            SmartHouseTheme {
                val navController = rememberNavController()

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
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { data ->
            Snackbar(
                containerColor  = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(vertical = 60.dp),
                snackbarData = data
            )
        } },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.applogo),
                    contentDescription = "app icon",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Log in", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
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
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                //var errorToast = Toast.makeText(LocalContext.current, "Invalid credentials", Toast.LENGTH_LONG)
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        WSHelper.authenticate(username, password, isRegistration = false, onLoginSuccess)
                        {scope.launch {
                            snackbarHostState.showSnackbar(
                                "Invalid credentials"
                            )
                        } }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Login")
                }

                OutlinedButton(
                    onClick = {
                        onRegistrationClick()
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    )
}



