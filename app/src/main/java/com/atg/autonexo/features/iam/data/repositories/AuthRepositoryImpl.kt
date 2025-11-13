package com.atg.autonexo.features.iam.data.repositories

import android.util.Log
import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.core.network.ConnectivityResult
import com.atg.autonexo.core.network.ConnectivityTest
import com.atg.autonexo.features.iam.data.local.dao.UserDao
import com.atg.autonexo.features.iam.data.local.models.UserEntity
import com.atg.autonexo.features.iam.data.remote.models.LoginRequestDto
import com.atg.autonexo.features.iam.data.remote.models.SignUpRequestDto
import com.atg.autonexo.features.iam.data.remote.models.UserDto
import com.atg.autonexo.features.iam.data.remote.services.AuthService
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.iam.domain.models.User
import com.atg.autonexo.features.iam.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación del repositorio de autenticación
 * Maneja la lógica de negocio entre la capa remota, local y preferencias
 */
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences,
    private val connectivityTest: ConnectivityTest
) : AuthRepository {
    
    companion object {
        private const val TAG = "AuthRepositoryImpl"
    }
    
    override suspend fun signIn(email: String, password: String): AuthResult<User> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting sign in for email: $email")
                
                // Test connectivity first
                val connectivityResult = connectivityTest.testConnectivity()
                when (connectivityResult) {
                    ConnectivityResult.NoNetwork -> {
                        Log.e(TAG, "No network available")
                        return@withContext AuthResult.Error("Sin conexión a Internet. Verifica tu conexión de red.")
                    }
                    ConnectivityResult.DnsResolutionFailed -> {
                        Log.e(TAG, "DNS resolution failed")
                        return@withContext AuthResult.Error("No se puede conectar al servidor. Verifica tu conexión.")
                    }
                    is ConnectivityResult.UnknownError -> {
                        Log.e(TAG, "Connectivity test failed: ${connectivityResult.message}")
                        return@withContext AuthResult.Error("Error de conectividad: ${connectivityResult.message}")
                    }
                    ConnectivityResult.Success -> {
                        Log.d(TAG, "Connectivity test passed, proceeding with login")
                    }
                }
                
                val request = LoginRequestDto(email, password)
                val response = authService.signIn(request)
                
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Log.d(TAG, "Sign in successful for: $email")
                        
                        // Guardar token y datos de usuario en SharedPreferences
                        userPreferences.saveAuthToken(loginResponse.token)
                        userPreferences.saveUserId(loginResponse.user.id)
                        userPreferences.saveUserEmail(loginResponse.user.email)
                        
                        // Mapear UserDto a User (dominio)
                        val user = loginResponse.user.toDomainModel()
                        
                        // Guardar usuario en Room para persistencia offline
                        userDao.insertUser(user.toEntity())
                        
                        // Actualizar preferencias basadas en roles
                        updateUserPreferencesFromRoles(user)
                        
                        return@withContext AuthResult.Success(user)
                    } else {
                        Log.e(TAG, "Sign in response body is null")
                        return@withContext AuthResult.Error("Empty response from server")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Email o contraseña incorrectos"
                        403 -> "Cuenta desactivada"
                        404 -> "Usuario no encontrado"
                        else -> response.message() ?: "Error al iniciar sesión"
                    }
                    Log.e(TAG, "Sign in failed: ${response.code()} - $errorMessage")
                    return@withContext AuthResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign in exception", e)
                return@withContext AuthResult.Error(
                    e.message ?: "Error de conexión. Verifica tu internet.",
                    null
                )
            }
        }
    
    override suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        requestedRole: String,
        invitationCode: String?
    ): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Attempting sign up for email: $email with role: $requestedRole")
            
            val request = SignUpRequestDto(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                requestedRole = requestedRole,
                invitationCode = invitationCode
            )
            
            val response = authService.signUp(request)
            
            Log.d(TAG, "Sign up response code: ${response.code()}")
            Log.d(TAG, "Sign up response message: ${response.message()}")
            Log.d(TAG, "Sign up response body: ${response.body()}")
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Usuario registrado exitosamente"
                Log.d(TAG, "Sign up successful: $message")
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    409 -> "El email ya está registrado"
                    400 -> "Datos inválidos. Verifica todos los campos"
                    else -> response.message() ?: "Error al registrar usuario"
                }
                Log.e(TAG, "Sign up failed: ${response.code()} - $errorMessage")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e(TAG, "JSON parsing error during sign up", e)
            return@withContext AuthResult.Error(
                "Error al procesar respuesta del servidor. Intenta nuevamente.",
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Sign up exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión. Verifica tu internet.",
                null
            )
        }
    }
    
    override suspend fun signOut(): Unit = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Signing out user")
            userPreferences.clearUserData()
            userDao.clearAllUsers()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out", e)
        }
    }
    
    override suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        try {
            val userEntity = userDao.getCurrentUser()
            return@withContext userEntity?.toDomainModel()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            return@withContext null
        }
    }
    
    override fun getCurrentUserFlow(): Flow<User?> {
        return userDao.getCurrentUserFlow().map { it?.toDomainModel() }
    }
    
    override fun isAuthenticated(): Boolean {
        return userPreferences.isLoggedIn()
    }
    
    override suspend fun refreshCurrentUser(): AuthResult<User> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Refreshing current user from backend")
            val response = authService.getCurrentUser()
            
            if (response.isSuccessful) {
                val userDto = response.body()
                if (userDto != null) {
                    val user = userDto.toDomainModel()
                    
                    // Actualizar en Room
                    userDao.insertUser(user.toEntity())
                    
                    return@withContext AuthResult.Success(user)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesión expirada. Inicia sesión nuevamente"
                    else -> "Error al obtener perfil"
                }
                Log.e(TAG, "Refresh user failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Refresh user exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): AuthResult<User> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating profile")
            val request = com.atg.autonexo.features.iam.data.remote.models.UpdateProfileRequestDto(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber
            )
            
            val response = authService.updateProfile(request)
            
            if (response.isSuccessful) {
                val userDto = response.body()
                if (userDto != null) {
                    val user = userDto.toDomainModel()
                    
                    // Actualizar en Room
                    userDao.insertUser(user.toEntity())
                    
                    return@withContext AuthResult.Success(user)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    401 -> "Sesión expirada"
                    else -> "Error al actualizar perfil"
                }
                Log.e(TAG, "Update profile failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Update profile exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Changing password")
            val request = com.atg.autonexo.features.iam.data.remote.models.ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            
            val response = authService.changePassword(request)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Contraseña actualizada exitosamente"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos"
                    401 -> "Contraseña actual incorrecta"
                    else -> "Error al cambiar contraseña"
                }
                Log.e(TAG, "Change password failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Change password exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun deactivateAccount(): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Deactivating account")
            val response = authService.deactivateAccount()
            
            if (response.isSuccessful) {
                // Limpiar datos locales
                signOut()
                
                val message = response.body() ?: "Cuenta desactivada exitosamente"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al desactivar cuenta"
                Log.e(TAG, "Deactivate account failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Deactivate account exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun resendVerificationEmail(email: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Resending verification email")
            val request = com.atg.autonexo.features.iam.data.remote.models.ResendVerificationRequestDto(email)
            
            val response = authService.resendVerificationEmail(request)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Email de verificación enviado"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Email inválido"
                    404 -> "Usuario no encontrado"
                    else -> "Error al enviar email"
                }
                Log.e(TAG, "Resend verification failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resend verification exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun verifyEmail(token: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Verifying email")
            val request = com.atg.autonexo.features.iam.data.remote.models.VerifyEmailRequestDto(token)
            
            val response = authService.verifyEmail(request)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Email verificado exitosamente"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Token inválido o expirado"
                    404 -> "Usuario no encontrado"
                    else -> "Error al verificar email"
                }
                Log.e(TAG, "Verify email failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Verify email exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun getVerificationStatus(email: String): AuthResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting verification status")
            val response = authService.getVerificationStatus(email)
            
            if (response.isSuccessful) {
                val statusDto = response.body()
                val isVerified = statusDto?.isVerified ?: false
                return@withContext AuthResult.Success(isVerified)
            } else {
                val errorMessage = "Error al obtener estado de verificación"
                Log.e(TAG, "Get verification status failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get verification status exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun requestPasswordReset(email: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Requesting password reset")
            val request = com.atg.autonexo.features.iam.data.remote.models.ForgotPasswordRequestDto(email)
            
            val response = authService.requestPasswordReset(request)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Se ha enviado un email con instrucciones"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Email no encontrado"
                    else -> "Error al solicitar reset de contraseña"
                }
                Log.e(TAG, "Request password reset failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Request password reset exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    override suspend fun resetPassword(token: String, newPassword: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Resetting password")
            val request = com.atg.autonexo.features.iam.data.remote.models.ResetPasswordRequestDto(
                token = token,
                newPassword = newPassword
            )
            
            val response = authService.resetPassword(request)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Contraseña restablecida exitosamente"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Token inválido o expirado"
                    else -> "Error al restablecer contraseña"
                }
                Log.e(TAG, "Reset password failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Reset password exception", e)
            return@withContext AuthResult.Error(
                e.message ?: "Error de conexión",
                null
            )
        }
    }
    
    // ========== Helper Methods ==========
    
    private fun updateUserPreferencesFromRoles(user: User) {
        user.workshopId?.let {
            userPreferences.setHasWorkshop(true)
        }
        
        if (user.isWorkshopManager()) {
            userPreferences.setIsWorkshopManager(true)
        }
    }
    
    // ========== Mapping Extensions ==========
    
    private fun UserDto.toDomainModel(): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            isVerified = isVerified,
            active = active,
            roles = roles,
            workshopId = workshopId
        )
    }
    
    private fun User.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            isVerified = isVerified,
            active = active,
            roles = roles,
            workshopId = workshopId,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    private fun UserEntity.toDomainModel(): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            isVerified = isVerified,
            active = active,
            roles = roles,
            workshopId = workshopId
        )
    }
}
