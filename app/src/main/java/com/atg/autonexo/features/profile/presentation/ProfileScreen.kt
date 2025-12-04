package com.atg.autonexo.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// Colores del diseño Figma
private val HeaderGradientTop = Color(0xFF1E2B36)
private val HeaderGradientBottom = Color(0xFF253442)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF3D5A80)
private val SwitchActive = Color(0xFF3D5A80)
private val SwitchInactive = Color(0xFFD9D9D9)
private val CardBackground = Color(0xFFFFFFFF)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onEditWorkshop: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Ocurrió un error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadProfile() }) {
                        Text("Reintentar")
                    }
                }
            }

            uiState.profile != null -> {
                ProfileContent(
                    profile = uiState.profile!!,
                    messageNotificationsEnabled = uiState.messageNotificationsEnabled,
                    offersNotificationsEnabled = uiState.offersNotificationsEnabled,
                    onMessageNotificationsChanged = { enabled ->
                        viewModel.updateMessageNotifications(enabled)
                    },
                    onOffersNotificationsChanged = { enabled ->
                        viewModel.updateOffersNotifications(enabled)
                    },
                    onBack = onBack,
                    onEditProfile = onEditProfile,
                    onEditWorkshop = onEditWorkshop
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: com.atg.autonexo.features.profile.domain.models.Profile,
    messageNotificationsEnabled: Boolean,
    offersNotificationsEnabled: Boolean,
    onMessageNotificationsChanged: (Boolean) -> Unit,
    onOffersNotificationsChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onEditWorkshop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con degradado
        ProfileHeader(
            user = profile.user,
            onBack = onBack,
            onEdit = onEditProfile
        )

        // Card blanco con contenido
        ProfileDetailsCard(
            user = profile.user,
            workshop = profile.workshop,
            messageNotificationsEnabled = messageNotificationsEnabled,
            offersNotificationsEnabled = offersNotificationsEnabled,
            onMessageNotificationsChanged = onMessageNotificationsChanged,
            onOffersNotificationsChanged = onOffersNotificationsChanged,
            onEditWorkshop = onEditWorkshop
        )
    }
}

@Composable
private fun ProfileHeader(
    user: com.atg.autonexo.features.auth.domain.models.User,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(HeaderGradientTop, HeaderGradientBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Top Bar con back, título y edit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White
                )

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar perfil",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp
                ),
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rol
            val roleDisplayName = when {
                user.roles.contains(com.atg.autonexo.features.auth.domain.models.Role.WORKSHOP_MANAGER) -> "Workshop Manager"
                user.roles.contains(com.atg.autonexo.features.auth.domain.models.Role.WORKSHOP_EMPLOYEE) -> "Mechanic"
                user.roles.contains(com.atg.autonexo.features.auth.domain.models.Role.CAR_OWNER) -> "Car Owner"
                else -> "Usuario"
            }

            Text(
                text = roleDisplayName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                ),
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating (placeholder - se puede obtener del backend en el futuro)
            Text(
                text = "4.5 ★",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ProfileDetailsCard(
    user: com.atg.autonexo.features.auth.domain.models.User,
    workshop: com.atg.autonexo.features.workshop.domain.models.Workshop?,
    messageNotificationsEnabled: Boolean,
    offersNotificationsEnabled: Boolean,
    onMessageNotificationsChanged: (Boolean) -> Unit,
    onOffersNotificationsChanged: (Boolean) -> Unit,
    onEditWorkshop: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-24).dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp)
        ) {
            // Email
            ProfileDetailRow(
                icon = Icons.Outlined.Email,
                label = "Email",
                value = user.email
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Phone Number
            ProfileDetailRow(
                icon = Icons.Outlined.Phone,
                label = "Phone Number",
                value = user.phoneNumber
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Current Workshop
            ProfileDetailRow(
                icon = Icons.Outlined.Build,
                label = "Current Workshop",
                value = workshop?.name ?: "No workshop"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Notifications Section
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    color = TextSecondary
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Message notifications
            NotificationSwitchRow(
                icon = Icons.Outlined.Email,
                label = "Message notifications",
                enabled = messageNotificationsEnabled,
                onCheckedChange = onMessageNotificationsChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Offers notifications
            NotificationSwitchRow(
                icon = Icons.Outlined.Notifications,
                label = "Offers notifications",
                enabled = offersNotificationsEnabled,
                onCheckedChange = onOffersNotificationsChanged
            )
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            )
        }
    }
}

@Composable
private fun NotificationSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            )
        }

        Switch(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SwitchActive,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = SwitchInactive
            )
        )
    }
}
