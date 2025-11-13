package com.atg.autonexo.features.matchingbooking.presentation.offer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.BottomNavBar
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Offer
import com.atg.autonexo.features.matchingbooking.domain.repositories.OfferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@Composable
fun OfferListScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: OfferListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var pendingExpanded by remember { mutableStateOf(true) }
    var realizedExpanded by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "offer",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Header curvo estilo existente
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
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Offer",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(
                title = "Pending",
                count = uiState.pending.size,
                expanded = pendingExpanded,
                onToggle = { pendingExpanded = !pendingExpanded }
            )

            if (pendingExpanded) {
                OfferList(items = uiState.pending)
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionHeader(
                title = "Realized",
                count = uiState.realized.size,
                expanded = realizedExpanded,
                onToggle = { realizedExpanded = !realizedExpanded }
            )

            if (realizedExpanded) {
                OfferList(items = uiState.realized)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = Color(0xFF8E8E8E),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$count", color = Color(0xFF282828))
        }
        TextButton(onClick = onToggle) {
            Text(text = if (expanded) "▾" else "▸", color = Color(0xFF8E8E8E))
        }
    }
}

@Composable
private fun OfferList(items: List<OfferUi>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { offer ->
            OfferCard(offer)
        }
    }
}

@Composable
private fun OfferCard(offer: OfferUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (offer.state == OfferState.Pending) Color(0xFF5E7B97).copy(alpha = 0.9f) else Color(0xFFE9E9E9)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "#${offer.id}", color = Color(0xFF8E8E8E), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(text = offer.ownerName, color = if (offer.state == OfferState.Pending) Color.White else Color(0xFF282828), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(text = offer.description, color = if (offer.state == OfferState.Pending) Color(0xFFE0E0E0) else Color(0xFF4A4A4A), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Min price: ${offer.minPrice}", color = if (offer.state == OfferState.Pending) Color.White else Color(0xFF282828), style = MaterialTheme.typography.bodySmall)
                Text(text = "Appointment: ${offer.appointment}", color = if (offer.state == OfferState.Pending) Color(0xFFBDBDBD) else Color(0xFF6B6B6B), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = offer.footer, color = if (offer.state == OfferState.Pending) Color(0xFFBDBDBD) else Color(0xFF8E8E8E), style = MaterialTheme.typography.labelSmall)
        }
    }
}

data class OfferUi(
    val id: String,
    val ownerName: String,
    val description: String,
    val minPrice: String,
    val appointment: String,
    val footer: String,
    val state: OfferState
)

enum class OfferState { Pending, Realized }

@HiltViewModel
class OfferListViewModel @Inject constructor(
    private val offerRepository: OfferRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "OfferListViewModel"
    }
    
    private val _uiState = MutableStateFlow(OfferUiState())
    val uiState: StateFlow<OfferUiState> = _uiState.asStateFlow()
    
    init {
        loadOffers()
    }
    
    fun loadOffers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                Log.d(TAG, "Loading workshop offers...")
                
                // Get pending offers
                val pendingResult = offerRepository.getMyWorkshopOffers("PENDING", null, null)
                val pendingOffers = when (pendingResult) {
                    is AuthResult.Success -> pendingResult.data
                    else -> emptyList()
                }
                
                // Get accepted/rejected offers (realized)
                val acceptedResult = offerRepository.getMyWorkshopOffers("ACCEPTED", null, null)
                val rejectedResult = offerRepository.getMyWorkshopOffers("REJECTED", null, null)
                val acceptedOffers = when (acceptedResult) {
                    is AuthResult.Success -> acceptedResult.data
                    else -> emptyList()
                }
                val rejectedOffers = when (rejectedResult) {
                    is AuthResult.Success -> rejectedResult.data
                    else -> emptyList()
                }
                val realizedOffers = acceptedOffers + rejectedOffers
                
                val pendingUi = pendingOffers.map { it.toOfferUi(OfferState.Pending) }
                val realizedUi = realizedOffers.map { it.toOfferUi(OfferState.Realized) }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pending = pendingUi,
                        realized = realizedUi
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading offers", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    private fun Offer.toOfferUi(state: OfferState): OfferUi {
        val price = "%.2f".format(estimatedPrice)
        val dateTime = formatOfferDateTime(proposedDate?.takeIf { it.isNotBlank() } ?: validUntil.takeIf { it.isNotBlank() } ?: createdAt)
        val footer = when (state) {
            OfferState.Pending -> calculateTimestamp(createdAt)
            OfferState.Realized -> when (status) {
                "ACCEPTED" -> "Accepted"
                "REJECTED" -> "Rejected"
                else -> "Completed"
            }
        }
        
        return OfferUi(
            id = id,
            ownerName = "Usuario ${serviceRequestId}", // TODO: Get real owner name
            description = description.ifEmpty { "No description provided" },
            minPrice = "$price ${currency ?: "soles"}",
            appointment = dateTime,
            footer = footer,
            state = state
        )
    }
    
    private fun formatOfferDateTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "N/A"
        
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(dateString, formatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            date.format(outputFormatter)
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    private fun calculateTimestamp(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "N/A"
        
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(dateString, formatter)
            val now = LocalDateTime.now()
            
            val hours = ChronoUnit.HOURS.between(date, now)
            val days = ChronoUnit.DAYS.between(date, now)
            
            when {
                hours < 1 -> "Hace menos de 1 hora"
                hours < 24 -> "Hace $hours horas"
                days == 1L -> "Hace 1 día"
                else -> "Hace $days días"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}

data class OfferUiState(
    val pending: List<OfferUi> = emptyList(),
    val realized: List<OfferUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


