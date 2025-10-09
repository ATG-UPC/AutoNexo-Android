package com.atg.autonexo.features.iam.presentation.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.theme.AppTheme


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Escuchar eventos del ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> onLoginSuccess()
            }
        }
    }
    
    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onLoginClick = viewModel::login,
        onRegisterClick = onNavigateToRegister,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onForgotPasswordClick = onNavigateToForgotPassword
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding() // Responsive para teclado
    ) {
        // Header con gradiente
        LoginHeader()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Título Login
        Text(
            text = "Login",
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
        
        // Ilustración del mecánico
        LoginIllustration()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Formulario de login
        LoginForm(
            uiState = uiState,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onForgotPasswordClick = onForgotPasswordClick,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
        
        Spacer(modifier = Modifier
            .height(24.dp)
            .weight(1f, fill = false)) // Espaciado flexible
    }
}

@Composable
private fun LoginHeader() {
    val headerHeight = 180.dp      // ajusta 170–220dp según gusto
    val curveDepth   = 60.dp       // ajusta 48–84dp para más/menos "semiluna"

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
    )
    {
        Text(
            text = "Mechanic",
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
private fun LoginIllustration() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        val illustrationHeight = (maxHeight * 0.25f).coerceAtMost(180.dp)
        
        Image(
            painter = painterResource(id = com.atg.autonexo.R.drawable.login_img),
            contentDescription = "Mecánico trabajando en un auto",
            modifier = Modifier
                .fillMaxWidth()
                .height(illustrationHeight),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Campo de Email
        Text(
            text = "Email",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF4A5568),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = {
                Text(
                    text = "mechanic@gmail.com",
                    color = Color(0xFFA0AEC0)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFFE53E3E)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = uiState.emailError != null
        )
        
        if (uiState.emailError != null) {
            Text(
                text = uiState.emailError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE53E3E)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Campo de Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4A5568),
                    fontWeight = FontWeight.Medium
                )
            )
            
            TextButton(
                onClick = onForgotPasswordClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = Color(0xFF4682B4) // Secondary Steel Blue
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = "**************",
                    color = Color(0xFFA0AEC0)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFFE53E3E)
            ),
            singleLine = true,
            visualTransformation = if (uiState.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (uiState.isPasswordVisible) {
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
                onDone = { onLoginClick() }
            ),
            isError = uiState.passwordError != null
        )
        
        if (uiState.passwordError != null) {
            Text(
                text = uiState.passwordError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE53E3E)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botón Register
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4682B4) // Secondary Steel Blue
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Separador "Or"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFD1D5DB) // Gray1
            )
            
            Text(
                text = "Or",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF9CA3AF) // Gray2
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFD1D5DB) // Gray1
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón Login
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF282828), // Negro secundario
                disabledContainerColor = Color(0xFFCCCCCC)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 8.dp
            ),
            enabled = uiState.isLoading.not() && uiState.email.isNotBlank() && uiState.password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Login",
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
private fun LoginScreenPreview() {
    AppTheme {
        LoginContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onRegisterClick = {},
            onTogglePasswordVisibility = {},
            onForgotPasswordClick = {}
        )
    }
}
