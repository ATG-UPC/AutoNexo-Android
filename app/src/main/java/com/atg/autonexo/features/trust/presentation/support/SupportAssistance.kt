package com.atg.autonexo.features.trust.presentation.support

import android.telecom.Call
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atg.autonexo.core.ui.components.AuthCurvedHeader
import com.atg.autonexo.core.ui.theme.AppTheme

@Composable
fun Support(
    onNavigateBack: () -> Unit,
    onMailClick: () -> Unit,
    onCallClick: () -> Unit,
    onFaqClick: () -> Unit,
    onTermsClick: () -> Unit,
    onTutorialClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AuthCurvedHeader(
            title = "Support and Assistance",
            navigationIcon = Icons.Default.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones de contacto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ContactOption(
                icon = Icons.Default.Call,
                title = "Call Us",
                subtitle = "Call our support team",
                onClick = onCallClick
            )
            ContactOption(
                icon = Icons.Default.Email,
                title = "Mail Us",
                subtitle = "Mail our support team",
                onClick = onMailClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de opciones
        SupportOptionItem(
            icon = Icons.Default.QuestionAnswer,
            text = "Frequently asked questions",
            onClick = onFaqClick
        )

        SupportOptionItem(
            icon = Icons.Default.Description,
            text = "Terms and conditions",
            onClick = onTermsClick
        )

        SupportOptionItem(
            icon = Icons.Default.School,
            text = "App tutorial",
            onClick = onTutorialClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bloque de video (simulado)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Gray
            )
        }

        Button(
            onClick = onTutorialClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Redirect to video")
        }
    }
}

@Composable
fun SupportOptionItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF3B4B8A))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun ContactOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF3B4B8A), modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Text(subtitle, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun SupportPreview() {
    AppTheme(dynamicColor = false) {
        Support(
            onNavigateBack = {},
            onMailClick = {},
            onCallClick = {},
            onFaqClick = {},
            onTermsClick = {},
            onTutorialClick = {}
        )
    }
}