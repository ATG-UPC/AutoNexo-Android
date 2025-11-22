package com.atg.autonexo.features.workshop.data.mappers

import com.atg.autonexo.features.workshop.data.remote.models.CapabilityTagResponseDto
import com.atg.autonexo.features.workshop.domain.models.CapabilityTag
import com.atg.autonexo.features.workshop.domain.models.TagCategory

fun CapabilityTagResponseDto.toDomain(): CapabilityTag {
    val category = try {
        TagCategory.valueOf(category)
    } catch (e: IllegalArgumentException) {
        // Si la categoría no coincide, usar VEHICLE_TYPE como default
        TagCategory.VEHICLE_TYPE
    }
    
    return CapabilityTag(
        code = code,
        displayName = displayName,
        category = category
    )
}

fun List<CapabilityTagResponseDto>.toDomain(): List<CapabilityTag> {
    return this.map { it.toDomain() }
}

