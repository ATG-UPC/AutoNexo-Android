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

    val showLabel = uiState.ogState.isBlank() &&
            uiState.ogCity.isBlank() &&
            uiState.ogStreet.isBlank() &&
            uiState.ogCountry.isBlank()
            && !uiState.isLoading

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

            if (uiState.selectedLocation == null &&
                uiState.ogLatitude == null &&
                uiState.ogLongitude == null
            ) {
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
            trailingIcon = {
                if (!showLabel) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar calle",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            label = {
                if (showLabel) {
                    Text("Calle *")
                } else {
                    Text("Editar Calle del Workshop")
                }
            },
            placeholder = {
                if (!showLabel && uiState.ogStreet.isNotBlank()) {
                    Text(uiState.ogStreet)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.city,
            onValueChange = viewModel::updateCity,
            trailingIcon = {
                if (!showLabel) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar ciudad",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            label = {
                if (showLabel) {
                    Text("Ciudad *")
                } else {
                    Text("Editar Ciudad")
                }
            },
            placeholder = {
                if (!showLabel && uiState.ogCity.isNotBlank()) {
                    Text(uiState.ogCity)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.state,
            onValueChange = viewModel::updateState,
            trailingIcon = {
                if (!showLabel) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar estado/provincia",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            label = {
                if (showLabel) {
                    Text("Estado/Provincia *")
                } else {
                    Text("Editar Estado/Provincia")
                }
            },
            placeholder = {
                if (!showLabel && uiState.ogState.isNotBlank()) {
                    Text(uiState.ogState)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.zip.orEmpty(),
            onValueChange = viewModel::updateZip,
            trailingIcon = {
                if (!showLabel && !uiState.ogZip.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar código postal",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            label = {
                if (showLabel) {
                    Text("Código Postal")
                } else {
                    Text("Editar Código Postal")
                }
            },
            placeholder = {
                if (!showLabel && !uiState.ogZip.isNullOrBlank()) {
                    Text(uiState.ogZip!!)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.country,
            onValueChange = viewModel::updateCountry,
            trailingIcon = {
                if (!showLabel) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar país",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            label = {
                if (showLabel) {
                    Text("País *")
                } else {
                    Text("Editar País")
                }
            },
            placeholder = {
                if (!showLabel && uiState.ogCountry.isNotBlank()) {
                    Text(uiState.ogCountry)
                }
            },
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

        val hasStreet  = uiState.street.isNotBlank()  || uiState.ogStreet.isNotBlank()
        val hasCity    = uiState.city.isNotBlank()    || uiState.ogCity.isNotBlank()
        val hasState   = uiState.state.isNotBlank()   || uiState.ogState.isNotBlank()
        val hasCountry = uiState.country.isNotBlank() || uiState.ogCountry.isNotBlank()
        val hasCoords  =
            uiState.selectedLocation != null ||
                    (uiState.ogLatitude != null && uiState.ogLongitude != null)

        Button(
            onClick = {
                viewModel.saveLocation(workshopId) { workshopName ->
                    onFinish(workshopName)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading &&
                    hasStreet &&
                    hasCity &&
                    hasState &&
                    hasCountry &&
                    hasCoords
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
