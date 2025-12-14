package com.example.appresina.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appresina.model.Producto
import com.example.appresina.ui.components.ProductoCard
import com.example.appresina.ui.components.SearchBar
import com.example.appresina.viewmodel.ProductoViewModel
import com.example.appresina.viewmodel.ProductoViewModelFactory
import com.example.appresina.viewmodel.TipoFiltro
import com.example.appresina.data.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.appresina.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onNavigateToProductDetail: (Producto) -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
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
    val productos by viewModel.listaProductos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val tipoFiltro by viewModel.tipoFiltro.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()

    var mostrarFiltros by remember { mutableStateOf(false) }
    var mostrarSeguridad by remember { mutableStateOf(SeguridadDialogState.debeMostrarse()) }

    BackHandler {
        onLogout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_resina),
                            contentDescription = "AppResina Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = "AppResina",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filtros"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            SearchBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onSearchChanged = { texto ->
                    viewModel.buscarProductos(texto)
                }
            )

            if (mostrarFiltros) {
                AdvancedFilters(
                    filtroActual = filtroActual,
                    tipoFiltro = tipoFiltro ?: "Todos",
                    onFiltroSelected = { filtro, tipo ->
                        viewModel.aplicarFiltro(filtro, tipo)
                    }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (productos.isEmpty()) {
                CustomerEmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { onNavigateToProductDetail(producto) },
                            onDelete = null, // Los clientes no pueden eliminar productos
                            onFavoriteToggle = { viewModel.toggleFavorito(producto.id) }
                        )
                    }
                }
            }
        }

        errorMessage?.let { error ->
            LaunchedEffect(error) {
                viewModel.limpiarError()
            }
        }
    }

    if (mostrarSeguridad) {
        SeguridadDialog(
            onDismiss = { 
                mostrarSeguridad = false
                SeguridadDialogState.marcarComoVisto()
            }
        )
    }
}

@Composable
fun CustomerEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No hay productos disponibles",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Explora nuestros productos de resina",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

