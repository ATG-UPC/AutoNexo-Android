package com.atg.autonexo.features.matching.presentation.offerlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atg.autonexo.core.ui.theme.StatusCancelledColor
import kotlinx.coroutines.delay

@Composable
fun WithdrawConfirmDialog(
    offerId: Long,
    onDismiss: () -> Unit,
    onConfirmWithdraw: suspend (Long) -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    var startProcess by remember { mutableStateOf(false) }

    if (startProcess) {
        LaunchedEffect(startProcess) {
            isProcessing = true
            onConfirmWithdraw(offerId)
            isDone = true
            delay(1000)
            onDismiss()
        }
    }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    "Withdraw Offer",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Are you sure you want to withdraw this offer?",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { if (!isProcessing) onDismiss() }
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusCancelledColor,
                            contentColor = Color.White
                        ),
                        onClick = {
                            startProcess = true
                        },
                        enabled = !isProcessing
                    ) {
                        when {
                            isDone -> Text("✔")
                            isProcessing -> Text("Processing…")
                            else -> Text("Withdraw")
                        }
                    }
                }
            }
        }
    }
}
