package com.atg.autonexo.features.matchingbooking.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.data.remote.models.CreateOfferRequestDto
import com.atg.autonexo.features.matchingbooking.data.remote.models.OfferDto
import com.atg.autonexo.features.matchingbooking.data.remote.services.OfferService
import com.atg.autonexo.features.matchingbooking.domain.models.Offer
import com.atg.autonexo.features.matchingbooking.domain.repositories.OfferRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfferRepositoryImpl @Inject constructor(
    private val offerService: OfferService
) : OfferRepository {
    
    companion object {
        private const val TAG = "OfferRepository"
    }
    
    override suspend fun createOffer(
        serviceRequestId: String,
        proposedPriceAmount: Double,
        currency: String,
        proposedDate: String?,
        message: String?
    ): AuthResult<Offer> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating offer for request: $serviceRequestId")
            val serviceRequestIdLong = serviceRequestId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid service request ID format", null)
            
            val request = CreateOfferRequestDto(
                serviceRequestId = serviceRequestIdLong,
                proposedPriceAmount = proposedPriceAmount,
                currency = currency,
                proposedDate = proposedDate,
                message = message
            )
            
            val response = offerService.createOffer(request)
            
            if (response.isSuccessful) {
                val offerDto = response.body()
                if (offerDto != null) {
                    val offer = offerDto.toDomainModel()
                    return@withContext AuthResult.Success(offer)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    404 -> "Solicitud no encontrada"
                    409 -> "Ya tienes una oferta para esta solicitud"
                    else -> "Error al crear oferta"
                }
                Log.e(TAG, "Create offer failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create offer exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getMyWorkshopOffers(status: String?, page: Int?, size: Int?): AuthResult<List<Offer>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting workshop offers")
            val response = offerService.getMyWorkshopOffers(status, page, size)
            
            if (response.isSuccessful) {
                val offerDtos = response.body() ?: emptyList()
                val offers = offerDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(offers)
            } else {
                val errorMessage = "Error al obtener ofertas"
                Log.e(TAG, "Get workshop offers failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get workshop offers exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getOffersByServiceRequest(serviceRequestId: String): AuthResult<List<Offer>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting offers by service request: $serviceRequestId")
            val serviceRequestIdLong = serviceRequestId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid service request ID format", null)
            
            val response = offerService.getOffersByServiceRequest(serviceRequestIdLong)
            
            if (response.isSuccessful) {
                val offerDtos = response.body() ?: emptyList()
                val offers = offerDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(offers)
            } else {
                val errorMessage = "Error al obtener ofertas"
                Log.e(TAG, "Get offers by service request failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get offers by service request exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getOfferById(offerId: String): AuthResult<Offer> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting offer by id: $offerId")
            val offerIdLong = offerId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid offer ID format", null)
            
            val response = offerService.getOfferById(offerIdLong)
            
            if (response.isSuccessful) {
                val offerDto = response.body()
                if (offerDto != null) {
                    val offer = offerDto.toDomainModel()
                    return@withContext AuthResult.Success(offer)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Oferta no encontrada"
                    else -> "Error al obtener oferta"
                }
                Log.e(TAG, "Get offer failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get offer exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun withdrawOffer(offerId: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Withdrawing offer: $offerId")
            val offerIdLong = offerId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid offer ID format", null)
            
            val response = offerService.withdrawOffer(offerIdLong)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Oferta retirada"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al retirar oferta"
                Log.e(TAG, "Withdraw offer failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Withdraw offer exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    private fun OfferDto.toDomainModel(): Offer {
        return Offer(
            id = id?.toString() ?: "0",
            serviceRequestId = serviceRequestId?.toString() ?: "0",
            workshopId = workshopId?.toString() ?: "0",
            workshopName = "", // Backend doesn't provide workshop name in offer resource
            workshopRating = null, // Backend doesn't provide rating in offer resource
            estimatedPrice = proposedPriceAmount ?: 0.0,
            estimatedDuration = 0, // Backend doesn't provide duration in offer resource
            description = message ?: "",
            status = status ?: "PENDING",
            validUntil = expiresAt ?: "",
            createdAt = createdAt ?: "",
            updatedAt = null,
            currency = currency,
            proposedDate = proposedDate
        )
    }
}
