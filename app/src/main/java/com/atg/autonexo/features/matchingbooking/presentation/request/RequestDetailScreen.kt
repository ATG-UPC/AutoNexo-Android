package com.atg.autonexo.features.matchingbooking.presentation.request

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.features.matchingbooking.presentation.request.models.ServiceRequestUi

@Composable
fun RequestDetailScreen(
    requestId: String,
    onNavigateBack: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        color = Color(0xFF2E3C47),
                        shape = BottomArcShape(64.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Request Details",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Imagen del auto: intenta cargar auto1/auto2; si no existen, usa ícono
            val context = LocalContext.current
            val imageRes = run {
                val name = if (requestId.hashCode() % 2 == 0) "auto1" else "auto2"
                val id = context.resources.getIdentifier(name, "drawable", context.packageName)
                id
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (imageRes != 0) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // placeholder; replaced below in list screen too
                            contentDescription = null,
                            tint = Color(0xFF8E8E8E)
                        )
                    }
                }
            }

            // Placeholder de datos del request (se puede conectar al ViewModel luego)
            Spacer(Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Request ID: $requestId", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF202D36))
                Spacer(Modifier.height(8.dp))
                Text(text = "Make: Nissan", color = Color(0xFF4A5568))
                Text(text = "Model: Sentra", color = Color(0xFF4A5568))
                Text(text = "Year: 2018", color = Color(0xFF4A5568))
                Spacer(Modifier.height(12.dp))
                Text(text = "Be careful with the air condition system.", color = Color(0xFF4A5568))
            }
        }
    }
}


