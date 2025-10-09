package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Generar lista de horas (07:00 a 21:00 en intervalos de 30 min)
    val timeSlots = remember {
        buildList {
            for (hour in 7..21) {
                add(String.format("%02d:00", hour))
                if (hour < 21) {
                    add(String.format("%02d:30", hour))
                }
            }
        }
    }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(48.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (enabled) Color(0xFF718096) else Color(0xFFD1D5DB)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4682B4),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                disabledBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C),
                disabledTextColor = Color(0xFF9CA3AF)
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .heightIn(max = 200.dp)
        ) {
            timeSlots.forEach { time ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = time,
                            fontSize = 14.sp,
                            color = Color(0xFF1A202C)
                        )
                    },
                    onClick = {
                        onValueChange(time)
                        expanded = false
                    }
                )
            }
        }
    }
}


