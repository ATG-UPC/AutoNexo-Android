package com.atg.autonexo.features.matching.data.remote.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class AcceptScheduleChangeRequestDto(
    @SerializedName("newScheduledDate")
    val newScheduledDate: LocalDateTime
)

