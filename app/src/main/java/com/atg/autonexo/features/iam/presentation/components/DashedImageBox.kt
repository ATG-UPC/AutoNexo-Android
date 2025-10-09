package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashedImageBox(
    label: String,
    imageUri: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4682B4)
        )
        
        // Caja con borde punteado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val dashLength = 10.dp.toPx()
                    val gapLength = 5.dp.toPx()
                    
                    val pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(dashLength, gapLength),
                        phase = 0f
                    )
                    
                    drawRoundRect(
                        color = Color(0xFFD1D5DB),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = pathEffect
                        ),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = size.copy(
                            width = size.width - strokeWidth,
                            height = size.height - strokeWidth
                        )
                    )
                }
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                // TODO: Implement image loading when URI is available
                // For now, show a placeholder when image is selected
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image selected",
                        fontSize = 12.sp,
                        color = Color(0xFF4682B4)
                    )
                }
            } else {
                // Mostrar placeholder con ícono de cámara
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Text(
                        text = "Add photo",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

