package com.atg.autonexo.features.trust.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.trust.data.remote.models.*
import com.atg.autonexo.features.trust.data.remote.services.TrustService
import com.atg.autonexo.features.trust.domain.models.Review
import com.atg.autonexo.features.trust.domain.models.TrustScore
import com.atg.autonexo.features.trust.domain.repositories.TrustRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrustRepositoryImpl @Inject constructor(
    private val trustService: TrustService
) : TrustRepository {
    
    companion object {
        private const val TAG = "TrustRepository"
    }
    
    override suspend fun createReview(serviceBookingId: Long, rating: Int, comment: String?): AuthResult<Review> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating review for booking: $serviceBookingId")
            val request = CreateReviewRequestDto(serviceBookingId, rating, comment)
            val response = trustService.createReview(request)
            
            if (response.isSuccessful) {
                val reviewDto = response.body()
                if (reviewDto != null) {
                    val review = reviewDto.toDomainModel()
                    return@withContext AuthResult.Success(review)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    404 -> "Reserva no encontrada"
                    409 -> "Ya has dejado una reseña"
                    else -> "Error al crear reseña"
                }
                Log.e(TAG, "Create review failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create review exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getReviewsForBooking(serviceBookingId: Long): AuthResult<List<Review>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting reviews for booking: $serviceBookingId")
            val response = trustService.getReviewsForBooking(serviceBookingId)
            
            if (response.isSuccessful) {
                val reviewDtos = response.body() ?: emptyList()
                val reviews = reviewDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(reviews)
            } else {
                val errorMessage = "Error al obtener reseñas"
                Log.e(TAG, "Get reviews failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get reviews exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getReceivedReviewsForWorkshop(workshopId: Long, status: String?, page: Int?, size: Int?): AuthResult<List<Review>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting received reviews for workshop: $workshopId")
            val response = trustService.getReceivedReviewsForWorkshop(workshopId, status, page, size)
            
            if (response.isSuccessful) {
                val reviewDtos = response.body() ?: emptyList()
                val reviews = reviewDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(reviews)
            } else {
                val errorMessage = "Error al obtener reseñas"
                Log.e(TAG, "Get received reviews failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get received reviews exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getWorkshopTrustScore(workshopId: Long): AuthResult<TrustScore> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting trust score for workshop: $workshopId")
            val response = trustService.getWorkshopTrustScore(workshopId)
            
            if (response.isSuccessful) {
                val scoreDto = response.body()
                if (scoreDto != null) {
                    val trustScore = scoreDto.toDomainModel()
                    return@withContext AuthResult.Success(trustScore)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al obtener score"
                Log.e(TAG, "Get trust score failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get trust score exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getMyTrustScore(): AuthResult<TrustScore> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting my trust score")
            val response = trustService.getMyTrustScore()
            
            if (response.isSuccessful) {
                val scoreDto = response.body()
                if (scoreDto != null) {
                    val trustScore = scoreDto.toDomainModel()
                    return@withContext AuthResult.Success(trustScore)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al obtener score"
                Log.e(TAG, "Get my trust score failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get my trust score exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    private fun ReviewDto.toDomainModel(): Review {
        return Review(
            id = id ?: 0,
            serviceBookingId = serviceBookingId ?: 0,
            reviewerId = reviewerId ?: 0,
            reviewerName = reviewerName ?: "",
            revieweeId = revieweeId ?: 0,
            revieweeName = revieweeName ?: "",
            reviewType = reviewType ?: "",
            rating = rating ?: 0,
            comment = comment,
            status = status ?: "ACTIVE",
            createdAt = createdAt
        )
    }
    
    private fun TrustScoreDto.toDomainModel(): TrustScore {
        return TrustScore(
            score = score ?: 0.0,
            totalReviews = totalReviews ?: 0,
            averageRating = averageRating ?: 0.0
        )
    }
}
