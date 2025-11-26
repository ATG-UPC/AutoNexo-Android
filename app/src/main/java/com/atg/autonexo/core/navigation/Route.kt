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
    // Workshop Navigation Routes
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
            fun createRoute(workshopName: String) =
                "workshop/invitation/code/${android.net.Uri.encode(workshopName)}"
        }
        object Management : Route("workshop/management/{workshopId}") {
            fun createRoute(workshopId: Long) = "workshop/management/$workshopId"
        }
        object InviteEmployee : Route("workshop/invitation/invite/{workshopId}") {
            fun createRoute(workshopId: Long) = "workshop/invitation/invite/$workshopId"
        }
        object AcceptInvitation : Route("workshop/invitation/accept")
    }

    object Matching {
        object Request : Route("matching/request")
    }

    // Payment Navigation Routes
    object Payment{
        object PaymentScreen : Route("payment/subscription"){


            // ruta con argumento para NavHost
            const val routeWithArg = "payment/subscription/{workshopId}"

            fun createRoute(workshopId: Long) = "payment/subscription/$workshopId"
        }
    }


    // Home Navigation Routes
    object HomeNav {
        object Requests : Route("home/requests")
        object Services : Route("home/services")
        object Profile : Route("home/profile")
    }
}

