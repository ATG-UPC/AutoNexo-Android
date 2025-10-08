package com.atg.autonexo.features.auth.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.atg.autonexo.core.ui.theme.AppTypography

@Composable
fun WorkshopCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onWhatsThisClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workshop Code",
                style = AppTypography.bodyMedium.copy(
                    color = Color(0xFF4A5568),
                    fontWeight = FontWeight.Medium
                )
            )
            
            Text(
                text = "(optional)",
                style = AppTypography.bodySmall.copy(
                    color = Color(0xFF718096)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = "#XXX-XXX-XXX",
                        color = Color(0xFFA0AEC0)
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4299E1),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedTextColor = Color(0xFF1A202C),
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            TextButton(
                onClick = onWhatsThisClick
            ) {
                Text(
                    text = "What's this?",
                    style = AppTypography.bodySmall.copy(
                        color = Color(0xFF4299E1),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
