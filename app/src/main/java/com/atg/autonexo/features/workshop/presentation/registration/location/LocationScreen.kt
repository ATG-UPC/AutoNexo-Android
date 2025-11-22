package com.atg.autonexo.features.workshop.presentation.registration.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationScreen(
    workshopId: Long,
    viewModel: LocationViewModel = hiltViewModel(),
    onFinish: (workshopName: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Ubicación por defecto (Lima, Perú)
    val defaultLocation = LatLng(-12.0464, -77.0428)
    val initialLocation = uiState.selectedLocation ?: defaultLocation
    
    // Inicializar la ubicación por defecto si no hay una seleccionada
    LaunchedEffect(Unit) {
        if (uiState.selectedLocation == null) {
            viewModel.updateLocation(defaultLocation)
        }
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }
    
    // Actualizar la cámara cuando cambia la ubicación seleccionada
    LaunchedEffect(uiState.selectedLocation) {
        uiState.selectedLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 15f)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Ubicación del Workshop",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Google Map con manejo de errores
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.updateLocation(latLng)
                },
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = false
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            ) {
                uiState.selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "Ubicación del Workshop"
                    )
                }
            }
            
            // Mostrar mensaje si no hay ubicación seleccionada
            if (uiState.selectedLocation == null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = "Toca el mapa para seleccionar la ubicación",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.street,
            onValueChange = viewModel::updateStreet,
            label = { Text("Calle *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.city,
            onValueChange = viewModel::updateCity,
            label = { Text("Ciudad *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.state,
            onValueChange = viewModel::updateState,
            label = { Text("Estado/Provincia *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.zip,
            onValueChange = viewModel::updateZip,
            label = { Text("Código Postal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.country,
            onValueChange = viewModel::updateCountry,
            label = { Text("País *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        uiState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { 
                viewModel.addLocation(workshopId) { workshopName ->
                    onFinish(workshopName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading &&
                     uiState.street.isNotBlank() &&
                     uiState.city.isNotBlank() &&
                     uiState.state.isNotBlank() &&
                     uiState.country.isNotBlank() &&
                     uiState.selectedLocation != null
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Finalizar")
            }
        }
    }
}

