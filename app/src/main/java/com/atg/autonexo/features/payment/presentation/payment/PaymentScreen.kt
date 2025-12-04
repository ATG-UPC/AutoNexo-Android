package com.atg.autonexo.features.payment.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier

// Colores del diseño
private val HeaderGradientTop = Color(0xFF1E2B36)
private val HeaderGradientBottom = Color(0xFF2E4256)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val SelectorBackground = Color(0xFFE4EAF3)
private val SelectorActive = Color(0xFF5473A1)
private val SelectorInactiveText = Color(0xFF555555)
private val PlanGradientTop = Color(0xFF839BC2)
private val PlanGradientBottom = Color(0xFF5A79A2)
private val PlanTitleColor = Color(0xFF0F1B2E)
private val PlanSubtitleColor = Color(0xFF243447)
private val PlanFeatureText = Color(0xFF1B2738)
private val PlanButtonBorder = Color(0xFF243447)

// Colores para plan seleccionado
private val SelectedPlanGradientTop = Color(0xFF3C4E6A)
private val SelectedPlanGradientBottom = Color(0xFF263854)
private val SelectedButtonBackground = Color(0xFF243447)
private val SelectedButtonText = Color(0xFFFFFFFF)

enum class BillingPeriod {
    MONTHLY, ANNUAL
}

@Composable
fun PaymentScreen(
    workshopId: Long,
    onBack: () -> Unit = {},
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var billingPeriod by remember { mutableStateOf(BillingPeriod.MONTHLY) }
    val currentTier = uiState.payment?.subscriptionTier

    LaunchedEffect(Unit) {
        viewModel.loadSubscription()
        viewModel.defaultSubscription(workshopId = workshopId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
        ) {

            RoundedHeader("Payment", onBack = onBack)

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Selector Monthly/Annual
                BillingPeriodSelector(
                    selectedPeriod = billingPeriod,
                    onPeriodSelected = { billingPeriod = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                )

                // Tarjetas de planes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Plan Basic
                    PlanCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Basic",
                        subtitle = "FIRST STEPS TO JOIN AUTONEXO'S TEAM AND IMPROVE YOUR BUSINESS",
                        price = if (billingPeriod == BillingPeriod.MONTHLY) "$10" else "$100",
                        features = listOf(
                            "Profile and service management",
                            "Price catalog",
                            "Service reports",
                            "Integrated payments",
                            "Up to 50 active clients"
                        ),
                        buttonLabel = "Join Basic",
                        isSelected = currentTier == SubscriptionTier.BASIC,
                        onJoinClick = {
                            viewModel.updateSubscriptionTier(SubscriptionTier.BASIC)
                        }
                    )

                    // Plan Premium
                    PlanCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Premium",
                        subtitle = "PERFECT FOR SERIOUS BUSINESSES LOOKING TO EXPAND THEIR SERVICES AND TECHNICAL CAPABILITIES",
                        price = if (billingPeriod == BillingPeriod.MONTHLY) "$20" else "$200",
                        features = listOf(
                            "Multi-site management",
                            "Marketing and promotions",
                            "Financial reporting",
                            "Unlimited customers and mechanics",
                            "Priority technical support"
                        ),
                        buttonLabel = "Join Premium",
                        isSelected = currentTier == SubscriptionTier.PREMIUM,
                        onJoinClick = {
                            viewModel.updateSubscriptionTier(SubscriptionTier.PREMIUM)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // Error message
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Mostrar Snackbar si lo necesitas
            }
        }
    }
}

@Composable
private fun BillingPeriodSelector(
    selectedPeriod: BillingPeriod,
    onPeriodSelected: (BillingPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = SelectorBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Monthly
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        if (selectedPeriod == BillingPeriod.MONTHLY) SelectorActive else Color.Transparent
                    )
                    .clickable { onPeriodSelected(BillingPeriod.MONTHLY) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Monthly",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = if (selectedPeriod == BillingPeriod.MONTHLY) Color.White else SelectorInactiveText
                )
            }

            // Annual
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        if (selectedPeriod == BillingPeriod.ANNUAL) SelectorActive else Color.Transparent
                    )
                    .clickable { onPeriodSelected(BillingPeriod.ANNUAL) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Annual",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = if (selectedPeriod == BillingPeriod.ANNUAL) Color.White else SelectorInactiveText
                )
            }
        }
    }
}

@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    price: String,
    features: List<String>,
    buttonLabel: String,
    isSelected: Boolean,
    onJoinClick: () -> Unit
) {
    val gradientColors = if (isSelected) {
        listOf(SelectedPlanGradientTop, SelectedPlanGradientBottom)
    } else {
        listOf(PlanGradientTop, PlanGradientBottom)
    }

    val effectiveButtonLabel = if (isSelected) "Selected" else buttonLabel

    // Colores de texto según selección
    val titleColor = if (isSelected) Color.White else PlanTitleColor
    val subtitleColor = if (isSelected) Color.White else PlanSubtitleColor
    val priceColor = if (isSelected) Color.White else Color.Black
    val featureTextColor = if (isSelected) Color.White else PlanFeatureText
    val checkIconTint = if (isSelected) Color.White else Color(0xFF1E2B36)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Título del plan
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtítulo
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = subtitleColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Precio
                Text(
                    text = price,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    ),
                    color = priceColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de beneficios
                features.forEach { feature ->
                    FeatureItem(
                        text = feature,
                        textColor = featureTextColor,
                        iconTint = checkIconTint
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón: cambia estilo si está seleccionado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(
                            width = 1.dp,
                            color = PlanButtonBorder,
                            shape = RoundedCornerShape(50)
                        )
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) SelectedButtonBackground else Color.Transparent
                        )
                        .clickable { onJoinClick() }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = effectiveButtonLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = if (isSelected) SelectedButtonText else PlanButtonBorder
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    text: String,
    textColor: Color = PlanFeatureText,
    iconTint: Color = Color(0xFF1E2B36)
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}
