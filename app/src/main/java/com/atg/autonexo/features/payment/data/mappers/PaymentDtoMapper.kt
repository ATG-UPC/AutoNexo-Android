package com.atg.autonexo.features.payment.data.mappers

import com.atg.autonexo.features.payment.data.remote.models.PaymentResponseDto
import com.atg.autonexo.features.payment.data.remote.models.UpdatePaymentRequestDto
import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.payment.domain.models.PaymentStatus
import com.atg.autonexo.features.payment.domain.models.SubscriptionStatus
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun PaymentResponseDto.toDomain(): Payment {
    val expiresAtParsed = this.expiresAt?.let { parseDateTime(it) }
    val safeStatus = status ?: PaymentStatus.PENDING
    val safeTier = tier ?: SubscriptionTier.FREE
    return Payment(
        status = safeStatus,
        subscriptionTier = safeTier,
        expiresAt = expiresAtParsed ?: LocalDateTime.now(),
        isActive = isActive,
        canAccessPremiumFeatures = canAccessPremiumFeatures
    )
}

fun Payment.toUpdateRequestDto(): UpdatePaymentRequestDto {
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    return UpdatePaymentRequestDto(
        status = SubscriptionStatus.ACTIVE,
        tier = this.subscriptionTier,
        expiresAt = this.expiresAt.format(formatter)
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}