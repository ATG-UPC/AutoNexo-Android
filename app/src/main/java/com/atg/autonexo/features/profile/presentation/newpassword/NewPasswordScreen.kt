package com.atg.autonexo.features.profile.presentation.newpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun NewPasswordScreen(
    viewModel: NewPasswordViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onPasswordChanged: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isPasswordChanged) {
        if (uiState.isPasswordChanged) {
            onPasswordChanged()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con degradado
            NewPasswordHeader(onBack = onBack)

            // Card blanco con contenido
            NewPasswordContent(
                uiState = uiState,
                onNewPasswordChange = viewModel::updateNewPassword,
                onRepeatPasswordChange = viewModel::updateRepeatPassword,
                onSave = {
                    viewModel.changePassword(onPasswordChanged)
                },
                onCancel = onBack
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
private fun NewPasswordHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(HeaderGradientTop, HeaderGradientBottom)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
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
                text = "New Password",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color.White
            )

            // Espaciador para centrar el título
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
private fun NewPasswordContent(
    uiState: NewPasswordUiState,
    onNewPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
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
                .padding(horizontal = 20.dp)
                .padding(top = 32.dp, bottom = 24.dp),

            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Change Password field
            Column {
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.newPassword,
                    onValueChange = onNewPasswordChange,
                    placeholder = {
                        Text(
                            text = "Type your new password",
                            color = TextSecondary.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Repeat Password field
            Column {
                Text(
                    text = "Repeat Password",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        color = TextSecondary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.repeatPassword,
                    onValueChange = onRepeatPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
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

