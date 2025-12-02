package com.atg.autonexo.features.home.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomNavItem("home", "Inicio", Icons.Default.Home)
    object Requests : BottomNavItem("matching/request", "Solicitudes", Icons.Default.Notifications)
    object Services : BottomNavItem("home/services", "Servicios", Icons.Default.Build)
    object WorkshopRegistration :
        BottomNavItem("workshop/registration/basic_info", "Registro", Icons.Default.Add)

    object Profile : BottomNavItem("home/profile", "Perfil", Icons.Default.Person)
    object Payment : BottomNavItem("payment/subscription", "Pago", Icons.Default.Payment)

}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Requests,
        BottomNavItem.Services,
        BottomNavItem.WorkshopRegistration,
        BottomNavItem.Profile,
    )

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

