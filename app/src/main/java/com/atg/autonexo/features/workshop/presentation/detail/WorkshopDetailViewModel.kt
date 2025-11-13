package com.atg.autonexo.features.workshop.presentation.detail

import android.util.Log
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
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import com.atg.autonexo.features.iam.domain.repositories.AuthRepository
import com.atg.autonexo.features.iam.domain.models.AuthResult

data class WorkshopDetailUiState(
    val workshop: WorkshopUi? = null,
    val isLoading: Boolean = false,
    val showCodeDialog: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class WorkshopDetailViewModel @Inject constructor(
    private val workshopRepository: WorkshopRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopDetailUiState())
    val uiState: StateFlow<WorkshopDetailUiState> = _uiState.asStateFlow()

    init {
        loadRealWorkshopData()
    }
    
    private fun loadRealWorkshopData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                // Obtener workshop del backend
                val workshopResult = workshopRepository.getMyWorkshop()
                
                if (workshopResult !is AuthResult.Success) {
                    val errorMsg = if (workshopResult is AuthResult.Error) {
                        workshopResult.message
                    } else {
                        "No se pudo cargar el workshop"
                    }
                    Log.e("WorkshopDetailVM", "Error al cargar workshop: $errorMsg")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                    return@launch
                }
                
                val workshop = workshopResult.data
                
                // Obtener usuario actual (owner)
                val currentUser = authRepository.getCurrentUser()
                
                // Mapear Workshop domain a WorkshopUi
                val workshopUi = WorkshopUi(
                    id = workshop.id.toString(),
                    name = workshop.name,
                    imageUrl = workshop.photoUrls.firstOrNull(), // Primera foto como imagen principal
                    logoUrl = workshop.logoUrl,
                    rating = 4.0f, // Hardcodeado
                    legalName = workshop.legalName,
                    ruc = workshop.ruc,
                    address = "Av Arequipa 1234", // Hardcodeado
                    district = "Surco", // Hardcodeado
                    city = "Lima", // Hardcodeado
                    description = workshop.description.takeIf { it.isNotBlank() } ?: "",
                    services = workshop.capabilityTags, // Tags como servicios
                    mechanics = if (currentUser != null) {
                        listOf(
                            MechanicUi(
                                id = currentUser.id.toString(),
                                name = currentUser.fullName,
                                role = "Workshop owner"
                            )
                        )
                    } else {
                        emptyList()
                    },
                    isOwner = true,
                    workshopCode = null // TODO: Obtener código del workshop si existe
                )
                
                _uiState.update { 
                    it.copy(
                        workshop = workshopUi,
                        isLoading = false
                    )
                }
                
            } catch (e: Exception) {
                Log.e("WorkshopDetailVM", "Exception loading workshop", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
    
    fun loadWorkshopAsOwner() {
        loadRealWorkshopData()
    }
    
    fun loadWorkshopAsMember() {
        loadRealWorkshopData()
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
                logoUrl = null,
                rating = 4.0f,
                legalName = null,
                ruc = null,
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
