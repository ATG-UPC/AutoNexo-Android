package com.atg.autonexo.features.workshop.domain.usecases

import android.net.Uri
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class UploadLogoUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(workshopId: Long, imageUri: Uri) = 
        repository.uploadLogo(workshopId, imageUri)
}

