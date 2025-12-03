package com.atg.autonexo.features.matching.presentation.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.atg.autonexo.features.matching.domain.models.Request

val primaryLightHighContrast = Color(0xFF0B1821)
val secondaryLightHighContrast = Color(0xFF000000)
val tertiaryContainerDarkMediumContrast = Color(0xFF7793B2)

@Composable
fun RequestsScreen(
    viewModel: RequestsViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    currentRoute: String
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { RequestTopBar() },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            // Sección de cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Requests",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Text(
                    text = "${uiState.requests.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(8.dp))

            Divider(
                color = Color.White.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            Spacer(Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Error",
                        color = Color.Red
                    )
                }

                uiState.requests.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("No hay solicitudes")
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.requests) { request ->
                            RequestCard(request)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestTopBar() {
    TopAppBar(
        title = { Text("Requests") },
    )
}

@Composable
fun RequestCard(request: Request) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = tertiaryContainerDarkMediumContrast
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // TAGS (requested services)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                request.requestedServices.forEach { service ->
                    Box(
                        modifier = Modifier
                            .background(secondaryLightHighContrast, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = service,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // IZQUIERDA (avatar + id + rating)
                Column {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(primaryLightHighContrast)
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = "Request #${request.id}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Estrellas
                    Row {
                        repeat(5) {
                            Text("★", color = Color.Black)
                        }
                    }
                }

                // DERECHA (fecha + descripción)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        text = "Created: ${request.createdAt}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = request.description,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // BOTONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = {
                        // TODO: abrir detalles
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryLightHighContrast,
                        contentColor = Color.White
                    )
                ) {
                    Text("Details")
                }

                Button(
                    onClick = {
                        // TODO: offer acción
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondaryLightHighContrast,
                        contentColor = Color.White
                    )
                ) {
                    Text("Offer")
                }
            }
        }
    }
}
