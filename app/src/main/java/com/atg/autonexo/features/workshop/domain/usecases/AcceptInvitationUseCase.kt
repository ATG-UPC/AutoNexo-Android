package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.models.AcceptInvitationRequest
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class AcceptInvitationUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(request: AcceptInvitationRequest) = 
        repository.acceptInvitation(request)
}

