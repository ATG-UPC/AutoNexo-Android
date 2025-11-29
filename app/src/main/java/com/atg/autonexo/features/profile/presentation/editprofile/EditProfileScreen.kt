package com.atg.autonexo.features.profile.presentation.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
private val HeaderGradientTop = Color(0xFF0E2A47)
private val HeaderGradientBottom = Color(0xFF1D3F63)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF3D5A80)
private val CardBackground = Color(0xFFFFFFFF)
private val ButtonSave = Color(0xFF5A7BA7)
private val ButtonCancel = Color(0xFF2C3E50)

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con degradado
            EditProfileHeader(onBack = onBack)

            // Card blanco con contenido
            EditProfileContent(
                uiState = uiState,
                onFullNameChange = viewModel::updateFullName,
                onEmailChange = viewModel::updateEmail,
                onPhoneNumberChange = viewModel::updatePhoneNumber,
                onSave = {
                    viewModel.saveProfile(onSaveSuccess)
                },
                onCancel = onBack,
                onChangePassword = onChangePassword
            )
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }

    // Mostrar error si existe
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // TODO: Mostrar snackbar o dialog con el error
        }
    }
}

@Composable
private fun EditProfileHeader(onBack: () -> Unit) {
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
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            // Top Bar con back y título
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
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White
                )

                // Espaciador para centrar el título
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar con icono de editar
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

                // Icono de editar en la esquina inferior derecha
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar foto",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onChangePassword: () -> Unit
) {
// Around line 182 in the provided file content
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-24).dp), // <--- This looks okay, but context matters
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Full Name
            EditProfileField(
                label = "Full Name",
                value = uiState.fullName,
                onValueChange = onFullNameChange,
                icon = Icons.Outlined.Person
            )

            // Email
            EditProfileField(
                label = "Email",
                value = uiState.email,
                onValueChange = onEmailChange,
                icon = Icons.Outlined.Email
            )

            // Phone Number
            EditProfileField(
                label = "Phone Number",
                value = uiState.phoneNumber,
                onValueChange = onPhoneNumberChange,
                icon = Icons.Outlined.Phone
            )

            // Password
            Column {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = "**********",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(
                            onClick = onChangePassword,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "Change Password?",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones Save y Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonSave
                    ),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonCancel
                    ),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                color = TextSecondary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar",
                    tint = TextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

