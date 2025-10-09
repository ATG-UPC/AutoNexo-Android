package com.atg.autonexo.features.workshop.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atg.autonexo.R
import com.atg.autonexo.core.ui.components.BottomArcShape
import com.atg.autonexo.features.iam.presentation.components.AuthBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WorkshopCodeJoinScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Format code as ###-###-###
    fun formatCode(input: String): String {
        val digitsOnly = input.filter { it.isDigit() }
        return when {
            digitsOnly.length <= 3 -> digitsOnly
            digitsOnly.length <= 6 -> "${digitsOnly.substring(0, 3)}-${digitsOnly.substring(3)}"
            else -> "${digitsOnly.substring(0, 3)}-${digitsOnly.substring(3, 6)}-${digitsOnly.substring(6, minOf(9, digitsOnly.length))}"
        }
    }
    
    fun isValidCode(input: String): Boolean {
        val pattern = """^\d{3}-\d{3}-\d{3}$""".toRegex()
        return pattern.matches(input)
    }
    
    fun onJoin() {
        if (!isValidCode(code)) {
            scope.launch {
                snackbarHostState.showSnackbar("Please enter a valid workshop code")
            }
            return
        }
        
        isLoading = true
        scope.launch {
            try {
                // TODO: Implement actual join workshop logic
                delay(1500)
                
                // Simulate success
                isLoading = false
                // Persistir que ya tiene workshop (miembro)
                // Nota: evitamos inyección aquí; lo haremos vía callback
                onSuccess()
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Failed to join workshop"
                snackbarHostState.showSnackbar(errorMessage ?: "Error")
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF202D36), Color(0xFF2E3C47))
                        ),
                        shape = BottomArcShape(64.dp)
                    )
            ) {
                AuthBackButton(
                    onBackClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                
                Text(
                    text = "Workshop",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title
                Text(
                    text = "Workshop Code",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4682B4),
                    textAlign = TextAlign.Center
                )
                
                // Subtitle
                Text(
                    text = "Complete the label with the single use code of the workshop you work for!",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                // Demo Image
                Image(
                    painter = painterResource(id = R.drawable.workshop_code_demo),
                    contentDescription = "Workshop Code Demo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(vertical = 16.dp),
                    contentScale = ContentScale.Fit
                )
                
                // Note
                Text(
                    text = "Note: Tell the workshop owner to generate the code with the button in the workshop tab",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Code Label
                Text(
                    text = "Code",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4682B4),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Code Input
                OutlinedTextField(
                    value = code,
                    onValueChange = { newValue ->
                        // Only allow digits and format automatically
                        val digitsOnly = newValue.filter { it.isDigit() }
                        if (digitsOnly.length <= 9) {
                            code = formatCode(digitsOnly)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "#XXX-XXX-XXX",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4682B4),
                        unfocusedBorderColor = Color(0xFFD1D5DB),
                        focusedTextColor = Color(0xFF1A202C),
                        unfocusedTextColor = Color(0xFF1A202C)
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        if (code.isNotEmpty()) {
                            IconButton(onClick = { code = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF718096)
                                )
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Join Button
                Button(
                    onClick = { onJoin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4682B4),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    enabled = isValidCode(code) && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Join Workshop",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

