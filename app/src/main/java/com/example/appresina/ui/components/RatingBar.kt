package com.example.appresina.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    onRatingChange: ((Int) -> Unit)? = null,
    enabled: Boolean = false,
    size: Int = 24
) {
    val currentRating = remember(rating) { mutableStateOf(if (rating > 0) rating.toInt() else 0) }
    
    // Sincronizar con el rating externo cuando cambia
    LaunchedEffect(rating) {
        if (rating > 0 && !enabled) {
            currentRating.value = rating.toInt()
        }
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxRating) { index ->
            val starValue = index + 1
            val isSelected = if (enabled) {
                starValue <= currentRating.value
            } else {
                starValue <= rating.toInt() || (starValue == rating.toInt() + 1 && rating % 1 >= 0.5)
            }
            
            IconButton(
                onClick = {
                    if (enabled && onRatingChange != null) {
                        currentRating.value = starValue
                        onRatingChange(starValue)
                    }
                },
                enabled = enabled,
                modifier = Modifier.size((size + 8).dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Estrella $starValue",
                    tint = if (isSelected) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.size(size.dp)
                )
            }
        }
        if (!enabled && rating > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RatingDisplay(
    rating: Double,
    totalReviews: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RatingBar(rating = rating, enabled = false)
        if (totalReviews > 0) {
            Text(
                text = "($totalReviews ${if (totalReviews == 1) "reseña" else "reseñas"})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

