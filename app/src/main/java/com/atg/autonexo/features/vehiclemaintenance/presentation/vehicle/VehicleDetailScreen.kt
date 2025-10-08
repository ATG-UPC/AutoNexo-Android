package com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape

@Composable
fun VehicleDetailScreen(
    vehicleId: String?,
    onNavigateBack: () -> Unit,
    viewModel: VehicleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vehicleId) {
        vehicleId?.let { viewModel.loadVehicle(it) }
        
        viewModel.events.collect { event ->
            when (event) {
                is VehicleDetailEvent.SaveSuccess -> {
                    snackbarHostState.showSnackbar("Vehicle saved successfully")
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
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
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Vehicles",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Brand
                Text(
                    text = "Make",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF282828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.brand,
                    onValueChange = { viewModel.updateBrand(it) },
                    placeholder = { Text("Nissan", color = Color(0xFF8E8E8E)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    isError = uiState.brandError != null,
                    supportingText = uiState.brandError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Model
                Text(
                    text = "Model",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF282828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.model,
                    onValueChange = { viewModel.updateModel(it) },
                    placeholder = { Text("Sentra", color = Color(0xFF8E8E8E)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    isError = uiState.modelError != null,
                    supportingText = uiState.modelError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Year
                Text(
                    text = "Year",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF282828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.year,
                    onValueChange = { viewModel.updateYear(it) },
                    placeholder = { Text("2018", color = Color(0xFF8E8E8E)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    isError = uiState.yearError != null,
                    supportingText = uiState.yearError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Photo y Maintenance Log
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Photo
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Photo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF282828)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFFD9D9D9),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { /* TODO: Select photo */ }
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            // Placeholder: mostrar un ícono de auto
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFF800C1F),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Maintenance Log
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Maintenance Log",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF282828)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFFD9D9D9),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { /* TODO: View log */ }
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = Color(0xFF282828),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // License Plate
                Text(
                    text = "License Plate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF282828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.licensePlate,
                    onValueChange = { viewModel.updateLicensePlate(it) },
                    placeholder = { Text("AB1-364", color = Color(0xFF8E8E8E)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    isError = uiState.licensePlateError != null,
                    supportingText = uiState.licensePlateError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "Description",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF282828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* TODO */ },
                    placeholder = { Text("Be careful with the air condition system", color = Color(0xFF8E8E8E)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

