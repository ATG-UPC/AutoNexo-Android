package com.atg.autonexo.features.home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

// Colores del diseño
private val HeaderGradientTop = Color(0xFF1E2B36)
private val HeaderGradientBottom = Color(0xFF253442)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF555555)
private val TextTertiary = Color(0xFF767676)
private val TextLight = Color(0xFFD5D5D5)
private val CardBackground = Color(0xFFFFFFFF)
private val ButtonNavy = Color(0xFF1F2D40)
private val ButtonRed = Color(0xFF8C1C1C)
private val StatusPending = Color(0xFFFFC107)
private val StatusDone = Color(0xFF4CAF50)
private val IconGray = Color(0xFFA7A7A7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Navegación que inyecta el workshopId cuando se va a Pago
    val navigateWithWorkshop: (String) -> Unit = { route ->
        if (route == BottomNavItem.Payment.route) {
            val workshopId = uiState.workshop?.id

            if (workshopId != null) {
                onNavigate(
                    com.atg.autonexo.core.navigation.Route.Payment.PaymentScreen
                        .createRoute(workshopId)
                )
            } else {
                onNavigate(BottomNavItem.WorkshopRegistration.route)
            }
        } else {
            onNavigate(route)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 1. Add ModalDrawerSheet here to handle the shape and background
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                NavigationDrawerContent(
                    userEmail = uiState.userEmail,
                    onLogoutClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        viewModel.logout(onLogout)
                    },
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onNavigate = navigateWithWorkshop
                )
            }
        },
        scrimColor = Color.Black.copy(alpha = 0.5f)
        // 2. REMOVED: drawerShape = RoundedCornerShape(...)
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = navigateWithWorkshop
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // <--- ADD THIS LINE
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con gradiente
                HomeHeader(
                    userName = uiState.userName,
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onNotificationClick = {
                        // TODO: Navegar a notificaciones cuando esté implementado
                    },
                    onRefreshClick = { viewModel.refresh() }
                )

                // Current Appointment
                CurrentAppointmentCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-24).dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mi Workshop
                MiWorkshopCard(
                    workshop = uiState.workshop,
                    hasWorkshop = uiState.hasWorkshop,
                    isLoading = uiState.isLoading,
                    onNavigate = onNavigate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // My Requests
                MyRequestsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String?,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(HeaderGradientTop, HeaderGradientBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Top bar con iconos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )

                // Logo centrado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Autonexo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Quick Service",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = Color(0xFFFF6B6B)
                    )
                }

                // Iconos de acción
                Row {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Saludo
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Hello ${userName ?: "User"}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextLight
                )
            }
        }
    }
}

@Composable
private fun CurrentAppointmentCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Current Appointment",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "02/10/205",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "14:00",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = TextSecondary
                    )
                }
            }

            // Detalles
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Maintenance: Tire Change",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextSecondary
                )
                Text(
                    text = "Owner: Sergio Iglesias",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = TextSecondary
                )
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // TODO: Implementar chat cuando esté disponible
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonNavy
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Chat",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        // TODO: Implementar cancelar cita cuando esté disponible
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonRed
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MiWorkshopCard(
    workshop: com.atg.autonexo.features.workshop.domain.models.Workshop?,
    hasWorkshop: Boolean,
    isLoading: Boolean,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi Workshop",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPrimary
                )
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (hasWorkshop && workshop != null) {
                val workshopData = workshop
                Column {
                    Text(
                        text = workshopData.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary
                    )
                    if (workshopData.shortDescription != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = workshopData.shortDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            ),
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = "${workshopData.capabilityTags.size} Tags",
                                    fontSize = 12.sp
                                ) 
                            }
                        )
                        if (workshopData.logoUrl != null) {
                            AssistChip(
                                onClick = { },
                                label = { 
                                    Text(
                                        text = "Logo",
                                        fontSize = 12.sp
                                    ) 
                                }
                            )
                        }
                        if (workshopData.photoUrls.isNotEmpty()) {
                            AssistChip(
                                onClick = { },
                                label = { 
                                    Text(
                                        text = "${workshopData.photoUrls.size} Fotos",
                                        fontSize = 12.sp
                                    ) 
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { 
                            onNavigate("workshop/management/${workshopData.id}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonNavy
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gestionar Taller",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = IconGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No tienes un workshop registrado",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigate(BottomNavItem.WorkshopRegistration.route) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonNavy
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Registrar Workshop",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyRequestsSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "My Requests",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RequestCard(
                status = "Pending",
                statusColor = StatusPending,
                serviceTitle = "Oil Change",
                mechanicName = "Arturo Gonzáles",
                isPending = true
            )

            RequestCard(
                status = "Done",
                statusColor = StatusDone,
                serviceTitle = "Changing Air Filters",
                mechanicName = "Arturo Gonzáles",
                isPending = false
            )

            RequestCard(
                status = "Pending",
                statusColor = StatusPending,
                serviceTitle = "Brake Service",
                mechanicName = "Arturo Gonzáles",
                isPending = true
            )
        }
    }
}

@Composable
private fun RequestCard(
    status: String,
    statusColor: Color,
    serviceTitle: String,
    mechanicName: String,
    isPending: Boolean
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Surface(
                color = statusColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isPending) Icons.Outlined.Schedule else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = statusColor
                    )
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = statusColor
                    )
                }
            }

            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = IconGray
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-8).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = IconGray
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Text(
                    text = serviceTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "By: $mechanicName",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp
                    ),
                    color = TextTertiary
                )
            }
        }
    }
}


private val DrawerBackground = Color(0xFF1E1E1E)
private val DrawerHeaderTop = Color(0xFF1E2B36)
private val DrawerHeaderBottom = Color(0xFF253442)
private val TextWhite = Color(0xFFFFFFFF)
private val LogoRed = Color(0xFFFF6B6B)

@Composable
fun NavigationDrawerContent(
    userEmail: String?,
    onLogoutClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DrawerBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header con gradiente y logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DrawerHeaderTop, DrawerHeaderBottom)
                    )
                )
                .padding(top = 32.dp, bottom = 24.dp)
        ) {

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = LogoRed
                    )
                    Text(
                        text = "N",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = LogoRed
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Autonexo",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = TextWhite
                )
            }
        }
        SideNavigationBar(
            onNavigate = { route ->
                onCloseDrawer()
                onNavigate(route)
            },
            onLogoutClick = {
                onCloseDrawer()
                onLogoutClick()
            }
        )
    }
}
