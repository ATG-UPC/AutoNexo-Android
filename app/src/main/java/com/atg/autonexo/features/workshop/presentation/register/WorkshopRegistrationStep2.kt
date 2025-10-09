package com.atg.autonexo.features.workshop.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.SuccessDialog
import com.atg.autonexo.core.ui.components.ErrorDialog
import com.atg.autonexo.features.iam.presentation.components.*

@Composable
fun WorkshopRegistrationStep2Screen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: WorkshopRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Show dialogs
    if (uiState.showSuccessDialog) {
        SuccessDialog(
            message = "The workshop was created successfully.",
            onDismiss = {
                viewModel.dismissSuccessDialog()
                onSuccess()
            }
        )
    }
    
    if (uiState.showErrorDialog) {
        ErrorDialog(
            message = uiState.errorMessage ?: "There was an error during the process.",
            onDismiss = viewModel::dismissErrorDialog
        )
    }
    
    WorkshopRegistrationStep2Content(
        uiState = uiState,
        onDayScheduleChange = viewModel::updateDaySchedule,
        onOpen24HoursChange = viewModel::updateOpen24Hours,
        onDescriptionChange = viewModel::updateDescription,
        onRegisterClick = viewModel::registerWorkshop,
        onBackClick = onNavigateBack
    )
}

@Composable
private fun WorkshopRegistrationStep2Content(
    uiState: WorkshopRegistrationUiState,
    onDayScheduleChange: (String, String?, String?, Boolean?) -> Unit,
    onOpen24HoursChange: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        WorkshopHeader(
            title = "Workshop",
            onBackClick = onBackClick
        )
        
        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Opening hours section
            Text(
                text = "Opening hours",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4682B4)
            )
            
            // Monday
            DayScheduleRow(
                day = "Monday",
                start = uiState.mondayStart,
                end = uiState.mondayEnd,
                isFreeDay = uiState.mondayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Monday", it, null, null) },
                onEndChange = { onDayScheduleChange("Monday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Monday", null, null, it) }
            )
            
            // Tuesday
            DayScheduleRow(
                day = "Tuesday",
                start = uiState.tuesdayStart,
                end = uiState.tuesdayEnd,
                isFreeDay = uiState.tuesdayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Tuesday", it, null, null) },
                onEndChange = { onDayScheduleChange("Tuesday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Tuesday", null, null, it) }
            )
            
            // Wednesday
            DayScheduleRow(
                day = "Wednesday",
                start = uiState.wednesdayStart,
                end = uiState.wednesdayEnd,
                isFreeDay = uiState.wednesdayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Wednesday", it, null, null) },
                onEndChange = { onDayScheduleChange("Wednesday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Wednesday", null, null, it) }
            )
            
            // Thursday
            DayScheduleRow(
                day = "Thursday",
                start = uiState.thursdayStart,
                end = uiState.thursdayEnd,
                isFreeDay = uiState.thursdayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Thursday", it, null, null) },
                onEndChange = { onDayScheduleChange("Thursday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Thursday", null, null, it) }
            )
            
            // Friday
            DayScheduleRow(
                day = "Friday",
                start = uiState.fridayStart,
                end = uiState.fridayEnd,
                isFreeDay = uiState.fridayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Friday", it, null, null) },
                onEndChange = { onDayScheduleChange("Friday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Friday", null, null, it) }
            )
            
            // Saturday
            DayScheduleRow(
                day = "Saturday",
                start = uiState.saturdayStart,
                end = uiState.saturdayEnd,
                isFreeDay = uiState.saturdayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Saturday", it, null, null) },
                onEndChange = { onDayScheduleChange("Saturday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Saturday", null, null, it) }
            )
            
            // Sunday
            DayScheduleRow(
                day = "Sunday",
                start = uiState.sundayStart,
                end = uiState.sundayEnd,
                isFreeDay = uiState.sundayFreeDay,
                enabled = !uiState.open24Hours,
                onStartChange = { onDayScheduleChange("Sunday", it, null, null) },
                onEndChange = { onDayScheduleChange("Sunday", null, it, null) },
                onFreeDayChange = { onDayScheduleChange("Sunday", null, null, it) }
            )
            
            // Open 24 hours checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = uiState.open24Hours,
                    onCheckedChange = onOpen24HoursChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4682B4),
                        uncheckedColor = Color(0xFF9CA3AF)
                    )
                )
                
                Text(
                    text = "Open 24 hours",
                    fontSize = 14.sp,
                    color = Color(0xFF4A5568)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description section
            Text(
                text = "Description",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text(
                        text = "Describe your workshop (e.g. Welcome to Adonz Automotive!)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = Color(0xFF1A202C),
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                maxLines = 6,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Save and Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save Button
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4682B4),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                // Cancel Button
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E3C47)
                    ),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DayScheduleRow(
    day: String,
    start: String,
    end: String,
    isFreeDay: Boolean,
    enabled: Boolean,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    onFreeDayChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Day name
        Text(
            text = day,
            fontSize = 14.sp,
            color = Color(0xFF4A5568),
            modifier = Modifier.width(90.dp)
        )
        
        // Start time
        TimeDropdown(
            value = start,
            onValueChange = onStartChange,
            enabled = enabled && !isFreeDay,
            modifier = Modifier.weight(1f)
        )
        
        // Dash separator
        Text(
            text = "-",
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF)
        )
        
        // End time
        TimeDropdown(
            value = end,
            onValueChange = onEndChange,
            enabled = enabled && !isFreeDay,
            modifier = Modifier.weight(1f)
        )
        
        // Free day checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.width(100.dp)
        ) {
            Checkbox(
                checked = isFreeDay,
                onCheckedChange = { if (enabled) onFreeDayChange(it) },
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4682B4),
                    uncheckedColor = Color(0xFF9CA3AF)
                ),
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = "Free day",
                fontSize = 12.sp,
                color = if (enabled) Color(0xFF4A5568) else Color(0xFF9CA3AF)
            )
        }
    }
}

