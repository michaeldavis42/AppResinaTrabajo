package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appresina.data.FavoritoRepository
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository
import com.example.appresina.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TipoFiltro {
    TODOS, TIPO_RESINA, MAS_RECIENTES, MEJOR_VALORADOS, MAS_VENDIDOS, MAS_POPULARES, TRENDING
}

open class ProductoViewModel(
    private val productoRepository: ProductoRepository,
    private val valoracionRepository: ValoracionRepository,
    private val favoritoRepository: FavoritoRepository
) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _tipo = MutableStateFlow("")
    val tipo: StateFlow<String> = _tipo.asStateFlow()

    private val _precio = MutableStateFlow("")
    val precio: StateFlow<String> = _precio.asStateFlow()

    private val _cantidad = MutableStateFlow("")
    val cantidad: StateFlow<String> = _cantidad.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    protected val _productos = MutableStateFlow<List<Producto>>(emptyList())
    private val _busqueda = MutableStateFlow("")
    private val _filtroActual = MutableStateFlow(TipoFiltro.TODOS)
    val filtroActual: StateFlow<TipoFiltro> = _filtroActual.asStateFlow()

    private val _tipoFiltro = MutableStateFlow<String?>("Todos")
    val tipoFiltro: StateFlow<String?> = _tipoFiltro.asStateFlow()

    val listaProductos: StateFlow<List<Producto>> = combine(
        _productos, _busqueda, _filtroActual, _tipoFiltro
    ) { productos, busqueda, filtro, tipoFiltroValue ->
        val filtradosPorBusqueda = if (busqueda.isBlank()) {
            productos
        } else {
            productos.filter { it.nombre.contains(busqueda, ignoreCase = true) }
        }

        when (filtro) {
            TipoFiltro.TODOS -> filtradosPorBusqueda
            TipoFiltro.TIPO_RESINA -> if (tipoFiltroValue != "Todos") filtradosPorBusqueda.filter { it.tipo == tipoFiltroValue } else filtradosPorBusqueda
            TipoFiltro.MAS_RECIENTES -> filtradosPorBusqueda.sortedByDescending { it.fechaCreacion }
            TipoFiltro.MEJOR_VALORADOS -> filtradosPorBusqueda.sortedByDescending { it.valoracionPromedio }
            else -> filtradosPorBusqueda
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())
    
    open val productoList: StateFlow<List<Producto>> = _productos.asStateFlow()

    fun actualizarNombre(nuevoNombre: String) { _nombre.value = nuevoNombre }
    fun actualizarTipo(nuevoTipo: String) { _tipo.value = nuevoTipo }
    fun actualizarPrecio(nuevoPrecio: String) { _precio.value = nuevoPrecio }
    fun actualizarCantidad(nuevaCantidad: String) { _cantidad.value = nuevaCantidad }
    fun actualizarDescripcion(nuevaDescripcion: String) { _descripcion.value = nuevaDescripcion }

    fun agregarProducto() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nuevoProducto = Producto(
                    id = 0,
                    nombre = nombre.value,
                    tipo = tipo.value,
                    precio = precio.value.toDoubleOrNull() ?: 0.0,
                    cantidad = cantidad.value.toIntOrNull() ?: 0,
                    descripcion = descripcion.value,
                    usuarioId = 1
                )
                productoRepository.insertarProducto(nuevoProducto)
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productoRepository.actualizarProducto(producto)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    open fun fetchProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productoRepository.obtenerProductos().collect { lista ->
                    _productos.value = lista
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarProductos(query: String) { _busqueda.value = query }

    fun aplicarFiltro(filtro: TipoFiltro, tipo: String?) {
        _filtroActual.value = filtro
        _tipoFiltro.value = tipo
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            productoRepository.eliminarProducto(producto)
        }
    }

    fun toggleFavorito(productoId: Int) {
        viewModelScope.launch {
            val esFavorito = favoritoRepository.esFavorito(1, productoId)
            if (esFavorito) {
                favoritoRepository.eliminarFavorito(1, productoId)
            } else {
                favoritoRepository.agregarFavorito(1, productoId)
            }
        }
    }

    fun limpiarError() { _errorMessage.value = null }
}