package com.atg.autonexo.features.profile.domain.models

import com.atg.autonexo.features.auth.domain.models.User
import com.atg.autonexo.features.workshop.domain.models.Workshop

data class Profile(
    val user: User,
    val workshop: Workshop? = null
)

