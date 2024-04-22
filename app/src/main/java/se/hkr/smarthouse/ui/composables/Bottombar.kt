package se.hkr.smarthouse.view.bottombar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Enum for bottom navigation items
enum class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    HOME("Devices", Icons.Rounded.Home, "Devices"),
    SENSORS("Sensors", Icons.Rounded.BarChart, "Sensors"),
    PROFILE("Profile", Icons.Rounded.Person, "Profile")
}

@Composable
fun BottomNavigation(
    navController: NavHostController
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        BottomNavItem.values().forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { if (currentRoute != item.route) navController.navigate(item.route) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = { Text(item.title) },
                alwaysShowLabel = true
            )
        }
    }
}