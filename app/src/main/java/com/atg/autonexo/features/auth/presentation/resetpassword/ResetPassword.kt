package com.atg.autonexo.features.auth.presentation.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.theme.AppTheme
import com.atg.autonexo.features.auth.presentation.components.AuthBackButton
import com.atg.autonexo.features.auth.presentation.resetpassword.ResetPasswordUiState
import com.atg.autonexo.features.auth.presentation.resetpassword.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    navController: NavController = rememberNavController(),
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ResetPasswordContent(
        uiState = uiState,
        onNewPasswordChange = viewModel::updateNewPassword,
        onRepeatPasswordChange = viewModel::updateRepeatPassword,
        onSubmitClick = viewModel::resetPassword,
        onToggleNewPasswordVisibility = viewModel::toggleNewPasswordVisibility,
        onToggleRepeatPasswordVisibility = viewModel::toggleRepeatPasswordVisibility,
        onBackClick = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin
    )

    // Navegar al login cuando sea exitoso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateToLogin()
        }
    }
}

@Composable
private fun ResetPasswordContent(
    uiState: ResetPasswordUiState,
    onNewPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleRepeatPasswordVisibility: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Header curvo
        ResetPasswordHeader(onBackClick = onBackClick)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Título principal
        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = Color(0xFF4682B4) // Secondary Steel Blue
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Formulario
        ResetPasswordForm(
            uiState = uiState,
            onNewPasswordChange = onNewPasswordChange,
            onRepeatPasswordChange = onRepeatPasswordChange,
            onSubmitClick = onSubmitClick,
            onToggleNewPasswordVisibility = onToggleNewPasswordVisibility,
            onToggleRepeatPasswordVisibility = onToggleRepeatPasswordVisibility,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
        
        Spacer(modifier = Modifier
            .height(24.dp)
            .weight(1f, fill = false))
    }
}

@Composable
private fun ResetPasswordHeader(onBackClick: () -> Unit) {
    val headerHeight = 200.dp      // 190–210dp según requisitos
    val curveDepth = 66.dp         // 60–72dp según requisitos

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF202D36), Color(0xFF2E3C47))
                ),
                shape = BottomArcShape(curveDepth)
            )
    ) {
        // Botón back
        AuthBackButton(
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 12.dp)
        )

        // Título centrado
        Text(
            text = "Registration",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp, // 26–28sp según requisitos
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ResetPasswordForm(
    uiState: ResetPasswordUiState,
    onNewPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleRepeatPasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        // Campo New Password
        Text(
            text = "New Password",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.newPassword,
            onValueChange = onNewPasswordChange,
            placeholder = {
                Text(
                    text = "New Password",
                    color = Color(0xFF9CA3AF) // Gray2
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4), // Steel Blue
                unfocusedBorderColor = Color(0xFFD1D5DB), // Gray1
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFF3C0007),
                errorTextColor = Color.White
            ),
            singleLine = true,
            visualTransformation = if (uiState.isNewPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = onToggleNewPasswordVisibility,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isNewPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (uiState.isNewPasswordVisible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        },
                        tint = Color(0xFF718096)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
            ),
            isError = uiState.newPasswordError != null
        )

        // Mensaje de error para New Password
        if (uiState.newPasswordError != null) {
            Text(
                text = uiState.newPasswordError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF3C0007)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Repeat Password
        Text(
            text = "Repeat Password",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.repeatPassword,
            onValueChange = onRepeatPasswordChange,
            placeholder = {
                Text(
                    text = "Repeat Password",
                    color = Color(0xFF9CA3AF) // Gray2
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4), // Steel Blue
                unfocusedBorderColor = Color(0xFFD1D5DB), // Gray1
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFF3C0007),
                errorTextColor = Color.White
            ),
            singleLine = true,
            visualTransformation = if (uiState.isRepeatPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = onToggleRepeatPasswordVisibility,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isRepeatPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (uiState.isRepeatPasswordVisible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        },
                        tint = Color(0xFF718096)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    onSubmitClick()
                }
            ),
            isError = uiState.repeatPasswordError != null
        )

        // Mensaje de error para Repeat Password
        if (uiState.repeatPasswordError != null) {
            Text(
                text = uiState.repeatPasswordError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF3C0007)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Submit
        Button(
            onClick = onSubmitClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4682B4), // Steel Blue
                disabledContainerColor = Color(0xFFCCCCCC)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 8.dp
            ),
            enabled = uiState.isFormValid && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordScreenPreview() {
    AppTheme {
        ResetPasswordContent(
            uiState = ResetPasswordUiState(),
            onNewPasswordChange = {},
            onRepeatPasswordChange = {},
            onSubmitClick = {},
            onToggleNewPasswordVisibility = {},
            onToggleRepeatPasswordVisibility = {},
            onBackClick = {},
            onNavigateToLogin = {}
        )
    }
}
