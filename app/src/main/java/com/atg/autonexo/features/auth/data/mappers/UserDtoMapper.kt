package com.atg.autonexo.features.auth.data.mappers

import com.atg.autonexo.features.auth.data.remote.models.UserDto
import com.atg.autonexo.features.auth.domain.models.Role
import com.atg.autonexo.features.auth.domain.models.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun UserDto.toDomain(): User {
    val roles = this.roles.mapNotNull { roleString ->
        try {
            Role.valueOf(roleString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    val createdAt = this.createdAt?.let { parseDateTime(it) }
    val updatedAt = this.updatedAt?.let { parseDateTime(it) }
    
    return User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        isVerified = isVerified,
        active = active,
        roles = roles,
        workshopId = workshopId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

