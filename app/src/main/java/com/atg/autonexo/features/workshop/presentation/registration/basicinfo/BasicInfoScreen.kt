package com.atg.autonexo.features.workshop.presentation.registration.basicinfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.CardBackground
import com.atg.autonexo.core.ui.theme.TextTertiary
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar

@Composable
fun BasicInfoScreen(
    viewModel: BasicInfoViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit,
    onBack: () -> Unit = {},
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkExistingWorkshop(
            onWorkshopExists = { workshopId ->
                // onNext(workshopId)
            },
            onNoWorkshop = { }
        )

        viewModel.loadMyWorkshop()
    }

    val showLabel = uiState.ogName.isBlank() &&
            uiState.ogShortDescription.isBlank() &&
            uiState.ogLegalName.isBlank() &&
            uiState.ogRuc.isBlank() &&
            !uiState.isLoading
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 0.dp)
                .padding(top = 0.dp),
            horizontalAlignment = Alignment.Start

        ) {
            RoundedHeader(
                title = "Workshop",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        // Nombre
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = viewModel::updateName,
                            trailingIcon = {
                                if (!showLabel) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar Nombre",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            label = {
                                if (showLabel) {
                                    Text("Nombre del Workshop *")
                                } else {
                                    Text("Editar Nombre del Workshop")
                                }
                            },
                            placeholder = {
                                if (!showLabel && uiState.ogName.isNotBlank()) {
                                    Text(uiState.ogName, color = TextTertiary)
                                }
                            },
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
                            isError = uiState.name.isNotBlank() && (uiState.name.length < 3 || uiState.name.length > 200),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ButtonNavy,
                                focusedLabelColor = ButtonNavy,
                                cursorColor = ButtonNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Descripción corta
                        OutlinedTextField(
                            value = uiState.shortDescription,
                            onValueChange = viewModel::updateShortDescription,
                            trailingIcon = {
                                if (!showLabel) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar descripción",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            label = {
                                if (showLabel) {
                                    Text("Descripción del Workshop *")
                                } else {
                                    Text("Editar Descripción del Workshop")
                                }
                            },
                            placeholder = {
                                if (!showLabel && uiState.ogShortDescription.isNotBlank()) {
                                    Text(uiState.ogShortDescription, color = TextTertiary)
                                }
                            },
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
                                    Text(
                                        text = "${uiState.shortDescription.length}/500",
                                        color = TextTertiary
                                    )
                                }
                            },
                            isError = uiState.shortDescription.length > 500,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ButtonNavy,
                                focusedLabelColor = ButtonNavy,
                                cursorColor = ButtonNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nombre legal
                        OutlinedTextField(
                            value = uiState.legalName,
                            onValueChange = viewModel::updateLegalName,
                            trailingIcon = {
                                if (!showLabel) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar Nombre legal",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            label = {
                                if (showLabel) {
                                    Text("Nombre legal del Workshop *")
                                } else {
                                    Text("Editar Nombre legal del Workshop")
                                }
                            },
                            placeholder = {
                                if (!showLabel && uiState.ogLegalName.isNotBlank()) {
                                    Text(uiState.ogLegalName, color = TextTertiary)
                                }
                            },
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
                            isError = uiState.legalName.length > 300,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ButtonNavy,
                                focusedLabelColor = ButtonNavy,
                                cursorColor = ButtonNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // RUC
                        OutlinedTextField(
                            value = uiState.ruc,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } && newValue.length <= 11) {
                                    viewModel.updateRuc(newValue)
                                }
                            },
                            trailingIcon = {
                                if (!showLabel) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "No se puede editar RUC",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            label = {
                                if (showLabel) {
                                    Text("Ruc del Workshop *")
                                } else {
                                    Text(uiState.ogRuc)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = if (uiState.ogRuc.isNotBlank()) false else !uiState.isLoading,
                            supportingText = {
                                when {
                                    uiState.ruc.isNotBlank() && uiState.ruc.length != 11 -> {
                                        Text(
                                            text = "El RUC debe tener exactamente 11 dígitos",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    uiState.ruc.isNotBlank() -> {
                                        Text(
                                            text = "${uiState.ruc.length}/11",
                                            color = TextTertiary
                                        )
                                    }
                                }
                            },
                            isError = uiState.ruc.isNotBlank() && uiState.ruc.length != 11,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ButtonNavy,
                                focusedLabelColor = ButtonNavy,
                                cursorColor = ButtonNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        uiState.errorMessage?.let { errorMessage ->
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (showLabel) {
                        viewModel.createWorkshop(onNext)
                    } else {
                        viewModel.saveEdits(onNext)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = !uiState.isLoading &&
                        uiState.name.isNotBlank() &&
                        uiState.name.length >= 3 &&
                        uiState.name.length <= 200 &&
                        (uiState.ruc.isBlank() || uiState.ruc.length == 11) &&
                        uiState.shortDescription.length <= 500 &&
                        uiState.legalName.length <= 300,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Siguiente")
                }
            }
        }
    }
}

