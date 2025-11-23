package com.atg.autonexo.features.auth.presentation.emailverification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EmailVerificationScreen(
    email: String,
    viewModel: EmailVerificationViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // Inicializar el email en el ViewModel cuando se carga la pantalla
        if (email.isNotBlank()) {
            viewModel.setEmail(email)
        }
    }

    LaunchedEffect(uiState.isVerificationSuccessful) {
        if (uiState.isVerificationSuccessful) {
            onVerificationSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verifica tu Email",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (email.isNotBlank()) {
            Text(
                text = "Hemos enviado un código de verificación a:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        Text(
            text = "Ingresa el código que recibiste en tu correo electrónico",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.token,
            onValueChange = viewModel::updateToken,
            label = { Text("Código de Verificación") },
            placeholder = { Text("Ej: 123456") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        uiState.message?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.verifyEmail(onVerificationSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && uiState.token.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Verificar Email")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (email.isNotBlank()) {
            TextButton(
                onClick = { viewModel.resendVerification(email) },
                enabled = !uiState.isLoading
            ) {
                Text("Reenviar código de verificación")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Volver a Iniciar Sesión")
        }
    }
}

