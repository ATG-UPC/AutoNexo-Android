package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atg.autonexo.core.ui.theme.AppTypography

@Composable
fun AuthHeader(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D3748),
                        Color(0xFF1A202C)
                    )
                )
            )
            .clip(
                RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 40.dp
                )
            )
    ) {
        // Botón de volver
        if (showBackButton && onBackClick != null) {
            AuthBackButton(
                onBackClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        
        // Título centrado
        Text(
            text = title,
            style = AppTypography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 28.sp
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun AuthTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = AppTypography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D)
        ),
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AuthIllustrationPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Color(0xFFF7FAFC),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ilustración del mecánico\n(Imagen placeholder)",
                style = AppTypography.bodyMedium.copy(
                    color = Color(0xFF718096),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
