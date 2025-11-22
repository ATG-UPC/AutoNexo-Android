package com.atg.autonexo.features.workshop.domain.models

data class CreateWorkshopRequest(
    val ownerUserId: Long,
    val name: String,
    val shortDescription: String? = null,
    val legalName: String? = null,
    val ruc: String? = null
)

