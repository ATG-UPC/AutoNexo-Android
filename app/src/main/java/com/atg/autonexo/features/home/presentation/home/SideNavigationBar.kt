package com.atg.autonexo.features.home.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atg.autonexo.R

// Colores del diseño
private val DrawerBackground = Color(0xFF1E1E1E)
private val DrawerHeaderTop = Color(0xFF1E2B36)
private val DrawerHeaderBottom = Color(0xFF253442)
private val TextWhite = Color(0xFFFFFFFF)
private val TextLightGray = Color(0xFFCFCFCF)
private val SelectedBlue = Color(0xFF5F7DAA)
private val IconWhite = Color(0xFFFFFFFF).copy(alpha = 0.85f)

enum class Language {
    SPANISH, ENGLISH
}

enum class Theme {
    DARK, LIGHT
}

@Composable
fun SideNavigationBar(
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf(Language.ENGLISH) }
    var selectedTheme by remember { mutableStateOf(Theme.LIGHT) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 16.dp)
    ) {
        // Profile
        DrawerMenuItem(
            icon = Icons.Default.Person,
            text = "Profile",
            onClick = { onNavigate(BottomNavItem.Profile.route) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Payment
        DrawerMenuItem(
            icon = Icons.Default.ShoppingCart,
            text = "Payment",
            onClick = { onNavigate(BottomNavItem.Payment.route) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Language",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    color = TextLightGray
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    color = TextLightGray
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ThemeSelector(
                selectedTheme = selectedTheme,
                onThemeSelected = { selectedTheme = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout
        DrawerMenuItem(
            icon = Icons.Default.PowerSettingsNew,
            text = "Logout",
            onClick = onLogoutClick,
            isLogout = true
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = if (isLogout) MaterialTheme.colorScheme.error else IconWhite
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = if (isLogout) MaterialTheme.colorScheme.error else TextWhite
            )
        }
    }
}

@Composable
private fun LanguageSelector(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = TextWhite.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Español
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp))
                    .background(
                        if (selectedLanguage == Language.SPANISH) SelectedBlue else Color.Transparent
                    )
                    .clickable { onLanguageSelected(Language.SPANISH) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Español",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextWhite
                )
            }

            // English
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                    .background(
                        if (selectedLanguage == Language.ENGLISH) SelectedBlue else Color.Transparent
                    )
                    .clickable { onLanguageSelected(Language.ENGLISH) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "English",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextWhite
                )
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = TextWhite.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Dark Mode (Luna)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp))
                    .background(
                        if (selectedTheme == Theme.DARK) SelectedBlue else Color.Transparent
                    )
                    .clickable { onThemeSelected(Theme.DARK) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = "Dark Mode",
                    modifier = Modifier.size(20.dp),
                    tint = TextWhite
                )
            }

            // Light Mode (Sol)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                    .background(
                        if (selectedTheme == Theme.LIGHT) SelectedBlue else Color.Transparent
                    )
                    .clickable { onThemeSelected(Theme.LIGHT) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LightMode,
                    contentDescription = "Light Mode",
                    modifier = Modifier.size(20.dp),
                    tint = TextWhite
                )
            }
        }
    }
}

