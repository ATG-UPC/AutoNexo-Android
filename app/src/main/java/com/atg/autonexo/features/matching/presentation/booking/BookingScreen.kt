package com.atg.autonexo.features.matching.presentation.booking

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.atg.autonexo.core.ui.theme.StatusPendingToScheduleColor
import com.atg.autonexo.core.ui.theme.TextPrimary
import com.atg.autonexo.core.ui.theme.TextSecondary
import com.atg.autonexo.core.ui.theme.TextTertiary
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.atg.autonexo.features.matching.domain.models.Booking
import com.atg.autonexo.features.matching.domain.models.BookingStatus
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Composable
fun BookingScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    currentRoute: String,
    onBack: () -> Unit = {}
){
    val uiState by viewModel.uiState.collectAsState()
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

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
            RoundedHeader("Booking Services", onBack = onBack)

            Spacer(Modifier.height(8.dp))

            // HEADER GENERAL: TOTAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Build,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Total Booking Services",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Text(
                    text = "${uiState.bookings.size}",
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
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            text = uiState.errorMessage ?: "Error",
                            color = Color.Red
                        )
                    }
                }

                uiState.bookings.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("No hay bookings", color = TextSecondary)
                    }
                }

                else -> {

                    val groupedByStatus = uiState.bookings.groupBy { it.status }

                    val statusOrder = listOf(
                        BookingStatus.PENDING_SCHEDULE,
                        BookingStatus.SCHEDULED,
                        BookingStatus.PENDING_PICKUP,
                        BookingStatus.IN_PROGRESS,
                        BookingStatus.COMPLETED,
                        BookingStatus.PICKED_UP,
                        BookingStatus.CANCELLED
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = padding.calculateBottomPadding())
                    ) {
                        statusOrder.forEach { status ->
                            val bookingsForStatus = groupedByStatus[status]
                            if (!bookingsForStatus.isNullOrEmpty()) {

                                // Encabezado por estado
                                item(key = "header_${status.name}") {
                                    BookingStatusHeader(
                                        status = status,
                                        count = bookingsForStatus.size
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }

                                // Items de ese estado
                                items(
                                    items = bookingsForStatus,
                                    key = { it.id }
                                ) { booking ->
                                    BookingCard(
                                        booking = booking,
                                        onBookingClick = { clicked ->
                                            selectedBooking = clicked
                                        }
                                    )
                                }

                                // Separación entre grupos
                                item(key = "spacer_${status.name}") {
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Popup de detalles
        if (selectedBooking != null) {
            BookingDetailsDialog(
                booking = selectedBooking!!,
                onDismiss = { selectedBooking = null }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BookingCard(
    booking: Booking,
    onBookingClick: (Booking) -> Unit = {},
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val scheduledText = booking.scheduledDate.format(dateFormatter)
    val createdText = booking.createdAt.format(dateFormatter)

    // Mostrar precio final si existe, si no, el propuesto
    val priceToShow = if (booking.finalPriceAmount > BigDecimal.ZERO) {
        booking.finalPriceAmount
    } else {
        booking.proposedPriceAmount
    }
    val currencyLabel = booking.currency ?: ""
    val priceText = "$currencyLabel $priceToShow"

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
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Booking vehicle",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Booking #${booking.id}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "Scheduled: $scheduledText",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = TextSecondary
                        )
                    }
                }

                StatusBadge(status = booking.status)
            }

            // Servicios solicitados
            if (booking.requestedServices.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    booking.requestedServices.forEach { service ->
                        Surface(
                            color = Color(0xFFE4EAF3),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = service?.name.orEmpty(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Descripción
            if (booking.description.isNotBlank()) {
                Text(
                    text = booking.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio
                Column {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        text = priceText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }

                // Fecha de creación
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

            // Botón Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onBookingClick(booking) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text("Details")
                }
            }
        }
    }
}

@Composable
private fun BookingStatusHeader(
    status: BookingStatus,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Api,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }

        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun StatusBadge(status: BookingStatus) {
    val (bgColor, textColor) = when (status) {
        BookingStatus.PENDING_SCHEDULE ->
            StatusPendingToScheduleColor.copy(alpha = 0.18f) to StatusPendingToScheduleColor
        BookingStatus.SCHEDULED ->
            StatusPendingColor.copy(alpha = 0.18f) to StatusPendingColor

        BookingStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) to MaterialTheme.colorScheme.primary

        BookingStatus.COMPLETED,
        BookingStatus.PICKED_UP ->
            StatusCompletedColor.copy(alpha = 0.18f) to StatusCompletedColor

        BookingStatus.PENDING_PICKUP ->
            StatusPendingColor.copy(alpha = 0.18f) to StatusPendingColor

        BookingStatus.CANCELLED ->
            StatusCancelledColor.copy(alpha = 0.18f) to StatusCancelledColor
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
fun BookingDetailsDialog(
    booking: Booking,
    onDismiss: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val scheduledText = booking.scheduledDate.format(dateFormatter)
    val createdText = booking.createdAt.format(dateFormatter)
    val completedText = booking.completedAt?.format(dateFormatter)
    val pickedUpText = booking.pickedUpAt?.format(dateFormatter)
    val cancelledText = booking.cancelledAt?.format(dateFormatter)

    val priceToShow = if (booking.finalPriceAmount > BigDecimal.ZERO) {
        booking.finalPriceAmount
    } else {
        booking.proposedPriceAmount
    }
    val currencyLabel = booking.currency ?: ""
    val priceText = "$currencyLabel $priceToShow"

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
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = ButtonNavy,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Booking #${booking.id}",
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
                    StatusBadge(status = booking.status)
                }

                Divider()

                // Fechas
                InfoRow(label = "Scheduled", value = scheduledText)
                InfoRow(label = "Created", value = createdText)
                completedText?.let { InfoRow(label = "Completed", value = it) }
                pickedUpText?.let { InfoRow(label = "Picked up", value = it) }
                cancelledText?.let { InfoRow(label = "Cancelled", value = it) }

                Divider()

                // Precio
                InfoRow(label = "Price", value = priceText)

                // Servicios
                if (booking.requestedServices.isNotEmpty()) {
                    Text(
                        text = "Services",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    Spacer(Modifier.height(4.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        booking.requestedServices.forEach { service ->
                            Surface(
                                color = Color(0xFFE4EAF3),
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = service?.name.orEmpty(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Descripción
                if (booking.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    Text(
                        text = booking.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }

                // Cancel reason
                if (!booking.cancelledReason.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Cancel reason",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    Text(
                        text = booking.cancelledReason.orEmpty(),
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
