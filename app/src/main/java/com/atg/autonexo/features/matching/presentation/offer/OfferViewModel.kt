package com.atg.autonexo.features.matching.presentation.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.matching.domain.models.CreateOfferRequest
import com.atg.autonexo.features.matching.domain.models.Offer
import com.atg.autonexo.features.matching.domain.usecases.CreateOfferUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class OfferViewModel @Inject constructor(
    private val createOfferUseCase: CreateOfferUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfferUiState())
    val uiState: StateFlow<OfferUiState> = _uiState.asStateFlow()

    // ---------------------------
    // Updates (UI bindings)
    // ---------------------------
    fun setServiceRequestId(id: Long) {
        _uiState.update { it.copy(serviceRequestId = id, errorMessage = null, isSuccess = false) }
    }

    fun updateProposedPriceInput(text: String) {
        // permitir coma o punto y caracteres numéricos y espacios (se normaliza luego)
        _uiState.update { it.copy(proposedPriceInput = text, errorMessage = null, isSuccess = false) }
    }

    fun updateCurrency(currency: String) {
        _uiState.update { it.copy(currency = currency, errorMessage = null) }
    }

    fun updateProposedDate(date: LocalDateTime) {
        _uiState.update { it.copy(proposedDate = date, errorMessage = null) }
    }

    fun updateMessage(text: String) {
        _uiState.update { it.copy(message = text, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ---------------------------
    // Helpers
    // ---------------------------
    private val OfferUiState.proposedPrice: BigDecimal?
        get() {
            val raw = this.proposedPriceInput.trim()
            if (raw.isEmpty()) return null
            val normalized = raw.replace(",", ".").replace("\\s+".toRegex(), "")
            return try {
                BigDecimal(normalized)
            } catch (_: NumberFormatException) {
                null
            }
        }

    private fun validate(): Boolean {
        val state = _uiState.value

        // serviceRequestId
        if (state.serviceRequestId == null) {
            _uiState.update { it.copy(errorMessage = "Request inválido.") }
            return false
        }

        // price
        val price = state.proposedPrice
        if (price == null) {
            _uiState.update { it.copy(errorMessage = "Ingrese un precio válido.") }
            return false
        }
        if (price <= BigDecimal.ZERO) {
            _uiState.update { it.copy(errorMessage = "El precio debe ser mayor a 0.") }
            return false
        }

        // date
        if (state.proposedDate == null) {
            _uiState.update { it.copy(errorMessage = "Seleccione una fecha propuesta.") }
            return false
        }

        // message
        if (state.message.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingrese un mensaje para la oferta.") }
            return false
        }
        // opcional: longitud maxima del mensaje
        if (state.message.length > 1000) {
            _uiState.update { it.copy(errorMessage = "El mensaje no puede exceder 1000 caracteres.") }
            return false
        }

        return true
    }

    // ---------------------------
    // Crear oferta
    // ---------------------------
    /**
     * onSuccess recibe el id de la oferta creada (Long) si es posible extraerlo.
     * Si tu CreateOfferUseCase devuelve otro tipo, ajusta la extracción dentro de onSuccess.
     */
    fun createOffer(onSuccess: (Long) -> Unit) {
        if (!validate()) return

        val state = _uiState.value
        val price = state.proposedPrice!!     // seguro tras validate
        val request = CreateOfferRequest(
            serviceRequestId = state.serviceRequestId!!,
            proposedPriceAmount = price,
            currency = state.currency,
            proposedDate = state.proposedDate!!,
            message = state.message.trim()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            try {
                // Asumimos que el use case devuelve Result<Offer> o Result<Long> o Result<something>.
                // Usamos onSuccess/onFailure pattern como en tu código.
                createOfferUseCase(request)
                    .onSuccess { result ->
                        // Intentamos obtener un id si el resultado es Offer o Long
                        val createdId: Long? = when (result) {
                            is Offer -> result.id
                            is Long -> result
                            is Number -> result.toLong()
                            else -> null
                        }

                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                isSuccess = true,
                                errorMessage = null,
                                // opcional: dejar datos tal cual o limpiar inputs
                            )
                        }

                        createdId?.let { onSuccess(it) }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                isSuccess = false,
                                errorMessage = e.message ?: "Error al crear la oferta"
                            )
                        }
                    }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSuccess = false,
                        errorMessage = t.message ?: "Error inesperado"
                    )
                }
            }
        }
    }

    // ---------------------------
    // Util: limpiar formulario tras exito (opcional)
    // ---------------------------
    fun clearAfterSuccess() {
        _uiState.update {
            it.copy(
                proposedPriceInput = "",
                proposedDate = null,
                message = "",
                isSubmitting = false,
                isSuccess = false,
                errorMessage = null
            )
        }
    }
}
