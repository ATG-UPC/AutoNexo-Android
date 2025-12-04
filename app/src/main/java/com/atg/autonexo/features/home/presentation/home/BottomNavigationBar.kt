package com.atg.autonexo.features.home.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Requests : BottomNavItem("home/requests", "Requests", Icons.Default.Mail)
    object Services : BottomNavItem("home/services", "Services", Icons.Default.DateRange)
    object WorkshopRegistration :
        BottomNavItem("workshop/registration/basic_info", "Workshop", Icons.Default.CarRepair)

    object Offers : BottomNavItem("home/offers", "Offers", Icons.Default.MonetizationOn)
    object Profile : BottomNavItem("home/profile", "Profile", Icons.Default.Person)
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
        BottomNavItem.Offers,
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

