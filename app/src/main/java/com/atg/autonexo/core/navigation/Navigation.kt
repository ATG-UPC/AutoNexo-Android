package com.atg.autonexo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.atg.autonexo.features.iam.presentation.forgotpassword.ForgotPasswordScreen
import com.atg.autonexo.features.iam.presentation.login.LoginScreen
import com.atg.autonexo.features.iam.presentation.otp.OtpVerificationScreen
import com.atg.autonexo.features.iam.presentation.register.RegisterScreen
import com.atg.autonexo.features.iam.presentation.register.RegisterWorkshopScreen
import com.atg.autonexo.features.iam.presentation.resetpassword.ResetPasswordScreen
import com.atg.autonexo.features.workshop.presentation.register.WorkshopRegistrationStep1Screen
import com.atg.autonexo.features.workshop.presentation.register.WorkshopRegistrationStep2Screen
import com.atg.autonexo.features.workshop.presentation.register.WorkshopCodeJoinScreen
import com.atg.autonexo.features.workshop.presentation.detail.WorkshopDetailScreen
import com.atg.autonexo.features.workshop.presentation.detail.WorkshopDetailViewModel
import com.atg.autonexo.features.matchingbooking.presentation.dashboard.DashboardScreen
import com.atg.autonexo.features.iam.presentation.profile.EditProfileScreen
import com.atg.autonexo.features.iam.presentation.profile.NewPasswordScreen
import com.atg.autonexo.features.iam.presentation.profile.ProfileScreen
import com.atg.autonexo.features.iam.presentation.profile.ProfileViewModel
import com.atg.autonexo.features.matchingbooking.presentation.dashboard.DashboardViewModel
import com.atg.autonexo.features.matchingbooking.presentation.request.RequestListScreen
import com.atg.autonexo.features.matchingbooking.presentation.request.RequestDetailScreen
import com.atg.autonexo.features.subscription.presentation.plans.SubscribePremiun
import com.atg.autonexo.features.subscription.presentation.plans.SubscribePro
import com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle.VehicleDetailScreen
import com.atg.autonexo.features.matchingbooking.presentation.offer.OfferListScreen
import androidx.compose.runtime.LaunchedEffect
import com.atg.autonexo.core.data.UserPreferences
import androidx.compose.runtime.remember
import com.atg.autonexo.features.subscription.presentation.plans.SubscribePro
import com.atg.autonexo.features.subscription.presentation.plans.SubscribePremiun

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
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Después del registro, redirigir al login
                    navController.navigate(Route.Auth.Login.route) {
                        popUpTo(Route.Auth.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Route.Auth.WorkshopRegistrationStep1.route) {
            WorkshopRegistrationStep1Screen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToStep2 = {
                    navController.navigate(Route.Auth.WorkshopRegistrationStep2.route)
                }
            )
        }
        
        composable(Route.Auth.WorkshopRegistrationStep2.route) {
            WorkshopRegistrationStep2Screen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.navigate(Route.WorkshopDetailOwner.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Route.Auth.WorkshopCodeJoin.route) {
            WorkshopCodeJoinScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.navigate(Route.WorkshopDetailMember.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Workshop detail (owner)
        composable(Route.WorkshopDetailOwner.route) {
            val vm: WorkshopDetailViewModel = hiltViewModel()
            vm.loadWorkshopAsOwner()
            WorkshopDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onGenerateCode = { vm.showCodeDialog() },
                onEditWorkshop = { 
                    navController.navigate(Route.Auth.WorkshopEditStep1.route)
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        
        // Workshop edit step 1
        composable(Route.Auth.WorkshopEditStep1.route) {
            WorkshopRegistrationStep1Screen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStep2 = {
                    navController.navigate(Route.Auth.WorkshopEditStep2.route)
                }
            )
        }
        
        // Workshop edit step 2
        composable(Route.Auth.WorkshopEditStep2.route) {
            WorkshopRegistrationStep2Screen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Route.WorkshopDetailOwner.route) {
                        popUpTo(Route.WorkshopDetailOwner.route) { inclusive = true }
                    }
                }
            )
        }

        // Workshop detail (member)
        composable(Route.WorkshopDetailMember.route) {
            val vm: WorkshopDetailViewModel = hiltViewModel()
            vm.loadWorkshopAsMember()
            WorkshopDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onGenerateCode = { /* no-op for member */ },
                onEditWorkshop = { /* no-op for member */ },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        
        composable(Route.Auth.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOtp = { phone: String ->
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
            DashboardScreen(navController = navController)
        }
        
        // Workshop entry: decide and redirect safely
        composable(Route.Workshop.route) {
            val userPrefs = remember { dagger.hilt.android.EntryPointAccessors.fromApplication(
                navController.context.applicationContext,
                UserPrefsEntryPoint::class.java
            ).userPreferences() }

            LaunchedEffect(Unit) {
                val isManager = userPrefs.isWorkshopManager()
                val hasWorkshop = userPrefs.hasWorkshop()
                val target = when {
                    isManager && hasWorkshop -> Route.WorkshopDetailOwner.route
                    !isManager && hasWorkshop -> Route.WorkshopDetailMember.route
                    isManager && !hasWorkshop -> Route.Auth.WorkshopRegistrationStep1.route
                    else -> Route.Auth.WorkshopCodeJoin.route
                }
                navController.navigate(target) {
                    popUpTo(Route.Home.route)
                }
            }
        }

        // Offer
        composable(Route.Offer.route) {
            OfferListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route ->
                    when (route) {
                        Route.Home.route,
                        Route.Request.route,
                        Route.Offer.route,
                        Route.Workshop.route,
                        Route.Service.route -> navController.navigate(route) {
                            popUpTo(Route.Home.route)
                        }
                        else -> navController.navigate(route)
                    }
                }
            )
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

        composable(Route.Pro.route){
            SubscribePro(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable(Route.Premiun.route){
            SubscribePremiun(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }


        composable("edit_profile") {
            val previousBackStackEntry = navController.previousBackStackEntry
            val profileViewModel = hiltViewModel<ProfileViewModel>(previousBackStackEntry!!)
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
                    navController.navigate(Route.RequestDetail.createRoute(requestId))
                },
                onOfferClick = { _ ->
                    // TODO: Mostrar dialog de offer
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route ->
                    when (route) {
                        Route.Home.route,
                        Route.Request.route,
                        Route.Offer.route,
                        Route.Workshop.route,
                        Route.Service.route -> navController.navigate(route) {
                            popUpTo(Route.Home.route)
                        }
                        else -> navController.navigate(route)
                    }
                }
            )
        }

        composable(
            route = Route.RequestDetail.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("requestId") ?: ""
            RequestDetailScreen(
                requestId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route ->
                    when (route) {
                        Route.Home.route,
                        Route.Request.route,
                        Route.Offer.route,
                        Route.Workshop.route,
                        Route.Service.route -> navController.navigate(route) {
                            popUpTo(Route.Home.route)
                        }
                        else -> navController.navigate(route)
                    }
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