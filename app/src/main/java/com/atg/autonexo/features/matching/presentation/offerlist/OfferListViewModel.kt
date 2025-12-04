package com.atg.autonexo.features.matching.presentation.offerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.matching.domain.usecases.GetMyOffersUseCase
import com.atg.autonexo.features.matching.domain.usecases.WithdrawOfferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
data class OfferListViewModel @Inject constructor(
    private val getMyOffersUseCase: GetMyOffersUseCase,
    private val withdrawOfferUseCase: WithdrawOfferUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfferListUiState())
    val uiState: StateFlow<OfferListUiState> = _uiState.asStateFlow()

    init {
        loadOffers()
    }
        fun loadOffers() {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = null
            )

            viewModelScope.launch {
                getMyOffersUseCase()
                    .onSuccess { offers ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            offers = offers.asReversed(),
                            errorMessage = null
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al obtener las ofertas"
                        )
                    }
            }
        }

}
