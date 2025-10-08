package com.atg.autonexo.features.iam.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.presentation.profile.models.UserProfileUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // TODO: Inyectar repositorios cuando estén listos
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            // Mock data
            _uiState.update {
                it.copy(
                    userProfile = UserProfileUi(
                        id = "1",
                        fullName = "Arturo Gonzáles",
                        email = "mechanic@gmail.com",
                        phoneNumber = "973372718",
                        role = "Mechanic",
                        rating = 4.5,
                        currentWorkshop = "Autoking Workshop",
                        messageNotifications = true,
                        offersNotifications = true
                    )
                )
            }
        }
    }

    fun updateMessageNotifications(enabled: Boolean) {
        _uiState.update {
            it.copy(
                userProfile = it.userProfile?.copy(messageNotifications = enabled)
            )
        }
        // TODO: Guardar en repositorio
    }

    fun updateOffersNotifications(enabled: Boolean) {
        _uiState.update {
            it.copy(
                userProfile = it.userProfile?.copy(offersNotifications = enabled)
            )
        }
        // TODO: Guardar en repositorio
    }
}

