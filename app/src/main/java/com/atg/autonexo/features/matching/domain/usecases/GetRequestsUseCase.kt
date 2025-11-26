package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.Request
import com.atg.autonexo.features.matching.domain.repositories.RequestRepository
import javax.inject.Inject

class GetRequestsUseCase @Inject constructor(
    private val repository: RequestRepository
) {
    suspend operator fun invoke() = repository.getServiceRequests()

    suspend fun getRequests(): Result<List<Request>>{
        return repository.getServiceRequests().fold(
            onSuccess = { requests ->
                Result.success(requests)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}