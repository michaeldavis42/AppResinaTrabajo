package com.example.appresina.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appresina.data.*
import com.example.appresina.viewmodel.AuthViewModel
import com.example.appresina.viewmodel.AuthViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.appresina.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = run {
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val usuarioRepository = UsuarioRepository(db.usuarioDao())
            val sessionManager = SessionManager(context)
            val authRepository = AuthRepository(usuarioRepository, sessionManager)
            AuthViewModelFactory(authRepository, sessionManager)
        }
    )
) {
    val nombre by viewModel.nombreRegistro.collectAsState()
    val email by viewModel.emailRegistro.collectAsState()
    val contrasena by viewModel.contrasenaRegistro.collectAsState()
    val confirmarContrasena by viewModel.confirmarContrasenaRegistro.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }

    // Navegar a home cuando se autentique
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onRegisterSuccess()
        }
    }

    // Limpiar error cuando cambie algún campo
    LaunchedEffect(nombre, email, contrasena, confirmarContrasena) {
        if (errorMessage != null) {
            viewModel.limpiarError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_resina),
                contentDescription = "AppResina Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )
            
            Text(
                text = "AppResina",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Mensaje de error
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.actualizarNombreRegistro(it) },
                label = { Text("Nombre") },
                placeholder = { Text("Tu nombre completo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Nombre"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null && nombre.isNotBlank()
            )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.actualizarEmailRegistro(it) },
                label = { Text("Email") },
                placeholder = { Text("tu@email.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null && email.isNotBlank()
            )

            // Campo Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { viewModel.actualizarContrasenaRegistro(it) },
                label = { Text("Contraseña") },
                placeholder = { Text("Mínimo 6 caracteres") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Contraseña"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                        Icon(
                            imageVector = if (contrasenaVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (contrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null && contrasena.isNotBlank(),
                supportingText = {
                    if (contrasena.isNotBlank() && contrasena.length < 6) {
                        Text(
                            text = "La contraseña debe tener al menos 6 caracteres",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { viewModel.actualizarConfirmarContrasenaRegistro(it) },
                label = { Text("Confirmar Contraseña") },
                placeholder = { Text("Repite tu contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirmar Contraseña"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                        Icon(
                            imageVector = if (confirmarContrasenaVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmarContrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null && confirmarContrasena.isNotBlank() && contrasena != confirmarContrasena,
                supportingText = {
                    if (confirmarContrasena.isNotBlank() && contrasena != confirmarContrasena) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            // Botón Registro
            Button(
                onClick = { viewModel.registrarUsuario() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                enabled = !isLoading && 
                        nombre.isNotBlank() && 
                        email.isNotBlank() && 
                        contrasena.isNotBlank() && 
                        confirmarContrasena.isNotBlank() &&
                        contrasena.length >= 6 &&
                        contrasena == confirmarContrasena,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Registrarse",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "o",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            // Botón Login
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

