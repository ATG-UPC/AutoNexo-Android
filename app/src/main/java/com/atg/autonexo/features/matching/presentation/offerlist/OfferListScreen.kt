package com.atg.autonexo.features.matching.presentation.offerlist

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.CardBackground
import com.atg.autonexo.core.ui.theme.IconGray
import com.atg.autonexo.core.ui.theme.StatusCancelledColor
import com.atg.autonexo.core.ui.theme.TextPrimary
import com.atg.autonexo.core.ui.theme.TextSecondary
import com.atg.autonexo.core.ui.theme.TextTertiary
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.atg.autonexo.features.matching.domain.models.Offer
import com.atg.autonexo.features.matching.domain.models.OfferStatus
import java.time.format.DateTimeFormatter
import java.math.BigDecimal

@SuppressLint("DefaultLocale")
@Composable
fun OfferListScreen(
    viewModel: OfferListViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    currentRoute: String,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val formatterDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    // Agrupar ofertas por estado
    val grouped = uiState.offers.groupBy { it.status }

    val statusOrder = listOf(
        OfferStatus.PENDING,
        OfferStatus.ACCEPTED,
        OfferStatus.REJECTED,
        OfferStatus.EXPIRED,
        OfferStatus.WITHDRAWN
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp)
        ) {
            RoundedHeader("Offers", onBack = onBack)

            Spacer(Modifier.height(8.dp))

            // total offers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                    text = "Total Offers",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Text(
                    text = "${uiState.offers.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            Spacer(Modifier.height(12.dp))

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

                uiState.offers.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("No hay ofertas", color = TextSecondary)
                    }
                }

                else -> {
                    // Lista agrupada por estado
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = padding.calculateBottomPadding())
                    ) {
                        // Iterar en el orden definido
                        items(
                            items = statusOrder,
                            key = { it.name }
                        ) { status ->
                            val offersForStatus = grouped[status].orEmpty()
                            StatusGroup(
                                status = status,
                                count = offersForStatus.size,
                                offers = offersForStatus,
                                onWithdraw = { },
                                formatter = formatterDateTime
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun statusIconFor(status: OfferStatus): ImageVector {
    return when (status) {
        OfferStatus.PENDING -> Icons.Filled.HourglassEmpty     // esperando
        OfferStatus.ACCEPTED -> Icons.Filled.CheckCircle       // aceptado
        OfferStatus.REJECTED -> Icons.Filled.Cancel            // rechazado
        OfferStatus.EXPIRED -> Icons.Filled.Schedule           // expirado
        OfferStatus.WITHDRAWN -> Icons.Filled.Undo             // retirado
        else -> Icons.Filled.Info                              // fallback
    }
}

@Composable
private fun StatusGroup(
    status: OfferStatus,
    count: Int,
    offers: List<Offer>,
    onWithdraw: (Long) -> Unit,
    formatter: DateTimeFormatter
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header fila para el estado
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
                        imageVector = statusIconFor(status),
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
                        text = "Ofertas: $count",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (expanded) {
            if (offers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay ofertas en este estado", color = TextSecondary)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    offers.forEach { offer ->
                        OfferCard(offer = offer, onWithdraw = onWithdraw, formatter = formatter)
                    }
                }
            }
        }  }
}

@Composable
private fun OfferCard(
    offer: Offer,
    onWithdraw: (Long) -> Unit,
    formatter: DateTimeFormatter
) {
    val proposedText = formatMoney(offer.proposedPriceAmount, offer.currency)
    val proposedDateText = offer.proposedDate.format(formatter)
    val expiresText = offer.expiresAt.format(formatter)
    val acceptedText = offer.acceptedAt?.format(formatter) ?: "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Header: icon + title + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ButtonNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AttachMoney,
                            contentDescription = "Price",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Offer #${offer.id}",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            color = TextPrimary
                        )
                        Text(
                            text = "Request #${offer.serviceRequestId} • Workshop #${offer.workshopId}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextTertiary
                        )
                    }
                }

                OfferStatusBadge(status = offer.status)
            }

            // message
            if (offer.message.isNotBlank()) {
                Text(
                    text = offer.message,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = TextSecondary
                )
            }

            // ----------- 2 COLUMNS -----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // -------- LEFT COLUMN --------
                Column {
                    // Price Proposed
                    Text(
                        text = "Price Proposed",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        text = proposedText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(10.dp))

                    // Proposed Date
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = IconGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = proposedDateText,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }

                // -------- RIGHT COLUMN --------
                Column(horizontalAlignment = Alignment.End) {

                    // Expires
                    Text(
                        text = "Expires",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        text = expiresText,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(10.dp))

                    // Accepted At
                    Text(
                        text = "Accepted At",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        text = acceptedText,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }

            // ----------- BUTTON -----------
            Button(
                onClick = { onWithdraw(offer.id) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusCancelledColor,
                    contentColor = Color.White
                )
            ) {
                Text("WITHDRAW")
            }
        }
    }
}


@Composable
private fun OfferStatusBadge(status: OfferStatus) {
    // COLORES PARA ESTADOS
    val StatusPendingColor = Color(0xFF546F8D)      // Azul grisáceo elegante (pending)
    val StatusCompletedColor = Color(0xFF4CAF50)    // Verde check básico
    val StatusRejectedColor = Color(0xFFFF5252)     // Rojo rechazo
    val StatusCancelledColor = Color(0xFFB0BEC5)    // Gris desaturado pro


    val (bgColor, textColor) = when (status) {
        OfferStatus.PENDING ->
            StatusPendingColor.copy(alpha = 0.18f) to StatusPendingColor

        OfferStatus.ACCEPTED ->
            StatusCompletedColor.copy(alpha = 0.18f) to StatusCompletedColor

        OfferStatus.REJECTED ->
            StatusRejectedColor.copy(alpha = 0.18f) to StatusRejectedColor

        OfferStatus.EXPIRED ->
            StatusRejectedColor.copy(alpha = 0.12f) to StatusRejectedColor

        OfferStatus.WITHDRAWN ->
            StatusCancelledColor.copy(alpha = 0.18f) to StatusCancelledColor

        else ->
            StatusPendingColor.copy(alpha = 0.18f) to StatusPendingColor
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = textColor
        )
    }
}

// formatear BigDecimal con moneda
private fun formatMoney(amount: BigDecimal, currency: String): String {
    return try {
        val valStr = amount.toPlainString()
        "$currency $valStr"
    } catch (e: Exception) {
        "$currency ${amount.toString()}"
    }
}
