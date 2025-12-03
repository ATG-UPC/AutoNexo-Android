package com.atg.autonexo.features.workshop.presentation.registration.media

import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

@Composable
fun MediaScreen(
    workshopId: Long,
    viewModel: MediaViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()


    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setLogo(it) }
    }


    val photosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val remainingSlots = 10 - (uiState.photoUrls.size + uiState.photoUris.size)
        if (remainingSlots > 0) {
            uris.take(remainingSlots).forEach { uri ->
                viewModel.addPhoto(uri)
            }
        }
    }

    LaunchedEffect(workshopId) {
        viewModel.loadWorkshopMedia()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Logo y Fotos",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Logo (Opcional)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val logoModel: Any? = uiState.logoUri ?: uiState.logoUrl
        val hasLogo = logoModel != null

        if (hasLogo) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = logoModel,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )

                // Solo puedes "borrar" el logo si es uno nuevo (Uri local)
                if (uiState.logoUri != null) {
                    IconButton(
                        onClick = { viewModel.removeLogo() },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, "Eliminar logo")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { logoPickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (hasLogo) "Cambiar Logo" else "Seleccionar Logo")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Fotos (Máximo 10, Opcional)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val totalPhotos = uiState.photoUrls.size + uiState.photoUris.size

        if (totalPhotos < 10) {
            OutlinedButton(
                onClick = { photosPickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Fotos")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.photoUrls.isNotEmpty()) {
            Text(
                text = "Fotos actuales",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val rowsExisting = (uiState.photoUrls.size + 1) / 2
            val gridHeightExisting = (rowsExisting * 150).dp

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(gridHeightExisting)
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

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.photoUris.isNotEmpty()) {
            Text(
                text = "Nuevas fotos",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val rowsNew = (uiState.photoUris.size + 1) / 2
            val gridHeightNew = (rowsNew * 150).dp

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(gridHeightNew)
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
                            Icon(Icons.Default.Close, "Eliminar foto")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        uiState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { viewModel.uploadMedia(workshopId) { onNext(workshopId) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isUploading
        ) {
            if (uiState.isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Siguiente")
            }
        }
    }
}
