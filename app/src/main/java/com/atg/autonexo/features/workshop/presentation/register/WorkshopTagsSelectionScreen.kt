package com.atg.autonexo.features.workshop.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.atg.autonexo.core.data.catalog.models.CapabilityTagDto
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.core.ui.components.ErrorDialog
import com.atg.autonexo.core.ui.components.SuccessDialog
import com.atg.autonexo.features.iam.presentation.components.AuthBackButton

@Composable
fun WorkshopTagsSelectionScreen(
    onNavigateBack: () -> Unit,
    onNext: () -> Unit,
    flowViewModel: WorkshopCreationFlowViewModel = hiltViewModel()
) {
    val flowState by flowViewModel.uiState.collectAsState()
    
    // Cargar tags al iniciar
    LaunchedEffect(Unit) {
        if (flowState.availableTags.isEmpty() && !flowState.isLoadingTags) {
            flowViewModel.loadCapabilityTags()
        }
    }
    
    // Show error dialog
    if (flowState.showErrorDialog) {
        ErrorDialog(
            message = flowState.errorMessage ?: "Hubo un error durante el proceso.",
            onDismiss = flowViewModel::dismissErrorDialog
        )
    }
    
    WorkshopTagsSelectionContent(
        availableTags = flowState.availableTags,
        selectedTags = flowState.selectedTags,
        isLoading = flowState.isLoadingTags,
        onTagClick = flowViewModel::toggleTagSelection,
        onNextClick = {
            flowViewModel.nextFromStep2()
            onNext()
        },
        onBackClick = onNavigateBack,
        onSkipClick = {
            flowViewModel.nextFromStep2()
            onNext()
        }
    )
}

@Composable
private fun WorkshopTagsSelectionContent(
    availableTags: List<CapabilityTagDto>,
    selectedTags: Set<String>,
    isLoading: Boolean,
    onTagClick: (String) -> Unit,
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
            title = "Capability Tags",
            onBackClick = onBackClick
        )
        
        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF4682B4))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona los tags que mejor describan las capacidades de tu taller",
                    fontSize = 14.sp,
                    color = Color(0xFF4A5568),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Tags list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableTags) { tag ->
                        TagChip(
                            tag = tag,
                            isSelected = selectedTags.contains(tag.code),
                            onClick = { tag.code?.let { onTagClick(it) } }
                        )
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
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Continuar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
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
}

@Composable
private fun TagChip(
    tag: CapabilityTagDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF4682B4) else Color(0xFFF3F4F6),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tag.displayName ?: tag.code ?: "Tag",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFF1A202C)
                )
                if (!tag.description.isNullOrBlank()) {
                    Text(
                        text = tag.description,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFFE5E7EB) else Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (!tag.categoryDisplayName.isNullOrBlank()) {
                    Text(
                        text = tag.categoryDisplayName,
                        fontSize = 11.sp,
                        color = if (isSelected) Color(0xFFD1D5DB) else Color(0xFF9CA3AF),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

