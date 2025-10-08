package com.atg.autonexo.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Request : BottomNavItem("request", Icons.Default.Inbox, "Request")
    object Offer : BottomNavItem("offer", Icons.Default.Add, "Offer")
    object Workshop : BottomNavItem("workshop", Icons.Default.DirectionsCar, "Workshop")
    object Service : BottomNavItem("service", Icons.Default.Build, "Service")
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Request,
        BottomNavItem.Offer,
        BottomNavItem.Workshop,
        BottomNavItem.Service
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                items.forEachIndexed { index, item ->
                    if (item is BottomNavItem.Offer) {
                        // Espacio para el FAB central
                        Spacer(modifier = Modifier.width(70.dp))
                    } else {
                        BottomNavItemButton(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = { onNavigate(item.route) }
                        )
                    }
                }
            }
            
            // FAB central flotante
            FloatingActionButton(
                onClick = { onNavigate(BottomNavItem.Offer.route) },
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-8).dp),
                shape = CircleShape,
                containerColor = Color(0xFF5E7B97),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Offer",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemButton(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(85.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) Color(0xFF000000) else Color(0xFF6B6B6B),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF000000) else Color(0xFF6B6B6B),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

