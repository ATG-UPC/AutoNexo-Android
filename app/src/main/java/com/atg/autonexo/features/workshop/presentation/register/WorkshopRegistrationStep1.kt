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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.features.iam.presentation.components.*

@Composable
fun WorkshopRegistrationStep1Screen(
    onNavigateBack: () -> Unit,
    onNavigateToStep2: () -> Unit,
    viewModel: WorkshopRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    WorkshopRegistrationStep1Content(
        uiState = uiState,
        onWorkshopNameChange = viewModel::updateWorkshopName,
        onRucChange = viewModel::updateRuc,
        onDistrictChange = viewModel::updateDistrict,
        onCityChange = viewModel::updateCity,
        onAddressChange = viewModel::updateAddress,
        onLogoClick = { /* TODO: Open image picker */ },
        onWorkshopImageClick = { /* TODO: Open image picker */ },
        onAddService = { /* TODO: Show dialog to add service */ },
        onRemoveService = viewModel::removeService,
        onNextClick = onNavigateToStep2,
        onBackClick = onNavigateBack
    )
}

@Composable
private fun WorkshopRegistrationStep1Content(
    uiState: WorkshopRegistrationUiState,
    onWorkshopNameChange: (String) -> Unit,
    onRucChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onLogoClick: () -> Unit,
    onWorkshopImageClick: () -> Unit,
    onAddService: () -> Unit,
    onRemoveService: (String) -> Unit,
    onNextClick: () -> Unit,
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
            // Workshop Name
            Text(
                text = "Workshop name",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = uiState.workshopName,
                onValueChange = onWorkshopNameChange,
                placeholder = {
                    Text(
                        text = "Commercial name (e.g. Adonz Automotive)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = Color(0xFF1A202C),
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // RUC
            Text(
                text = "RUC",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = uiState.ruc,
                onValueChange = onRucChange,
                placeholder = {
                    Text(
                        text = "Enter a valid RUC number",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = Color(0xFF1A202C),
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            
            // District and City Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "District",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4682B4),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    DistrictCityDropdown(
                        value = uiState.district,
                        onValueChange = onDistrictChange,
                        options = listOf("Lima", "Miraflores", "San Isidro", "Surco", "La Molina"),
                        placeholder = "Select",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "City",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4682B4),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    DistrictCityDropdown(
                        value = uiState.city,
                        onValueChange = onCityChange,
                        options = listOf("Lima", "Callao", "Arequipa", "Cusco", "Trujillo"),
                        placeholder = "Select",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Workshop Address
            Text(
                text = "Workshop address",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            
            OutlinedTextField(
                value = uiState.address,
                onValueChange = onAddressChange,
                placeholder = {
                    Text(
                        text = "Street name and number (e.g. Av Real 123)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = Color(0xFF1A202C),
                    unfocusedTextColor = Color(0xFF1A202C)
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // Logo and Workshop Image Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashedImageBox(
                    label = "Logo",
                    imageUri = uiState.logoUri,
                    onClick = onLogoClick,
                    modifier = Modifier.weight(1f)
                )
                
                DashedImageBox(
                    label = "Workshop image",
                    imageUri = uiState.workshopImageUri,
                    onClick = onWorkshopImageClick,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Services Available
            ServiceChipField(
                services = uiState.services,
                onRemoveService = onRemoveService,
                onAddClick = onAddService,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Next Button
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4682B4),
                    disabledContainerColor = Color(0xFFCCCCCC)
                ),
                enabled = uiState.isStep1Valid
            ) {
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun WorkshopHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF202D36), Color(0xFF2E3C47))
                ),
                shape = BottomArcShape(64.dp)
            )
    ) {
        // Botón de volver
        AuthBackButton(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        
        // Título
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

