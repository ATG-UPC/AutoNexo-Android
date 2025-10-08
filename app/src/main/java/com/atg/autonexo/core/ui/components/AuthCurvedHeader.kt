package com.atg.autonexo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthCurvedHeader(
    title: String,
    modifier: Modifier = Modifier,
    gradient: List<Color> = listOf(
        Color(0xFF202D36),
        Color(0xFF2E3C47)
    ),
    height: Dp = 200.dp,
    curve: Dp = 64.dp,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    topActions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(gradient),
                shape = BottomArcShape(curve)
            )
    ) {
        // Navigation icon (back or menu)
        navigationIcon?.let {
            IconButton(
                onClick = onNavigationClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = "Navigation",
                    tint = Color.White
                )
            }
        }

        // Top actions (notifications, menu)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = topActions
        )

        // Title
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
        )
    }
}

