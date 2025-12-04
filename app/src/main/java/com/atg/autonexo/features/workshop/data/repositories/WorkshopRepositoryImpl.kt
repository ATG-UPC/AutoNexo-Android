package com.atg.autonexo.features.workshop.data.repositories

import android.content.Context
import android.net.Uri
import com.atg.autonexo.features.auth.data.remote.models.ErrorResponseDto
import com.atg.autonexo.features.workshop.data.mappers.toDomain
import com.atg.autonexo.features.workshop.data.remote.models.AcceptInvitationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.AddLocationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.CreateInvitationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.CreateWorkshopRequestDto
import com.atg.autonexo.features.workshop.data.remote.services.WorkshopApiService
import com.atg.autonexo.features.workshop.domain.models.AcceptInvitationRequest
import com.atg.autonexo.features.workshop.domain.models.CreateInvitationRequest
import com.atg.autonexo.features.workshop.domain.models.CreateWorkshopRequest
import com.atg.autonexo.features.workshop.domain.models.Invitation
import com.atg.autonexo.features.workshop.domain.models.Location
import com.atg.autonexo.features.workshop.domain.models.Workshop
import com.atg.autonexo.features.workshop.domain.models.WorkshopEmployee
import com.atg.autonexo.features.workshop.domain.models.WorkshopStaff
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class WorkshopRepositoryImpl @Inject constructor(
    private val apiService: WorkshopApiService,
    private val gson: Gson,
    private val context: Context
) : WorkshopRepository {

    override suspend fun createWorkshop(request: CreateWorkshopRequest): Result<Workshop> {
        return try {
            val dto = CreateWorkshopRequestDto(
                ownerUserId = request.ownerUserId,
                name = request.name,
                shortDescription = request.shortDescription,
                legalName = request.legalName,
                ruc = request.ruc
            )

            val response = apiService.createWorkshop(dto)

            if (response.isSuccessful && response.body() != null) {
                val workshop = response.body()!!.toDomain()
                Result.success(workshop)
            } else {
                Result.failure(Exception(parseError(response, "Error al crear workshop")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al crear workshop")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWorkshopById(workshopId: Long): Result<Workshop> {
        return try {
            val response = apiService.getWorkshopById(workshopId)

            if (response.isSuccessful && response.body() != null) {
                val workshop = response.body()!!.toDomain()
                Result.success(workshop)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener workshop")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener workshop")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyWorkshop(): Result<Workshop> {
        return try {
            val response = apiService.getMyWorkshop()

            if (response.isSuccessful && response.body() != null) {
                val workshop = response.body()!!.toDomain()
                Result.success(workshop)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener tu workshop")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener tu workshop")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWorkshop(workshop: Workshop): Result<Workshop> {
        return try {
            val dto = com.atg.autonexo.features.workshop.data.remote.models.UpdateWorkshopRequestDto(
                name = workshop.name,
                shortDescription = workshop.shortDescription,
                legalName = workshop.legalName,
                ruc = workshop.ruc
            )

            val response = apiService.updateWorkshop(dto)

            if (response.isSuccessful && response.body() != null) {
                val updatedWorkshop = response.body()!!.toDomain()
                Result.success(updatedWorkshop)
            } else {
                Result.failure(Exception(parseError(response, "Error al actualizar workshop")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al actualizar workshop")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWorkshopLocations(workshopId: Long): Result<List<Location>> {
        return try {
            val response = apiService.getWorkshopLocations()

            if (response.isSuccessful && response.body() != null) {
                val locations = response.body()!!.map { it.toDomain() }
                Result.success(locations)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener ubicaciones")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener ubicaciones")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== TAGS ==========
    override suspend fun updateTags(workshopId: Long, tags: List<String>): Result<Workshop> {
        return try {
            val response = apiService.updateTags(tags)

            android.util.Log.d("WorkshopRepository", "updateTags - Código HTTP: ${response.code()}")
            android.util.Log.d("WorkshopRepository", "updateTags - Es exitoso: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val workshopDto = response.body()
                if (workshopDto != null) {
                    android.util.Log.d("WorkshopRepository", "updateTags - Workshop recibido: id=${workshopDto.id}")
                    val workshop = workshopDto.toDomain()
                    Result.success(workshop)
                } else {
                    // El backend puede devolver un string exitoso en lugar de un objeto
                    // En ese caso, obtenemos el workshop actualizado desde el backend
                    android.util.Log.w("WorkshopRepository", "updateTags - Body es null, obteniendo workshop actualizado")
                    // Intentar obtener el workshop actualizado
                    getMyWorkshop()
                }
            } else {
                // Leer el errorBody para obtener más información
                val errorBodyString = try {
                    response.errorBody()?.string() ?: ""
                } catch (ex: Exception) {
                    ""
                }

                android.util.Log.e("WorkshopRepository", "updateTags - Error HTTP ${response.code()}: $errorBodyString")
                Result.failure(Exception(parseError(response, "Error al actualizar tags")))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            // Error específico de parsing JSON - el backend devolvió un string en lugar de objeto
            android.util.Log.e("WorkshopRepository", "updateTags - Error de parsing JSON: ${e.message}")

            // Si el error es porque esperaba objeto pero recibió string,
            // probablemente el backend devolvió un mensaje de éxito como string
            // Intentamos obtener el workshop actualizado
            try {
                android.util.Log.d("WorkshopRepository", "updateTags - Intentando obtener workshop actualizado después de error JSON")
                getMyWorkshop()
            } catch (ex: Exception) {
                Result.failure(Exception("Error al actualizar tags. El formato de respuesta no es el esperado. ${e.message}"))
            }
        } catch (e: HttpException) {
            android.util.Log.e("WorkshopRepository", "updateTags - HttpException: ${e.message}")
            Result.failure(Exception(parseHttpException(e, "Error al actualizar tags")))
        } catch (e: IOException) {
            android.util.Log.e("WorkshopRepository", "updateTags - IOException: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("WorkshopRepository", "updateTags - Exception: ${e.message}")
            android.util.Log.e("WorkshopRepository", "updateTags - Stack trace: ${e.stackTraceToString()}")
            Result.failure(Exception("Error inesperado al actualizar tags: ${e.message}"))
        }
    }

    override suspend fun addTags(workshopId: Long, tags: List<String>): Result<Workshop> {
        return try {
            val response = apiService.addTags(tags)

            if (response.isSuccessful && response.body() != null) {
                val workshop = response.body()!!.toDomain()
                Result.success(workshop)
            } else {
                Result.failure(Exception(parseError(response, "Error al agregar tags")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al agregar tags")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== PHOTOS ==========
    override suspend fun uploadLogo(workshopId: Long, imageUri: Uri): Result<String> {
        return try {
            val file = uriToFile(imageUri) ?: return Result.failure(Exception("No se pudo procesar la imagen"))

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadLogo(body)

            if (response.isSuccessful && response.body() != null) {
                val url = response.body()!!.url
                Result.success(url)
            } else {
                Result.failure(Exception(parseError(response, "Error al subir logo")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al subir logo")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPhoto(workshopId: Long, imageUri: Uri): Result<String> {
        return try {
            val file = uriToFile(imageUri) ?: return Result.failure(Exception("No se pudo procesar la imagen"))

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.uploadPhoto(body)

            if (response.isSuccessful && response.body() != null) {
                val url = response.body()!!.url
                Result.success(url)
            } else {
                Result.failure(Exception(parseError(response, "Error al subir foto")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al subir foto")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePhoto(workshopId: Long, photoIndex: Int): Result<Unit> {
        return try {
            val response = apiService.deletePhoto(photoIndex)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response, "Error al eliminar foto")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al eliminar foto")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== LOCATION ==========
    override suspend fun addLocation(workshopId: Long, location: Location): Result<Location> {
        return try {
            val dto = AddLocationRequestDto(
                street = location.street,
                city = location.city,
                state = location.state,
                zip = location.zip,
                country = location.country,
                latitude = location.latitude,
                longitude = location.longitude
            )

            val response = apiService.addLocation(dto)

            if (response.isSuccessful && response.body() != null) {
                val createdLocation = response.body()!!.toDomain()
                Result.success(createdLocation)
            } else {
                Result.failure(Exception(parseError(response, "Error al agregar ubicación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al agregar ubicación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getLocationById(workshopId: Long, locationId: Long): Result<Location> {
        return try {
            val response = apiService.getLocationById(locationId)

            if (response.isSuccessful && response.body() != null) {
                val location = response.body()!!.toDomain()
                Result.success(location)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener ubicación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener ubicación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLocation(workshopId: Long, locationId: Long, location: Location): Result<Location> {
        return try {
            val dto = com.atg.autonexo.features.workshop.data.remote.models.UpdateLocationRequestDto(
                street = location.street,
                city = location.city,
                state = location.state,
                zip = location.zip,
                country = location.country,
                latitude = location.latitude,
                longitude = location.longitude
            )

            val response = apiService.updateLocation(locationId, dto)

            if (response.isSuccessful && response.body() != null) {
                val updatedLocation = response.body()!!.toDomain()
                Result.success(updatedLocation)
            } else {
                Result.failure(Exception(parseError(response, "Error al actualizar ubicación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al actualizar ubicación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLocation(workshopId: Long, locationId: Long): Result<Unit> {
        return try {
            val response = apiService.deleteLocation(locationId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response, "Error al eliminar ubicación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al eliminar ubicación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file
        } catch (e: Exception) {
            null
        }
    }

    private fun parseError(response: retrofit2.Response<*>, defaultMessage: String): String {
        val errorBodyString = try {
            response.errorBody()?.string() ?: ""
        } catch (e: Exception) {
            ""
        }

        return when {
            errorBodyString.isNotBlank() -> {
                try {
                    val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                    val backendMessage = errorResponse.message ?: errorResponse.error

                    when (response.code()) {
                        400 -> backendMessage ?: "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        409 -> backendMessage ?: "Conflicto"
                        422 -> backendMessage ?: "Datos de validación incorrectos"
                        500 -> backendMessage ?: "Error interno del servidor"
                        503 -> "Servicio no disponible"
                        else -> backendMessage ?: defaultMessage
                    }
                } catch (e: Exception) {
                    when (response.code()) {
                        400 -> "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> "Error interno del servidor"
                        else -> defaultMessage
                    }
                }
            }
            else -> {
                when (response.code()) {
                    400 -> "Datos inválidos"
                    401 -> "No autorizado"
                    403 -> "Acceso denegado"
                    404 -> "No encontrado"
                    500 -> "Error interno del servidor"
                    else -> defaultMessage
                }
            }
        }
    }

    private fun parseHttpException(e: HttpException, defaultMessage: String): String {
        val errorBodyString = try {
            e.response()?.errorBody()?.string() ?: ""
        } catch (ex: Exception) {
            ""
        }

        return when {
            errorBodyString.isNotBlank() -> {
                try {
                    val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                    val backendMessage = errorResponse.message ?: errorResponse.error

                    when (e.code()) {
                        400 -> backendMessage ?: "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> backendMessage ?: "Error interno del servidor"
                        else -> backendMessage ?: defaultMessage
                    }
                } catch (ex: Exception) {
                    when (e.code()) {
                        400 -> "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> "Error interno del servidor"
                        else -> defaultMessage
                    }
                }
            }
            else -> {
                when (e.code()) {
                    400 -> "Datos inválidos"
                    401 -> "No autorizado"
                    403 -> "Acceso denegado"
                    404 -> "No encontrado"
                    500 -> "Error interno del servidor"
                    else -> defaultMessage
                }
            }
        }
    }

    // ========== INVITACIONES ==========

    override suspend fun createInvitation(request: CreateInvitationRequest): Result<Invitation> {
        return try {
            val dto = CreateInvitationRequestDto(
                email = request.email,
                message = request.message,
                validityDays = request.validityDays
            )

            val response = apiService.createInvitation(dto)

            if (response.isSuccessful && response.body() != null) {
                val invitationDto = response.body()!!
                val invitation = invitationDto.toDomain()
                Result.success(invitation)
            } else {
                Result.failure(Exception(parseError(response, "Error al crear invitación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al crear invitación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInvitations(): Result<List<Invitation>> {
        return try {
            val response = apiService.getInvitations()

            if (response.isSuccessful) {
                // Si el body es null, devolver lista vacía en lugar de error
                val invitationsDto = response.body() ?: emptyList()
                val invitations = invitationsDto.toDomain()
                Result.success(invitations)
            } else {
                // Si es 404, puede que simplemente no haya invitaciones aún, devolver lista vacía
                if (response.code() == 404) {
                    Result.success(emptyList())
                } else {
                    Result.failure(Exception(parseError(response, "Error al obtener invitaciones")))
                }
            }
        } catch (e: HttpException) {
            // Si es 404, devolver lista vacía
            if (e.code() == 404) {
                Result.success(emptyList())
            } else {
                Result.failure(Exception(parseHttpException(e, "Error al obtener invitaciones")))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("WorkshopRepository", "getInvitations - Error: ${e.message}")
            android.util.Log.e("WorkshopRepository", "getInvitations - Stack trace: ${e.stackTraceToString()}")
            Result.failure(Exception("Error inesperado al obtener invitaciones: ${e.message}"))
        }
    }

    override suspend fun getInvitationByCode(code: String): Result<Invitation> {
        return try {
            val response = apiService.getInvitationByCode(code)

            if (response.isSuccessful && response.body() != null) {
                val invitationDto = response.body()!!
                val invitation = invitationDto.toDomain()
                Result.success(invitation)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener invitación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener invitación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptInvitation(request: AcceptInvitationRequest): Result<String> {
        return try {
            val dto = AcceptInvitationRequestDto(
                invitationCode = request.invitationCode,
                email = request.email
            )

            val response = apiService.acceptInvitation(dto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parseError(response, "Error al aceptar invitación")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al aceptar invitación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== EMPLEADOS ==========

    override suspend fun getWorkshopEmployees(workshopId: Long): Result<List<WorkshopEmployee>> {
        return try {
            val response = apiService.getWorkshopEmployees(workshopId)

            if (response.isSuccessful) {
                // Si el body es null, devolver lista vacía en lugar de error
                val employeesDto = response.body() ?: emptyList()
                val employees = employeesDto.toDomain()
                Result.success(employees)
            } else {
                // Si es 404, puede que simplemente no haya empleados aún, devolver lista vacía
                if (response.code() == 404) {
                    Result.success(emptyList())
                } else {
                    Result.failure(Exception(parseError(response, "Error al obtener empleados")))
                }
            }
        } catch (e: HttpException) {
            // Si es 404, devolver lista vacía
            if (e.code() == 404) {
                Result.success(emptyList())
            } else {
                Result.failure(Exception(parseHttpException(e, "Error al obtener empleados")))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("WorkshopRepository", "getWorkshopEmployees - Error: ${e.message}")
            android.util.Log.e("WorkshopRepository", "getWorkshopEmployees - Stack trace: ${e.stackTraceToString()}")
            Result.failure(Exception("Error inesperado al obtener empleados: ${e.message}"))
        }
    }

    override suspend fun deactivateEmployee(workshopId: Long, employeeId: Long): Result<Unit> {
        return try {
            val response = apiService.deactivateEmployee(workshopId, employeeId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response, "Error al desactivar empleado")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al desactivar empleado")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun activateEmployee(workshopId: Long, employeeId: Long): Result<Unit> {
        return try {
            val response = apiService.activateEmployee(workshopId, employeeId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response, "Error al activar empleado")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al activar empleado")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== STAFF ==========
    override suspend fun getWorkshopStaff(): Result<List<WorkshopStaff>> {
        return try {
            val response = apiService.getWorkshopStaff()

            if (response.isSuccessful && response.body() != null) {
                val staff = response.body()!!.map { it.toDomain() }
                Result.success(staff)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener staff del workshop")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener staff del workshop")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

