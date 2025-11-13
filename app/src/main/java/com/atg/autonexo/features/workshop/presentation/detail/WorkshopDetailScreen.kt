package com.atg.autonexo.features.workshop.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.R
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.BottomNavBar
import com.atg.autonexo.features.workshop.presentation.models.MechanicUi
import com.atg.autonexo.features.workshop.presentation.models.WorkshopUi

@Composable
fun WorkshopDetailScreen(
    onNavigateBack: () -> Unit,
    onGenerateCode: () -> Unit,
    onEditWorkshop: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: WorkshopDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "workshop",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                uiState.workshop?.let { workshop ->
                    WorkshopDetailContent(
                        workshop = workshop,
                        onNavigateBack = onNavigateBack,
                        onGenerateCode = { viewModel.showCodeDialog() },
                        onEditWorkshop = onEditWorkshop
                    )
                }
            }
        }
    }

    // Diálogo de código del workshop (solo para owners)
    if (uiState.showCodeDialog) {
        WorkshopCodeDialog(
            code = uiState.workshop?.workshopCode ?: "000-000-000",
            onDismiss = { viewModel.dismissCodeDialog() }
        )
    }
}

@Composable
private fun WorkshopDetailContent(
    workshop: WorkshopUi,
    onNavigateBack: () -> Unit,
    onGenerateCode: () -> Unit,
    onEditWorkshop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    text = "Workshop",
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del workshop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                // TODO: Cargar desde workshop.imageUrl cuando se implemente carga de URLs
                // Por ahora muestra la imagen por defecto
                Image(
                    painter = painterResource(R.drawable.workshop),
                    contentDescription = "Workshop image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre, rating y ubicación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = workshop.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = workshop.rating.toString(),
                                fontSize = 16.sp,
                                color = Color(0xFF4A5568)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFA500),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${workshop.district}, ${workshop.city}",
                        fontSize = 14.sp,
                        color = Color(0xFF718096)
                    )
                }

                // Logo del workshop
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Cargar desde workshop.logoUrl cuando se implemente carga de URLs
                    // Por ahora muestra el logo por defecto
                    Image(
                        painter = painterResource(R.drawable.logoworkshop),
                        contentDescription = "Workshop logo",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Razón Social
            if (!workshop.legalName.isNullOrBlank()) {
                Text(
                    text = workshop.legalName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A202C)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // RUC
            if (!workshop.ruc.isNullOrBlank()) {
                Text(
                    text = "RUC: ${workshop.ruc}",
                    fontSize = 13.sp,
                    color = Color(0xFF718096)
                )
            }
            
            // Espaciado después de legalName/RUC
            if (!workshop.legalName.isNullOrBlank() || !workshop.ruc.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Descripción
            Text(
                text = workshop.description,
                fontSize = 14.sp,
                color = Color(0xFF4A5568),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Servicios disponibles
            ServiceChips(services = workshop.services)

            Spacer(modifier = Modifier.height(20.dp))

            // Mechanics section
            Text(
                text = "Mechanics",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A202C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de mecánicos
            workshop.mechanics.forEach { mechanic ->
                MechanicItem(mechanic = mechanic)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones solo para owners
            if (workshop.isOwner) {
                OwnerActionButtons(
                    onGenerateCode = onGenerateCode,
                    onEditWorkshop = onEditWorkshop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ServiceChips(services: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        services.forEach { service ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF2E3C47)
            ) {
                Text(
                    text = service,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MechanicItem(mechanic: MechanicUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(28.dp)
            )
        }

        // Nombre y rol
        Column {
            Text(
                text = mechanic.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A202C)
            )
            Text(
                text = mechanic.role,
                fontSize = 13.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun OwnerActionButtons(
    onGenerateCode: () -> Unit,
    onEditWorkshop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Generate Code Button
        Button(
            onClick = onGenerateCode,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4682B4)
            )
        ) {
            Text(
                text = "Generate Code",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Edit Workshop Button
        Button(
            onClick = onEditWorkshop,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E3C47)
            )
        ) {
            Text(
                text = "Edit Workshop",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun WorkshopCodeDialog(
    code: String,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Workshop Code",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C)
                )

                // Subtítulo
                Text(
                    text = "Share with a member of your workshop",
                    fontSize = 14.sp,
                    color = Color(0xFF718096)
                )

                // Código
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF7FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = code,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C),
                        modifier = Modifier.padding(20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Copy to clipboard button
                OutlinedButton(
                    onClick = { /* TODO: Copiar al portapapeles */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4682B4)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Copy to Clipboard",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botón OK
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4682B4)
                    )
                ) {
                    Text(
                        text = "OK",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

