package com.atg.autonexo.features.matchingbooking.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.atg.autonexo.core.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = uiState.userName,
                onNavigateToProfile = {
                    navController.navigate(com.atg.autonexo.core.navigation.Route.Profile.route)
                },
                onNavigateToPayment = {
                    // TODO: Navegar a pagos
                },
                onNavigateToSupport = {
                    // TODO: Navegar a soporte
                },
                onNavigateToTerms = {
                    // TODO: Navegar a términos
                },
                onNavigateToPrivacy = {
                    // TODO: Navegar a privacidad
                },
                onLogout = {
                    // TODO: Implementar logout
                    navController.navigate("auth/login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onClose = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                BottomNavBar(
                    currentRoute = navController.currentDestination?.route ?: "home",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header con avatar, nombre y acciones
                    HomeHeader(
                        userName = uiState.userName,
                        onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        onNotificationClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Notificaciones")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Current Appointment Card
                    uiState.currentAppointment?.let { appointment ->
                        CurrentAppointmentCard(
                            appointment = appointment,
                            onPostpone = {
                                viewModel.onPostponeAppointment()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Cita pospuesta")
                                }
                            },
                            onCancel = {
                                viewModel.onCancelAppointment()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Cita cancelada")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Schedule Grid
                    ScheduleGrid(
                        selectedMonth = uiState.selectedMonth,
                        selectedYear = uiState.selectedYear,
                        onMonthClick = {
                            viewModel.onMonthClick()
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        Color(0xFF202D36),
                        Color(0xFF2E3C47)
                    )
                ),
                shape = com.atg.autonexo.core.ui.components.BottomArcShape(64.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top row: Avatar, userName y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar y saludo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4682B4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Hello $userName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Welcome back",
                            fontSize = 12.sp,
                            color = Color(0xFFE0E0E0)
                        )
                    }
                }

                // Acciones: Notificaciones y Menú
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logo centrado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Autonexo",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                // TODO: Reemplazar con logo real cuando exista
                // Image(
                //     painter = painterResource(R.drawable.autonexo_logo),
                //     contentDescription = "Autonexo Logo",
                //     modifier = Modifier.height(80.dp)
                // )
            }
        }
    }
}
