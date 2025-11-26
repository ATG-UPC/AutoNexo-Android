package com.atg.autonexo.features.payment.presentation.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

    LaunchedEffect(Unit) {
        viewModel.loadSubscription()
        viewModel.defaultSubscription(workshopId = workshopId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Mi Suscripción",
            style = MaterialTheme.typography.headlineLarge
        )

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

                // Decidimos a qué tier cambiar
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
    }
}
