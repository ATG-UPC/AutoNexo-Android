package com.atg.autonexo.features.iam.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.SettingRow
import com.atg.autonexo.core.ui.components.SwitchRow

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header curvo con información del usuario
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            // Fondo con degradado y forma curva
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF202D36),
                                Color(0xFF2E3C47)
                            )
                        ),
                        shape = BottomArcShape(64.dp)
                    )
            ) {
                // Barra superior con navegación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.White
                        )
                    }
                }
            }

            // Avatar y datos del usuario (centrado y superpuesto)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCCCCCC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nombre
                Text(
                    text = profile?.fullName ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rol
                Text(
                    text = profile?.role ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                Text(
                    text = "${profile?.rating ?: 0.0} ★",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Contenido scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            // Email
            SettingRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = profile?.email ?: ""
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone Number
            SettingRow(
                icon = Icons.Default.Phone,
                label = "Phone Number",
                value = profile?.phoneNumber ?: ""
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Current Workshop
            SettingRow(
                icon = Icons.Default.Business,
                label = "Current Workshop",
                value = profile?.currentWorkshop ?: "",
                showDivider = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications section
            Text(
                text = "Notifications",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4682B4),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SwitchRow(
                label = "Message notifications",
                checked = profile?.messageNotifications ?: false,
                onCheckedChange = { viewModel.updateMessageNotifications(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SwitchRow(
                label = "Offers notifications",
                checked = profile?.offersNotifications ?: false,
                onCheckedChange = { viewModel.updateOffersNotifications(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

