package com.atg.autonexo.features.home.domain.usecases

import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.features.auth.domain.usecases.GetCurrentUserUseCase
import com.atg.autonexo.features.workshop.domain.models.Workshop
import com.atg.autonexo.features.workshop.domain.usecases.GetMyWorkshopUseCase
import javax.inject.Inject

data class HomeInfo(
    val userEmail: String?,
    val userName: String?,
    val workshop: Workshop?
)

class GetHomeInfoUseCase @Inject constructor(
    private val userPreferences: UserPreferences,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyWorkshopUseCase: GetMyWorkshopUseCase
) {
    suspend operator fun invoke(): Result<HomeInfo> {
        return try {
            val email = userPreferences.getUserEmail()
            
            // Obtener el nombre del usuario
            val userName = getCurrentUserUseCase().getOrNull()?.let { user ->
                "${user.firstName} ${user.lastName}".trim().takeIf { it.isNotBlank() }
            }
            
            // Intentar obtener el workshop, pero no fallar si no existe
            val workshop = getMyWorkshopUseCase().getOrNull()
            
            Result.success(HomeInfo(
                userEmail = email,
                userName = userName,
                workshop = workshop
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

