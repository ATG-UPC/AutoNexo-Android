package com.atg.autonexo.features.subscription.presentation.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// TODO: PlansUiState
@Composable
fun PlanTypeSelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val options = listOf("Monthly", "Annual")

        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(
                        if (isSelected) Color(0xFF202D36) else Color.LightGray.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
                    )
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else Color.DarkGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}