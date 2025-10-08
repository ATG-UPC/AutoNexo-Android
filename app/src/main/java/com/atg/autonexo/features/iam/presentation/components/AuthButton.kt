package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atg.autonexo.core.ui.theme.AppTypography

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    isPrimary: Boolean = true
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3748),
                disabledContainerColor = Color(0xFFA0AEC0)
            ),
            enabled = enabled && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = AppTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF4A5568),
                disabledContentColor = Color(0xFFA0AEC0)
            ),
            enabled = enabled && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFF4A5568),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = AppTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun AuthDivider(
    text: String = "Or",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFE2E8F0)
        )
        
        Text(
            text = text,
            style = AppTypography.bodySmall.copy(
                color = Color(0xFFA0AEC0)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Divider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFE2E8F0)
        )
    }
}
