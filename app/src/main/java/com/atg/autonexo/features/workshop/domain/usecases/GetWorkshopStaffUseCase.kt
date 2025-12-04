package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class GetWorkshopStaffUseCase @Inject constructor(
    private val repository: WorkshopRepository
){
    suspend operator fun invoke() = repository.getWorkshopStaff()

}