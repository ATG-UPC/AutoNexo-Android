package com.atg.autonexo.features.profile.data.repositories

import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import com.atg.autonexo.features.profile.domain.models.Profile
import com.atg.autonexo.features.profile.domain.repositories.ProfileRepository
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val workshopRepository: WorkshopRepository
) : ProfileRepository {

    override suspend fun getProfile(): Result<Profile> {
        return try {
            // Obtener el usuario actual
            val userResult = authRepository.getCurrentUser()
            
            if (userResult.isFailure) {
                return Result.failure(
                    userResult.exceptionOrNull() ?: Exception("Error al obtener usuario")
                )
            }
            
            val user = userResult.getOrNull()!!
            
            // Intentar obtener el workshop asociado (si existe)
            // Si falla, simplemente retornamos null (el usuario puede no tener workshop aún)
            val workshop = if (user.workshopId != null) {
                try {
                    workshopRepository.getMyWorkshop().getOrNull()
                } catch (e: Exception) {
                    // Si falla obtener el workshop, simplemente no lo incluimos
                    // No es un error crítico, el usuario puede no tener workshop aún
                    null
                }
            } else {
                null
            }
            
            Result.success(Profile(user = user, workshop = workshop))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

