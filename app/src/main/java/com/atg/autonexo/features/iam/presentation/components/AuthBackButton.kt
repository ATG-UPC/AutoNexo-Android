package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuthBackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onBackClick,
        modifier = modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Volver",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
