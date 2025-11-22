package com.atg.autonexo.features.workshop.presentation.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.models.CreateInvitationRequest
import com.atg.autonexo.features.workshop.domain.usecases.ActivateEmployeeUseCase
import com.atg.autonexo.features.workshop.domain.usecases.CreateInvitationUseCase
import com.atg.autonexo.features.workshop.domain.usecases.DeactivateEmployeeUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetInvitationsUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetWorkshopEmployeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkshopManagementViewModel @Inject constructor(
    private val createInvitationUseCase: CreateInvitationUseCase,
    private val getInvitationsUseCase: GetInvitationsUseCase,
    private val getWorkshopEmployeesUseCase: GetWorkshopEmployeesUseCase,
    private val deactivateEmployeeUseCase: DeactivateEmployeeUseCase,
    private val activateEmployeeUseCase: ActivateEmployeeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopManagementUiState())
    val uiState: StateFlow<WorkshopManagementUiState> = _uiState.asStateFlow()

    fun loadInvitationCode(workshopId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                workshopId = workshopId,
                errorMessage = null
            )

            getInvitationsUseCase.getActiveInvitation()
                .onSuccess { invitation ->
                    if (invitation != null) {
                        // Hay una invitación activa
                        _uiState.value = _uiState.value.copy(
                            invitationCode = invitation.invitationCode,
                            isLoading = false
                        )
                    } else {
                        // No hay invitación activa, intentar crear una nueva
                        _uiState.value = _uiState.value.copy(isLoading = true)
                        if (workshopId > 0) {
                            createNewInvitation(workshopId)
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "ID de taller inválido"
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    // Si falla obtener invitaciones, intentar crear una nueva directamente
                    android.util.Log.e("WorkshopManagementVM", "Error al obtener invitaciones: ${exception.message}")
                    if (workshopId > 0) {
                        createNewInvitation(workshopId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al cargar código de invitación"
                        )
                    }
                }
        }
    }

    private fun createNewInvitation(workshopId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val request = CreateInvitationRequest(
                email = null,
                message = "Código de invitación para el taller",
                validityDays = 365
            )
            
            createInvitationUseCase(request)
                .onSuccess { invitation ->
                    _uiState.value = _uiState.value.copy(
                        invitationCode = invitation.invitationCode,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    android.util.Log.e("WorkshopManagementVM", "Error al crear invitación: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al crear código de invitación. Verifica tu conexión e intenta nuevamente."
                    )
                }
        }
    }

    fun loadEmployees(workshopId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingEmployees = true,
                errorMessage = null
            )

            getWorkshopEmployeesUseCase(workshopId)
                .onSuccess { employees ->
                    val activeCount = employees.count { it.active }
                    _uiState.value = _uiState.value.copy(
                        employees = employees,
                        activeEmployeesCount = activeCount,
                        totalEmployeesCount = employees.size,
                        isLoadingEmployees = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingEmployees = false,
                        errorMessage = exception.message ?: "Error al cargar empleados"
                    )
                }
        }
    }

    fun refreshInvitationCode(workshopId: Long) {
        createNewInvitation(workshopId)
    }

    fun deactivateEmployee(workshopId: Long, employeeId: Long) {
        viewModelScope.launch {
            deactivateEmployeeUseCase(workshopId, employeeId)
                .onSuccess {
                    // Recargar lista de empleados
                    loadEmployees(workshopId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Error al desactivar empleado"
                    )
                }
        }
    }

    fun activateEmployee(workshopId: Long, employeeId: Long) {
        viewModelScope.launch {
            activateEmployeeUseCase(workshopId, employeeId)
                .onSuccess {
                    // Recargar lista de empleados
                    loadEmployees(workshopId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Error al activar empleado"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

