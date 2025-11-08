package com.example.appresina.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appresina.model.Producto
import com.example.appresina.ui.components.RatingBar
import com.example.appresina.ui.components.RatingDisplay
import com.example.appresina.ui.components.CommentList
import com.example.appresina.viewmodel.ProductViewModel
import com.example.appresina.viewmodel.ProductViewModelFactory
import com.example.appresina.data.*
import androidx.compose.ui.platform.LocalContext
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productoId: Int,
    onNavigateBack: () -> Unit,
    onEditProduct: (Producto) -> Unit,
    viewModel: ProductViewModel = viewModel(
        factory = run {
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val valoracionRepository = ValoracionRepository(
                db.valoracionDao(),
                db.usuarioDao()
            )
            val favoritoRepository = FavoritoRepository(
                db.favoritoDao(),
                db.estadisticaProductoDao()
            )
            val estadisticaRepository = EstadisticaProductoRepository(db.estadisticaProductoDao())
            val productoRepository = ProductoRepository(
                db.productoDao(),
                valoracionRepository,
                favoritoRepository,
                estadisticaRepository
            )
            ProductViewModelFactory(
                productoRepository,
                valoracionRepository,
                favoritoRepository
            )
        }
    )
) {
    var isAnimating by remember { mutableStateOf(false) }
    var mostrarDialogValoracion by remember { mutableStateOf(false) }
    var calificacionSeleccionada by remember { mutableStateOf(0) }
    var comentarioValoracion by remember { mutableStateOf("") }
    
    val productoActual by viewModel.productoActual.collectAsState()
    val valoraciones by viewModel.valoraciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Cargar producto al iniciar
    LaunchedEffect(productoId) {
        viewModel.obtenerProductoPorId(productoId)
        isAnimating = true
    }
    
    // Animación de entrada
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0.9f,
        animationSpec = tween(600)
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(600)
    )

    val producto = productoActual ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Producto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.toggleFavorito(producto.id)
                    }) {
                        Icon(
                            imageVector = if (producto.esFavorito) 
                                Icons.Default.Favorite 
                            else 
                                Icons.Default.FavoriteBorder,
                            contentDescription = if (producto.esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (producto.esFavorito) Color(0xFFFF1744) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* TODO: Implementar compartir */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir"
                        )
                    }
                    IconButton(onClick = { onEditProduct(producto) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .scale(scale)
                .alpha(alpha)
        ) {
            // Imagen del producto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (producto.imagenUrl.isNotEmpty()) {
                        // Aquí cargarías la imagen real
                        Text(
                            text = "Imagen del Producto",
                            style = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        Text(
                            text = "Sin imagen disponible",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Información del producto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre y tipo con rating
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = producto.tipo,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (producto.valoracionPromedio > 0) {
                            RatingDisplay(
                                rating = producto.valoracionPromedio,
                                totalReviews = producto.cantidadValoraciones,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Estadísticas del producto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Estadísticas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                icon = Icons.Default.Visibility,
                                label = "Vistas",
                                value = producto.vistas.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatItem(
                                icon = Icons.Default.Favorite,
                                label = "Favoritos",
                                value = producto.cantidadFavoritos.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatItem(
                                icon = Icons.Default.Star,
                                label = "Reseñas",
                                value = producto.cantidadValoraciones.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Precio y cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Precio",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(producto.precio),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (producto.cantidad > 0) 
                                MaterialTheme.colorScheme.tertiaryContainer
                            else 
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Stock",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (producto.cantidad > 0) 
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                else 
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${producto.cantidad} unidades",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (producto.cantidad > 0) 
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                else 
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Descripción
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = producto.descripcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Sección de Valoraciones
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Valoraciones y Reseñas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = { mostrarDialogValoracion = true }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar valoración",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Agregar")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        CommentList(
                            valoraciones = valoraciones,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Información adicional
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información Adicional",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fecha de creación:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .format(java.util.Date(producto.fechaCreacion)),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Estado:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (producto.disponible) "Disponible" else "No disponible",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (producto.disponible) Color.Green else Color.Red
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog para agregar valoración
    if (mostrarDialogValoracion) {
        AlertDialog(
            onDismissRequest = { 
                mostrarDialogValoracion = false
                calificacionSeleccionada = 0
                comentarioValoracion = ""
            },
            title = { Text("Agregar Valoración") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Text("Calificación:")
                    RatingBar(
                        rating = if (calificacionSeleccionada > 0) calificacionSeleccionada.toDouble() else 0.0,
                        enabled = true,
                        onRatingChange = { calificacionSeleccionada = it },
                        maxRating = 5
                    )
                    
                    OutlinedTextField(
                        value = comentarioValoracion,
                        onValueChange = { comentarioValoracion = it },
                        label = { Text("Comentario (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (calificacionSeleccionada > 0) {
                            viewModel.agregarValoracion(
                                productoId = producto.id,
                                calificacion = calificacionSeleccionada,
                                comentario = comentarioValoracion
                            )
                            mostrarDialogValoracion = false
                            calificacionSeleccionada = 0
                            comentarioValoracion = ""
                        }
                    },
                    enabled = calificacionSeleccionada > 0
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    mostrarDialogValoracion = false
                    calificacionSeleccionada = 0
                    comentarioValoracion = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
