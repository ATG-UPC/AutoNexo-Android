package com.atg.autonexo.features.workshop.presentation.registration.basicinfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun BasicInfoScreen(
    viewModel: BasicInfoViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // Verificar si ya existe un workshop
        viewModel.checkExistingWorkshop(
            onWorkshopExists = { workshopId ->
                // Si ya existe, navegar directamente a la siguiente pantalla
                onNext(workshopId)
            },
            onNoWorkshop = {
                // Si no existe, continuar con el formulario
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Información Básica del Workshop",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::updateName,
            label = { Text("Nombre del Workshop *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            supportingText = {
                if (uiState.name.isNotBlank() && (uiState.name.length < 3 || uiState.name.length > 200)) {
                    Text(
                        text = "El nombre debe tener entre 3 y 200 caracteres",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = uiState.name.isNotBlank() && (uiState.name.length < 3 || uiState.name.length > 200)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.shortDescription,
            onValueChange = viewModel::updateShortDescription,
            label = { Text("Descripción Corta") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            enabled = !uiState.isLoading,
            supportingText = {
                if (uiState.shortDescription.length > 500) {
                    Text(
                        text = "Máximo 500 caracteres",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("${uiState.shortDescription.length}/500")
                }
            },
            isError = uiState.shortDescription.length > 500
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.legalName,
            onValueChange = viewModel::updateLegalName,
            label = { Text("Nombre Legal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            supportingText = {
                if (uiState.legalName.length > 300) {
                    Text(
                        text = "Máximo 300 caracteres",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = uiState.legalName.length > 300
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.ruc,
            onValueChange = { newValue ->
                // Solo permitir números
                if (newValue.all { it.isDigit() } && newValue.length <= 11) {
                    viewModel.updateRuc(newValue)
                }
            },
            label = { Text("RUC") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading,
            supportingText = {
                if (uiState.ruc.isNotBlank() && uiState.ruc.length != 11) {
                    Text(
                        text = "El RUC debe tener exactamente 11 dígitos",
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (uiState.ruc.isNotBlank()) {
                    Text("${uiState.ruc.length}/11")
                }
            },
            isError = uiState.ruc.isNotBlank() && uiState.ruc.length != 11
        )

        Spacer(modifier = Modifier.height(24.dp))

        uiState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { viewModel.createWorkshop(onNext) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && 
                     uiState.name.isNotBlank() && 
                     uiState.name.length >= 3 && 
                     uiState.name.length <= 200 &&
                     (uiState.ruc.isBlank() || uiState.ruc.length == 11) &&
                     uiState.shortDescription.length <= 500 &&
                     uiState.legalName.length <= 300
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Siguiente")
            }
        }
    }
}

