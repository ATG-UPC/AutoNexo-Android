package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun TermsAndConditionsDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    var isChecked by remember { mutableStateOf(false) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF2E3C47),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Terms and conditions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
                
                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TermSection(
                        number = "1",
                        title = "GENERAL INFORMATION",
                        content = "AutoNexus is a digital platform for third-parties vehicle owners with repair and maintenance needs to connect with workshops, repair, and mechanics. Autonexus platform does not provides services directly. You agree to these terms. If you do not agree to these terms, do not use the platform."
                    )
                    
                    TermSection(
                        number = "2",
                        title = "DEFINITIONS",
                        content = "\"Services\" refers to all services and features made available through the AutoNexus platform. \"Shop\" refers to any business offering repair services through our platform. \"User\" means any person using the platform to request, offer, or manage automotive services."
                    )
                    
                    TermSection(
                        number = "3",
                        title = "REGISTRATION AND ACCOUNT",
                        content = "You must be at least 18 years old users will generally accepted will not be permitted to contact you. You are responsible for keeping your information current, using the platform in accordance with laws, and keeping your account secure."
                    )
                    
                    TermSection(
                        number = "4",
                        title = "PLATFORM SERVICES",
                        content = "Our vehicle owners: AutoNexus allows you to register and manage information about their vehicles, Shops, and services received. For repair Shops: Autonexus allows you to create a shop profile, receive service requests from customers, manage services pricing, communicate with shops, and track the services received."
                    )
                    
                    TermSection(
                        number = "5",
                        title = "PAYMENTS AND BILLING",
                        content = "Prices, payment schedules and financial terms for services will be set by the shop, AutoNexus does not set prices; we only process them when payment methods, and bank transfers. Prices are subject to change between transactions. Prices are subject to change between transactions. All fees are subject to applicable government taxes. Cancellation dispute must be resolved between the parties."
                    )
                    
                    TermSection(
                        number = "6",
                        title = "RESPONSIBILITIES AND WARRANTIES",
                        content = "AutoNexus cannot and does not directly offer or guarantee the availability of repair shops, and it NOT liable for any damage to vehicles, errors or issues (except connection between users)."
                    )
                }
                
                // Footer con checkbox y botón
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF4682B4),
                                uncheckedColor = Color(0xFF9CA3AF)
                            )
                        )
                        
                        Text(
                            text = "Signing in you agree with our Terms and Condition",
                            fontSize = 13.sp,
                            color = Color(0xFF4A5568),
                            lineHeight = 18.sp
                        )
                    }
                    
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4682B4),
                            disabledContainerColor = Color(0xFFCCCCCC)
                        ),
                        enabled = isChecked
                    ) {
                        Text(
                            text = "Register",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TermSection(
    number: String,
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "$number. $title",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A202C)
        )
        
        Text(
            text = content,
            fontSize = 11.sp,
            color = Color(0xFF4A5568),
            lineHeight = 16.sp
        )
    }
}

