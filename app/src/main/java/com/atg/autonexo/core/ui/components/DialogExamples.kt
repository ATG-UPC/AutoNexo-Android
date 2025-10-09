package com.atg.autonexo.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * EJEMPLOS DE USO DE LOS COMPONENTES DE DIÁLOGO
 * 
 * Este archivo contiene ejemplos de cómo usar los componentes de diálogo
 * en diferentes situaciones comunes de la aplicación.
 */

// ============================================
// EJEMPLO 1: Diálogo de éxito en Edit Profile
// ============================================
@Composable
fun EditProfileSuccessExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    // Mostrar el diálogo cuando showDialog es true
    if (showDialog) {
        SuccessDialog(
            message = "The profile was edited successfully.",
            onDismiss = {
                showDialog = false
                // Navegar a otra pantalla o actualizar UI
            }
        )
    }
    
    // Para mostrar el diálogo, simplemente cambia showDialog a true
    // showDialog = true
}

// ============================================
// EJEMPLO 2: Diálogo de error en Edit Profile
// ============================================
@Composable
fun EditProfileErrorExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        ErrorDialog(
            message = "There was an error during the process.",
            onDismiss = {
                showDialog = false
            }
        )
    }
}

// ============================================
// EJEMPLO 3: Diálogo de éxito en crear cita
// ============================================
@Composable
fun CreateAppointmentSuccessExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        SuccessDialog(
            title = "Success!",
            message = "Your appointment has been created successfully.",
            onDismiss = {
                showDialog = false
                // Navegar a la pantalla de citas
            }
        )
    }
}

// ============================================
// EJEMPLO 4: Diálogo de error en login
// ============================================
@Composable
fun LoginErrorExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        ErrorDialog(
            message = "Invalid email or password. Please try again.",
            onDismiss = {
                showDialog = false
            }
        )
    }
}

// ============================================
// EJEMPLO 5: Diálogo de éxito en registro
// ============================================
@Composable
fun RegisterSuccessExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        SuccessDialog(
            message = "Your account has been created successfully!",
            onDismiss = {
                showDialog = false
                // Navegar a login o dashboard
            }
        )
    }
}

// ============================================
// EJEMPLO 6: Diálogo personalizado con advertencia
// ============================================
@Composable
fun WarningDialogExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        CustomDialog(
            icon = Icons.Default.Warning,
            iconBackgroundColor = Color(0xFFFFF3E0),
            iconTint = Color(0xFFFF9800),
            title = "Warning",
            message = "Are you sure you want to proceed with this action?",
            onDismiss = {
                showDialog = false
            },
            buttonText = "Continue"
        )
    }
}

// ============================================
// EJEMPLO 7: Diálogo personalizado con información
// ============================================
@Composable
fun InfoDialogExample() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        CustomDialog(
            icon = Icons.Default.Info,
            iconBackgroundColor = Color(0xFFE3F2FD),
            iconTint = Color(0xFF2196F3),
            title = "Information",
            message = "This feature is currently in development.",
            onDismiss = {
                showDialog = false
            },
            buttonText = "Got it"
        )
    }
}

// ============================================
// EJEMPLO 8: Uso completo en un ViewModel/Screen
// ============================================
@Composable
fun CompleteEditProfileExample(
    onSaveProfile: (name: String, email: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Función para validar y guardar
    fun validateAndSave(name: String, email: String) {
        when {
            name.isBlank() -> {
                errorMessage = "Name cannot be empty."
                showErrorDialog = true
            }
            email.isBlank() -> {
                errorMessage = "Email cannot be empty."
                showErrorDialog = true
            }
            !email.contains("@") -> {
                errorMessage = "Please enter a valid email address."
                showErrorDialog = true
            }
            else -> {
                // Todos los campos son válidos
                try {
                    onSaveProfile(name, email)
                    showSuccessDialog = true
                } catch (e: Exception) {
                    errorMessage = "There was an error during the process."
                    showErrorDialog = true
                }
            }
        }
    }
    
    // Diálogo de éxito
    if (showSuccessDialog) {
        SuccessDialog(
            message = "The profile was edited successfully.",
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }
    
    // Diálogo de error
    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
            }
        )
    }
    
    // Aquí va el resto de tu UI...
}

/**
 * GUÍA RÁPIDA DE USO:
 * 
 * 1. Importa el componente en tu pantalla:
 *    import com.atg.autonexo.core.ui.components.SuccessDialog
 *    import com.atg.autonexo.core.ui.components.ErrorDialog
 * 
 * 2. Crea un estado para controlar cuándo mostrar el diálogo:
 *    var showDialog by remember { mutableStateOf(false) }
 * 
 * 3. Muestra el diálogo condicionalmente:
 *    if (showDialog) {
 *        SuccessDialog(
 *            message = "Tu mensaje aquí",
 *            onDismiss = { showDialog = false }
 *        )
 *    }
 * 
 * 4. Cambia showDialog a true cuando necesites mostrar el diálogo:
 *    showDialog = true
 * 
 * COMPONENTES DISPONIBLES:
 * 
 * - SuccessDialog: Para mostrar mensajes de éxito (check verde)
 * - ErrorDialog: Para mostrar mensajes de error (X roja)
 * - CustomDialog: Para diálogos personalizados con otros íconos y colores
 * 
 * PARÁMETROS PERSONALIZABLES:
 * 
 * - title: Cambia el título (por defecto "Success!" o "Error")
 * - message: El mensaje a mostrar (obligatorio)
 * - onDismiss: Qué hacer cuando se presiona OK
 * - buttonText: Cambia el texto del botón (por defecto "OK")
 */

