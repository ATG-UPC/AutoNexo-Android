package com.atg.autonexo.features.workshop.presentation.registration.tags

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.features.workshop.domain.models.TagCategory

@Composable
fun TagsScreen(
    workshopId: Long,
    viewModel: TagsViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Selecciona los Tags",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (uiState.isLoading && uiState.availableTags.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.tagsGroupedByCategory.forEach { (category, tags) ->
                    item {
                        TagsCategorySection(
                            category = category,
                            tags = tags,
                            selectedTags = uiState.selectedTags,
                            onTagToggle = viewModel::toggleTag
                        )
                    }
                }
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.updateTags(workshopId) { onNext(workshopId) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
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
}

@Composable
fun TagsCategorySection(
    category: TagCategory,
    tags: List<com.atg.autonexo.features.workshop.domain.models.CapabilityTag>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            tags.forEach { tag ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedTags.contains(tag.code),
                        onCheckedChange = { onTagToggle(tag.code) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tag.displayName,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

