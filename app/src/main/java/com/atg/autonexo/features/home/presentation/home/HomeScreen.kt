package com.atg.autonexo.features.home.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.atg.autonexo.core.ui.theme.StatusCancelledColor
import com.atg.autonexo.core.ui.theme.StatusCompletedColor
import com.atg.autonexo.core.ui.theme.StatusPendingColor
import com.atg.autonexo.core.ui.theme.StatusPendingToScheduleColor
import com.atg.autonexo.features.matching.domain.models.Booking
import com.atg.autonexo.features.matching.domain.models.BookingStatus
import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier
import com.atg.autonexo.features.payment.presentation.payment.PaymentViewModel
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
    homeViewModel: HomeViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val paymentUiState by paymentViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    LaunchedEffect(Unit) {
        paymentViewModel.loadSubscription()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                NavigationDrawerContent(
                    userEmail = uiState.userEmail,
                    onLogoutClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        homeViewModel.logout(onLogout)
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
                    .padding(paddingValues)
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
                    /*
                    onNotificationClick = {
                        // TODO: Navegar a notificaciones cuando esté implementado
                    },
                    */
                    onRefreshClick = { homeViewModel.refresh() }
                )

                // Current Plan (reemplaza Current Appointment)
                CurrentPlanCard(
                    payment = paymentUiState.payment,
                    isLoading = paymentUiState.isLoading,
                    onManagePlan = { navigateWithWorkshop(BottomNavItem.Payment.route) },
                    onChangePlan = { navigateWithWorkshop(BottomNavItem.Payment.route) },
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


                MyServicesSection(
                    bookings = uiState.servicesPickedUp,
                    isLoading = uiState.isLoadingServices,
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
    /*
    onNotificationClick: () -> Unit,
     */
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
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )

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

                Row {
                    /*
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                     */
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
private fun CurrentPlanCard(
    payment: Payment?,
    isLoading: Boolean,
    onManagePlan: () -> Unit,
    onChangePlan: () -> Unit,
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
                        imageVector = Icons.Outlined.CreditCard,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Current Plan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = payment?.subscriptionTier?.toReadableName() ?: "No active plan",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = TextPrimary
                        )
                        payment?.expiresAt?.let { expiresAt ->
                            Text(
                                text = "Expires on ${expiresAt.toLocalDate()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            if (payment != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = when (payment.subscriptionTier) {
                            SubscriptionTier.FREE ->
                                "Free access to basic tools while you explore Autonexo."
                            SubscriptionTier.BASIC ->
                                "Integrated payments, price catalog, service reports, up to 50 active clients."
                            SubscriptionTier.PREMIUM ->
                                "Multi-site management, marketing, financial reporting, unlimited clients and mechanics."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusDone.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusDone,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = StatusDone
                                )
                            }
                        }
                    }
                }
            } else if (!isLoading) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You don't have an active subscription yet.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = TextSecondary
                    )
                }
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onManagePlan,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonRed
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (payment != null) "Manage Plan" else "View Plans",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun SubscriptionTier.toReadableName(): String =
    when (this) {
        SubscriptionTier.FREE -> "Free Plan"
        SubscriptionTier.BASIC -> "Basic Plan"
        SubscriptionTier.PREMIUM -> "Premium Plan"
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CarRepair,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "My Workshop",
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
                val rating = workshopData.trustScore ?: 0f
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = workshopData.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = TextPrimary
                            )

                            // Rating (número + estrella)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format("%.1f", rating),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp
                                    ),
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    modifier = Modifier.size(14.dp),
                                    tint = ButtonNavy
                                )
                            }

                            if (workshopData.shortDescription != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = workshopData.shortDescription,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp
                                    ),
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }
                        }

                        if (workshopData.logoUrl != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = workshopData.logoUrl,
                                contentDescription = "Logo del taller",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val hasPhoto = workshopData.photoUrls.isNotEmpty()

                    if (hasPhoto) {
                        AsyncImage(
                            model = workshopData.photoUrls.first(),
                            contentDescription = "Foto del taller",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE4E7EE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Sin foto",
                                    tint = IconGray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No photo yet",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = TextTertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chips de info
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
private fun MyServicesSection(
    bookings: List<Booking>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
)  {
    if (bookings.isEmpty() && !isLoading) {
        return
    }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "My Booking Services Completed",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = TextPrimary,
            )

            if (isLoading) {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        if (!isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                bookings.forEach { booking ->
                    ServiceCard(
                        status = booking.status.toString(),
                        statusColor = if (booking.status == BookingStatus.PICKED_UP) StatusDone else StatusPending,
                        serviceTitle = "Booking #${booking.id}",
                        description = booking.description,
                        isPending = booking.status == BookingStatus.PICKED_UP
                    )
                }
            }
        }



    }
}

@Composable
private fun ServiceCard(
    status: String,
    statusColor: Color,
    serviceTitle: String,
    description: String,
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
                        imageVector = Icons.Default.DateRange,
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
                    imageVector = Icons.Default.DateRange,
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
                    text = description,
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
