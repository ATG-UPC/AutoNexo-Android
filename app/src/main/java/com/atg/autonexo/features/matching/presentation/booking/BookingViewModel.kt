package com.atg.autonexo.features.matching.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.matching.domain.models.AcceptScheduleRequest
import com.atg.autonexo.features.matching.domain.models.Booking
import com.atg.autonexo.features.matching.domain.models.CompleteBookingRequest
import com.atg.autonexo.features.matching.domain.usecases.AcceptScheduleUseCase
import com.atg.autonexo.features.matching.domain.usecases.CompleteBookingUseCase
import com.atg.autonexo.features.matching.domain.usecases.GetMyBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getMyBookingsUseCase: GetMyBookingsUseCase,
    private val acceptScheduleUseCase: AcceptScheduleUseCase,
    private val completeBookingUseCase: CompleteBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadBooking()
    }

    fun loadBooking() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            getMyBookingsUseCase()
                .onSuccess { bookings ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookings = bookings.asReversed(),
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al obtener bookings"
                    )
                }
        }
    }

    /** Confirmar el horario propuesto para este booking */
    fun confirmSchedule(booking: Booking) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConfirmingSchedule = true)

            acceptScheduleUseCase(
                bookingId = booking.id,
                request = AcceptScheduleRequest(
                    scheduledDate = booking.scheduledDate
                )
            )
                .onSuccess {
                    loadBooking()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Error al confirmar el horario"
                    )
                }

            _uiState.value = _uiState.value.copy(isConfirmingSchedule = false)
        }
    }
    fun completeBooking(bookingId: Long, request: CompleteBookingRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCompletingBooking = true,
                errorMessage = null
            )

            completeBookingUseCase(bookingId, request)
                .onSuccess { updatedBooking ->
                    // Actualizar la lista en memoria si encontramos el booking, si no recargamos
                    val currentList = _uiState.value.bookings.toMutableList()
                    val index = currentList.indexOfFirst { it.id == updatedBooking.id }

                    if (index != -1) {
                        currentList[index] = updatedBooking
                        _uiState.value = _uiState.value.copy(
                            bookings = currentList,
                            isCompletingBooking = false
                        )
                    } else {
                        // fallback: recargar todo
                        loadBooking()
                        _uiState.value = _uiState.value.copy(
                            isCompletingBooking = false
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCompletingBooking = false,
                        errorMessage = exception.message ?: "Error al completar el servicio"
                    )
                }
        }
    }

}
