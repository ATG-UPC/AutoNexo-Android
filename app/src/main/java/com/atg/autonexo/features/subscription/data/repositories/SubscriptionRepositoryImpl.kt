package com.atg.autonexo.features.subscription.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.subscription.data.remote.models.*
import com.atg.autonexo.features.subscription.data.remote.services.PaymentService
import com.atg.autonexo.features.subscription.domain.models.Payment
import com.atg.autonexo.features.subscription.domain.repositories.SubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val paymentService: PaymentService
) : SubscriptionRepository {
    
    companion object {
        private const val TAG = "SubscriptionRepository"
    }
    
    override suspend fun createSubscriptionPayment(
        workshopId: Long,
        subscriptionTier: String,
        paymentMethod: String,
        paymentType: String,
        description: String
    ): AuthResult<Payment> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating subscription payment for tier: $subscriptionTier")
            val request = CreatePaymentRequestDto(
                workshopId = workshopId,
                subscriptionTier = subscriptionTier,
                paymentMethod = paymentMethod,
                paymentType = paymentType,
                description = description
            )
            
            // Log detallado del request
            Log.d(TAG, "=== REQUEST DETAILS ===")
            Log.d(TAG, "workshopId: $workshopId (type: ${workshopId::class.simpleName})")
            Log.d(TAG, "subscriptionTier: $subscriptionTier (type: String)")
            Log.d(TAG, "paymentMethod: $paymentMethod (type: String)")
            Log.d(TAG, "paymentType: $paymentType (type: String)")
            Log.d(TAG, "description: $description")
            
            // Convertir a JSON para ver qué se envía
            val gson = com.google.gson.Gson()
            val jsonString = gson.toJson(request)
            Log.d(TAG, "JSON to send: $jsonString")
            Log.d(TAG, "======================")
            
            val response = paymentService.createSubscriptionPayment(request)
            
            if (response.isSuccessful) {
                val paymentDto = response.body()
                if (paymentDto != null) {
                    val payment = paymentDto.toDomainModel()
                    return@withContext AuthResult.Success(payment)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Create payment failed: ${response.code()}")
                Log.e(TAG, "Error body: $errorBody")
                
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos: $errorBody"
                    409 -> "Ya tienes una suscripción activa"
                    else -> "Error al crear suscripción: ${response.message()}"
                }
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create payment exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getPaymentById(paymentId: Long): AuthResult<Payment> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting payment by id: $paymentId")
            val response = paymentService.getPaymentById(paymentId)
            
            if (response.isSuccessful) {
                val paymentDto = response.body()
                if (paymentDto != null) {
                    val payment = paymentDto.toDomainModel()
                    return@withContext AuthResult.Success(payment)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Pago no encontrado"
                    else -> "Error al obtener pago"
                }
                Log.e(TAG, "Get payment failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get payment exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getMyPayments(page: Int?, size: Int?): AuthResult<List<Payment>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting my payments")
            val response = paymentService.getMyPayments(page, size)
            
            if (response.isSuccessful) {
                val paymentDtos = response.body() ?: emptyList()
                val payments = paymentDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(payments)
            } else {
                val errorMessage = "Error al obtener pagos"
                Log.e(TAG, "Get payments failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get payments exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun completePayment(paymentId: Long): AuthResult<Payment> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Completing payment: $paymentId")
            val response = paymentService.completePayment(paymentId)
            
            if (response.isSuccessful) {
                val paymentDto = response.body()
                if (paymentDto != null) {
                    val payment = paymentDto.toDomainModel()
                    return@withContext AuthResult.Success(payment)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al completar pago"
                Log.e(TAG, "Complete payment failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Complete payment exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun cancelPayment(paymentId: Long): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Canceling payment: $paymentId")
            val response = paymentService.cancelPayment(paymentId)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Pago cancelado"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al cancelar pago"
                Log.e(TAG, "Cancel payment failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cancel payment exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    private fun PaymentDto.toDomainModel(): Payment {
        return Payment(
            id = id ?: 0,
            workshopId = workshopId ?: 0,
            amount = amount ?: 0.0,
            currency = currency ?: "USD",
            subscriptionTier = subscriptionTier ?: "FREE",
            status = status ?: "PENDING",
            paymentMethod = paymentMethod,
            createdAt = createdAt,
            completedAt = completedAt
        )
    }
}
