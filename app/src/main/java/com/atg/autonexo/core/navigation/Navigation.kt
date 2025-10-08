package com.atg.autonexo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.atg.autonexo.features.auth.presentation.forgotpassword.ForgotPasswordScreen
import com.atg.autonexo.features.auth.presentation.login.LoginScreen
import com.atg.autonexo.features.auth.presentation.otp.OtpVerificationScreen
import com.atg.autonexo.features.auth.presentation.register.RegisterWorkshopScreen
import com.atg.autonexo.features.auth.presentation.resetpassword.ResetPasswordScreen
import com.atg.autonexo.features.home.presentation.home.HomeScreen
import com.atg.autonexo.features.home.presentation.profile.EditProfileScreen
import com.atg.autonexo.features.home.presentation.profile.NewPasswordScreen
import com.atg.autonexo.features.home.presentation.profile.ProfileScreen
import com.atg.autonexo.features.matchingbooking.presentation.request.RequestListScreen
import com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle.VehicleDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Route.Auth.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Rutas de autenticación
        composable(Route.Auth.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Route.Auth.Register.route) {
                        popUpTo(Route.Auth.Login.route) {
                            inclusive = false
                        }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Route.Auth.ForgotPassword.route) {
                        popUpTo(Route.Auth.Login.route) {
                            inclusive = false
                        }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Route.Auth.Register.route) {
            RegisterWorkshopScreen(
                onNavigateBack = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Auth.Register.route) {
                            inclusive = false
                        }
                    }
                },
                onNavigateToNextStep = {
                    // TODO: Navegar al paso 2 del registro
                    // navController.navigate(Route.Auth.RegisterStep2.route)
                }
            )
        }
        
        composable(Route.Auth.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOtp = { phone ->
                    navController.navigate("auth/otp_verification/$phone") {
                        popUpTo(Route.Auth.ForgotPassword.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(
            route = Route.Auth.OtpVerification.route,
            arguments = listOf(
                navArgument("phone") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            OtpVerificationScreen(
                phone = phone,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNext = {
                    navController.navigate(Route.Auth.ResetPassword.route) {
                        popUpTo(Route.Auth.OtpVerification.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(Route.Auth.ResetPassword.route) {
            ResetPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Ruta principal Home
        composable(Route.Home.route) {
            HomeScreen(navController = navController)
        }
        
        // Rutas de Profile
        composable(Route.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = {
                    navController.navigate("edit_profile")
                }
            )
        }
        
        composable("edit_profile") {
            val previousBackStackEntry = navController.previousBackStackEntry
            val profileViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.atg.autonexo.features.home.presentation.profile.ProfileViewModel>(previousBackStackEntry!!)
            val profileState = profileViewModel.uiState.collectAsState().value
            
            EditProfileScreen(
                fullName = profileState.userProfile?.fullName ?: "",
                email = profileState.userProfile?.email ?: "",
                phoneNumber = profileState.userProfile?.phoneNumber ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChangePassword = {
                    navController.navigate("new_password")
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("new_password") {
            NewPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack("profile", inclusive = false)
                }
            )
        }
        
        // Rutas de Matching & Booking
        composable(Route.Request.route) {
            RequestListScreen(
                onRequestClick = { requestId ->
                    // TODO: Navegar a detalles del request
                },
                onOfferClick = { requestId ->
                    // TODO: Mostrar dialog de offer
                }
            )
        }
        
        // Rutas de Vehicle & Maintenance
        composable(
            route = Route.Vehicle.Detail.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")
            VehicleDetailScreen(
                vehicleId = vehicleId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}