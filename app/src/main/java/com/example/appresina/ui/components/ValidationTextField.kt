package com.example.appresina.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null,
    isValid: Boolean = false
) {
    // Animación para el estado de validación
    val scale by animateFloatAsState(
        targetValue = if (isError) 1.02f else 1f,
        animationSpec = tween(200)
    )

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            keyboardOptions = keyboardOptions,
            maxLines = maxLines,
            isError = isError,
            trailingIcon = {
                when {
                    isValid && !isError -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Válido",
                            tint = Color.Green
                        )
                    }
                    isError -> {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            supportingText = if (errorMessage != null) {
                {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) 
                    MaterialTheme.colorScheme.error 
                else if (isValid) 
                    Color.Green 
                else 
                    MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError) 
                    MaterialTheme.colorScheme.error 
                else if (isValid) 
                    Color.Green 
                else 
                    MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer
            )
        )
    }
}
