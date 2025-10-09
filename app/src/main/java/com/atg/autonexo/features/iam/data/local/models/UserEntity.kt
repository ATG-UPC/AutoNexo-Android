package com.atg.autonexo.features.iam.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.atg.autonexo.features.iam.data.local.database.StringListConverter

/**
 * Entidad Room para persistir datos del usuario localmente
 */
@Entity(tableName = "users")
@TypeConverters(StringListConverter::class)
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isVerified: Boolean,
    val active: Boolean,
    val roles: List<String>,
    val workshopId: Long?,
    val lastUpdated: Long = System.currentTimeMillis()
)
