package com.atg.autonexo.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerContent(
    userName: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToPayment: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var isDarkTheme by remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = modifier.width(300.dp),
        drawerContainerColor = Color(0xFF2E3C47)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo placeholder (usar Image con painterResource cuando exista)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Autonexo",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // TODO: Reemplazar con logo real
                // Image(
                //     painter = painterResource(R.drawable.autonexo_logo),
                //     contentDescription = "Autonexo Logo"
                // )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu items
            DrawerMenuItem(
                icon = Icons.Default.Person,
                label = "Profile",
                onClick = {
                    onNavigateToProfile()
                    onClose()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Payment,
                label = "Payment",
                onClick = {
                    onNavigateToPayment()
                    onClose()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Support,
                label = "Support and Assistance",
                onClick = {
                    onNavigateToSupport()
                    onClose()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Description,
                label = "Terms of use",
                onClick = {
                    onNavigateToTerms()
                    onClose()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Security,
                label = "Privacy Policy",
                onClick = {
                    onNavigateToPrivacy()
                    onClose()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Logout,
                label = "Logout",
                onClick = {
                    onLogout()
                    onClose()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Language selector
            Text(
                text = "Language",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SegmentedControl(
                options = listOf("Español", "English"),
                selectedOption = selectedLanguage,
                onOptionSelected = { selectedLanguage = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Theme selector
            Text(
                text = "Theme",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SegmentedControl(
                options = listOf("Dark", "Light"),
                selectedOption = if (isDarkTheme) "Dark" else "Light",
                onOptionSelected = { isDarkTheme = it == "Dark" },
                modifier = Modifier.fillMaxWidth(),
                iconStart = Icons.Default.DarkMode,
                iconEnd = Icons.Default.LightMode
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF4682B4)
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun SegmentedControl(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    iconStart: ImageVector? = null,
    iconEnd: ImageVector? = null
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .background(
                color = Color(0xFF202D36),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selectedOption
            
            Button(
                onClick = { onOptionSelected(option) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF4682B4) else Color.Transparent,
                    contentColor = if (isSelected) Color.White else Color(0xFF9E9E9E)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isSelected) 2.dp else 0.dp
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (index == 0 && iconStart != null) {
                        Icon(
                            imageVector = iconStart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = option,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                    
                    if (index == 1 && iconEnd != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = iconEnd,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

