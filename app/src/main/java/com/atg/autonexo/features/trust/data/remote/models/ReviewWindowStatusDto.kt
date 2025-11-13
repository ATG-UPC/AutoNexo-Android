package com.atg.autonexo.features.trust.data.remote.models

import com.google.gson.annotations.SerializedName

data class ReviewWindowStatusDto(
    @SerializedName("canReview")
    val canReview: Boolean?,
    
    @SerializedName("daysRemaining")
    val daysRemaining: Int?,
    
    @SerializedName("hasReviewed")
    val hasReviewed: Boolean?
)

