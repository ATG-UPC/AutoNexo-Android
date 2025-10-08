package com.atg.autonexo.features.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.atg.autonexo.core.ui.theme.AppTypography

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = AppTypography.bodyMedium.copy(
                    color = Color(0xFF4A5568),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFA0AEC0)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color.Red else Color(0xFF4299E1),
                unfocusedBorderColor = if (isError) Color.Red else Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF1A202C),
                unfocusedTextColor = Color(0xFF1A202C)
            ),
            singleLine = true,
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
                {
                    TextButton(onClick = onTogglePasswordVisibility) {
                        Text(
                            text = if (isPasswordVisible) "Hide" else "Show",
                            style = AppTypography.bodySmall.copy(
                                color = Color(0xFF718096)
                            )
                        )
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError
        )
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = AppTypography.bodySmall.copy(
                    color = Color.Red
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}