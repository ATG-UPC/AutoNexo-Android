package com.atg.autonexo.features.workshop.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.ErrorDialog
import com.atg.autonexo.core.ui.components.SuccessDialog
import com.atg.autonexo.features.iam.presentation.components.AuthBackButton
import com.atg.autonexo.features.iam.presentation.components.DashedImageBox

@Composable
fun WorkshopMediaUploadScreen(
    onNavigateBack: () -> Unit,
    onNext: () -> Unit,
    onSelectLogo: () -> Unit,
    onSelectPhotos: () -> Unit,
    flowViewModel: WorkshopCreationFlowViewModel = hiltViewModel()
) {
    val flowState by flowViewModel.uiState.collectAsState()
    
    // Show error dialog
    if (flowState.showErrorDialog) {
        ErrorDialog(
            message = flowState.errorMessage ?: "Hubo un error durante el proceso.",
            onDismiss = flowViewModel::dismissErrorDialog
        )
    }
    
    WorkshopMediaUploadContent(
        logoUri = flowState.logoUri,
        photoUris = flowState.photoUris,
        onLogoClick = onSelectLogo,
        onPhotosClick = onSelectPhotos,
        onRemovePhoto = flowViewModel::removePhotoUri,
        onNextClick = {
            flowViewModel.nextFromStep3()
            onNext()
        },
        onBackClick = onNavigateBack,
        onSkipClick = {
            flowViewModel.nextFromStep3()
            onNext()
        }
    )
}

@Composable
private fun WorkshopMediaUploadContent(
    logoUri: String?,
    photoUris: List<String>,
    onLogoClick: () -> Unit,
    onPhotosClick: () -> Unit,
    onRemovePhoto: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        WorkshopHeader(
            title = "Media",
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
            // Logo Section
            Text(
                text = "Logo del Taller",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4682B4)
            )
            
            DashedImageBox(
                label = "Logo",
                imageUri = logoUri,
                onClick = onLogoClick,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Photos Section
            Text(
                text = "Fotos del Taller",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4682B4)
            )
            
            // Add Photo Button
            DashedImageBox(
                label = "Agregar Foto",
                imageUri = null,
                onClick = onPhotosClick,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Photos List
            if (photoUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(photoUris) { photoUri ->
                        PhotoPreviewItem(
                            photoUri = photoUri,
                            onRemove = { onRemovePhoto(photoUri) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                )
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Skip Button
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Omitir",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PhotoPreviewItem(
    photoUri: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp)
    ) {
        // Placeholder for image preview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Foto\nseleccionada",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

