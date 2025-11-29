package com.atg.autonexo.features.auth.data.repositories

import com.atg.autonexo.core.data.PreferencesDataStore
import com.atg.autonexo.features.auth.data.mappers.toDomain
import com.atg.autonexo.features.auth.data.remote.models.SignInRequestDto
import com.atg.autonexo.features.auth.data.remote.models.SignUpRequestDto
import com.atg.autonexo.features.auth.data.remote.models.VerifyEmailRequestDto
import com.atg.autonexo.features.auth.data.remote.models.ResendVerificationRequestDto
import com.atg.autonexo.features.auth.data.remote.models.ForgotPasswordRequestDto
import com.atg.autonexo.features.auth.data.remote.models.ResetPasswordRequestDto
import com.atg.autonexo.features.auth.data.remote.models.UpdateProfileRequestDto
import com.atg.autonexo.features.auth.data.remote.models.ChangePasswordRequestDto
import com.atg.autonexo.features.auth.data.remote.services.AuthApiService
import com.atg.autonexo.features.auth.domain.models.Role
import com.atg.autonexo.features.auth.domain.models.SignUpRequest
import com.atg.autonexo.features.auth.domain.models.User
import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import com.atg.autonexo.features.auth.data.remote.models.ErrorResponseDto
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val preferencesDataStore: PreferencesDataStore,
    private val gson: com.google.gson.Gson
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Pair<String, User>> {
        return try {
            val request = SignInRequestDto(email = email, password = password)
            val response = apiService.signIn(request)
            
            if (response.isSuccessful && response.body() != null) {
                val signInResponse = response.body()!!
                
                // Guardar token y datos del usuario
                preferencesDataStore.saveToken(signInResponse.token)
                preferencesDataStore.saveUserInfo(
                    userId = signInResponse.user.id.toString(),
                    email = signInResponse.user.email
                )
                
                val user = signInResponse.user.toDomain()
                Result.success(Pair(signInResponse.token, user))
            } else {
                val errorMessage = try {
                    response.errorBody()?.string() ?: response.message() ?: "Error al iniciar sesión"
                } catch (ex: Exception) {
                    response.message() ?: "Error al iniciar sesión"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            val errorMessage = try {
                e.response()?.errorBody()?.string() ?: e.message() ?: "Error HTTP"
            } catch (ex: Exception) {
                e.message() ?: "Error HTTP"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(request: SignUpRequest): Result<String> {
        return try {
            // Validaciones adicionales antes de enviar
            if (request.email.isBlank()) {
                return Result.failure(Exception("El email no puede estar vacío"))
            }
            
            if (request.password.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }
            
            // Crear el DTO con el rol exactamente como el backend lo espera
            // El rol se envía como string del enum: "WORKSHOP_MANAGER" o "WORKSHOP_EMPLOYEE"
            // IMPORTANTE: Debe ser exactamente como está en el enum, sin espacios ni minúsculas
            val roleName = request.requestedRole.name
            
            // Validar formato del rol antes de enviar
            if (roleName.isBlank() || !roleName.matches(Regex("^[A-Z_]+$"))) {
                return Result.failure(Exception("Formato de rol inválido: '$roleName'. Debe ser en mayúsculas con guiones bajos (ej: WORKSHOP_MANAGER)"))
            }
            
            val dto = SignUpRequestDto(
                email = request.email.trim(),
                password = request.password,
                firstName = request.firstName.trim(),
                lastName = request.lastName.trim(),
                phoneNumber = request.phoneNumber.trim(),
                requestedRole = roleName, // Ejemplo: "WORKSHOP_MANAGER"
                invitationCode = request.invitationCode?.trim()
            )
            
            // Logging del JSON que se enviará (para debug)
            val jsonToSend = gson.toJson(dto)
            android.util.Log.d("AuthRepository", "=== REGISTRO ===")
            android.util.Log.d("AuthRepository", "DTO objeto: $dto")
            android.util.Log.d("AuthRepository", "JSON generado:")
            android.util.Log.d("AuthRepository", jsonToSend)
            android.util.Log.d("AuthRepository", "Rol enviado: '$roleName'")
            android.util.Log.d("AuthRepository", "Email: '${dto.email}'")
            android.util.Log.d("AuthRepository", "InvitationCode: ${if (dto.invitationCode == null) "NULL" else "'${dto.invitationCode}'"}")
            
            // Verificar que el JSON sea válido comparándolo con el ejemplo que funciona
            val expectedFormat = """{"email":"${dto.email}","password":"${dto.password}","firstName":"${dto.firstName}","lastName":"${dto.lastName}","phoneNumber":"${dto.phoneNumber}","requestedRole":"${dto.requestedRole}","invitationCode":null}"""
            android.util.Log.d("AuthRepository", "JSON esperado (formato): $expectedFormat")
            
            val response = apiService.signUp(dto)
            
            android.util.Log.d("AuthRepository", "=== RESPUESTA ===")
            android.util.Log.d("AuthRepository", "Código: ${response.code()}")
            android.util.Log.d("AuthRepository", "Exitosa: ${response.isSuccessful}")
            android.util.Log.d("AuthRepository", "Mensaje: ${response.message()}")
            
            if (response.isSuccessful) {
                // El backend devuelve un String simple: "User registered successfully"
                // Gson lo parsea automáticamente como String
                val responseBody = response.body()
                Result.success(responseBody ?: "Usuario registrado exitosamente")
            } else {
                // Leer el errorBody una sola vez
                val errorBodyString = try {
                    response.errorBody()?.string() ?: ""
                } catch (e: Exception) {
                    ""
                }
                
                // Logging del error para debug
                android.util.Log.e("AuthRepository", "Error del servidor: $errorBodyString")
                android.util.Log.e("AuthRepository", "Código HTTP: ${response.code()}")
                
                val errorMessage = when {
                    errorBodyString.isNotBlank() -> {
                        try {
                            // Intentar parsear como ErrorResponseDto
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                            val backendMessage = errorResponse.message ?: errorResponse.error
                            
                            // Mensajes específicos según el código de estado y contenido del error
                            when {
                                // Error 500 con mensaje sobre rol no encontrado
                                response.code() == 500 && (backendMessage?.contains("role", ignoreCase = true) == true || 
                                                          backendMessage?.contains("Requested role", ignoreCase = true) == true ||
                                                          errorBodyString.contains("role", ignoreCase = true)) -> {
                                    "El rol '$roleName' no está disponible en el servidor. Contacta al administrador."
                                }
                                // Error 500 con mensaje sobre código de invitación
                                response.code() == 500 && (backendMessage?.contains("invitation", ignoreCase = true) == true ||
                                                          backendMessage?.contains("código", ignoreCase = true) == true ||
                                                          errorBodyString.contains("invitation", ignoreCase = true)) -> {
                                    "El código de invitación es inválido o ha expirado. Verifica el código e intenta nuevamente."
                                }
                                // Otros errores según código HTTP
                                response.code() == 400 -> backendMessage ?: "Datos inválidos. Verifica que todos los campos estén correctos."
                                response.code() == 401 -> "No autorizado. Verifica tus credenciales."
                                response.code() == 403 -> "Acceso denegado. No tienes permisos para realizar esta acción."
                                response.code() == 404 -> "Endpoint no encontrado. El servicio puede estar en mantenimiento."
                                response.code() == 409 -> backendMessage ?: "El email ya está registrado. Intenta con otro email o inicia sesión."
                                response.code() == 422 -> backendMessage ?: "Datos de validación incorrectos. Revisa los campos del formulario."
                                response.code() == 500 -> backendMessage ?: "Error interno del servidor. El rol '$roleName' puede no estar disponible. Contacta al administrador."
                                response.code() == 503 -> "Servicio no disponible. El servidor está temporalmente fuera de servicio."
                                else -> backendMessage ?: "Error al registrar usuario (Código: ${response.code()})"
                            }
                        } catch (e: Exception) {
                            // Si no se puede parsear, analizar el contenido del error
                            when {
                                errorBodyString.contains("role", ignoreCase = true) || 
                                errorBodyString.contains("Requested role", ignoreCase = true) -> {
                                    "El rol '$roleName' no está disponible en el servidor. Contacta al administrador."
                                }
                                errorBodyString.contains("invitation", ignoreCase = true) -> {
                                    "El código de invitación es inválido. Verifica el código e intenta nuevamente."
                                }
                                else -> "Error del servidor: $errorBodyString"
                            }
                        }
                    }
                    else -> {
                        // Mensajes según código HTTP cuando no hay body
                        when (response.code()) {
                            400 -> "Solicitud inválida. Verifica los datos ingresados."
                            401 -> "No autorizado. Verifica tus credenciales."
                            403 -> "Acceso denegado."
                            404 -> "Servicio no encontrado."
                            409 -> "El email ya está registrado. Intenta con otro email."
                            422 -> "Datos de validación incorrectos."
                            500 -> "Error interno del servidor. El rol '$roleName' puede no estar disponible. Contacta al administrador."
                            503 -> "Servicio no disponible temporalmente."
                            else -> "Error al registrar usuario (Código HTTP: ${response.code()})"
                        }
                    }
                }
                
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            // Leer el errorBody una sola vez
            val errorBodyString = try {
                e.response()?.errorBody()?.string() ?: ""
            } catch (ex: Exception) {
                ""
            }
            
            val roleName = request.requestedRole.name
            android.util.Log.e("AuthRepository", "HttpException código: ${e.code()}")
            android.util.Log.e("AuthRepository", "Error body: $errorBodyString")
            
            val errorMessage = when {
                errorBodyString.isNotBlank() -> {
                    try {
                        val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                        val backendMessage = errorResponse.message ?: errorResponse.error
                        
                        when {
                            // Error 500 relacionado con rol no encontrado (CAUSA MÁS PROBABLE)
                            e.code() == 500 && (backendMessage?.contains("role", ignoreCase = true) == true || 
                                               backendMessage?.contains("Requested role", ignoreCase = true) == true ||
                                               backendMessage?.contains("not found", ignoreCase = true) == true ||
                                               errorBodyString.contains("role", ignoreCase = true) ||
                                               errorBodyString.contains("Requested role", ignoreCase = true)) -> {
                                "El rol '$roleName' no está disponible en el servidor. El backend no tiene este rol configurado. Contacta al administrador."
                            }
                            // Error 500 relacionado con código de invitación inválido
                            e.code() == 500 && (backendMessage?.contains("invitation", ignoreCase = true) == true ||
                                               backendMessage?.contains("código", ignoreCase = true) == true ||
                                               backendMessage?.contains("invitation code", ignoreCase = true) == true ||
                                               errorBodyString.contains("invitation", ignoreCase = true)) -> {
                                "El código de invitación es inválido o ha expirado. Verifica el código e intenta nuevamente."
                            }
                            // Error 500 relacionado con email de verificación
                            e.code() == 500 && (backendMessage?.contains("email", ignoreCase = true) == true ||
                                               backendMessage?.contains("verification", ignoreCase = true) == true ||
                                               errorBodyString.contains("email", ignoreCase = true)) -> {
                                "Error al enviar email de verificación. El registro puede haberse completado pero el email no se pudo enviar."
                            }
                            // Otros errores HTTP
                            e.code() == 400 -> backendMessage ?: "Solicitud inválida. Verifica los datos."
                            e.code() == 401 -> "No autorizado."
                            e.code() == 403 -> "Acceso denegado."
                            e.code() == 404 -> "Servicio no encontrado."
                            e.code() == 409 -> backendMessage ?: "El email ya está registrado."
                            e.code() == 422 -> backendMessage ?: "Datos de validación incorrectos."
                            e.code() == 500 -> backendMessage ?: "Error interno del servidor. El rol '$roleName' puede no estar disponible en la base de datos. Contacta al administrador."
                            e.code() == 503 -> "Servicio no disponible."
                            else -> backendMessage ?: "Error HTTP ${e.code()}"
                        }
                    } catch (ex: Exception) {
                        // Analizar contenido del error sin parsear JSON
                        when {
                            errorBodyString.contains("role", ignoreCase = true) || 
                            errorBodyString.contains("Requested role", ignoreCase = true) ||
                            errorBodyString.contains("not found", ignoreCase = true) -> {
                                "El rol '$roleName' no está disponible en el servidor. Contacta al administrador."
                            }
                            errorBodyString.contains("invitation", ignoreCase = true) -> {
                                "El código de invitación es inválido."
                            }
                            else -> "Error del servidor: $errorBodyString"
                        }
                    }
                }
                else -> {
                    when (e.code()) {
                        400 -> "Solicitud inválida. Verifica los datos ingresados."
                        401 -> "No autorizado."
                        403 -> "Acceso denegado."
                        404 -> "Servicio no encontrado."
                        409 -> "El email ya está registrado. Intenta con otro email."
                        422 -> "Datos de validación incorrectos."
                        500 -> "Error interno del servidor. El rol '$roleName' puede no estar disponible en la base de datos. Contacta al administrador."
                        503 -> "Servicio no disponible temporalmente."
                        else -> "Error HTTP ${e.code()}: ${e.message()}"
                    }
                }
            }
            
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión a internet. Verifica tu conexión e intenta nuevamente."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado. El servidor tardó demasiado en responder. Intenta nuevamente."))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se puede conectar al servidor. Verifica tu conexión a internet."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message ?: "Error desconocido. Por favor intenta nuevamente."}"))
        }
    }

    override suspend fun verifyEmail(token: String): Result<String> {
        return try {
            val request = VerifyEmailRequestDto(token = token)
            val response = apiService.verifyEmail(request)
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Email verificado exitosamente")
            } else {
                Result.failure(Exception("Error al verificar email: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resendVerification(email: String): Result<String> {
        return try {
            val request = ResendVerificationRequestDto(email = email)
            val response = apiService.resendVerification(request)
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Email de verificación enviado")
            } else {
                Result.failure(Exception("Error al reenviar verificación: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val request = ForgotPasswordRequestDto(email = email)
            val response = apiService.forgotPassword(request)
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Si existe una cuenta con este email, se ha enviado un enlace de recuperación.")
            } else {
                Result.failure(Exception("Error al solicitar recuperación: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> {
        return try {
            val request = ResetPasswordRequestDto(token = token, newPassword = newPassword)
            val response = apiService.resetPassword(request)
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Contraseña restablecida exitosamente")
            } else {
                Result.failure(Exception("Error al restablecer contraseña: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            
            android.util.Log.d("AuthRepository", "getCurrentUser - Código HTTP: ${response.code()}")
            android.util.Log.d("AuthRepository", "getCurrentUser - Es exitoso: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val userDto = response.body()
                if (userDto != null) {
                    android.util.Log.d("AuthRepository", "getCurrentUser - UserDto recibido: id=${userDto.id}, email=${userDto.email}")
                    val user = userDto.toDomain()
                    Result.success(user)
                } else {
                    // Intentar leer el body como string para debugging
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthRepository", "getCurrentUser - Body es null. Error body: $errorBody")
                    Result.failure(Exception("El servidor no devolvió datos del usuario. Error: $errorBody"))
                }
            } else {
                // Leer el errorBody para obtener más información
                val errorBodyString = try {
                    response.errorBody()?.string() ?: ""
                } catch (ex: Exception) {
                    ""
                }
                
                android.util.Log.e("AuthRepository", "getCurrentUser - Error HTTP ${response.code()}: $errorBodyString")
                
                val errorMessage = when {
                    errorBodyString.isNotBlank() -> {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                            errorResponse.message ?: errorResponse.error ?: "Error al obtener usuario actual"
                        } catch (ex: Exception) {
                            "Error al obtener usuario actual: $errorBodyString"
                        }
                    }
                    else -> "Error al obtener usuario actual: ${response.message()}"
                }
                
                Result.failure(Exception(errorMessage))
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            // Error específico de parsing JSON
            android.util.Log.e("AuthRepository", "getCurrentUser - Error de parsing JSON: ${e.message}")
            android.util.Log.e("AuthRepository", "getCurrentUser - Stack trace: ${e.stackTraceToString()}")
            Result.failure(Exception("Error al parsear respuesta del servidor. El formato no es el esperado. ${e.message}"))
        } catch (e: HttpException) {
            android.util.Log.e("AuthRepository", "getCurrentUser - HttpException: ${e.message}")
            Result.failure(Exception("Error HTTP: ${e.message()}"))
        } catch (e: IOException) {
            android.util.Log.e("AuthRepository", "getCurrentUser - IOException: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "getCurrentUser - Exception: ${e.message}")
            android.util.Log.e("AuthRepository", "getCurrentUser - Stack trace: ${e.stackTraceToString()}")
            Result.failure(Exception("Error inesperado al obtener usuario: ${e.message}"))
        }
    }

    override suspend fun updateProfile(firstName: String, lastName: String, phoneNumber: String): Result<User> {
        return try {
            val request = UpdateProfileRequestDto(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                phoneNumber = phoneNumber.trim()
            )
            
            val response = apiService.updateProfile(request)
            
            if (response.isSuccessful && response.body() != null) {
                val userDto = response.body()!!
                val user = userDto.toDomain()
                Result.success(user)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: ""
                } catch (ex: Exception) {
                    ""
                }
                
                val errorMessage = when {
                    errorBodyString.isNotBlank() -> {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                            errorResponse.message ?: errorResponse.error ?: "Error al actualizar perfil"
                        } catch (ex: Exception) {
                            "Error al actualizar perfil: $errorBodyString"
                        }
                    }
                    else -> "Error al actualizar perfil: ${response.message()}"
                }
                
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            val errorBodyString = try {
                e.response()?.errorBody()?.string() ?: ""
            } catch (ex: Exception) {
                ""
            }
            
            val errorMessage = when {
                errorBodyString.isNotBlank() -> {
                    try {
                        val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                        errorResponse.message ?: errorResponse.error ?: "Error al actualizar perfil"
                    } catch (ex: Exception) {
                        "Error al actualizar perfil: $errorBodyString"
                    }
                }
                else -> "Error HTTP: ${e.message()}"
            }
            
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado al actualizar perfil: ${e.message}"))
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        return try {
            val request = ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            
            val response = apiService.changePassword(request)
            
            if (response.isSuccessful) {
                Result.success(response.body() ?: "Contraseña actualizada exitosamente")
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: ""
                } catch (ex: Exception) {
                    ""
                }
                
                val errorMessage = when {
                    errorBodyString.isNotBlank() -> {
                        try {
                            val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                            errorResponse.message ?: errorResponse.error ?: "Error al cambiar contraseña"
                        } catch (ex: Exception) {
                            "Error al cambiar contraseña: $errorBodyString"
                        }
                    }
                    else -> "Error al cambiar contraseña: ${response.message()}"
                }
                
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            val errorBodyString = try {
                e.response()?.errorBody()?.string() ?: ""
            } catch (ex: Exception) {
                ""
            }
            
            val errorMessage = when {
                errorBodyString.isNotBlank() -> {
                    try {
                        val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                        errorResponse.message ?: errorResponse.error ?: "Error al cambiar contraseña"
                    } catch (ex: Exception) {
                        "Error al cambiar contraseña: $errorBodyString"
                    }
                }
                else -> "Error HTTP: ${e.message()}"
            }
            
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado al cambiar contraseña: ${e.message}"))
        }
    }
}

