package com.atg.autonexo.features.workshop.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.workshop.data.remote.models.*
import com.atg.autonexo.features.workshop.data.remote.services.WorkshopService
import com.atg.autonexo.features.workshop.domain.models.*
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class WorkshopRepositoryImpl @Inject constructor(
    private val workshopService: WorkshopService
) : WorkshopRepository {
    
    companion object {
        private const val TAG = "WorkshopRepository"
    }
    
    // ========== Workshop Management ==========
    
    override suspend fun createWorkshop(
        ownerUserId: Long,
        name: String,
        shortDescription: String?,
        legalName: String?,
        ruc: String?
    ): AuthResult<Workshop> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating workshop: $name for owner: $ownerUserId")
            val request = CreateWorkshopRequestDto(
                ownerUserId = ownerUserId,
                name = name,
                shortDescription = shortDescription?.takeIf { it.isNotBlank() },
                legalName = legalName?.takeIf { it.isNotBlank() },
                ruc = ruc?.takeIf { it.isNotBlank() }
            )
            
            val response = workshopService.createWorkshop(request)
            
            if (response.isSuccessful) {
                val workshopDto = response.body()
                if (workshopDto != null) {
                    val workshop = workshopDto.toDomainModel()
                    return@withContext AuthResult.Success(workshop)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    409 -> "Ya tienes un taller registrado"
                    else -> "Error al crear taller"
                }
                Log.e(TAG, "Create workshop failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create workshop exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun getMyWorkshop(): AuthResult<Workshop> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting my workshop")
            val response = workshopService.getMyWorkshop()
            
            if (response.isSuccessful) {
                val workshopDto = response.body()
                if (workshopDto != null) {
                    val workshop = workshopDto.toDomainModel()
                    return@withContext AuthResult.Success(workshop)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "No tienes un taller registrado"
                    401 -> "Sesión expirada"
                    else -> "Error al obtener taller"
                }
                Log.e(TAG, "Get my workshop failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get my workshop exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun getWorkshopById(workshopId: Long): AuthResult<Workshop> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting workshop by id: $workshopId")
            val response = workshopService.getWorkshopById(workshopId)
            
            if (response.isSuccessful) {
                val workshopDto = response.body()
                if (workshopDto != null) {
                    val workshop = workshopDto.toDomainModel()
                    return@withContext AuthResult.Success(workshop)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Taller no encontrado"
                    else -> "Error al obtener taller"
                }
                Log.e(TAG, "Get workshop by id failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get workshop by id exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun updateWorkshop(
        name: String?,
        shortDescription: String?,
        description: String?,
        contactEmail: String?,
        contactPhone: String?
    ): AuthResult<Workshop> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating workshop")
            val request = UpdateWorkshopRequestDto(
                name = name,
                shortDescription = shortDescription,
                description = description,
                contactEmail = contactEmail,
                contactPhone = contactPhone
            )
            
            val response = workshopService.updateWorkshop(request)
            
            if (response.isSuccessful) {
                val workshopDto = response.body()
                if (workshopDto != null) {
                    val workshop = workshopDto.toDomainModel()
                    return@withContext AuthResult.Success(workshop)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    404 -> "Taller no encontrado"
                    else -> "Error al actualizar taller"
                }
                Log.e(TAG, "Update workshop failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update workshop exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Location Management ==========
    
    override suspend fun addLocation(
        street: String,
        city: String,
        state: String,
        zip: String,
        country: String,
        latitude: Double?,
        longitude: Double?
    ): AuthResult<Location> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Adding location: $street, $city, $state")
            val request = CreateLocationRequestDto(
                street = street,
                city = city,
                state = state,
                zip = zip,
                country = country,
                latitude = latitude,
                longitude = longitude
            )
            
            val response = workshopService.addLocation(request)
            
            if (response.isSuccessful) {
                val locationDto = response.body()
                if (locationDto != null) {
                    val location = locationDto.toDomainModel()
                    return@withContext AuthResult.Success(location)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    else -> "Error al agregar ubicación"
                }
                Log.e(TAG, "Add location failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Add location exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun getMyWorkshopLocations(): AuthResult<List<Location>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting workshop locations")
            val response = workshopService.getMyWorkshopLocations()
            
            if (response.isSuccessful) {
                val locationDtos = response.body() ?: emptyList()
                val locations = locationDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(locations)
            } else {
                val errorMessage = "Error al obtener ubicaciones"
                Log.e(TAG, "Get locations failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get locations exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun deleteLocation(locationId: Long): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Deleting location: $locationId")
            val response = workshopService.deleteLocation(locationId)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Ubicación eliminada"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Ubicación no encontrada"
                    else -> "Error al eliminar ubicación"
                }
                Log.e(TAG, "Delete location failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Delete location exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Service Templates ==========
    
    override suspend fun addServiceTemplate(
        serviceName: String,
        serviceCategory: String,
        basePrice: Double,
        estimatedDurationMinutes: Int,
        description: String?
    ): AuthResult<ServiceTemplate> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Adding service template")
            val request = CreateServiceTemplateRequestDto(
                serviceName = serviceName,
                serviceCategory = serviceCategory,
                basePrice = basePrice,
                estimatedDurationMinutes = estimatedDurationMinutes,
                description = description
            )
            
            val response = workshopService.addServiceTemplate(request)
            
            if (response.isSuccessful) {
                val serviceDto = response.body()
                if (serviceDto != null) {
                    val service = serviceDto.toDomainModel()
                    return@withContext AuthResult.Success(service)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    else -> "Error al agregar servicio"
                }
                Log.e(TAG, "Add service template failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Add service template exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Capability Tags ==========
    
    override suspend fun addCapabilityTag(tag: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Adding capability tag: $tag")
            val response = workshopService.addCapabilityTag(tag)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Tag agregado"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al agregar tag"
                Log.e(TAG, "Add capability tag failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Add capability tag exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun updateCapabilityTags(tags: List<String>): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating capability tags")
            val response = workshopService.updateCapabilityTags(tags)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Tags actualizados"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al actualizar tags"
                Log.e(TAG, "Update capability tags failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update capability tags exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Media Management ==========
    
    override suspend fun uploadLogo(imageFile: File): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Uploading logo")
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("logo", imageFile.name, requestBody)
            
            val response = workshopService.uploadLogo(part)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Logo subido exitosamente"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al subir logo"
                Log.e(TAG, "Upload logo failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload logo exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun addPhoto(imageFile: File): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Adding photo")
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("photo", imageFile.name, requestBody)
            
            val response = workshopService.addPhoto(part)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Foto agregada exitosamente"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al agregar foto"
                Log.e(TAG, "Add photo failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Add photo exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun deletePhoto(photoIndex: Int): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Deleting photo: $photoIndex")
            val response = workshopService.deletePhoto(photoIndex)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Foto eliminada"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al eliminar foto"
                Log.e(TAG, "Delete photo failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Delete photo exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Subscription ==========
    
    override suspend fun getSubscriptionStatus(): AuthResult<SubscriptionInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting subscription status")
            val response = workshopService.getSubscriptionStatus()
            
            if (response.isSuccessful) {
                val subscriptionDto = response.body()
                if (subscriptionDto != null) {
                    val subscription = subscriptionDto.toDomainModel()
                    return@withContext AuthResult.Success(subscription)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al obtener suscripción"
                Log.e(TAG, "Get subscription status failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get subscription status exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun updateSubscription(tier: String, autoRenew: Boolean?): AuthResult<SubscriptionInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating subscription")
            val request = UpdateSubscriptionRequestDto(tier = tier, autoRenew = autoRenew)
            
            val response = workshopService.updateSubscription(request)
            
            if (response.isSuccessful) {
                val subscriptionDto = response.body()
                if (subscriptionDto != null) {
                    val subscription = subscriptionDto.toDomainModel()
                    return@withContext AuthResult.Success(subscription)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al actualizar suscripción"
                Log.e(TAG, "Update subscription failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update subscription exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Invitations ==========
    
    override suspend fun createInvitation(invitedEmail: String): AuthResult<Invitation> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating invitation for: $invitedEmail")
            val request = CreateInvitationRequestDto(invitedEmail)
            
            val response = workshopService.createInvitation(request)
            
            if (response.isSuccessful) {
                val invitationDto = response.body()
                if (invitationDto != null) {
                    val invitation = invitationDto.toDomainModel()
                    return@withContext AuthResult.Success(invitation)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Email inválido"
                    409 -> "Ya existe una invitación pendiente"
                    else -> "Error al crear invitación"
                }
                Log.e(TAG, "Create invitation failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create invitation exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun getWorkshopInvitations(): AuthResult<List<Invitation>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting workshop invitations")
            val response = workshopService.getWorkshopInvitations()
            
            if (response.isSuccessful) {
                val invitationDtos = response.body() ?: emptyList()
                val invitations = invitationDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(invitations)
            } else {
                val errorMessage = "Error al obtener invitaciones"
                Log.e(TAG, "Get workshop invitations failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get workshop invitations exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun acceptInvitation(code: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Accepting invitation: $code")
            val response = workshopService.acceptInvitation(code)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Invitación aceptada"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Invitación no encontrada"
                    400 -> "Invitación expirada o inválida"
                    else -> "Error al aceptar invitación"
                }
                Log.e(TAG, "Accept invitation failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Accept invitation exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun getInvitationByCode(code: String): AuthResult<Invitation> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting invitation by code: $code")
            val response = workshopService.getInvitationByCode(code)
            
            if (response.isSuccessful) {
                val invitationDto = response.body()
                if (invitationDto != null) {
                    val invitation = invitationDto.toDomainModel()
                    return@withContext AuthResult.Success(invitation)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Invitación no encontrada"
                    else -> "Error al obtener invitación"
                }
                Log.e(TAG, "Get invitation by code failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get invitation by code exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Mapping Extensions ==========
    
    private fun WorkshopDto.toDomainModel(): Workshop {
        return Workshop(
            id = id ?: 0,
            name = name ?: "",
            description = shortDescription ?: description ?: "",
            legalName = legalName,
            ruc = ruc,
            contactEmail = contactEmail ?: "",
            contactPhone = contactPhone ?: "",
            logoUrl = logoUrl,
            photoUrls = photoUrls ?: emptyList(),
            locations = locations?.map { it.toDomainModel() } ?: emptyList(),
            serviceTemplates = serviceTemplates?.map { it.toDomainModel() } ?: emptyList(),
            capabilityTags = capabilityTags ?: emptyList(),
            subscriptionTier = subscriptionTier ?: "FREE",
            subscriptionStatus = subscriptionStatus ?: "ACTIVE",
            trustScore = trustScore ?: 0.0,
            ownerId = ownerId ?: 0,
            active = active ?: true,
            createdAt = createdAt
        )
    }
    
    private fun LocationDto.toDomainModel(): Location {
        return Location(
            id = id ?: 0,
            street = street ?: "",
            city = city ?: "",
            state = state ?: "",
            zip = zip ?: "",
            country = country ?: "",
            latitude = latitude,
            longitude = longitude,
            active = active ?: true
        )
    }
    
    private fun ServiceTemplateDto.toDomainModel(): ServiceTemplate {
        return ServiceTemplate(
            id = id ?: 0,
            serviceName = serviceName ?: "",
            serviceCategory = serviceCategory ?: "",
            basePrice = basePrice ?: 0.0,
            estimatedDurationMinutes = estimatedDurationMinutes ?: 0,
            description = description,
            available = available ?: true
        )
    }
    
    private fun InvitationDto.toDomainModel(): Invitation {
        return Invitation(
            id = id ?: 0,
            invitationCode = invitationCode ?: "",
            workshopId = workshopId ?: 0,
            workshopName = workshopName,
            invitedEmail = invitedEmail ?: "",
            status = status ?: "PENDING",
            expiresAt = expiresAt,
            createdAt = createdAt
        )
    }
    
    private fun SubscriptionDto.toDomainModel(): SubscriptionInfo {
        return SubscriptionInfo(
            tier = tier ?: "FREE",
            status = status ?: "ACTIVE",
            startDate = startDate,
            expiresAt = expiresAt,
            autoRenew = autoRenew ?: false
        )
    }
}
