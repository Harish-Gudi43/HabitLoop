package com.uk.ac.tees.mad.habitloop.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.uk.ac.tees.mad.habitloop.presentation.navigation.GraphRoutes

@Composable
fun BottomNavigationBar(selectedTitle: String, navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home, GraphRoutes.DashBoard),
        BottomNavItem("Add Habit", Icons.Default.Add, GraphRoutes.AddHabbit),
        BottomNavItem("Profile", Icons.Default.Person, GraphRoutes.Profile),
        BottomNavItem("Settings", Icons.Default.Settings, GraphRoutes.Settings),
        BottomNavItem("Notifications", Icons.Default.Notifications, GraphRoutes.Notifications)
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = item.title == selectedTitle,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val title: String, val icon: ImageVector, val route: GraphRoutes)
