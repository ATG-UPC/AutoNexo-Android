package com.atg.autonexo.features.trust.data.remote.models

import com.google.gson.annotations.SerializedName

data class TrustScoreDto(
    @SerializedName("score")
    val score: Double?,
    
    @SerializedName("totalReviews")
    val totalReviews: Int?,
    
    @SerializedName("averageRating")
    val averageRating: Double?
)

