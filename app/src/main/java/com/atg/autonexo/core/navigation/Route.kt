package com.atg.autonexo.core.navigation

sealed class Route(val route: String) {
    object Home : Route("home")
    
    object Auth {
        object Login : Route("auth/login")
        object Register : Route("auth/register")
        object EmailVerification : Route("auth/email_verification/{email}") {
            fun createRoute(email: String) = "auth/email_verification/${android.net.Uri.encode(email)}"
        }
        object ForgotPassword : Route("auth/forgot_password")
        object ResetPassword : Route("auth/reset_password")
    }
    
    object Workshop {
        object BasicInfo : Route("workshop/registration/basic_info")
        object Tags : Route("workshop/registration/tags/{workshopId}") {
            fun createRoute(workshopId: Long) = "workshop/registration/tags/$workshopId"
        }
        object Media : Route("workshop/registration/media/{workshopId}") {
            fun createRoute(workshopId: Long) = "workshop/registration/media/$workshopId"
        }
        object Location : Route("workshop/registration/location/{workshopId}") {
            fun createRoute(workshopId: Long) = "workshop/registration/location/$workshopId"
        }
        object InvitationCodeDisplay : Route("workshop/invitation/code/{workshopName}") {
            fun createRoute(workshopName: String) = "workshop/invitation/code/${android.net.Uri.encode(workshopName)}"
        }
        object Management : Route("workshop/management/{workshopId}") {
            fun createRoute(workshopId: Long) = "workshop/management/$workshopId"
        }
        object AcceptInvitation : Route("workshop/invitation/accept")
    }

    
    // Home Navigation Routes
    object HomeNav {
        object Requests : Route("home/requests")
        object Services : Route("home/services")
        object Profile : Route("home/profile")
    }
}

