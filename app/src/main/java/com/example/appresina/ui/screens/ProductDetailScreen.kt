package com.example.appresina.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.appresina.data.*
import com.example.appresina.model.Producto
import com.example.appresina.ui.components.CommentList
import com.example.appresina.ui.components.RatingBar
import com.example.appresina.ui.components.RatingDisplay
import com.example.appresina.viewmodel.ProductoViewModel
import com.example.appresina.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productoId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Producto) -> Unit,
    viewModel: ProductoViewModel = viewModel(
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
            ProductoViewModelFactory(
                productoRepository,
                valoracionRepository,
                favoritoRepository
            )
        }
    )
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val sessionManager = SessionManager(context)
    val valoracionRepository = ValoracionRepository(
        db.valoracionDao(),
        db.usuarioDao()
    )
    val favoritoRepository = FavoritoRepository(
        db.favoritoDao(),
        db.estadisticaProductoDao()
    )
    val estadisticaRepository = EstadisticaProductoRepository(db.estadisticaProductoDao())
    
    var producto by remember { mutableStateOf<Producto?>(null) }
    var esFavorito by remember { mutableStateOf(false) }
    var cantidadFavoritos by remember { mutableStateOf(0) }
    val valoraciones by valoracionRepository.obtenerValoracionesPorProducto(productoId)
        .collectAsState(initial = emptyList())
    
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Obtener el usuario actual
    val userId by sessionManager.userId.collectAsState(initial = null)
    
    LaunchedEffect(productoId, userId) {
        coroutineScope.launch {
            val productoRepository = ProductoRepository(
                db.productoDao(),
                valoracionRepository,
                favoritoRepository,
                estadisticaRepository
            )
            val productoObtenido = productoRepository.obtenerProductoPorId(productoId)
            productoObtenido?.let {
                estadisticaRepository.incrementarVista(productoId)
                // Verificar si es favorito
                userId?.let { uid ->
                    esFavorito = favoritoRepository.esFavorito(uid, productoId)
                }
                // Obtener cantidad de favoritos
                cantidadFavoritos = favoritoRepository.obtenerCantidadFavoritos(productoId)
                // Actualizar el producto con el estado de favorito
                producto = it.copy(
                    esFavorito = esFavorito,
                    cantidadFavoritos = cantidadFavoritos
                )
            }
        }
    }
    
    // Función para toggle favorito
    fun toggleFavorito() {
        coroutineScope.launch {
            userId?.let { uid ->
                if (esFavorito) {
                    favoritoRepository.eliminarFavorito(uid, productoId)
                    esFavorito = false
                    cantidadFavoritos = maxOf(0, cantidadFavoritos - 1)
                } else {
                    favoritoRepository.agregarFavorito(uid, productoId)
                    esFavorito = true
                    cantidadFavoritos += 1
                }
                // Actualizar el producto para reflejar el cambio
                producto = producto?.copy(
                    esFavorito = esFavorito,
                    cantidadFavoritos = cantidadFavoritos
                )
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    producto?.let { prod ->
                        // Botón de favoritos (corazón)
                        IconButton(onClick = { toggleFavorito() }) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Outlined.Favorite,
                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (esFavorito) Color(0xFFFF1744) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Botón de editar
                        IconButton(onClick = { onNavigateToEdit(prod) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading && producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Producto no encontrado")
            }
        } else {
            producto?.let { prod ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        // Imagen del producto
                        if (prod.imagenUrl.isNotEmpty()) {
                            AsyncImage(
                                model = Uri.parse(prod.imagenUrl),
                                contentDescription = "Imagen de ${prod.nombre}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin imagen",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Nombre y tipo
                            Column {
                                Text(
                                    text = prod.nombre,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = prod.tipo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Valoración, vistas y favoritos
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RatingDisplay(
                                    rating = prod.valoracionPromedio,
                                    totalReviews = prod.cantidadValoraciones
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Favoritos
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { toggleFavorito() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Outlined.Favorite,
                                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                                modifier = Modifier.size(20.dp),
                                                tint = if (esFavorito) Color(0xFFFF1744) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "$cantidadFavoritos",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    // Vistas
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "Vistas",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${prod.vistas} vistas",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            
                            Divider()
                            
                            // Precio y stock
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Precio",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(prod.precio),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Stock",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${prod.cantidad} unidades",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (prod.cantidad > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            
                            if (!prod.disponible) {
                                Text(
                                    text = "NO DISPONIBLE",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                            
                            Divider()
                            
                            // Botón grande de favoritos
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = { toggleFavorito() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (esFavorito) Color(0xFFFF1744) else MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = if (esFavorito) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Outlined.Favorite,
                                        contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            
                            Divider()
                            
                            // Descripción
                            Column {
                                Text(
                                    text = "Descripción",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = prod.descripcion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            Divider()
                            
                            // Valoraciones/Comentarios
                            Column {
                                Text(
                                    text = "Valoraciones (${prod.cantidadValoraciones})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                CommentList(
                                    valoraciones = valoraciones,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

