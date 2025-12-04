package com.atg.autonexo.features.workshop.presentation.registration.tags

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atg.autonexo.core.components.RoundedHeader
import com.atg.autonexo.core.ui.theme.ButtonNavy
import com.atg.autonexo.core.ui.theme.CardBackground
import com.atg.autonexo.core.ui.theme.TextPrimary
import com.atg.autonexo.core.ui.theme.TextSecondary
import com.atg.autonexo.features.home.presentation.home.BottomNavigationBar
import com.atg.autonexo.features.workshop.domain.models.TagCategory
@Composable
fun TagsScreen(
    workshopId: Long,
    initialSelectedTagCodes: Set<String> = emptySet(),
    viewModel: TagsViewModel = hiltViewModel(),
    onNext: (workshopId: Long) -> Unit,
    onBack: () -> Unit = {},
    currentRoute: String,
    onNavigate: (route: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWorkshopTags()
    }

    Scaffold (bottomBar = {
        BottomNavigationBar(
            currentRoute = currentRoute,
            onNavigate = onNavigate
        )
    }){ innerPadding  ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        // HEADER
        RoundedHeader(
            title = "Tags del Workshop",
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LISTA DE TAGS
        if (uiState.isLoading && uiState.availableTags.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
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

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // BOTÓN CONTINUAR
            Button(
                onClick = {
                    viewModel.updateTags(workshopId) {
                        onNext(workshopId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonNavy,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isLoading) {
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
}

@Composable
fun TagsCategorySection(
    category: TagCategory,
    tags: List<com.atg.autonexo.features.workshop.domain.models.CapabilityTag>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


