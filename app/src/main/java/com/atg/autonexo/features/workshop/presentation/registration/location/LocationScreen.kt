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

    val showLabel = uiState.ogState.isBlank() &&
            uiState.ogCity.isBlank() &&
            uiState.ogStreet.isBlank() &&
            uiState.ogCountry.isBlank() &&
            !uiState.isLoading

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

        Spacer(modifier = Modifier.height(24.dp))

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
                    .height(280.dp)
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
                        zoomControlsEnabled = false,
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

        Spacer(modifier = Modifier.height(24.dp))

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

                // Calle
                OutlinedTextField(
                    value = uiState.street,
                    onValueChange = viewModel::updateStreet,
                    trailingIcon = {
                        if (!showLabel) Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                    },
                    label = {
                        if (showLabel) Text("Calle *")
                        else Text("Editar Calle del Workshop")
                    },
                    placeholder = {
                        if (!showLabel && uiState.ogStreet.isNotBlank()) Text(uiState.ogStreet)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // Ciudad
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = viewModel::updateCity,
                    trailingIcon = {
                        if (!showLabel) Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                    },
                    label = {
                        if (showLabel) Text("Ciudad *")
                        else Text("Editar Ciudad")
                    },
                    placeholder = {
                        if (!showLabel && uiState.ogCity.isNotBlank()) Text(uiState.ogCity)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // Estado
                OutlinedTextField(
                    value = uiState.state,
                    onValueChange = viewModel::updateState,
                    trailingIcon = {
                        if (!showLabel) Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                    },
                    label = {
                        if (showLabel) Text("Estado/Provincia *")
                        else Text("Editar Estado/Provincia")
                    },
                    placeholder = {
                        if (!showLabel && uiState.ogState.isNotBlank()) Text(uiState.ogState)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // ZIP
                OutlinedTextField(
                    value = uiState.zip.orEmpty(),
                    onValueChange = viewModel::updateZip,
                    trailingIcon = {
                        if (!showLabel && !uiState.ogZip.isNullOrBlank())
                            Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                    },
                    label = {
                        if (showLabel) Text("Código Postal")
                        else Text("Editar Código Postal")
                    },
                    placeholder = {
                        if (!showLabel && !uiState.ogZip.isNullOrBlank()) Text(uiState.ogZip!!)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonNavy,
                        focusedLabelColor = ButtonNavy,
                        cursorColor = ButtonNavy
                    )
                )

                // País
                OutlinedTextField(
                    value = uiState.country,
                    onValueChange = viewModel::updateCountry,
                    trailingIcon = {
                        if (!showLabel) Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                    },
                    label = {
                        if (showLabel) Text("País *")
                        else Text("Editar País")
                    },
                    placeholder = {
                        if (!showLabel && uiState.ogCountry.isNotBlank()) Text(uiState.ogCountry)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                    (uiState.street.isNotBlank() || uiState.ogStreet.isNotBlank()) &&
                    (uiState.city.isNotBlank() || uiState.ogCity.isNotBlank()) &&
                    (uiState.state.isNotBlank() || uiState.ogState.isNotBlank()) &&
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

