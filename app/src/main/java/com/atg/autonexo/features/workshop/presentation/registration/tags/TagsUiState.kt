package com.atg.autonexo.features.workshop.presentation.registration.tags

import com.atg.autonexo.features.workshop.domain.models.CapabilityTag
import com.atg.autonexo.features.workshop.domain.models.TagCategory

data class TagsUiState(
    val availableTags: List<CapabilityTag> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val tagsGroupedByCategory: Map<TagCategory, List<CapabilityTag>> = emptyMap(),
    val originalSelectedTags: Set<String> = emptySet(),
)

