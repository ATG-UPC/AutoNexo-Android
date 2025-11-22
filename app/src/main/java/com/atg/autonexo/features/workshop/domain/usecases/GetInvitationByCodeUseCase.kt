package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class GetInvitationByCodeUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(code: String) = repository.getInvitationByCode(code)
}

