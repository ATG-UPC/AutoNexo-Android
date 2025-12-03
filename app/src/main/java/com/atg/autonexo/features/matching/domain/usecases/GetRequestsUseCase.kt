package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.Request
import com.atg.autonexo.features.matching.domain.repositories.RequestRepository
import javax.inject.Inject

class GetRequestsUseCase @Inject constructor(
    private val repository: RequestRepository
) {
    suspend operator fun invoke() = repository.getRequests()
}