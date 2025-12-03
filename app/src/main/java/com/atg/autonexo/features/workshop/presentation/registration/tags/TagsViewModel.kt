package com.atg.autonexo.features.workshop.presentation.registration.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.models.TagCategory
import com.atg.autonexo.features.workshop.domain.usecases.GetCapabilityTagsUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetMyWorkshopUseCase
import com.atg.autonexo.features.workshop.domain.usecases.UpdateWorkshopTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val getCapabilityTagsUseCase: GetCapabilityTagsUseCase,
    private val updateWorkshopTagsUseCase: UpdateWorkshopTagsUseCase,
    private val getMyWorkshopUseCase: GetMyWorkshopUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TagsUiState())
    val uiState: StateFlow<TagsUiState> = _uiState.asStateFlow()

    init {
        loadTags()
    }


    fun setInitialWorkshopTags(codes: Collection<String>) {
        val set = codes.toSet()
        _uiState.value = _uiState.value.copy(
            selectedTags = set,
            originalSelectedTags = set,
            errorMessage = null
        )
    }

    private fun loadTags() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            getCapabilityTagsUseCase()
                .onSuccess { tags ->
                    val groupedTags = tags.groupBy { it.category }
                    _uiState.value = _uiState.value.copy(
                        availableTags = tags,
                        tagsGroupedByCategory = groupedTags,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar tags"
                    )
                }
        }
    }
    fun loadWorkshopTags() {
        viewModelScope.launch {
            getMyWorkshopUseCase()
                .onSuccess { workshop ->
                    val codes: List<String> = workshop.capabilityTags
                    setInitialWorkshopTags(codes)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al cargar tags del taller"
                    )
                }
        }
    }


    fun toggleTag(tagCode: String) {
        val currentSelected = _uiState.value.selectedTags.toMutableSet()
        if (currentSelected.contains(tagCode)) {
            currentSelected.remove(tagCode)
        } else {
            currentSelected.add(tagCode)
        }
        _uiState.value = _uiState.value.copy(selectedTags = currentSelected, errorMessage = null)
    }



    fun updateTags(workshopId: Long, onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val currentSelection = currentState.selectedTags
        val originalSelection = currentState.originalSelectedTags

        if (currentSelection == originalSelection) {
            onSuccess()
            return
        }

        val tagsList = currentSelection.toList()

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            updateWorkshopTagsUseCase(workshopId, tagsList)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        originalSelectedTags = currentSelection  // nueva "verdad" guardada
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al actualizar tags"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

