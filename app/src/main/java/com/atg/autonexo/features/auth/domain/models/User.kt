package com.atg.autonexo.features.auth.domain.models

import java.time.LocalDateTime

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isVerified: Boolean,
    val active: Boolean,
    val roles: List<Role>,
    val workshopId: Long?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

