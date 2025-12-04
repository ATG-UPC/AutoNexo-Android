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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.atg.autonexo.features.matching.domain.models.CompleteBookingRequest
import com.atg.autonexo.features.matching.domain.models.ServiceCatalog
import com.atg.autonexo.features.matching.domain.models.ServicePerformed
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Composable
fun BookingScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    currentRoute: String,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedBookingDetails by remember { mutableStateOf<Booking?>(null) }
    var bookingToComplete by remember { mutableStateOf<Booking?>(null) }

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

                    // 👇 solo mostramos estados que tengan al menos 1 booking
                    val nonEmptyStatuses = statusOrder.filter { !groupedByStatus[it].isNullOrEmpty() }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = padding.calculateBottomPadding())
                    ) {
                        items(
                            items = nonEmptyStatuses,
                            key = { it.name }
                        ) { status ->
                            val bookingsForStatus = groupedByStatus[status].orEmpty()

                            BookingStatusGroup(
                                status = status,
                                count = bookingsForStatus.size,
                                bookings = bookingsForStatus,
                                onDetailsClick = { clicked ->
                                    selectedBookingDetails = clicked
                                },
                                onCompleteClick = { clicked ->
                                    bookingToComplete = clicked
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Popup de DETALLES (confirm schedule / info)
        if (selectedBookingDetails != null) {
            BookingDetailsDialog(
                booking = selectedBookingDetails!!,
                onDismiss = { selectedBookingDetails = null },
                onConfirmSchedule = { bookingToConfirm ->
                    viewModel.confirmSchedule(bookingToConfirm)
                    selectedBookingDetails = null
                }
            )
        }

        // Popup de COMPLETE (formulario con mileage, services, etc.)
        if (bookingToComplete != null) {
            CompleteBookingDialog(
                booking = bookingToComplete!!,
                onDismiss = { bookingToComplete = null },
                onConfirm = { bookingId, request ->
                    viewModel.completeBooking(bookingId, request)
                    bookingToComplete = null
                }
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BookingCard(
    booking: Booking,
    onDetailsClick: (Booking) -> Unit = {},
    onCompleteClick: (Booking) -> Unit = {}
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val scheduledText = booking.scheduledDate.format(dateFormatter)
    val createdText = booking.createdAt.format(dateFormatter)

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (booking.status == BookingStatus.SCHEDULED) {
                    // 👇 Cuando está programado, botón COMPLETE
                    Button(
                        onClick = { onCompleteClick(booking) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonNavy,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Complete")
                    }
                } else {
                    // 👇 Para otros estados, Details
                    Button(
                        onClick = { onDetailsClick(booking) },
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

/**
 * Grupo desplegable por estado (similar a OfferListScreen.StatusGroup)
 */
@Composable
private fun BookingStatusGroup(
    status: BookingStatus,
    count: Int,
    bookings: List<Booking>,
    onDetailsClick: (Booking) -> Unit,
    onCompleteClick: (Booking) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ButtonNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Api,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = TextPrimary
                    )
                    Text(
                        text = "Servicios: $count",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                bookings.forEach { booking ->
                    BookingCard(
                        booking = booking,
                        onDetailsClick = onDetailsClick,
                        onCompleteClick = onCompleteClick
                    )
                }
            }
        }
    }
}

@Composable
fun BookingDetailsDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onConfirmSchedule: (Booking) -> Unit = {}
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
            if (booking.status == BookingStatus.PENDING_SCHEDULE) {
                Button(
                    onClick = { onConfirmSchedule(booking) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirm schedule")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text(
                    text = "Cerrar",
                    color = ButtonNavy
                )
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

                InfoRow(label = "Scheduled", value = scheduledText)
                InfoRow(label = "Created", value = createdText)
                completedText?.let { InfoRow(label = "Completed", value = it) }
                pickedUpText?.let { InfoRow(label = "Picked up", value = it) }
                cancelledText?.let { InfoRow(label = "Cancelled", value = it) }

                Divider()

                InfoRow(label = "Price", value = priceText)

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

/**
 * Dialog para completar un booking (JSON:
 * mileage, services[], observations, imageUrls[], finalPriceAmount, currency)
 */
@Composable
fun CompleteBookingDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onConfirm: (bookingId: Long, request: CompleteBookingRequest) -> Unit
) {
    var mileageText by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var finalPriceText by remember { mutableStateOf("") }
    var currencyText by remember { mutableStateOf(booking.currency ?: "PEN") }
    var imageUrlsText by remember { mutableStateOf("") }

    var selectedService by remember { mutableStateOf<ServiceCatalog?>(null) }
    var serviceDescription by remember { mutableStateOf("") }
    var serviceCostText by remember { mutableStateOf("") }
    var serviceMenuExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val scheduledText = booking.scheduledDate.format(dateFormatter)

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    // Validaciones simples
                    val mileage = mileageText.toIntOrNull()
                    val finalPrice = finalPriceText.replace(",", ".").toBigDecimalOrNull()
                    val cost = serviceCostText.replace(",", ".").toBigDecimalOrNull()

                    if (mileage == null || mileage < 0) {
                        errorMessage = "Ingresa un kilometraje válido"
                        return@Button
                    }

                    if (selectedService == null) {
                        errorMessage = "Selecciona al menos un servicio"
                        return@Button
                    }

                    if (cost == null || cost <= BigDecimal.ZERO) {
                        errorMessage = "Ingresa un costo válido para el servicio"
                        return@Button
                    }

                    if (finalPrice == null || finalPrice <= BigDecimal.ZERO) {
                        errorMessage = "Ingresa un monto total final válido"
                        return@Button
                    }

                    val imageUrls = imageUrlsText
                        .split("\n", ",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    val services = listOf(
                        ServicePerformed(
                            serviceType = selectedService!!.name,   // enum name -> backend enum
                            description = serviceDescription,
                            cost = cost
                        )
                    )

                    val request = CompleteBookingRequest(
                        mileage = mileage,
                        services = services,
                        observations = observations,
                        imageUrls = imageUrls,
                        finalPriceAmount = finalPrice,
                        currency = currencyText.ifBlank { "PEN" }
                    )

                    isSubmitting = true
                    errorMessage = null
                    onConfirm(booking.id, request)
                    isSubmitting = false
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonNavy,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Complete booking")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { if (!isSubmitting) onDismiss() },
                enabled = !isSubmitting,
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = ButtonNavy
                )
            }
        },
        title = {
            Column {
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
                        text = "Complete Booking #${booking.id}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Text(
                    text = "Scheduled: $scheduledText",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mileage
                OutlinedTextField(
                    value = mileageText,
                    onValueChange = {
                        mileageText = it.filter { c -> c.isDigit() }.take(7)
                        errorMessage = null
                    },
                    label = { Text("Mileage (km)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // --- Servicio (usamos ServiceCatalog)
                Text(
                    text = "Service performed",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Selector de tipo de servicio
                    OutlinedButton(
                        onClick = { serviceMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedService?.displayName ?: "Seleccionar servicio",
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = serviceMenuExpanded,
                        onDismissRequest = { serviceMenuExpanded = false }
                    ) {
                        ServiceCatalog.values().forEach { service ->
                            DropdownMenuItem(
                                text = { Text(service.displayName) },
                                onClick = {
                                    selectedService = service
                                    serviceMenuExpanded = false
                                    errorMessage = null
                                }
                            )
                        }
                    }

                    // Descripción opcional
                    OutlinedTextField(
                        value = serviceDescription,
                        onValueChange = {
                            serviceDescription = it
                        },
                        label = { Text("Descripción del servicio (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonNavy,
                            focusedLabelColor = ButtonNavy,
                            cursorColor = ButtonNavy
                        )
                    )

                    // Costo del servicio
                    OutlinedTextField(
                        value = serviceCostText,
                        onValueChange = {
                            serviceCostText = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                            errorMessage = null
                        },
                        label = { Text("Costo del servicio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonNavy,
                            focusedLabelColor = ButtonNavy,
                            cursorColor = ButtonNavy
                        )
                    )
                }

                // Observaciones
                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Observations (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // URLs de imágenes (una por línea o separadas por coma)
                OutlinedTextField(
                    value = imageUrlsText,
                    onValueChange = { imageUrlsText = it },
                    label = { Text("Image URLs (opcional)") },
                    supportingText = {
                        Text(
                            text = "Una URL por línea o separadas por coma",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // Precio final + moneda
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = finalPriceText,
                        onValueChange = {
                            finalPriceText = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                            errorMessage = null
                        },
                        label = { Text("Final price amount") },
                        singleLine = true,
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonNavy,
                            focusedLabelColor = ButtonNavy,
                            cursorColor = ButtonNavy
                        )
                    )

                    OutlinedTextField(
                        value = currencyText,
                        onValueChange = { currencyText = it.take(3) },
                        label = { Text("Currency") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonNavy,
                            focusedLabelColor = ButtonNavy,
                            cursorColor = ButtonNavy
                        )
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        containerColor = CardBackground,
        textContentColor = TextPrimary
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
