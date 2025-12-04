package com.atg.autonexo.features.workshop.presentation.invitation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopManagementScreen(
    workshopId: Long,
    onBack: () -> Unit,
    onInviteEmployee: (Long) -> Unit,
    viewModel: WorkshopManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(workshopId) {
        viewModel.loadInvitationCode(workshopId)
        viewModel.loadEmployees(workshopId)
        viewModel.loadWorkshop()
        viewModel.loadLocation(workshopId)
    }

    Scaffold(
        topBar = {
            com.atg.autonexo.core.components.RoundedHeader(
                title = "Gestión del Taller",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER DEL WORKSHOP
            item {
                when {
                    uiState.isLodingWorkshop -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        WorkshopHeader(
                            name = uiState.workshopName,
                            rating = uiState.trustScore ?: 0f,
                            city = uiState.city ?: "",
                            state = uiState.state ?: "",
                            address = (uiState.street + " " + uiState.zip),
                            shortDescription = uiState.workshopShortDescription,
                            logoUrl = uiState.logoUrl,
                            photoUrls = uiState.photoUrls
                        )
                    }
                }
            }

            // TAGS
            item {
                when {
                    uiState.isLodingWorkshop -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.capabilityTags.isNotEmpty() -> {
                        ServiceTagsRow(tags = uiState.capabilityTags)
                    }

                    else -> {
                        Text(
                            text = "No hay tags registradas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.atg.autonexo.core.ui.theme.TextSecondary
                        )
                    }
                }
            }

            // CÓDIGO DE INVITACIÓN
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = com.atg.autonexo.core.ui.theme.CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Código de invitación",
                            style = MaterialTheme.typography.titleMedium,
                            color = com.atg.autonexo.core.ui.theme.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (uiState.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Text(
                                text = uiState.invitationCode ?: "Cargando...",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = com.atg.autonexo.core.ui.theme.ButtonNavy,
                                letterSpacing = 4.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    uiState.invitationCode?.let { code ->
                                        copyToClipboard(code, context)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = uiState.invitationCode != null && !uiState.isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = com.atg.autonexo.core.ui.theme.ButtonNavy,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copiar")
                            }

                            Button(
                                onClick = {
                                    uiState.invitationCode?.let { code ->
                                        shareCode(code, context)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = uiState.invitationCode != null && !uiState.isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = com.atg.autonexo.core.ui.theme.ButtonNavy,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Compartir")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onInviteEmployee(workshopId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Invitar empleado")
                        }
                    }
                }
            }

            // TÍTULO LISTA EMPLEADOS
            item {
                Text(
                    text = "Lista de empleados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = com.atg.autonexo.core.ui.theme.TextPrimary
                )
            }

            // ESTADOS EMPLEADOS
            if (uiState.isLoadingEmployees) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.employees.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = com.atg.autonexo.core.ui.theme.CardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                modifier = Modifier.size(48.dp),
                                tint = com.atg.autonexo.core.ui.theme.TextTertiary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay empleados registrados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = com.atg.autonexo.core.ui.theme.TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(uiState.employees) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onToggleActive = { isActive ->
                            uiState.workshopId?.let { id ->
                                if (isActive) {
                                    viewModel.activateEmployee(id, employee.id)
                                } else {
                                    viewModel.deactivateEmployee(id, employee.id)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmployeeCard(
    employee: com.atg.autonexo.features.workshop.domain.models.WorkshopEmployee,
    onToggleActive: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.atg.autonexo.core.ui.theme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.email,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = com.atg.autonexo.core.ui.theme.TextPrimary
                )

                if (employee.firstName != null || employee.lastName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${employee.firstName.orEmpty()} ${employee.lastName.orEmpty()}".trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.atg.autonexo.core.ui.theme.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { },
                        label = { Text(employee.role) }
                    )

                    AssistChip(
                        onClick = { },
                        label = { Text(if (employee.active) "Activo" else "Inactivo") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (employee.active)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }

            IconButton(
                onClick = { onToggleActive(!employee.active) }
            ) {
                Icon(
                    imageVector = if (employee.active) Icons.Default.Block else Icons.Default.CheckCircle,
                    contentDescription = if (employee.active) "Desactivar" else "Activar",
                    tint = if (employee.active)
                        MaterialTheme.colorScheme.error
                    else
                        com.atg.autonexo.core.ui.theme.ButtonNavy
                )
            }
        }
    }
}


private fun copyToClipboard(text: String, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Código de Invitación", text)
    clipboard.setPrimaryClip(clip)
}

private fun shareCode(code: String, context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Únete a mi taller usando este código: $code")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@Composable
fun ServiceTagsRow(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tags.forEach { tag ->
            ServiceTag(text = tag)
        }
    }
}

@Composable
fun ServiceTag(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFE4EAF3)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = com.atg.autonexo.core.ui.theme.TextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun WorkshopHeader(
    name: String?,
    rating: Float,
    city: String?,
    state: String?,
    address: String,
    shortDescription: String?,
    logoUrl: String?,
    photoUrls: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.atg.autonexo.core.ui.theme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Carrusel de fotos
            val pagerState = rememberPagerState(
                pageCount = { maxOf(photoUrls.size, 1) }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (photoUrls.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = photoUrls[page],
                            contentDescription = "Foto del taller",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Placeholder si no hay fotos
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Logo redondo abajo a la derecha
                if (!logoUrl.isNullOrBlank()) {
                    Surface(
                        shape = CircleShape,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(64.dp)
                    ) {
                        AsyncImage(
                            model = logoUrl,
                            contentDescription = "Logo del taller",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Texto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!name.isNullOrBlank()) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = String.format("%.1f", rating),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificación",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${city ?: ""}${if (!city.isNullOrBlank() && !state.isNullOrBlank()) ", " else ""}${state ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Dirección",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = shortDescription ?: "Sin descripción...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}