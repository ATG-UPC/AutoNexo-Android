package com.atg.autonexo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Componente reutilizable para mostrar un diálogo de éxito
 * 
 * @param title Título del diálogo (por defecto "Success!")
 * @param message Mensaje personalizado a mostrar
 * @param onDismiss Acción al presionar el botón OK
 * @param buttonText Texto del botón (por defecto "OK")
 */
@Composable
fun SuccessDialog(
    title: String = "Success!",
    message: String,
    onDismiss: () -> Unit,
    buttonText: String = "OK"
) {
    ResultDialog(
        icon = Icons.Default.Check,
        iconBackgroundColor = Color(0xFFE8F5E9),
        iconTint = Color(0xFF4CAF50),
        title = title,
        message = message,
        onDismiss = onDismiss,
        buttonText = buttonText
    )
}

/**
 * Componente reutilizable para mostrar un diálogo de error
 * 
 * @param title Título del diálogo (por defecto "Error")
 * @param message Mensaje personalizado a mostrar
 * @param onDismiss Acción al presionar el botón OK
 * @param buttonText Texto del botón (por defecto "OK")
 */
@Composable
fun ErrorDialog(
    title: String = "Error",
    message: String,
    onDismiss: () -> Unit,
    buttonText: String = "OK"
) {
    ResultDialog(
        icon = Icons.Default.Close,
        iconBackgroundColor = Color(0xFFFFEBEE),
        iconTint = Color(0xFFF44336),
        title = title,
        message = message,
        onDismiss = onDismiss,
        buttonText = buttonText
    )
}

/**
 * Componente base para diálogos de resultado (éxito/error)
 * Este componente es privado y se usa internamente por SuccessDialog y ErrorDialog
 */
@Composable
private fun ResultDialog(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    buttonText: String
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2E3C47)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícono circular
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = iconTint
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón OK
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2E3C47)
                    )
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Componente genérico para diálogos personalizados
 * Permite mayor flexibilidad para casos especiales
 * 
 * @param icon Ícono a mostrar
 * @param iconBackgroundColor Color de fondo del círculo del ícono
 * @param iconTint Color del ícono
 * @param title Título del diálogo
 * @param message Mensaje a mostrar
 * @param onDismiss Acción al presionar el botón principal
 * @param buttonText Texto del botón principal
 * @param buttonColor Color del botón principal (opcional)
 */
@Composable
fun CustomDialog(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    buttonText: String = "OK",
    buttonColor: Color = Color.White
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2E3C47)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícono circular
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = iconTint
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color(0xFF2E3C47)
                    )
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

