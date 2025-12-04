package com.atg.autonexo.features.matching.presentation.requests

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.CardBackground
import com.atg.autonexo.core.ui.theme.IconGray
import com.atg.autonexo.core.ui.theme.StatusCancelledColor
import com.atg.autonexo.core.ui.theme.StatusCompletedColor
import com.atg.autonexo.core.ui.theme.StatusPendingColor
import com.atg.autonexo.core.ui.theme.StatusRejectedColor
import com.atg.autonexo.core.ui.theme.TextPrimary
import com.atg.autonexo.core.ui.theme.TextSecondary
import com.atg.autonexo.core.ui.theme.TextTertiary
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.atg.autonexo.features.matching.domain.models.Request
import com.atg.autonexo.features.matching.domain.models.RequestStatus
import com.atg.autonexo.features.matching.presentation.offer.OfferDialog
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RequestsScreen(
    viewModel: RequestsViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    currentRoute: String,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var showOfferDialog by remember { mutableStateOf(false) }
    var selectedRequestId by remember { mutableStateOf<Long?>(null) }
    var selectedRequest by remember { mutableStateOf<Request?>(null) }

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
                .padding(top = 0.dp)
        ) {
            RoundedHeader("Requests", onBack = onBack)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mail,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Total Requests",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Text(
                    text = "${uiState.requests.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            Spacer(Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Error",
                        color = Color.Red
                    )
                }

                uiState.requests.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("No hay solicitudes", color = TextSecondary)
                    }
                }

                else -> {
                    if (showOfferDialog && selectedRequestId != null) {
                        OfferDialog(
                            serviceRequestId = selectedRequestId!!,
                            onDismiss = { showOfferDialog = false },
                            onSuccess = { requestId ->
                                showOfferDialog = false
                            }

                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = padding.calculateBottomPadding())
                    ) {
                        items(uiState.requests) { request ->
                            RequestCard(
                                request = request,
                                onOfferClick = { id ->
                                    selectedRequestId = id
                                    showOfferDialog = true
                                },
                                onDetailsClick = { r ->
                                    selectedRequest = r
                                }
                            )
                        }
                    }
                }
            }
        }

        // Popup de detalles del Request
        if (selectedRequest != null) {
            RequestDetailsDialog(
                request = selectedRequest!!,
                onDismiss = { selectedRequest = null }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun RequestCard(
    request: Request,
    onOfferClick: (Long) -> Unit,
    onDetailsClick: (Request) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
    val createdText = request.createdAt.format(formatter)
    val matchPercent = (request.matchScore * 100).coerceIn(0.0, 100.0)
    val distanceText = String.format(Locale.getDefault(), "%.1f km", request.distanceKm)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ButtonNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = "Vehículo",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Request #${request.id}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp
                            ),
                            color = TextPrimary
                        )
                        /*
                        Text(
                            text = "Vehículo ID: ${request.vehicleId}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = TextTertiary
                        )
                         */
                    }
                }

                StatusBadge(status = request.status)
            }

            // Servicios solicitados
            if (request.requestedServices.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    request.requestedServices.forEach { service ->
                        Surface(
                            color = Color(0xFFE4EAF3),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = service,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Descripción
            if (request.description.isNotBlank()) {
                Text(
                    text = request.description.ifBlank { "Sin descripción" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextSecondary
                )
            }

            // Match, distancia, fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Match score
                Column {
                    Text(
                        text = "Coincidencia",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${matchPercent.roundToInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }

                // Distancia
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Distancia",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = IconGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = distanceText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }

                // Fecha creación
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Creado",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = IconGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = createdText,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onDetailsClick(request) },
                    modifier = Modifier.weight(1f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text(
                        text = "Detalles",
                        color = ButtonNavy
                    )
                }

                Button(
                    onClick = {
                        onOfferClick(request.id)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text("Proponer oferta")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: RequestStatus) {
    val (bgColor, textColor) = when (status) {
        RequestStatus.PENDING ->
            StatusPendingColor.copy(alpha = 0.18f) to StatusPendingColor
        RequestStatus.COMPLETED ->
            StatusCompletedColor.copy(alpha = 0.18f) to StatusCompletedColor
        RequestStatus.CANCELLED ->
            StatusCancelledColor.copy(alpha = 0.18f) to StatusCancelledColor
        RequestStatus.REJECTED ->
            StatusRejectedColor.copy(alpha = 0.18f) to StatusRejectedColor
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun RequestDetailsDialog(
    request: Request,
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val createdText = request.createdAt.format(formatter)
    val matchPercent = (request.matchScore * 100).coerceIn(0.0, 100.0)
    val distanceText = String.format("%.1f km", request.distanceKm)
    val coordsText = "Lat: ${request.latitude}, Lng: ${request.longitude}"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonNavy,
                    contentColor = Color.White
                )
            ) {
                Text("Cerrar")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = ButtonNavy,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Request #${request.id}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Estado
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    StatusBadge(status = request.status)
                }

                Divider()

                InfoRow(label = "Vehicle ID", value = request.vehicleId.toString())
                InfoRow(label = "Created", value = createdText)
                InfoRow(label = "Match", value = "${matchPercent.roundToInt()}%")
                InfoRow(label = "Distance", value = distanceText)
                InfoRow(label = "Location", value = coordsText)

                if (request.requestedServices.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Services",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    Spacer(Modifier.height(4.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        request.requestedServices.forEach { service ->
                            Surface(
                                color = Color(0xFFE4EAF3),
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = service,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                if (request.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    Text(
                        text = request.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary
        )
    }
}
