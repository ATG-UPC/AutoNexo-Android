package com.atg.autonexo.core.navigation

sealed class Route(val route: String) {
    // Rutas principales
    object Home : Route("home")
    object Profile : Route("profile")
    object Request : Route("request")
    object RequestDetail : Route("request/{requestId}") {
        fun createRoute(requestId: String) = "request/$requestId"
    }
    object Offer : Route("offer")
    object Workshop : Route("workshop")
    object WorkshopDetailOwner : Route("workshop/detail_owner")
    object WorkshopDetailMember : Route("workshop/detail_member")
    object Service : Route("service")

    // Rutas Secundarias
    object Pro : Route("pro")
    object Premiun : Route("premiun")

    // Rutas de autenticación
    object Auth : Route("auth") {
        object Login : Route("auth/login")
        object Register : Route("auth/register")
        object ForgotPassword : Route("auth/forgot_password")
        object OtpVerification : Route("auth/otp_verification/{phone}")
        object ResetPassword : Route("auth/reset_password")
        object WorkshopRegistrationStep1 : Route("auth/workshop_registration_step1")
        object WorkshopRegistrationStep2 : Route("auth/workshop_registration_step2")
        object WorkshopEditStep1 : Route("auth/workshop_edit_step1")
        object WorkshopEditStep2 : Route("auth/workshop_edit_step2")
        object WorkshopCodeJoin : Route("auth/workshop_code_join")
    }
    
    // Rutas de Vehicle & Maintenance
    object Vehicle : Route("vehicle") {
        object Detail : Route("vehicle/{vehicleId}") {
            fun createRoute(vehicleId: String) = "vehicle/$vehicleId"
        }
    }

    // Rutas de Service Order
    object ServiceOrder : Route("service") {
        object List : Route("service")
        object Register : Route("service/register")
    }
}