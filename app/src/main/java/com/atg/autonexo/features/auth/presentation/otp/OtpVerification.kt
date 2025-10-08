package com.atg.autonexo.features.auth.presentation.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
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
import com.atg.autonexo.features.auth.presentation.components.AuthBackButton
import com.atg.autonexo.features.auth.presentation.otp.OtpVerificationViewModel

@Composable
fun OtpVerificationScreen(
    phone: String = "985 153 158",
    onNavigateBack: () -> Unit = {},
    onNavigateToNext: () -> Unit = {},
    navController: NavController = rememberNavController(),
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    OtpVerificationContent(
        phone = phone,
        uiState = uiState,
        onOtpChange = viewModel::updateOtp,
        onVerifyClick = viewModel::verifyOtp,
        onResendClick = viewModel::resendOtp,
        onBackClick = onNavigateBack,
        onNavigateToNext = onNavigateToNext
    )

    // Navegar cuando sea exitoso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateToNext()
        }
    }
}

@Composable
private fun OtpVerificationContent(
    phone: String,
    uiState: OtpVerificationUiState,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Header curvo
        OtpVerificationHeader(onBackClick = onBackClick)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Título principal
        Text(
            text = "OTP Verification",
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Subtítulo con número de teléfono
        Text(
            text = "Enter the OTP sent to $phone",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                color = Color(0xFF9CA3AF) // Gray2
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Campos OTP
        OtpFields(
            uiState = uiState,
            onOtpChange = onOtpChange,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Línea de ayuda para reenvío
        OtpResendSection(
            uiState = uiState,
            onResendClick = onResendClick,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botón Verify & Proceed
        Button(
            onClick = onVerifyClick,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5E7B97), // Alternativa a #4682B4
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
                    text = "Verify & Proceed",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier
            .height(24.dp)
            .weight(1f, fill = false))
    }
}

@Composable
private fun OtpVerificationHeader(onBackClick: () -> Unit) {
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
private fun OtpFields(
    uiState: OtpVerificationUiState,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) { index ->
            OtpField(
                value = uiState.otp.getOrNull(index)?.toString() ?: "",
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                        val newOtp = uiState.otp.toMutableList()
                        if (newValue.isBlank()) {
                            if (index < newOtp.size) {
                                newOtp.removeAt(index)
                            }
                        } else {
                            if (index < newOtp.size) {
                                newOtp[index] = newValue.first()
                            } else {
                                newOtp.add(newValue.first())
                            }
                        }
                        onOtpChange(newOtp.joinToString(""))
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OtpField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4682B4),
            unfocusedBorderColor = Color(0xFFD1D5DB),
            focusedTextColor = Color(0xFF1A202C),
            unfocusedTextColor = Color(0xFF1A202C),
            focusedContainerColor = Color(0xFFF7FAFC),
            unfocusedContainerColor = Color(0xFFF7FAFC)
        ),
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) }
        )
    )
}

@Composable
private fun OtpResendSection(
    uiState: OtpVerificationUiState,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Don't receive the OTP? ",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF) // Gray2
            )
        )
        
        TextButton(
            onClick = onResendClick,
            enabled = uiState.canResend,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "RESEND OTP",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.canResend) Color(0xFF282828) else Color(0xFF9CA3AF)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpVerificationScreenPreview() {
    AppTheme {
        OtpVerificationContent(
            phone = "985 153 158",
            uiState = OtpVerificationUiState(),
            onOtpChange = {},
            onVerifyClick = {},
            onResendClick = {},
            onBackClick = {},
            onNavigateToNext = {}
        )
    }
}
