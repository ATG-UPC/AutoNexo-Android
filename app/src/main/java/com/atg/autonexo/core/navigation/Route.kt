package com.atg.autonexo.core.navigation

sealed class Route(val route: String) {
    // Rutas principales
    object Home : Route("home")
    object Profile : Route("profile")
    object Request : Route("request")
    object Offer : Route("offer")
    object Workshop : Route("workshop")
    object Service : Route("service")
    
    // Rutas de autenticación
    object Auth : Route("auth") {
        object Login : Route("auth/login")
        object Register : Route("auth/register")
        object ForgotPassword : Route("auth/forgot_password")
        object OtpVerification : Route("auth/otp_verification/{phone}")
        object ResetPassword : Route("auth/reset_password")
    }
    
    // Rutas de Vehicle & Maintenance
    object Vehicle : Route("vehicle") {
        object Detail : Route("vehicle/{vehicleId}") {
            fun createRoute(vehicleId: String) = "vehicle/$vehicleId"
        }
    }
}