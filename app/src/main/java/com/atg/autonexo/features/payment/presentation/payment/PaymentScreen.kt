package com.atg.autonexo.features.payment.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier

@Composable
fun PaymentScreen(
    workshopId: Long,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadSubscription()
        viewModel.defaultSubscription(workshopId = workshopId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Mi Suscripción",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.payment != null -> {
                val p = uiState.payment!!

                val nextTier = when (p.subscriptionTier) {
                    SubscriptionTier.FREE -> SubscriptionTier.BASIC
                    SubscriptionTier.BASIC -> SubscriptionTier.PREMIUM
                    SubscriptionTier.PREMIUM -> SubscriptionTier.FREE
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Tier actual: ${p.subscriptionTier}")
                        Text("Estado: ${p.status}")
                        Text("Vence: ${p.expiresAt}")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.updateSubscriptionTier(nextTier) },
                            enabled = !uiState.isLoading
                        ) {
                            Text("Cambiar a plan ${nextTier.displayName}")
                        }
                    }
                }
            }
        }

        // Tarjeta tipo Plan Pro
        Spacer(modifier = Modifier.height(32.dp))

        PlanCard(
            modifier = Modifier.fillMaxWidth(),
            title = "Basic",
            subtitle = "FIRST STEPS TO JOIN AUTONEXO'S TEAM AND IMPROVE YOUR BUSINESS",
            price = "$10",
            features = listOf(
                "Profile and service management",
                "Price catalog",
                "Service reports",
                "Integrated payments",
                "Up to 50 active clients"
            ),
            buttonLabel = "Join Basic",
            onJoinClick = {
                viewModel.updateSubscriptionTier(SubscriptionTier.BASIC)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        PlanCard(
            modifier = Modifier.fillMaxWidth(),
            title = "Premiun",
            subtitle = "Perfect for serious businesses looking to expand their services and technical capabilities.",
            price = "$20",
            features = listOf(
                "Multi-site management",
                "Marketing and promotions",
                "Financial reporting",
                "Unlimited customers and mechanics",
                "Priority technical support"
            ),
            buttonLabel = "Join Premiun",
            onJoinClick = {
                viewModel.updateSubscriptionTier(SubscriptionTier.PREMIUM)
            }
        )
    }
}

/**
 * Tarjeta de plan *
 */
@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    price: String,
    features: List<String>,
    buttonLabel: String,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            features.forEach { feature ->
                FeatureItem(feature)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onJoinClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(buttonLabel)
            }
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
