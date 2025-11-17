package com.example.appresina.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun MainScreen() {
    var modoEspecial by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    val colorFondo by animateColorAsState(
        targetValue = if (modoEspecial) Color(0xFFB2FF59) else Color(0xFFFF5252)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (modoEspecial) "Modo Especial ACTIVADO" else "Modo Especial DESACTIVADO",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                cargando = true
                modoEspecial = !modoEspecial
            }) {
                Text(text = if (modoEspecial) "Desactivar" else "Activar")
            }

            if (cargando) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(40.dp)
                )

                LaunchedEffect(Unit) {
                    delay(1000)
                    cargando = false
                }
            }
        }
    }
}
