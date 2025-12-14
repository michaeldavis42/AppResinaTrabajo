package com.example.appresina.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.appresina.data.*
import com.example.appresina.ui.components.ImagePicker
import com.example.appresina.viewmodel.AuthViewModel
import com.example.appresina.viewmodel.AuthViewModelFactory
import com.example.appresina.viewmodel.UserProfileViewModel
import com.example.appresina.viewmodel.UserProfileViewModelFactory
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: UserProfileViewModel = viewModel(
        factory = run {
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val usuarioRepository = UsuarioRepository(db.usuarioDao())
            val sessionManager = SessionManager(context)
            val authRepository = AuthRepository(usuarioRepository, sessionManager)
            UserProfileViewModelFactory(usuarioRepository, authRepository, sessionManager)
        }
    )
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val isCreador by sessionManager.isCreador.collectAsState(initial = false)
    val usuario by viewModel.usuario.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val biografia by viewModel.biografia.collectAsState()
    val avatarUrl by viewModel.avatarUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    val contrasenaActual by viewModel.contrasenaActual.collectAsState()
    val nuevaContrasena by viewModel.nuevaContrasena.collectAsState()
    val confirmarNuevaContrasena by viewModel.confirmarNuevaContrasena.collectAsState()
    
    var contrasenaActualVisible by remember { mutableStateOf(false) }
    var nuevaContrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
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

            // Mensaje de éxito
            if (successMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = successMessage!!,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ){
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = Uri.parse(avatarUrl),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                if (isCreador) {
                    Text(
                        text = "Admin",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            ImagePicker(onImageSelected = { uri -> viewModel.actualizarAvatarUrl(uri?.toString() ?: "") })

            // Información del usuario
            if (usuario != null) {
                Text(
                    text = usuario!!.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 32.dp)
                )
            }

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.actualizarNombre(it) },
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
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Biografía
            OutlinedTextField(
                value = biografia,
                onValueChange = { viewModel.actualizarBiografia(it) },
                label = { Text("Biografía") },
                placeholder = { Text("Cuéntanos sobre ti...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Biografía"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            // Botón Actualizar Perfil
            Button(
                onClick = { 
                    viewModel.actualizarPerfil()
                    viewModel.limpiarSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 24.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Guardar Cambios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Sección Cambiar Contraseña
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cambiar Contraseña",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showChangePassword = !showChangePassword }) {
                    Icon(
                        imageVector = if (showChangePassword) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showChangePassword) "Ocultar" else "Mostrar"
                    )
                }
            }

            if (showChangePassword) {
                // Campo Contraseña Actual
                OutlinedTextField(
                    value = contrasenaActual,
                    onValueChange = { viewModel.actualizarContrasenaActual(it) },
                    label = { Text("Contraseña Actual") },
                    placeholder = { Text("Tu contraseña actual") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    visualTransformation = if (contrasenaActualVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Contraseña Actual"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { contrasenaActualVisible = !contrasenaActualVisible }) {
                            Icon(
                                imageVector = if (contrasenaActualVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (contrasenaActualVisible) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Nueva Contraseña
                OutlinedTextField(
                    value = nuevaContrasena,
                    onValueChange = { viewModel.actualizarNuevaContrasena(it) },
                    label = { Text("Nueva Contraseña") },
                    placeholder = { Text("Mínimo 6 caracteres") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    visualTransformation = if (nuevaContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Nueva Contraseña"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { nuevaContrasenaVisible = !nuevaContrasenaVisible }) {
                            Icon(
                                imageVector = if (nuevaContrasenaVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (nuevaContrasenaVisible) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Confirmar Nueva Contraseña
                OutlinedTextField(
                    value = confirmarNuevaContrasena,
                    onValueChange = { viewModel.actualizarConfirmarNuevaContrasena(it) },
                    label = { Text("Confirmar Nueva Contraseña") },
                    placeholder = { Text("Repite la nueva contraseña") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirmar Nueva Contraseña"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                            Icon(
                                imageVector = if (confirmarContrasenaVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmarContrasenaVisible) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    isError = nuevaContrasena.isNotBlank() && confirmarNuevaContrasena.isNotBlank() && nuevaContrasena != confirmarNuevaContrasena,
                    supportingText = {
                        if (nuevaContrasena.isNotBlank() && confirmarNuevaContrasena.isNotBlank() && nuevaContrasena != confirmarNuevaContrasena) {
                            Text(
                                text = "Las contraseñas no coinciden",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                // Botón Cambiar Contraseña
                Button(
                    onClick = { 
                        viewModel.cambiarContrasena()
                        viewModel.limpiarSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 24.dp),
                    enabled = !isLoading && 
                            contrasenaActual.isNotBlank() && 
                            nuevaContrasena.isNotBlank() && 
                            confirmarNuevaContrasena.isNotBlank() &&
                            nuevaContrasena.length >= 6 &&
                            nuevaContrasena == confirmarNuevaContrasena,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Cambiar Contraseña",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Botón Cerrar Sesión
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onNavigateToLogin()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

