package com.atg.autonexo.features.workshop.presentation.registration.media

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.TextPrimary
import com.atg.autonexo.core.ui.theme.TextSecondary

@Composable
fun MediaScreen(
    workshopId: Long,
    viewModel: MediaViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val logoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { it?.let(viewModel::setLogo) }

    val photosPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val remaining = 10 - (uiState.photoUrls.size + uiState.photoUris.size)
        if (remaining > 0) uris.take(remaining).forEach(viewModel::addPhoto)
    }

    LaunchedEffect(workshopId) {
        viewModel.loadWorkshopMedia()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        RoundedHeader(
            title = "Logo y Fotos",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Logo (Opcional)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            val logoModel: Any? = uiState.logoUri ?: uiState.logoUrl
            val hasLogo = logoModel != null

            if (hasLogo) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = logoModel,
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(180.dp),
                        contentScale = ContentScale.Fit
                    )

                    if (uiState.logoUri != null) {
                        IconButton(
                            onClick = viewModel::removeLogo,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Close, "Eliminar logo")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = { logoPickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ButtonNavy
                )
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(if (hasLogo) "Cambiar Logo" else "Seleccionar Logo")
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Fotos (Máximo 10, Opcional)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            val totalPhotos = uiState.photoUrls.size + uiState.photoUris.size

            if (totalPhotos < 10) {
                OutlinedButton(
                    onClick = { photosPickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ButtonNavy
                    )
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar Fotos")
                }
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.photoUrls.isNotEmpty()) {
                Text(
                    text = "Fotos actuales",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )

                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 600.dp)
                ) {
                    itemsIndexed(uiState.photoUrls) { index, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Foto actual $index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            if (uiState.photoUris.isNotEmpty()) {
                Text(
                    text = "Nuevas fotos",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )

                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 600.dp)
                ) {
                    itemsIndexed(uiState.photoUris) { index, uri ->
                        Box {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Foto nueva $index",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removePhoto(index) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.uploadMedia(workshopId) { onNext(workshopId) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                enabled = !uiState.isUploading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonNavy,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Siguiente")
                }
            }
        }
    }
}
