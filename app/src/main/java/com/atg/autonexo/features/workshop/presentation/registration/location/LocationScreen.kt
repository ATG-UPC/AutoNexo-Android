package com.atg.autonexo.features.workshop.presentation.registration.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.TextTertiary
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationScreen(
    workshopId: Long,
    viewModel: LocationViewModel = hiltViewModel(),
    onFinish: (workshopName: String) -> Unit,
    onBack: () -> Unit = {},
    currentRoute: String,
    onNavigate: (route: String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val defaultLocation = LatLng(-12.0464, -77.0428)
    val initialLocation = uiState.selectedLocation ?: defaultLocation

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    LaunchedEffect(uiState.selectedLocation) {
        uiState.selectedLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 15f)
            )
        }
    }

    LaunchedEffect(workshopId) {
        viewModel.loadWorkshopLocation(workshopId)
    }
    Scaffold (bottomBar = {
        BottomNavigationBar(
            currentRoute = currentRoute,
            onNavigate = onNavigate
        )
    }){ innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
    ) {

        RoundedHeader(
            title = "Ubicación",
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mapa más grande y prominente
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng -> viewModel.updateLocation(latLng) },
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

                // Indicador de carga de geocoding
                if (uiState.isGeocoding) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = ButtonNavy
                            )
                            Text(
                                text = "Obteniendo dirección...",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                        }
                    }
                } else if (uiState.selectedLocation == null &&
                    uiState.ogLatitude == null &&
                    uiState.ogLongitude == null
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Toca el mapa para seleccionar la ubicación",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos simplificados - solo esenciales
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Dirección (completada automáticamente)",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Calle (opcional, se completa automáticamente)
                OutlinedTextField(
                    value = uiState.street,
                    onValueChange = viewModel::updateStreet,
                    label = { Text("Calle") },
                    placeholder = { Text("Se completará automáticamente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isGeocoding,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // Ciudad (requerida)
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = viewModel::updateCity,
                    label = { Text("Ciudad *") },
                    placeholder = { Text("Ej: Lima") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isGeocoding,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // País (requerido)
                OutlinedTextField(
                    value = uiState.country,
                    onValueChange = viewModel::updateCountry,
                    label = { Text("País *") },
                    placeholder = { Text("Ej: Perú") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isGeocoding,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        uiState.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                viewModel.saveLocation(workshopId) { workshopName ->
                    onFinish(workshopName)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = !uiState.isLoading && 
                    !uiState.isGeocoding &&
                    (uiState.city.isNotBlank() || uiState.ogCity.isNotBlank()) &&
                    (uiState.country.isNotBlank() || uiState.ogCountry.isNotBlank()) &&
                    (uiState.selectedLocation != null ||
                            (uiState.ogLatitude != null && uiState.ogLongitude != null)),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonNavy,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text("Finalizar")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
    }
}

