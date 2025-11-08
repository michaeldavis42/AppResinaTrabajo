package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.appresina.model.Producto
import com.example.appresina.model.Valoracion
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository
import com.example.appresina.data.FavoritoRepository

enum class TipoFiltro {
    TODOS,
    MAS_VENDIDOS,
    MAS_POPULARES,
    TRENDING,
    MEJOR_VALORADOS,
    MAS_RECIENTES,
    TIPO_RESINA
}

class ProductViewModel(
    private val repository: ProductoRepository,
    private val valoracionRepository: ValoracionRepository,
    private val favoritoRepository: FavoritoRepository,
    private val usuarioActualId: Int = 1
) : ViewModel() {

    // Lista reactiva de productos
    private val _listaProductos = MutableStateFlow<List<Producto>>(emptyList())
    val listaProductos: StateFlow<List<Producto>> = _listaProductos.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Campos del formulario (para agregar/editar)
    private val _nombreProducto = MutableStateFlow("")
    val nombreProducto: StateFlow<String> = _nombreProducto.asStateFlow()

    private val _tipoResina = MutableStateFlow("")
    val tipoResina: StateFlow<String> = _tipoResina.asStateFlow()

    private val _precio = MutableStateFlow("")
    val precio: StateFlow<String> = _precio.asStateFlow()

    private val _cantidad = MutableStateFlow("")
    val cantidad: StateFlow<String> = _cantidad.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    // Filtros
    private val _filtroActual = MutableStateFlow(TipoFiltro.TODOS)
    val filtroActual: StateFlow<TipoFiltro> = _filtroActual.asStateFlow()

    private val _tipoFiltro = MutableStateFlow("Todos")
    val tipoFiltro: StateFlow<String> = _tipoFiltro.asStateFlow()

    // Búsqueda
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    // Valoraciones del producto actual
    private val _valoraciones = MutableStateFlow<List<Valoracion>>(emptyList())
    val valoraciones: StateFlow<List<Valoracion>> = _valoraciones.asStateFlow()

    // Producto seleccionado actualmente
    private val _productoActual = MutableStateFlow<Producto?>(null)
    val productoActual: StateFlow<Producto?> = _productoActual.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.obtenerProductos().collect { productos ->
                    _listaProductos.value = aplicarBusqueda(productos)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun aplicarFiltro(tipoFiltro: TipoFiltro, tipoResina: String? = null) {
        _filtroActual.value = tipoFiltro
        if (tipoResina != null) {
            _tipoFiltro.value = tipoResina
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val productos = when (tipoFiltro) {
                    TipoFiltro.TODOS -> {
                        if (tipoResina != null && tipoResina != "Todos") {
                            repository.obtenerProductosPorTipo(tipoResina)
                        } else {
                            repository.obtenerProductos()
                        }
                    }
                    TipoFiltro.MAS_VENDIDOS -> {
                        val lista = repository.obtenerProductosTopVendidos()
                        kotlinx.coroutines.flow.flowOf(lista)
                    }
                    TipoFiltro.MAS_POPULARES -> {
                        val lista = repository.obtenerProductosTopVistos()
                        kotlinx.coroutines.flow.flowOf(lista)
                    }
                    TipoFiltro.TRENDING -> {
                        val lista = repository.obtenerProductosTrending()
                        kotlinx.coroutines.flow.flowOf(lista)
                    }
                    TipoFiltro.MEJOR_VALORADOS -> {
                        repository.obtenerProductos().map { lista: List<Producto> ->
                            lista.sortedByDescending { producto: Producto -> producto.valoracionPromedio }
                        }
                    }
                    TipoFiltro.MAS_RECIENTES -> {
                        val lista = repository.obtenerProductosMasRecientes()
                        kotlinx.coroutines.flow.flowOf(lista)
                    }
                    TipoFiltro.TIPO_RESINA -> {
                        if (tipoResina != null && tipoResina != "Todos") {
                            repository.obtenerProductosPorTipo(tipoResina)
                        } else {
                            repository.obtenerProductos()
                        }
                    }
                }

                productos.collect { lista ->
                    _listaProductos.value = aplicarBusqueda(lista)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al filtrar productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filtrarPorTipo(tipo: String) {
        _tipoFiltro.value = tipo
        aplicarFiltro(TipoFiltro.TIPO_RESINA, tipo)
    }

    fun buscarProductos(texto: String) {
        _textoBusqueda.value = texto
        val productosActuales = _listaProductos.value
        _listaProductos.value = aplicarBusqueda(productosActuales)
    }

    private fun aplicarBusqueda(productos: List<Producto>): List<Producto> {
        val texto = _textoBusqueda.value.lowercase().trim()
        return if (texto.isEmpty()) {
            productos
        } else {
            productos.filter {
                it.nombre.lowercase().contains(texto) ||
                it.descripcion.lowercase().contains(texto) ||
                it.tipo.lowercase().contains(texto)
            }
        }
    }

    fun agregarProducto() {
        viewModelScope.launch {
            try {
                if (validarCampos()) {
                    val nuevo = Producto(
                        nombre = _nombreProducto.value,
                        tipo = _tipoResina.value,
                        precio = _precio.value.toDoubleOrNull() ?: 0.0,
                        cantidad = _cantidad.value.toIntOrNull() ?: 0,
                        descripcion = _descripcion.value,
                        usuarioId = usuarioActualId
                    )
                    repository.insertarProducto(nuevo)
                    limpiarCampos()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar producto: ${e.message}"
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                repository.actualizarProducto(producto)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar producto: ${e.message}"
            }
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                repository.eliminarProducto(producto)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar producto: ${e.message}"
            }
        }
    }

    fun obtenerProductoPorId(id: Int) {
        viewModelScope.launch {
            try {
                val producto = repository.obtenerProductoPorId(id)
                if (producto != null) {
                    _productoActual.value = producto
                    // Registrar vista
                    repository.registrarVista(id)
                    // Cargar valoraciones
                    cargarValoraciones(id)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar producto: ${e.message}"
            }
        }
    }

    fun toggleFavorito(productoId: Int) {
        viewModelScope.launch {
            try {
                val esFavorito = favoritoRepository.esFavorito(usuarioActualId, productoId)
                if (esFavorito) {
                    favoritoRepository.eliminarFavorito(usuarioActualId, productoId)
                } else {
                    favoritoRepository.agregarFavorito(usuarioActualId, productoId)
                }
                // Actualizar producto actual si está seleccionado
                _productoActual.value?.let { producto ->
                    if (producto.id == productoId) {
                        obtenerProductoPorId(productoId)
                    }
                }
                // Recargar lista de productos
                aplicarFiltro(_filtroActual.value, _tipoFiltro.value.takeIf { it != "Todos" })
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar favorito: ${e.message}"
            }
        }
    }

    fun agregarValoracion(productoId: Int, calificacion: Int, comentario: String) {
        viewModelScope.launch {
            try {
                val valoracion = Valoracion(
                    productoId = productoId,
                    usuarioId = usuarioActualId,
                    calificacion = calificacion,
                    comentario = comentario
                )
                valoracionRepository.insertarValoracion(valoracion)
                // Recargar producto y valoraciones
                obtenerProductoPorId(productoId)
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar valoración: ${e.message}"
            }
        }
    }

    private fun cargarValoraciones(productoId: Int) {
        viewModelScope.launch {
            try {
                valoracionRepository.obtenerValoracionesPorProducto(productoId).collect { valoraciones ->
                    _valoraciones.value = valoraciones
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar valoraciones: ${e.message}"
            }
        }
    }

    fun actualizarNombre(nombre: String) {
        _nombreProducto.value = nombre
    }

    fun actualizarTipo(tipo: String) {
        _tipoResina.value = tipo
    }

    fun actualizarPrecio(precio: String) {
        _precio.value = precio
    }

    fun actualizarCantidad(cantidad: String) {
        _cantidad.value = cantidad
    }

    fun actualizarDescripcion(descripcion: String) {
        _descripcion.value = descripcion
    }

    private fun validarCampos(): Boolean {
        return _nombreProducto.value.isNotBlank() &&
                _tipoResina.value.isNotBlank() &&
                _precio.value.toDoubleOrNull() != null &&
                _cantidad.value.toIntOrNull() != null &&
                _descripcion.value.isNotBlank()
    }

    fun limpiarCampos() {
        _nombreProducto.value = ""
        _tipoResina.value = ""
        _precio.value = ""
        _cantidad.value = ""
        _descripcion.value = ""
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}

class ProductViewModelFactory(
    private val repository: ProductoRepository,
    private val valoracionRepository: ValoracionRepository,
    private val favoritoRepository: FavoritoRepository,
    private val usuarioActualId: Int = 1
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository, valoracionRepository, favoritoRepository, usuarioActualId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
