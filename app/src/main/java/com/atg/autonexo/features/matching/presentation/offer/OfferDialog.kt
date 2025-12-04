package com.atg.autonexo.features.matching.presentation.offer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.CardBackground
import com.atg.autonexo.core.ui.theme.TextPrimary
import com.atg.autonexo.core.ui.theme.TextSecondary
import com.atg.autonexo.core.ui.theme.TextTertiary
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDialog(
    serviceRequestId: Long,
    onDismiss: () -> Unit,
    onSuccess: (Long) -> Unit,
    viewModel: OfferViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Para abrir/cerrar el selector de fecha y hora
    var showDateTimePicker by remember { mutableStateOf(false) }

    // Formato bonito para mostrar la fecha/hora seleccionada
    val dateTimeFormatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }
    val proposedDateLabel = state.proposedDate
        ?.format(dateTimeFormatter)
        ?: "Selecciona fecha y hora"

    // Inicializar el requestId cuando se abre el diálogo
    LaunchedEffect(Unit) {
        viewModel.setServiceRequestId(serviceRequestId)
    }

    // Cuando isSuccess se pone en true
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            delay(6500)
            viewModel.clearAfterSuccess()
            onSuccess(serviceRequestId)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            color = CardBackground
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // Título
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = ButtonNavy
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Create Offer",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Request #$serviceRequestId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Precio
                OutlinedTextField(
                    value = state.proposedPriceInput,
                    onValueChange = viewModel::updateProposedPriceInput,
                    label = { Text("Proposed Price (S/.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje
                OutlinedTextField(
                    value = state.message,
                    onValueChange = viewModel::updateMessage,
                    label = { Text("Message (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bloque de fecha y hora
                Text(
                    text = "Fecha y hora propuesta",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )

                Spacer(Modifier.height(4.dp))

                OutlinedButton(
                    onClick = { showDateTimePicker = true },
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        tint = ButtonNavy
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = proposedDateLabel,
                        color = ButtonNavy
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            viewModel.clearAfterSuccess()
                            onDismiss()
                        }
                    ) {
                        Text("Cancel", color = ButtonNavy)
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Validamos que exista monto y fecha antes de enviar
                            val amount = state.proposedPriceInput
                                .replace(",", ".")
                                .toBigDecimalOrNull()

                            if (amount == null || amount <= BigDecimal.ZERO) {
                                return@Button
                            }

                            if (state.proposedDate == null) {
                                return@Button
                            }

                            viewModel.clearError()
                            viewModel.createOffer { /* ya manejas el éxito en LaunchedEffect */ }
                        },
                        enabled = !state.isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonNavy,
                            contentColor = Color.White
                        )
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

    // Diálogo de calendario + hora (LocalDateTime)
    if (showDateTimePicker) {
        DateTimePickerDialog(
            initialDateTime = state.proposedDate ?: LocalDateTime.now().plusDays(1),
            onDismiss = { showDateTimePicker = false },
            onConfirm = { dateTime ->
                // 👇 SEGUIR USANDO EL MISMO MÉTODO DEL VIEWMODEL
                viewModel.updateProposedDate(dateTime)
                showDateTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime) -> Unit
) {
    val initialMillis = initialDateTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    var hourText by remember {
        mutableStateOf(initialDateTime.hour.toString().padStart(2, '0'))
    }
    var minuteText by remember {
        mutableStateOf(initialDateTime.minute.toString().padStart(2, '0'))
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val pickedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 9
                        val minute = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0

                        val result = LocalDateTime.of(
                            pickedDate,
                            LocalTime.of(hour, minute)
                        )
                        onConfirm(result)
                    } else {
                        // si no eligió nada, simplemente no confirmamos
                        onDismiss()
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            DatePicker(state = datePickerState)

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Hora",
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary
            )

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hourText,
                    onValueChange = {
                        hourText = it.filter { c -> c.isDigit() }.take(2)
                    },
                    label = { Text("HH") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minuteText,
                    onValueChange = {
                        minuteText = it.filter { c -> c.isDigit() }.take(2)
                    },
                    label = { Text("mm") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
