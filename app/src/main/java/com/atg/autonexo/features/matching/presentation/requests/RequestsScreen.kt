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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.atg.autonexo.features.matching.domain.models.Request
import com.atg.autonexo.features.matching.domain.models.RequestStatus
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// Colores base (alineados al Home)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF555555)
private val TextTertiary = Color(0xFF767676)
private val CardBackground = Color(0xFFFFFFFF)
private val ButtonNavy = Color(0xFF1F2D40)
private val IconGray = Color(0xFFA7A7A7)
private val StatusPendingColor = Color(0xFFFFC107)
private val StatusCompletedColor = Color(0xFF4CAF50)
private val StatusCancelledColor = Color(0xFF8C1C1C)
private val StatusRejectedColor = Color(0xFFB0B0B0)

@Composable
fun RequestsScreen(
    viewModel: RequestsViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    currentRoute: String
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { RequestTopBar() },
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
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
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
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.requests) { request ->
                            RequestCard(request)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestTopBar() {
    TopAppBar(
        title = { Text("Requests") },
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun RequestCard(request: Request) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val createdText = request.createdAt.format(formatter)
    val matchPercent = (request.matchScore * 100).coerceIn(0.0, 100.0)
    val distanceText = String.format("%.1f km", request.distanceKm)

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
                            contentDescription = "Vehicle",
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
                            text = "Vehicle ID: ${request.vehicleId}",
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
                    text = request.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextSecondary
                )
            }

            // Info: match, distancia, fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Match score
                Column {
                    Text(
                        text = "Match",
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
                        text = "Distance",
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
                        text = "Created",
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
                    onClick = {
                        // TODO: abrir detalles
                    },
                    modifier = Modifier.weight(1f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text(
                        text = "Details",
                        color = ButtonNavy
                    )
                }

                Button(
                    onClick = {
                        // TODO: acción para ofrecer servicio
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text("Offer")
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
