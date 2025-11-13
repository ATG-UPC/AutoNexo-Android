package com.atg.autonexo.features.workshop.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
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
import com.atg.autonexo.core.ui.components.ErrorDialog
import com.atg.autonexo.core.ui.components.SuccessDialog
import com.atg.autonexo.features.iam.presentation.components.AuthBackButton
import com.atg.autonexo.features.workshop.domain.models.Location

@Composable
fun WorkshopLocationsScreen(
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit,
    flowViewModel: WorkshopCreationFlowViewModel = hiltViewModel(),
    localViewModel: WorkshopLocationsViewModel = hiltViewModel()
) {
    val flowState by flowViewModel.uiState.collectAsState()
    val localState by localViewModel.uiState.collectAsState()
    var shouldNavigate by remember { mutableStateOf(false) }
    
    // Navegar después del éxito
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            kotlinx.coroutines.delay(100)
            onComplete()
            shouldNavigate = false
        }
    }
    
    // Show success dialog
    if (flowState.showSuccessDialog) {
        SuccessDialog(
            message = "¡Taller creado exitosamente!",
            onDismiss = {
                flowViewModel.dismissSuccessDialog()
                shouldNavigate = true
            }
        )
    }
    
    // Show error dialog
    if (flowState.showErrorDialog) {
        ErrorDialog(
            message = flowState.errorMessage ?: "Hubo un error durante el proceso.",
            onDismiss = flowViewModel::dismissErrorDialog
        )
    }
    
    // Show local success dialog (al agregar ubicación)
    if (localState.showSuccessDialog) {
        SuccessDialog(
            message = "Ubicación agregada.",
            onDismiss = localViewModel::dismissSuccessDialog
        )
    }
    
    // Show local error dialog
    if (localState.showErrorDialog) {
        ErrorDialog(
            message = localState.errorMessage ?: "Error al agregar ubicación.",
            onDismiss = localViewModel::dismissErrorDialog
        )
    }
    
    WorkshopLocationsContent(
        locations = flowState.locations,
        currentForm = localState.currentForm,
        isAdding = localState.isAdding,
        isSaving = flowState.isSaving,
        onStreetChange = localViewModel::updateStreet,
        onCityChange = localViewModel::updateCity,
        onStateChange = localViewModel::updateState,
        onZipChange = localViewModel::updateZip,
        onCountryChange = localViewModel::updateCountry,
        onLatitudeChange = localViewModel::updateLatitude,
        onLongitudeChange = localViewModel::updateLongitude,
        onAddLocation = {
            val form = localState.currentForm
            if (form.isValid) {
                val location = LocationInput(
                    street = form.street.trim(),
                    city = form.city.trim(),
                    state = form.state.trim(),
                    zip = form.zip.trim(),
                    country = form.country.trim(),
                    latitude = form.latitude.toDoubleOrNull(),
                    longitude = form.longitude.toDoubleOrNull()
                )
                flowViewModel.addLocation(location)
                // Limpiar formulario local
                localViewModel.updateStreet("")
                localViewModel.updateCity("")
                localViewModel.updateState("")
                localViewModel.updateZip("")
                localViewModel.updateCountry("")
                localViewModel.updateLatitude("")
                localViewModel.updateLongitude("")
            }
        },
        onRemoveLocation = { index -> flowViewModel.removeLocation(index) },
        onBackClick = onNavigateBack,
        onCompleteClick = { flowViewModel.saveAllWorkshopData() },
        onSkipClick = { flowViewModel.saveAllWorkshopData() }
    )
}

@Composable
private fun WorkshopLocationsContent(
    locations: List<LocationInput>,
    currentForm: LocationFormState,
    isAdding: Boolean,
    isSaving: Boolean,
    onStreetChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onZipChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onAddLocation: () -> Unit,
    onRemoveLocation: (Int) -> Unit,
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        WorkshopHeader(
            title = "Ubicaciones",
            onBackClick = onBackClick
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Agregar Nueva Ubicación",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4682B4)
            )
            
            // Street
            Text(
                text = "Calle",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            OutlinedTextField(
                value = currentForm.street,
                onValueChange = onStreetChange,
                placeholder = { Text("Ej: Av. Real 123", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            // City
            Text(
                text = "Ciudad",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            OutlinedTextField(
                value = currentForm.city,
                onValueChange = onCityChange,
                placeholder = { Text("Ej: Lima", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            // State
            Text(
                text = "Estado/Provincia",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            OutlinedTextField(
                value = currentForm.state,
                onValueChange = onStateChange,
                placeholder = { Text("Ej: Lima", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            // Zip
            Text(
                text = "Código Postal",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            OutlinedTextField(
                value = currentForm.zip,
                onValueChange = onZipChange,
                placeholder = { Text("Ej: 15001", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
            
            // Country
            Text(
                text = "País",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4682B4)
            )
            OutlinedTextField(
                value = currentForm.country,
                onValueChange = onCountryChange,
                placeholder = { Text("Ej: Perú", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4682B4),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            // Latitude and Longitude (Optional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Latitud (opcional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4682B4),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = currentForm.latitude,
                        onValueChange = onLatitudeChange,
                        placeholder = { Text("Ej: -12.0464", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4682B4),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        ),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Longitud (opcional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4682B4),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = currentForm.longitude,
                        onValueChange = onLongitudeChange,
                        placeholder = { Text("Ej: -77.0428", color = Color(0xFF9CA3AF), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4682B4),
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        ),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
                    )
                }
            }
            
            // Add Location Button
            Button(
                onClick = onAddLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4682B4),
                    disabledContainerColor = Color(0xFFCCCCCC)
                ),
                enabled = currentForm.isValid && !isAdding
            ) {
                if (isAdding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Agregar Ubicación",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            // Locations List
            if (locations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ubicaciones Agregadas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4682B4)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(locations) { index, location ->
                        LocationInputItem(
                            location = location,
                            onRemove = { onRemoveLocation(index) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Complete Button
            Button(
                onClick = onCompleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4682B4)
                )
            ) {
                Text(
                    text = "Finalizar",
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
private fun LocationInputItem(
    location: LocationInput,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.street,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A202C)
                )
                Text(
                    text = "${location.city}, ${location.state}, ${location.zip}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = location.country,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
private fun LocationItem(
    location: Location,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.street,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A202C)
                )
                Text(
                    text = "${location.city}, ${location.state}, ${location.zip}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = location.country,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

