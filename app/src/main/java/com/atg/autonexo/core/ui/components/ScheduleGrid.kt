package com.atg.autonexo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleGrid(
    selectedMonth: String,
    selectedYear: String,
    onMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Title
        Text(
            text = "Schedule",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4682B4),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Calendar container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // Month/Year selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF202D36),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onMonthClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = selectedMonth,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = selectedYear,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Seleccionar fecha",
                            tint = Color.White
                        )
                    }
                }
            }

            // Days header - Scrollable horizontally
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F2))
                    .padding(vertical = 12.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.width(80.dp)) // Espacio para la columna de tiempo
                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF202D36),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(100.dp)
                    )
                }
            }

            // Time slots - Scrollable vertically
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val timeSlots = generateTimeSlots()
                
                timeSlots.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // Time label (fixed column)
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(50.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xFFE0E0E0)
                                )
                                .background(Color(0xFFFAFAFA))
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = time,
                                fontSize = 11.sp,
                                color = Color(0xFF757575),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Empty cells for each day (7 days)
                        repeat(7) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(50.dp)
                                    .border(
                                        width = 0.5.dp,
                                        color = Color(0xFFE0E0E0)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    // TODO: Actualizar fecha seleccionada
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    selectedDayContainerColor = Color(0xFF4682B4)
                )
            )
        }
    }
}

// Función para generar slots de tiempo desde 6 AM hasta 12 AM (medianoche)
private fun generateTimeSlots(): List<String> {
    val timeSlots = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    val format12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    // Desde las 6 AM (hora 6) hasta las 12 AM del día siguiente (hora 24 = 0 del siguiente día)
    for (hour in 6..23) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        timeSlots.add(format12.format(calendar.time))
    }
    
    // Agregar 12 AM (medianoche)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    timeSlots.add(format12.format(calendar.time))
    
    return timeSlots
}

