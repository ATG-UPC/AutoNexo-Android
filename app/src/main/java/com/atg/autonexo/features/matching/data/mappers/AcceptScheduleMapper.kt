package com.atg.autonexo.features.matching.data.mappers

import com.atg.autonexo.features.matching.data.remote.models.AcceptScheduleRequestDto
import com.atg.autonexo.features.matching.domain.models.AcceptScheduleRequest
import java.time.format.DateTimeFormatter

private val BACKEND_DATE_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

fun AcceptScheduleRequest.toDto(): AcceptScheduleRequestDto =
    AcceptScheduleRequestDto(
        scheduledDate = scheduledDate.format(BACKEND_DATE_TIME_FORMATTER)
    )