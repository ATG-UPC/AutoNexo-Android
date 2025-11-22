package com.atg.autonexo.features.workshop.presentation.registration.media

import android.net.Uri

data class MediaUiState(
    val logoUri: Uri? = null,
    val photoUris: List<Uri> = emptyList(),
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val errorMessage: String? = null,
    val logoUrl: String? = null,
    val photoUrls: List<String> = emptyList()
)

