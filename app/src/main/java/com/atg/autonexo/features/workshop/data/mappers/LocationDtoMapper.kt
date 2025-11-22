package com.atg.autonexo.features.workshop.data.mappers

import com.atg.autonexo.features.workshop.data.remote.models.AddLocationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.LocationResponseDto
import com.atg.autonexo.features.workshop.domain.models.Location

fun LocationResponseDto.toDomain(): Location {
    return Location(
        id = id,
        name = name,
        street = street,
        city = city,
        state = state,
        zip = zip,
        country = country,
        latitude = latitude,
        longitude = longitude,
        isPrimary = isPrimary,
        active = active
    )
}

fun List<LocationResponseDto>.toDomain(): List<Location> {
    return this.map { it.toDomain() }
}

fun com.atg.autonexo.features.workshop.domain.models.Location.toDto(): AddLocationRequestDto {
    return AddLocationRequestDto(
        street = street,
        city = city,
        state = state,
        zip = zip,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
}

