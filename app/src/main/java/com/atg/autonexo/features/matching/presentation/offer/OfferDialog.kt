package com.atg.autonexo.features.matching.presentation.offer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime

@Composable
fun OfferDialog(
    serviceRequestId: Long,
    onDismiss: () -> Unit,
    onSuccess: (Long) -> Unit,
    viewModel: OfferViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Inicializar serviceRequestId cuando abre
    LaunchedEffect(Unit) {
        viewModel.setServiceRequestId(serviceRequestId)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            kotlinx.coroutines.delay(6500)
            viewModel.clearAfterSuccess()
            onSuccess(serviceRequestId)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text("Create Offer", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(12.dp))

                // Precio
                TextField(
                    value = state.proposedPriceInput,
                    onValueChange = viewModel::updateProposedPriceInput,
                    label = { Text("Proposed Price") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message
                TextField(
                    value = state.message,
                    onValueChange = viewModel::updateMessage,
                    label = { Text("Message") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha
                Text("Date: ${state.proposedDate ?: "Select..."}")

                Button(onClick = {
                    viewModel.updateProposedDate(LocalDateTime.now().plusDays(1))
                }) {
                    Text("Pick date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        viewModel.clearAfterSuccess()
                        onDismiss()
                    }) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            viewModel.createOffer { }
                        },
                        enabled = !state.isSubmitting
                    ) {
                        when {
                            state.isSubmitting -> Text("Sending...")
                            state.isSuccess -> Text("✔ Success")
                            else -> Text("Send")
                        }
                    }
                }
            }
        }
    }
}
