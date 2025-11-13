package com.atg.autonexo.features.trust.data.remote.models

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("serviceBookingId")
    val serviceBookingId: Long?,
    
    @SerializedName("reviewerId")
    val reviewerId: Long?,
    
    @SerializedName("reviewerName")
    val reviewerName: String?,
    
    @SerializedName("revieweeId")
    val revieweeId: Long?,
    
    @SerializedName("revieweeName")
    val revieweeName: String?,
    
    @SerializedName("reviewType")
    val reviewType: String?,
    
    @SerializedName("rating")
    val rating: Int?,
    
    @SerializedName("comment")
    val comment: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?
)
