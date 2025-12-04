package com.atg.autonexo.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.atg.autonexo.features.auth.presentation.login.LoginScreen
import com.atg.autonexo.features.auth.presentation.register.RegisterScreen
import com.atg.autonexo.features.auth.presentation.emailverification.EmailVerificationScreen
import com.atg.autonexo.features.auth.presentation.forgotpassword.ForgotPasswordScreen
import com.atg.autonexo.features.auth.presentation.resetpassword.ResetPasswordScreen
import com.atg.autonexo.features.workshop.presentation.registration.basicinfo.BasicInfoScreen
import com.atg.autonexo.features.workshop.presentation.registration.tags.TagsScreen
import com.atg.autonexo.features.workshop.presentation.registration.media.MediaScreen
import com.atg.autonexo.features.workshop.presentation.registration.location.LocationScreen
import com.atg.autonexo.features.workshop.presentation.invitation.WorkshopManagementScreen
import com.atg.autonexo.features.workshop.presentation.invitation.InviteEmployeeScreen
import com.atg.autonexo.features.workshop.presentation.invitation.AcceptInvitationScreen
import com.atg.autonexo.features.workshop.presentation.invitation.InvitationCodeDisplayScreen
import com.atg.autonexo.features.home.presentation.home.HomeScreen
import com.atg.autonexo.features.matching.presentation.booking.BookingScreen
import com.atg.autonexo.features.matching.presentation.offerlist.OfferListScreen
import com.atg.autonexo.features.matching.presentation.requests.RequestsScreen
import com.atg.autonexo.features.payment.presentation.payment.PaymentScreen
import com.atg.autonexo.features.profile.presentation.ProfileScreen
import com.atg.autonexo.features.profile.presentation.editprofile.EditProfileScreen
import com.atg.autonexo.features.profile.presentation.newpassword.NewPasswordScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Auth.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Route.Auth.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.Auth.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Auth.Login.route) { inclusive = true }
                    }
                },
                onEmailNotVerified = { email ->
                    navController.navigate(Route.Auth.EmailVerification.createRoute(email)) {
                        popUpTo(Route.Auth.Login.route) { inclusive = false }
                    }
                }
            )
        }
        
        composable(Route.Auth.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Auth.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = { email ->
                    navController.navigate(Route.Auth.EmailVerification.createRoute(email))
                }
            )
        }
        
        composable(
            route = Route.Auth.EmailVerification.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                onNavigateToLogin = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Auth.EmailVerification.route) { inclusive = true }
                    }
                },
                onVerificationSuccess = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Auth.EmailVerification.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Route.Auth.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetLinkSent = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Route.Auth.ResetPassword.route) {
            ResetPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPasswordResetSuccess = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Auth.ResetPassword.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Route.Home.route) {
            HomeScreen(
                currentRoute = Route.Home.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // No hacer popUpTo para mantener el historial
                    }
                },
                onLogout = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Navigation Routes
        composable(Route.HomeNav.Requests.route) {
            HomeScreen(
                currentRoute = Route.HomeNav.Requests.route,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // BOOKING SERVICES
        composable(Route.HomeNav.Services.route) {
            BookingScreen(
            currentRoute = Route.HomeNav.Services.route,
            onNavigate = { route ->
                navController.navigate(route)
            }, onBack = {
                navController.popBackStack()
            }
        )
        }

        // REQUEST
        composable(Route.HomeNav.Requests.route){
            RequestsScreen(
                currentRoute = Route.HomeNav.Requests.route,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.HomeNav.Offers.route){
            OfferListScreen(
                currentRoute = Route.HomeNav.Offers.route,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        //PROFILE
        composable(Route.HomeNav.Profile.route) {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEditProfile = {
                    navController.navigate(Route.Profile.EditProfile.route)
                },
                onEditWorkshop = {
                    // TODO: Navegar a EditWorkshopScreen cuando esté implementada
                    // navController.navigate(Route.Workshop.EditWorkshop.route)
                }
            )
        }

        // Profile Edit
        composable(Route.Profile.EditProfile.route) {
            EditProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onChangePassword = {
                    navController.navigate(Route.Profile.NewPassword.route)
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // New Password
        composable(Route.Profile.NewPassword.route) {
            NewPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPasswordChanged = {
                    navController.popBackStack()
                }
            )
        }
        
        // Workshop Registration Flow
        composable(Route.Workshop.BasicInfo.route) {
            BasicInfoScreen(
                onNext = { workshopId ->
                    navController.navigate(Route.Workshop.Tags.createRoute(workshopId))
                },
                onBack = {
                    navController.popBackStack()
                },
                currentRoute = Route.Workshop.BasicInfo.route,
                onNavigate = { route ->
                    navController.navigate(route)
                },
            )
        }
        
        composable(
            route = Route.Workshop.Tags.route,
            arguments = listOf(
                navArgument("workshopId") {
                    type = NavType.LongType
                },

            )
        ) { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getLong("workshopId") ?: 0L
            TagsScreen(
                workshopId = workshopId,
                onNext = { workshopId ->
                    navController.navigate(Route.Workshop.Media.createRoute(workshopId))
                },
                onBack = {
                    navController.popBackStack()
                },
                currentRoute = Route.Workshop.Tags.route,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        composable(
            route = Route.Workshop.Media.route,
            arguments = listOf(
                navArgument("workshopId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getLong("workshopId") ?: 0L
            MediaScreen(
                workshopId = workshopId,
                onNext = { workshopId ->
                    navController.navigate(Route.Workshop.Location.createRoute(workshopId))
                },
                onBack = {
                    navController.popBackStack()
                },
                currentRoute = Route.Workshop.Media.route,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        composable(
            route = Route.Workshop.Location.route,
            arguments = listOf(
                navArgument("workshopId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getLong("workshopId") ?: 0L
            LocationScreen(
                workshopId = workshopId,
                onFinish = { workshopName ->
                    navController.navigate(Route.Workshop.InvitationCodeDisplay.createRoute(workshopName)) {
                        popUpTo(Route.Workshop.BasicInfo.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                currentRoute = Route.Workshop.Media.route,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        // Invitation Code Display (después de crear taller)
        composable(
            route = Route.Workshop.InvitationCodeDisplay.route,
            arguments = listOf(
                navArgument("workshopName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val workshopName = backStackEntry.arguments?.getString("workshopName") ?: "tu taller"
            InvitationCodeDisplayScreen(
                workshopName = workshopName,
                onContinue = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Workshop.InvitationCodeDisplay.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Workshop Management
        composable(
            route = Route.Workshop.Management.route,
            arguments = listOf(
                navArgument("workshopId") {
                    type = NavType.LongType
                }
            )

        ) { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getLong("workshopId") ?: 0L
            WorkshopManagementScreen(
                workshopId = workshopId,
                onBack = { navController.popBackStack() },
                onInviteEmployee = { id ->
                    navController.navigate(Route.Workshop.InviteEmployee.createRoute(id))
                }

            )
        }
        
        // Invite employee screen
        composable(
            route = Route.Workshop.InviteEmployee.route,
            arguments = listOf(
                navArgument("workshopId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getLong("workshopId") ?: 0L
            InviteEmployeeScreen(
                workshopId = workshopId,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Accept Invitation
        composable(Route.Workshop.AcceptInvitation.route) {
            AcceptInvitationScreen(
                onSuccess = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Workshop.AcceptInvitation.route) { inclusive = true }
                    }
                }
            )
        }

        // PAYMENT
        composable(
            route = Route.Payment.PaymentScreen.routeWithArg,
            arguments = listOf(
                navArgument("workshopId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getLong("workshopId") ?: 0L
            PaymentScreen(
                workshopId = workshopId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}

