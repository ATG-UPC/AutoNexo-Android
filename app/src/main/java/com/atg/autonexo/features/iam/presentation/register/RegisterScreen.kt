package com.atg.autonexo.features.iam.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Escuchar eventos del ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterEvent.RegisterSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                    kotlinx.coroutines.delay(1000)
                    onRegisterSuccess()
                }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        RegisterContent(
            uiState = uiState,
            onEmailChange = viewModel::updateEmail,
            onPasswordChange = viewModel::updatePassword,
            onConfirmPasswordChange = viewModel::updateConfirmPassword,
            onFirstNameChange = viewModel::updateFirstName,
            onLastNameChange = viewModel::updateLastName,
            onPhoneNumberChange = viewModel::updatePhoneNumber,
            onRoleChange = viewModel::updateSelectedRole,
            onInvitationCodeChange = viewModel::updateInvitationCode,
            onRegisterClick = viewModel::register,
            onBackClick = onNavigateBack,
            onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
            onToggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun RegisterContent(
    uiState: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onInvitationCodeChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Header
        RegisterHeader()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Título
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = Color(0xFF4682B4)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Formulario
        RegisterForm(
            uiState = uiState,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onFirstNameChange = onFirstNameChange,
            onLastNameChange = onLastNameChange,
            onPhoneNumberChange = onPhoneNumberChange,
            onRoleChange = onRoleChange,
            onInvitationCodeChange = onInvitationCodeChange,
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RegisterHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF202D36), Color(0xFF2E3C47))
                ),
                shape = BottomArcShape(48.dp)
            )
    ) {
        Text(
            text = "Sign Up",
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
private fun RegisterForm(
    uiState: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onInvitationCodeChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        
        // First Name
        FormField(
            label = "First Name",
            value = uiState.firstName,
            onValueChange = onFirstNameChange,
            placeholder = "John",
            error = uiState.firstNameError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Last Name
        FormField(
            label = "Last Name",
            value = uiState.lastName,
            onValueChange = onLastNameChange,
            placeholder = "Doe",
            error = uiState.lastNameError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email
        FormField(
            label = "Email",
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "mechanic@gmail.com",
            keyboardType = KeyboardType.Email,
            error = uiState.emailError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Phone Number
        FormField(
            label = "Phone Number",
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = "+51 999 999 999",
            keyboardType = KeyboardType.Phone,
            error = uiState.phoneNumberError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Role Selection
        Text(
            text = "I am a:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF4A5568),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        RoleSelector(
            selectedRole = uiState.selectedRole,
            onRoleSelected = onRoleChange
        )
        
        // Invitation Code (solo para WORKSHOP_EMPLOYEE)
        if (uiState.selectedRole == "WORKSHOP_EMPLOYEE") {
            Spacer(modifier = Modifier.height(16.dp))
            FormField(
                label = "Invitation Code",
                value = uiState.invitationCode,
                onValueChange = onInvitationCodeChange,
                placeholder = "Enter workshop code",
                error = uiState.invitationCodeError
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password
        PasswordField(
            label = "Password",
            value = uiState.password,
            onValueChange = onPasswordChange,
            isVisible = uiState.isPasswordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            error = uiState.passwordError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm Password
        PasswordField(
            label = "Confirm Password",
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            isVisible = uiState.isConfirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPasswordVisibility,
            error = uiState.confirmPasswordError
        )
        
        // Error Message
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE53E3E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFFED7D7),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Register Button
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4682B4),
                disabledContainerColor = Color(0xFFCCCCCC)
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Back to Login
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? Login",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4682B4)
                )
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF4A5568),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFFA0AEC0)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = Color(0xFFE53E3E)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            isError = error != null
        )
        
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE53E3E)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF4A5568),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("**************", color = Color(0xFFA0AEC0)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = Color(0xFFE53E3E)
            ),
            singleLine = true,
            visualTransformation = if (isVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (isVisible) "Ocultar" else "Mostrar",
                        tint = Color(0xFF718096)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isError = error != null
        )
        
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFE53E3E)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        
        RoleOption(
            role = "WORKSHOP_MANAGER",
            label = "Workshop Manager",
            description = "I manage a workshop",
            isSelected = selectedRole == "WORKSHOP_MANAGER",
            onClick = { onRoleSelected("WORKSHOP_MANAGER") }
        )
        
        RoleOption(
            role = "WORKSHOP_EMPLOYEE",
            label = "Workshop Employee",
            description = "I work at a workshop",
            isSelected = selectedRole == "WORKSHOP_EMPLOYEE",
            onClick = { onRoleSelected("WORKSHOP_EMPLOYEE") }
        )
    }
}

@Composable
private fun RoleOption(
    role: String,
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4682B4))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF4682B4)
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A202C)
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF718096)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    AppTheme {
        RegisterContent(
            uiState = RegisterUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onFirstNameChange = {},
            onLastNameChange = {},
            onPhoneNumberChange = {},
            onRoleChange = {},
            onInvitationCodeChange = {},
            onRegisterClick = {},
            onBackClick = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            modifier = Modifier
        )
    }
}

