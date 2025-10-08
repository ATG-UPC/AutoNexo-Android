package com.atg.autonexo.features.iam.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.atg.autonexo.core.ui.theme.AppTypography

@Composable
fun AuthCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    onTermsClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2D3748),
                uncheckedColor = Color(0xFF718096)
            )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        if (onTermsClick != null) {
            val annotatedText = buildAnnotatedString {
                val termsStart = text.indexOf("Terms and Condition")
                
                append(text.substring(0, termsStart))
                
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF4299E1),
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("Terms and Condition")
                }
            }
            
            Text(
                text = annotatedText,
                style = AppTypography.bodyMedium.copy(
                    color = Color(0xFF4A5568)
                ),
                modifier = Modifier.clickable { onTermsClick() }
            )
        } else {
            Text(
                text = text,
                style = AppTypography.bodyMedium.copy(
                    color = Color(0xFF4A5568)
                )
            )
        }
    }
}
