package com.atg.autonexo.features.trust.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreateReviewRequestDto(
    @SerializedName("serviceBookingId")
    val serviceBookingId: Long,
    
    @SerializedName("rating")
    val rating: Int,
    
    @SerializedName("comment")
    val comment: String?
)

