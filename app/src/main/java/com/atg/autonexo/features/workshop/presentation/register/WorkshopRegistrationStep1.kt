package com.atg.autonexo.features.workshop.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.SuccessDialog
import com.atg.autonexo.core.ui.components.ErrorDialog
import com.atg.autonexo.features.iam.presentation.components.*

@Composable
fun WorkshopRegistrationStep1Screen(
    onNavigateBack: () -> Unit,
    onNext: () -> Unit,
    flowViewModel: WorkshopCreationFlowViewModel = hiltViewModel()
) {
    val flowState by flowViewModel.uiState.collectAsState()
    
    // Show success dialog
    if (flowState.showSuccessDialog) {
        SuccessDialog(
            message = "Taller creado exitosamente.",
            onDismiss = {
                flowViewModel.dismissSuccessDialog()
                onNext()
            }
        )
    }
    
    // Show error dialog
    if (flowState.showErrorDialog) {
        ErrorDialog(
            message = flowState.errorMessage ?: "Hubo un error durante el proceso.",
            onDismiss = flowViewModel::dismissErrorDialog
        )
    }
    
    WorkshopRegistrationStep1Content(
        workshopName = flowState.workshopName,
        shortDescription = flowState.shortDescription,
        legalName = flowState.legalName,
        ruc = flowState.ruc,
        isValid = flowState.isStep1Valid,
        isLoading = flowState.isSaving,
        onWorkshopNameChange = flowViewModel::updateWorkshopName,
        onShortDescriptionChange = flowViewModel::updateShortDescription,
        onLegalNameChange = flowViewModel::updateLegalName,
        onRucChange = flowViewModel::updateRuc,
        onSaveClick = {
            if (flowViewModel.validateAndNextFromStep1()) {
                flowViewModel.saveBasicWorkshop()
            }
        },
        onBackClick = onNavigateBack
    )
}

@Composable
private fun WorkshopRegistrationStep1Content(
    workshopName: String,
    shortDescription: String,
    legalName: String,
    ruc: String,
    isValid: Boolean,
    isLoading: Boolean = false,
    onWorkshopNameChange: (String) -> Unit,
    onShortDescriptionChange: (String) -> Unit,
    onLegalNameChange: (String) -> Unit,
    onRucChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        WorkshopHeader(
            title = "Workshop",
            onBackClick = onBackClick
        )
        
        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Workshop Name
            Text(
                text = "Workshop name",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = workshopName,
                onValueChange = onWorkshopNameChange,
                placeholder = {
                    Text(
                        text = "Commercial name (e.g. Adonz Automotive)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
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
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // Short Description
            Text(
                text = "Short Description",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = shortDescription,
                onValueChange = onShortDescriptionChange,
                placeholder = {
                    Text(
                        text = "Brief description of your workshop (optional, max 500 characters)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = Color(0xFF1A202C),
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                maxLines = 5,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // Legal Name
            Text(
                text = "Legal Name",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = legalName,
                onValueChange = onLegalNameChange,
                placeholder = {
                    Text(
                        text = "Legal business name (optional, max 300 characters)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
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
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // RUC
            Text(
                text = "RUC",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = ruc,
                onValueChange = onRucChange,
                placeholder = {
                    Text(
                        text = "Enter a valid RUC number (11 digits, optional)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
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
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Save Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4682B4),
                    disabledContainerColor = Color(0xFFCCCCCC)
                ),
                enabled = isValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Guardar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun WorkshopHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF202D36), Color(0xFF2E3C47))
                ),
                shape = BottomArcShape(64.dp)
            )
    ) {
        // Botón de volver
        AuthBackButton(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        
        // Título
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

