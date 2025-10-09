package com.atg.autonexo.features.workshop.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.atg.autonexo.features.workshop.presentation.models.MechanicUi
import com.atg.autonexo.features.workshop.presentation.models.WorkshopUi

data class WorkshopDetailUiState(
    val workshop: WorkshopUi? = null,
    val isLoading: Boolean = false,
    val showCodeDialog: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class WorkshopDetailViewModel @Inject constructor(
    // TODO: Inyectar repositorios cuando estén listos
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopDetailUiState())
    val uiState: StateFlow<WorkshopDetailUiState> = _uiState.asStateFlow()

    init {
        // TODO: Cargar datos del workshop desde el repositorio
        // Por ahora, datos de ejemplo - Por defecto como owner
        loadMockData(isOwner = true)
    }
    
    fun loadWorkshopAsOwner() {
        loadMockData(isOwner = true)
    }
    
    fun loadWorkshopAsMember() {
        loadMockData(isOwner = false)
    }

    private fun loadMockData(isOwner: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simular carga de datos
            kotlinx.coroutines.delay(500)
            
            val mockWorkshop = WorkshopUi(
                id = "1",
                name = "Adonz Automotive",
                imageUrl = null,
                rating = 4.0f,
                address = "Av Arequipa 1234",
                district = "Surco",
                city = "Lima",
                description = "Somos Adonz Automotive, un taller automotriz especializado en mantenimiento, reparación y diagnóstico de vehículos. Nos caracteriza la calidad, confianza y compromiso para que tu auto siempre tenga el mejor rendimiento y seguridad.",
                services = listOf("Tire change", "Oil change", "Gas System", "Car wash"),
                mechanics = listOf(
                    MechanicUi("1", "Arturo Gonzales", "Workshop owner"),
                    MechanicUi("2", "Andre Carrillo", "Workshop member"),
                    MechanicUi("3", "Efrain Rakoton", "Workshop member"),
                    MechanicUi("4", "Henry Román", "Workshop member")
                ),
                isOwner = isOwner,
                workshopCode = "239-321-421"
            )
            
            _uiState.update { 
                it.copy(
                    workshop = mockWorkshop,
                    isLoading = false
                )
            }
        }
    }

    fun showCodeDialog() {
        _uiState.update { it.copy(showCodeDialog = true) }
    }

    fun dismissCodeDialog() {
        _uiState.update { it.copy(showCodeDialog = false) }
    }

    fun loadWorkshopByCode(code: String) {
        // Cuando se une con código, es un member (no owner)
        loadMockData(isOwner = false)
    }
    
    fun loadWorkshopData(workshopId: String, isOwner: Boolean) {
        // TODO: Implementar carga real desde repositorio
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Aquí iría la llamada al repositorio
                // val workshop = repository.getWorkshopById(workshopId)
                
                // Por ahora usamos mock data
                loadMockData(isOwner = isOwner)
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}
