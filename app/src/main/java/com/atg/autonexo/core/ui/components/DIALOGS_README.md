# 🎯 Componentes de Diálogos Reutilizables

## 📋 Descripción

Estos componentes proporcionan diálogos estandarizados para mostrar mensajes de éxito, error y personalizados en toda la aplicación. Se pueden reutilizar en cualquier pantalla cambiando solo el mensaje.

## 📦 Componentes Disponibles

### 1. SuccessDialog ✅
Muestra un diálogo con un check verde para indicar que una operación fue exitosa.

**Parámetros:**
- `title`: Título del diálogo (por defecto "Success!")
- `message`: Mensaje personalizado a mostrar (obligatorio)
- `onDismiss`: Acción al presionar el botón OK
- `buttonText`: Texto del botón (por defecto "OK")

**Ejemplo de uso:**
```kotlin
if (showSuccessDialog) {
    SuccessDialog(
        message = "El perfil se editó correctamente.",
        onDismiss = {
            showSuccessDialog = false
            // Navegar a otra pantalla o actualizar UI
        }
    )
}
```

### 2. ErrorDialog ❌
Muestra un diálogo con una X roja para indicar que ocurrió un error.

**Parámetros:**
- `title`: Título del diálogo (por defecto "Error")
- `message`: Mensaje personalizado a mostrar (obligatorio)
- `onDismiss`: Acción al presionar el botón OK
- `buttonText`: Texto del botón (por defecto "OK")

**Ejemplo de uso:**
```kotlin
if (showErrorDialog) {
    ErrorDialog(
        message = "Hubo un error durante el proceso.",
        onDismiss = {
            showErrorDialog = false
        }
    )
}
```

### 3. CustomDialog 🎨
Diálogo personalizable con cualquier ícono y colores.

**Parámetros:**
- `icon`: Ícono a mostrar
- `iconBackgroundColor`: Color de fondo del círculo del ícono
- `iconTint`: Color del ícono
- `title`: Título del diálogo
- `message`: Mensaje a mostrar
- `onDismiss`: Acción al presionar el botón
- `buttonText`: Texto del botón
- `buttonColor`: Color del botón (opcional)

**Ejemplo de uso:**
```kotlin
if (showWarningDialog) {
    CustomDialog(
        icon = Icons.Default.Warning,
        iconBackgroundColor = Color(0xFFFFF3E0),
        iconTint = Color(0xFFFF9800),
        title = "Advertencia",
        message = "¿Estás seguro de que deseas continuar?",
        onDismiss = { showWarningDialog = false },
        buttonText = "Continuar"
    )
}
```

## 🚀 Implementación Paso a Paso

### Paso 1: Actualizar el UiState
Agrega estados para controlar la visualización de los diálogos:

```kotlin
data class TuPantallaUiState(
    // ... otros campos ...
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String = ""
)
```

### Paso 2: Actualizar el ViewModel
Agrega funciones para mostrar y ocultar los diálogos:

```kotlin
fun saveData() {
    viewModelScope.launch {
        try {
            // Tu lógica de negocio aquí
            val resultado = repository.save(data)
            
            if (resultado.isSuccess) {
                _uiState.update { it.copy(showSuccessDialog = true) }
            } else {
                _uiState.update { 
                    it.copy(
                        showErrorDialog = true,
                        errorMessage = "Error al guardar los datos."
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    showErrorDialog = true,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }
}

fun dismissSuccessDialog() {
    _uiState.update { it.copy(showSuccessDialog = false) }
}

fun dismissErrorDialog() {
    _uiState.update { it.copy(showErrorDialog = false) }
}
```

### Paso 3: Actualizar la Screen
Importa los componentes y muéstralos condicionalmente:

```kotlin
import com.atg.autonexo.core.ui.components.SuccessDialog
import com.atg.autonexo.core.ui.components.ErrorDialog

@Composable
fun TuPantallaScreen(
    viewModel: TuPantallaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Tu UI principal aquí...
    
    // Diálogos al final
    if (uiState.showSuccessDialog) {
        SuccessDialog(
            message = "La operación se completó exitosamente.",
            onDismiss = {
                viewModel.dismissSuccessDialog()
                onNavigateBack() // Opcional: navegar después del éxito
            }
        )
    }
    
    if (uiState.showErrorDialog) {
        ErrorDialog(
            message = uiState.errorMessage,
            onDismiss = {
                viewModel.dismissErrorDialog()
            }
        )
    }
}
```

## 📝 Ejemplos de Uso Común

### Validación de Formularios
```kotlin
fun validateAndSave() {
    when {
        name.isBlank() -> {
            _uiState.update {
                it.copy(
                    showErrorDialog = true,
                    errorMessage = "El nombre no puede estar vacío."
                )
            }
        }
        !email.contains("@") -> {
            _uiState.update {
                it.copy(
                    showErrorDialog = true,
                    errorMessage = "Por favor ingresa un email válido."
                )
            }
        }
        else -> {
            // Guardar datos
            saveData()
        }
    }
}
```

### Operaciones Asíncronas
```kotlin
fun createAppointment() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        try {
            repository.createAppointment(appointmentData)
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    showSuccessDialog = true
                )
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    showErrorDialog = true,
                    errorMessage = "No se pudo crear la cita. Intenta nuevamente."
                )
            }
        }
    }
}
```

### Confirmaciones
```kotlin
fun deleteAccount() {
    viewModelScope.launch {
        try {
            repository.deleteAccount()
            _uiState.update { it.copy(showSuccessDialog = true) }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    showErrorDialog = true,
                    errorMessage = "No se pudo eliminar la cuenta."
                )
            }
        }
    }
}
```

## 🎨 Personalización

### Cambiar Títulos
```kotlin
SuccessDialog(
    title = "¡Genial!",
    message = "Tu operación fue exitosa.",
    onDismiss = { /* ... */ }
)

ErrorDialog(
    title = "Ups...",
    message = "Algo salió mal.",
    onDismiss = { /* ... */ }
)
```

### Cambiar Texto del Botón
```kotlin
SuccessDialog(
    message = "Perfil actualizado correctamente.",
    buttonText = "Continuar",
    onDismiss = { /* ... */ }
)
```

### Diálogo de Advertencia Personalizado
```kotlin
CustomDialog(
    icon = Icons.Default.Warning,
    iconBackgroundColor = Color(0xFFFFF3E0),
    iconTint = Color(0xFFFF9800),
    title = "Advertencia",
    message = "Esta acción no se puede deshacer.",
    buttonText = "Entendido",
    onDismiss = { /* ... */ }
)
```

### Diálogo de Información
```kotlin
CustomDialog(
    icon = Icons.Default.Info,
    iconBackgroundColor = Color(0xFFE3F2FD),
    iconTint = Color(0xFF2196F3),
    title = "Información",
    message = "Esta función estará disponible próximamente.",
    buttonText = "Ok",
    onDismiss = { /* ... */ }
)
```

## 📍 Ubicación de los Archivos

- **Componentes:** `core/ui/components/DialogComponents.kt`
- **Ejemplos:** `core/ui/components/DialogExamples.kt`
- **Implementación real:** `features/iam/presentation/profile/EditProfileScreen.kt`

## 💡 Mejores Prácticas

1. **Siempre usa estados en el UiState** para controlar la visibilidad de los diálogos
2. **Maneja errores específicos** con mensajes claros para el usuario
3. **No cierres el diálogo automáticamente**, deja que el usuario presione OK
4. **Usa el callback onDismiss** para navegación o limpieza de estado
5. **Mantén mensajes concisos** pero informativos
6. **Personaliza títulos y textos de botones** cuando sea apropiado
7. **Usa CustomDialog** para casos especiales que no sean éxito/error

## 🔧 Próximos Pasos

Puedes usar estos componentes en cualquier pantalla de la aplicación:

- ✅ Edit Profile (ya implementado)
- Login
- Registro
- Cambiar contraseña
- Crear cita
- Eliminar cuenta
- Actualizar datos
- Cualquier operación CRUD

## 🐛 Solución de Problemas

### El diálogo no se muestra
- Verifica que el estado sea `true` en el UiState
- Asegúrate de que el condicional `if (uiState.showDialog)` esté correctamente colocado

### El diálogo se muestra pero no se cierra
- Verifica que estés llamando a la función `dismiss` en el ViewModel
- Asegúrate de actualizar el estado a `false` en la función dismiss

### El mensaje no se actualiza
- Verifica que estés actualizando `errorMessage` en el UiState
- Asegúrate de usar `_uiState.update { it.copy(errorMessage = "nuevo mensaje") }`

## 📞 Soporte

Si tienes preguntas o necesitas ayuda, revisa los ejemplos en `DialogExamples.kt` o consulta la implementación en `EditProfileScreen.kt`.

