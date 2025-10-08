package com.atg.autonexo.features.iam.presentation.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.atg.autonexo.features.iam.presentation.components.AuthBackButton

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToOtp: (String) -> Unit = {},
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordContent(
        uiState = uiState,
        onPhoneChange = viewModel::updatePhone,
        onSubmitClick = viewModel::submitPhone,
        onBackClick = onNavigateBack,
        onNavigateToOtp = onNavigateToOtp
    )

    // Navegar a OTP cuando sea exitoso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess && uiState.phone.isNotBlank()) {
            onNavigateToOtp(uiState.phone)
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    onPhoneChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToOtp: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Header curvo
        ForgotPasswordHeader(onBackClick = onBackClick)
        
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
        ForgotPasswordForm(
            uiState = uiState,
            onPhoneChange = onPhoneChange,
            onSubmitClick = onSubmitClick,
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
private fun ForgotPasswordHeader(onBackClick: () -> Unit) {
    val headerHeight = 180.dp
    val curveDepth = 60.dp

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
                fontSize = 28.sp,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ForgotPasswordForm(
    uiState: ForgotPasswordUiState,
    onPhoneChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        // Label del campo
        Text(
            text = "Phone Number",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Campo de teléfono
        OutlinedTextField(
            value = uiState.phone,
            onValueChange = { value: String ->
                // Solo permitir dígitos, espacios y guiones
                if (value.all { char -> char.isDigit() || char == ' ' || char == '-' }) {
                    onPhoneChange(value)
                }
            },
            placeholder = {
                Text(
                    text = "Mobile Number",
                    color = Color(0xFF9CA3AF) // Gray2
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFD1D5DB), // Gray1
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFF3C0007),
                errorTextColor = Color.White
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    onSubmitClick()
                }
            ),
            isError = uiState.phoneError != null
        )

        // Mensaje de error
        if (uiState.phoneError != null) {
            Text(
                text = uiState.phoneError,
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
private fun ForgotPasswordScreenPreview() {
    AppTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(),
            onPhoneChange = {},
            onSubmitClick = {},
            onBackClick = {},
            onNavigateToOtp = {}
        )
    }
}