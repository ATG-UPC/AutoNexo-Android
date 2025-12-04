package com.atg.autonexo.core.geocoding

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("results")
    val results: List<GeocodingResult>,
    @SerializedName("status")
    val status: String
)

data class GeocodingResult(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>,
    @SerializedName("geometry")
    val geometry: Geometry
)

data class AddressComponent(
    @SerializedName("long_name")
    val longName: String,
    @SerializedName("short_name")
    val shortName: String,
    @SerializedName("types")
    val types: List<String>
)

data class Geometry(
    @SerializedName("location")
    val location: GeocodingLocation
)

data class GeocodingLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class AddressInfo(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val zip: String?,
    val fullAddress: String
)

fun GeocodingResult.toAddressInfo(): AddressInfo {
    var street = ""
    var city = ""
    var state = ""
    var country = ""
    var zip: String? = null

    addressComponents.forEach { component ->
        when {
            component.types.contains("street_number") || component.types.contains("route") -> {
                street = if (street.isBlank()) component.longName else "$street ${component.longName}"
            }
            component.types.contains("locality") || component.types.contains("administrative_area_level_2") -> {
                if (city.isBlank()) city = component.longName
            }
            component.types.contains("administrative_area_level_1") -> {
                state = component.longName
            }
            component.types.contains("country") -> {
                country = component.longName
            }
            component.types.contains("postal_code") -> {
                zip = component.longName
            }
        }
    }

    // Si no encontramos calle, usar la primera parte de la dirección formateada
    if (street.isBlank() && formattedAddress.isNotBlank()) {
        street = formattedAddress.split(",").firstOrNull()?.trim() ?: ""
    }

    return AddressInfo(
        street = street.trim(),
        city = city.ifBlank { state },
        state = state,
        country = country,
        zip = zip,
        fullAddress = formattedAddress
    )
}

