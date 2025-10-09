package com.atg.autonexo.features.subscription.presentation.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atg.autonexo.core.ui.components.AuthCurvedHeader
import com.atg.autonexo.core.ui.theme.AppTheme

@Composable
fun SubscribePremiun(){
    var selectedOption by remember { mutableStateOf("Monthly") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AuthCurvedHeader(
            title = "Plans",
            navigationIcon = androidx.compose.material.icons.Icons.Default.ArrowBack,
            onNavigationClick = { /* Acción volver */ }
        )

        PlanTypeSelector(
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val plans = if (selectedOption == "Monthly") {
            listOf(
                Triple("Premiun", "$20", "Perfect for serious businesses looking to expand their services and technical capabilities.\n"),
            )
        } else {
            listOf(
                Triple("Premiun", "$200", "Perfect for serious businesses looking to expand their services and technical capabilities.\n"),
            )
        }

        LazyColumn {
            items(plans) { (name, price, desc) ->
                PlanCard(
                    planName = name,
                    description = desc,
                    price = price,
                    benefits = listOf(
                        "Multi-site management",
                        "Marketing and promotions",
                        "Financial reporting",
                        "APIs and integrations",
                        "Unlimited customers and mechanics",
                        "Priority technical support"
                    ),
                    buttonText = if (name == "Pro") "Join Pro" else "Join Premium",
                    onJoinClick = { /* acción */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuscribePremiunPreview(){
    AppTheme {
        SubscribePremiun()
    }
}