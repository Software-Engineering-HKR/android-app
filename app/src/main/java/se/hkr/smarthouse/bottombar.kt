package se.hkr.smarthouse.view.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// Enum for bottom navigation items
enum class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    HOME("home", Icons.Rounded.Home, "Home"),
    SENSORS("sensors", Icons.Rounded.BarChart, "Sensors"),
    PROFILE("profile", Icons.Rounded.Person, "Profile")
}

// Bottom navigation composable
@Composable
fun BottomNavigation(
    currentNavItem: MutableState<BottomNavItem>,
    onNavItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        BottomNavItem.values().forEach { item ->
            NavigationBarItem(
                selected = currentNavItem.value == item,
                onClick = { onNavItemClick(item) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = { Text(item.title) },
                alwaysShowLabel = true
            )
        }
    }
}
