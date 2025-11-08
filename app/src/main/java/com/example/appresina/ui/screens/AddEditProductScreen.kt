package com.example.appresina.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appresina.model.Producto
import com.example.appresina.ui.components.ValidationTextField
import com.example.appresina.ui.components.ImagePicker
import com.example.appresina.viewmodel.ProductViewModel
import com.example.appresina.viewmodel.ProductViewModelFactory
import com.example.appresina.data.*
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    producto: Producto? = null,
    onNavigateBack: () -> Unit,
    onProductSaved: () -> Unit,
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
    val nombre by viewModel.nombreProducto.collectAsState()
    val tipo by viewModel.tipoResina.collectAsState()
    val precio by viewModel.precio.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estados de validación
    var nombreError by remember { mutableStateOf<String?>(null) }
    var tipoError by remember { mutableStateOf<String?>(null) }
    var precioError by remember { mutableStateOf<String?>(null) }
    var cantidadError by remember { mutableStateOf<String?>(null) }
    var descripcionError by remember { mutableStateOf<String?>(null) }

    var showImagePicker by remember { mutableStateOf(false) }

    // Animación del botón
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = tween(200)
    )

    // Cargar datos del producto si estamos editando
    LaunchedEffect(producto) {
        producto?.let {
            viewModel.actualizarNombre(it.nombre)
            viewModel.actualizarTipo(it.tipo)
            viewModel.actualizarPrecio(it.precio.toString())
            viewModel.actualizarCantidad(it.cantidad.toString())
            viewModel.actualizarDescripcion(it.descripcion)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (producto == null) "Agregar Producto" else "Editar Producto",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de imagen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Imagen del producto",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Imagen del Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ImagePicker(
                        onImageSelected = { uri ->
                            // Aquí manejarías la URI de la imagen seleccionada
                            // Por ahora solo cerramos el diálogo
                        }
                    )
                }
            }

            // Formulario
            ValidationTextField(
                value = nombre,
                onValueChange = { 
                    viewModel.actualizarNombre(it)
                    nombreError = null
                },
                label = "Nombre del Producto",
                placeholder = "Ej: Resina Epoxi Premium",
                isError = nombreError != null,
                errorMessage = nombreError,
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown para tipo de resina
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { }
            ) {
                OutlinedTextField(
                    value = tipo,
                    onValueChange = { 
                        viewModel.actualizarTipo(it)
                        tipoError = null
                    },
                    label = { Text("Tipo de Resina") },
                    placeholder = { Text("Selecciona el tipo") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = tipoError != null,
                    supportingText = if (tipoError != null) {
                        { Text(tipoError!!, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
                ExposedDropdownMenu(
                    expanded = false,
                    onDismissRequest = { }
                ) {
                    listOf("Epoxi", "Poliuretano", "Acrílica", "UV", "Otros").forEach { tipoResina ->
                        DropdownMenuItem(
                            text = { Text(tipoResina) },
                            onClick = { 
                                viewModel.actualizarTipo(tipoResina)
                                tipoError = null
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ValidationTextField(
                    value = precio,
                    onValueChange = { 
                        viewModel.actualizarPrecio(it)
                        precioError = null
                    },
                    label = "Precio",
                    placeholder = "0.0",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = precioError != null,
                    errorMessage = precioError,
                    modifier = Modifier.weight(1f)
                )

                ValidationTextField(
                    value = cantidad,
                    onValueChange = { 
                        viewModel.actualizarCantidad(it)
                        cantidadError = null
                    },
                    label = "Cantidad",
                    placeholder = "0",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = cantidadError != null,
                    errorMessage = cantidadError,
                    modifier = Modifier.weight(1f)
                )
            }

            ValidationTextField(
                value = descripcion,
                onValueChange = { 
                    viewModel.actualizarDescripcion(it)
                    descripcionError = null
                },
                label = "Descripción",
                placeholder = "Describe las características del producto...",
                maxLines = 3,
                isError = descripcionError != null,
                errorMessage = descripcionError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            Button(
                onClick = {
                    // Validar campos
                    var hasErrors = false
                    
                    if (nombre.isBlank()) {
                        nombreError = "El nombre es obligatorio"
                        hasErrors = true
                    }
                    
                    if (tipo.isBlank()) {
                        tipoError = "Debes seleccionar un tipo"
                        hasErrors = true
                    }
                    
                    if (precio.isBlank() || precio.toDoubleOrNull() == null || precio.toDouble() <= 0) {
                        precioError = "El precio debe ser mayor a 0"
                        hasErrors = true
                    }
                    
                    if (cantidad.isBlank() || cantidad.toIntOrNull() == null || cantidad.toInt() < 0) {
                        cantidadError = "La cantidad debe ser un número válido"
                        hasErrors = true
                    }
                    
                    if (descripcion.isBlank()) {
                        descripcionError = "La descripción es obligatoria"
                        hasErrors = true
                    }

                    if (!hasErrors) {
                        if (producto == null) {
                            viewModel.agregarProducto()
                        } else {
                            val productoActualizado = producto.copy(
                                nombre = nombre,
                                tipo = tipo,
                                precio = precio.toDouble(),
                                cantidad = cantidad.toInt(),
                                descripcion = descripcion
                            )
                            viewModel.actualizarProducto(productoActualizado)
                        }
                        onProductSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonScale),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (producto == null) "Agregar Producto" else "Actualizar Producto",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
