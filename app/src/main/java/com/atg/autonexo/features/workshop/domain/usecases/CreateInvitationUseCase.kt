package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.models.CreateInvitationRequest
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class CreateInvitationUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(request: CreateInvitationRequest) = 
        repository.createInvitation(request)
}

