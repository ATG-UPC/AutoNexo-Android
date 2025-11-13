package com.atg.autonexo.features.matchingbooking.presentation.request

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.BottomNavBar
import com.atg.autonexo.features.matchingbooking.presentation.offer.MakeOfferDialog

@Composable
fun RequestDetailScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: RequestDetailViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOfferDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(requestId) {
        viewModel.loadServiceRequest(requestId)
    }
    
    LaunchedEffect(uiState.offerCreated) {
        if (uiState.offerCreated) {
            showOfferDialog = false
            viewModel.resetOfferCreated()
        }
    }

    // Dialogos de resultado usando componentes existentes
    if (uiState.offerCreated) {
        AlertDialog(
            onDismissRequest = { viewModel.resetOfferCreated() },
            confirmButton = {
                TextButton(onClick = { viewModel.resetOfferCreated() }) { Text("OK") }
            },
            title = { Text("Success!") },
            text = { Text("The offer was sent successfully.") }
        )
    }
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { /* Error will be cleared on next state update */ },
            confirmButton = {
                TextButton(onClick = { /* Error will be cleared on next state update */ }) { Text("OK") }
            },
            title = { Text("Error") },
            text = { Text(uiState.errorMessage ?: "There was an error during the process.") }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "request",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF202D36), Color(0xFF2E3C47))
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
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Request",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del auto: intenta cargar auto1/auto2; si no existen, usa ícono
            val context = LocalContext.current
            val fallback = if (requestId.hashCode() % 2 == 0) "auto1" else "auto2"
            val resolvedName = ImageResolver.resolveNameFromDescription("") ?: fallback
            val imageRes = context.resources.getIdentifier(resolvedName, "drawable", context.packageName)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (imageRes != 0) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.DirectionsCar, contentDescription = null, tint = Color(0xFF8E8E8E))
                    }
                }
            }

            // Sección de datos del vehículo (placeholders)
            OutlinedTextField(
                value = "Nissan",
                onValueChange = {},
                label = { Text("Make", color = Color(0xFF000000)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    disabledTextColor = Color(0xFF000000),
                    focusedLabelColor = Color(0xFF000000),
                    unfocusedLabelColor = Color(0xFF000000),
                    disabledLabelColor = Color(0xFF000000)
                )
            )
            OutlinedTextField(
                value = "Sentra",
                onValueChange = {},
                label = { Text("Model", color = Color(0xFF000000)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    disabledTextColor = Color(0xFF000000),
                    focusedLabelColor = Color(0xFF000000),
                    unfocusedLabelColor = Color(0xFF000000),
                    disabledLabelColor = Color(0xFF000000)
                )
            )
            OutlinedTextField(
                value = "2018",
                onValueChange = {},
                label = { Text("Year", color = Color(0xFF000000)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    disabledTextColor = Color(0xFF000000),
                    focusedLabelColor = Color(0xFF000000),
                    unfocusedLabelColor = Color(0xFF000000),
                    disabledLabelColor = Color(0xFF000000)
                )
            )

            OutlinedTextField(
                value = "AB1-364",
                onValueChange = {},
                label = { Text("License Plate", color = Color(0xFF000000)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    disabledTextColor = Color(0xFF000000),
                    focusedLabelColor = Color(0xFF000000),
                    unfocusedLabelColor = Color(0xFF000000),
                    disabledLabelColor = Color(0xFF000000)
                )
            )

            OutlinedTextField(
                value = "Be careful with the air condition system.",
                onValueChange = {},
                label = { Text("Description", color = Color(0xFF000000)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9D9D9),
                    unfocusedBorderColor = Color(0xFFD9D9D9),
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    disabledTextColor = Color(0xFF000000),
                    focusedLabelColor = Color(0xFF000000),
                    unfocusedLabelColor = Color(0xFF000000),
                    disabledLabelColor = Color(0xFF000000)
                )
            )

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showOfferDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4682B4))
                ) { Text("Make Offer") }

                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282828))
                ) { Text("Cancel", color = Color.White) }
            }
        }
    }

    if (showOfferDialog) {
        MakeOfferDialog(
            serviceRequestId = requestId,
            onDismiss = { showOfferDialog = false },
            onSendOffer = { price, dateTime, message ->
                viewModel.createOffer(requestId, price, dateTime, message)
            },
            isLoading = uiState.isCreatingOffer
        )
    }
}}


