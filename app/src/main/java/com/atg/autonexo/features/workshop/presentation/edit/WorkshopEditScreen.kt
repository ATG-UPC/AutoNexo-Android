package com.atg.autonexo.features.workshop.presentation.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.features.iam.presentation.components.ErrorDialog
import com.atg.autonexo.features.iam.presentation.components.SuccessDialog

@Composable
fun WorkshopEditScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: WorkshopEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Show success dialog
    if (uiState.showSuccessDialog) {
        SuccessDialog(
            message = "Taller actualizado exitosamente.",
            onDismiss = {
                viewModel.dismissSuccessDialog()
                onSuccess()
            }
        )
    }

    // Show error dialog
    if (uiState.showErrorDialog) {
        ErrorDialog(
            message = uiState.errorMessage ?: "Hubo un error durante el proceso.",
            onDismiss = viewModel::dismissErrorDialog
        )
    }

    WorkshopEditContent(
        workshopName = uiState.workshopName,
        shortDescription = uiState.shortDescription,
        legalName = uiState.legalName,
        ruc = uiState.ruc,
        isValid = uiState.isValid,
        isLoading = uiState.isLoading,
        isSaving = uiState.isSaving,
        onWorkshopNameChange = viewModel::updateWorkshopName,
        onShortDescriptionChange = viewModel::updateShortDescription,
        onLegalNameChange = viewModel::updateLegalName,
        onRucChange = viewModel::updateRuc,
        onSaveClick = viewModel::saveWorkshop,
        onBackClick = onNavigateBack
    )
}

@Composable
private fun WorkshopEditContent(
    workshopName: String,
    shortDescription: String,
    legalName: String,
    ruc: String,
    isValid: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
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
        // Header curvo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Editar Taller",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Información Básica",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A202C)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Workshop Name
                OutlinedTextField(
                    value = workshopName,
                    onValueChange = onWorkshopNameChange,
                    label = { Text("Nombre del Taller *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Short Description
                OutlinedTextField(
                    value = shortDescription,
                    onValueChange = onShortDescriptionChange,
                    label = { Text("Descripción Breve") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    supportingText = {
                        Text(
                            text = "${shortDescription.length}/500",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Legal Name
                OutlinedTextField(
                    value = legalName,
                    onValueChange = onLegalNameChange,
                    label = { Text("Razón Social") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // RUC
                OutlinedTextField(
                    value = ruc,
                    onValueChange = onRucChange,
                    label = { Text("RUC") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    supportingText = {
                        Text(
                            text = "11 dígitos",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Helper text
                Text(
                    text = "* Campos obligatorios",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                    enabled = isValid && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Guardar Cambios",
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
}

