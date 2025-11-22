package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class GetInvitationsUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke() = repository.getInvitations()
    
    suspend fun getActiveInvitation(): Result<com.atg.autonexo.features.workshop.domain.models.Invitation?> {
        return repository.getInvitations().fold(
            onSuccess = { invitations ->
                Result.success(invitations.find { it.canBeUsed && !it.used && !it.expired })
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}

