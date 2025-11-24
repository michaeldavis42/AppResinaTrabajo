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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
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
    
    private var productosInicializados = false

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
                // Suscribirse al flujo y verificar si necesita inicializar productos de ejemplo
                productoRepository.obtenerProductos().collect { lista ->
                    _productos.value = lista
                    
                    // Si no hay productos y aún no se han inicializado, agregar productos de ejemplo
                    if (lista.isEmpty() && !productosInicializados) {
                        inicializarProductosEjemplo()
                        productosInicializados = true
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun inicializarProductosEjemplo() {
        val productosIniciales = listOf(
            Producto(
                id = 0,
                nombre = "Resina Epoxi Premium 1L",
                tipo = "Epoxi",
                precio = 25000.0,
                cantidad = 15,
                descripcion = "Resina epoxi de alta calidad, perfecta para proyectos artísticos y decorativos. Transparente y de secado rápido. Ideal para mesas, encimeras y proyectos de arte.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina UV Transparente 500ml",
                tipo = "UV",
                precio = 18000.0,
                cantidad = 20,
                descripcion = "Resina UV que se cura con luz ultravioleta. Ideal para joyería y pequeños proyectos. Secado instantáneo bajo luz UV.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Acrílica Decorativa",
                tipo = "Acrílica",
                precio = 15000.0,
                cantidad = 25,
                descripcion = "Resina acrílica para acabados decorativos. Disponible en varios colores. Perfecta para manualidades y decoración.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Poliuretano Industrial",
                tipo = "Poliuretano",
                precio = 35000.0,
                cantidad = 10,
                descripcion = "Resina poliuretano resistente para uso industrial y proyectos de gran escala. Alta resistencia y durabilidad.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Kit Iniciación Resina Epoxi",
                tipo = "Epoxi",
                precio = 45000.0,
                cantidad = 8,
                descripcion = "Kit completo con resina epoxi, endurecedor, colorantes y herramientas básicas para empezar. Incluye guía de uso.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina UV Color Amarillo",
                tipo = "UV",
                precio = 20000.0,
                cantidad = 12,
                descripcion = "Resina UV con pigmento amarillo incorporado. Perfecta para proyectos vibrantes y llamativos.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Epoxi Cristal 2L",
                tipo = "Epoxi",
                precio = 48000.0,
                cantidad = 6,
                descripcion = "Resina epoxi de máxima transparencia. Perfecta para proyectos que requieren claridad óptica excepcional.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina UV Color Azul Marino",
                tipo = "UV",
                precio = 22000.0,
                cantidad = 14,
                descripcion = "Resina UV con pigmento azul marino. Ideal para proyectos acuáticos y decorativos modernos.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Epoxi para Ríos 1.5L",
                tipo = "Epoxi",
                precio = 38000.0,
                cantidad = 9,
                descripcion = "Resina epoxi especializada para crear efectos de río en mesas de madera. Alta viscosidad y excelente acabado.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina UV Transparente 1L",
                tipo = "UV",
                precio = 32000.0,
                cantidad = 11,
                descripcion = "Resina UV transparente en presentación de 1 litro. Ideal para proyectos grandes que requieren curación rápida.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Acrílica Color Rojo",
                tipo = "Acrílica",
                precio = 17000.0,
                cantidad = 18,
                descripcion = "Resina acrílica con pigmento rojo intenso. Perfecta para proyectos decorativos y artísticos con color vibrante.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Epoxi con Pigmentos Metálicos",
                tipo = "Epoxi",
                precio = 42000.0,
                cantidad = 7,
                descripcion = "Resina epoxi con efecto metálico. Incluye pigmentos dorados y plateados para acabados de lujo.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina UV para Joyería 250ml",
                tipo = "UV",
                precio = 12000.0,
                cantidad = 22,
                descripcion = "Resina UV especialmente formulada para joyería. Viscosidad baja, perfecta para trabajos delicados.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Poliuretano Transparente",
                tipo = "Poliuretano",
                precio = 40000.0,
                cantidad = 5,
                descripcion = "Resina poliuretano de alta transparencia. Resistente a rayones y desgaste. Ideal para superficies de alto tráfico.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Kit Avanzado Resina Epoxi",
                tipo = "Epoxi",
                precio = 65000.0,
                cantidad = 4,
                descripcion = "Kit completo para proyectos avanzados. Incluye resina, endurecedor, múltiples colorantes, herramientas profesionales y guía avanzada.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina UV Color Verde Esmeralda",
                tipo = "UV",
                precio = 21000.0,
                cantidad = 13,
                descripcion = "Resina UV con pigmento verde esmeralda. Perfecta para proyectos naturales y ecológicos.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Epoxi Resistente al Calor",
                tipo = "Epoxi",
                precio = 55000.0,
                cantidad = 6,
                descripcion = "Resina epoxi especial que resiste altas temperaturas. Ideal para encimeras de cocina y superficies expuestas al calor.",
                usuarioId = 1
            ),
            Producto(
                id = 0,
                nombre = "Resina Acrílica con Brillo",
                tipo = "Acrílica",
                precio = 19000.0,
                cantidad = 16,
                descripcion = "Resina acrílica con efecto brillante incorporado. No requiere acabado adicional, brillo permanente.",
                usuarioId = 1
            )
        )
        
        // Insertar todos los productos en la base de datos
        productosIniciales.forEach { producto ->
            try {
                productoRepository.insertarProducto(producto)
            } catch (e: Exception) {
                // Ignorar errores al insertar productos
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