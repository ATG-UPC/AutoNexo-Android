package com.atg.autonexo.features.iam.presentation.register

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.theme.AppTheme
import com.atg.autonexo.features.iam.presentation.components.*
import com.atg.autonexo.features.iam.presentation.register.RegisterWorkshopUiState

@Composable
fun RegisterWorkshopScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCreateWorkshop: () -> Unit = {},
    onNavigateToJoinWorkshop: () -> Unit = {},
    viewModel: RegisterWorkshopViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Mostrar diálogo de términos
    if (uiState.showTermsDialog) {
        TermsAndConditionsDialog(
            onAccept = {
                viewModel.acceptTermsAndRegister()
                if (uiState.isWorkshopManager) {
                    onNavigateToCreateWorkshop()
                } else {
                    onNavigateToJoinWorkshop()
                }
            },
            onDismiss = viewModel::dismissTermsDialog
        )
    }
    
    RegisterWorkshopContent(
        uiState = uiState,
        onFullNameChange = viewModel::updateFullName,
        onEmailChange = viewModel::updateEmail,
        onPhoneChange = viewModel::updatePhone,
        onPasswordChange = viewModel::updatePassword,
        onRepeatPasswordChange = viewModel::updateRepeatPassword,
        onIsWorkshopManagerChange = viewModel::updateIsWorkshopManager,
        onTermsCheckedChange = viewModel::updateTermsAccepted,
        onNextClick = viewModel::proceedToNextStep,
        onBackClick = onNavigateBack,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onToggleRepeatPasswordVisibility = viewModel::toggleRepeatPasswordVisibility,
        onTermsClick = viewModel::showTermsAndConditions
    )
}

@Composable
private fun RegisterWorkshopContent(
    uiState: RegisterWorkshopUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onIsWorkshopManagerChange: (Boolean) -> Unit,
    onTermsCheckedChange: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleRepeatPasswordVisibility: () -> Unit,
    onTermsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding() // Responsive para teclado
    ) {
        // Header con gradiente y sombra
        RegistrationHeader(onBackClick = onBackClick)
        
        // Formulario de registro
        RegistrationForm(
            uiState = uiState,
            onFullNameChange = onFullNameChange,
            onEmailChange = onEmailChange,
            onPhoneChange = onPhoneChange,
            onPasswordChange = onPasswordChange,
            onRepeatPasswordChange = onRepeatPasswordChange,
            onIsWorkshopManagerChange = onIsWorkshopManagerChange,
            onTermsCheckedChange = onTermsCheckedChange,
            onNextClick = onNextClick,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onToggleRepeatPasswordVisibility = onToggleRepeatPasswordVisibility,
            onTermsClick = onTermsClick,
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
private fun RegistrationHeader(onBackClick: () -> Unit) {
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
    ) {
        // Botón de volver
        AuthBackButton(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
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
private fun RegistrationForm(
    uiState: RegisterWorkshopUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onIsWorkshopManagerChange: (Boolean) -> Unit,
    onTermsCheckedChange: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleRepeatPasswordVisibility: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        // Campo Full Name
        Text(
            text = "Full Name",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = onFullNameChange,
            placeholder = {
                Text(
                    text = "Arturo González",
                    color = Color(0xFF9CA3AF) // Gray2
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
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
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.fullNameError != null
        )
        
        if (uiState.fullNameError != null) {
            Text(
                text = uiState.fullNameError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF3C0007)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Campo Email
        Text(
            text = "Email",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = {
                Text(
                    text = "mechanic@gmail.com",
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFF3C0007),
                errorTextColor = Color.White
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.emailError != null
        )
        
        if (uiState.emailError != null) {
            Text(
                text = uiState.emailError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF3C0007)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Campo Phone Number
        Text(
            text = "Phone Number",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = uiState.phone,
            onValueChange = { value ->
                // Solo permitir dígitos y guiones
                if (value.all { it.isDigit() || it == '-' } && value.length <= 15) {
                    onPhoneChange(value)
                }
            },
            placeholder = {
                Text(
                    text = "982841848",
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFF3C0007),
                errorTextColor = Color.White
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.phoneError != null
        )
        
        if (uiState.phoneError != null) {
            Text(
                text = uiState.phoneError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF3C0007)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Campo Password
        Text(
            text = "Password",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = "**************",
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                errorBorderColor = Color(0xFF3C0007),
                errorTextColor = Color.White
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
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.passwordError != null
        )
        
        if (uiState.passwordError != null) {
            Text(
                text = uiState.passwordError,
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
                    text = "**************",
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFD1D5DB),
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
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.repeatPasswordError != null
        )
        
        if (uiState.repeatPasswordError != null) {
            Text(
                text = uiState.repeatPasswordError,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF3C0007)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Campo "¿Are you a Workshop manager?"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = uiState.isWorkshopManager,
                    onCheckedChange = onIsWorkshopManagerChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4682B4),
                        uncheckedColor = Color(0xFF9CA3AF)
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "¿Are you a Workshop manager?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF4A5568)
                    )
                )
            }
            
            Text(
                text = "(optional)",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                ),
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón Next
        Button(
            onClick = onNextClick,
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
                    text = "Next",
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
private fun RegisterWorkshopScreenPreview() {
    AppTheme {
        RegisterWorkshopContent(
            uiState = RegisterWorkshopUiState(),
            onFullNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = {},
            onIsWorkshopManagerChange = {},
            onTermsCheckedChange = {},
            onNextClick = {},
            onBackClick = {},
            onTogglePasswordVisibility = {},
            onToggleRepeatPasswordVisibility = {},
            onTermsClick = {}
        )
    }
}
