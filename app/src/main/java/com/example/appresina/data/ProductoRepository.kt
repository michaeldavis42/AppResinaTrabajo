package com.example.appresina.data

import com.example.appresina.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

class ProductoRepository(
    private val productoDao: ProductoDao,
    private val valoracionRepository: ValoracionRepository,
    private val favoritoRepository: FavoritoRepository,
    private val estadisticaRepository: EstadisticaProductoRepository,
    private val usuarioActualId: Int = 1 // Usuario por defecto
) {

    fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerProductos().map { entities ->
            entities.map { entity ->
                enriquecerProducto(entity.toProducto())
            }
        }
    }

    fun obtenerProductosPorTipo(tipo: String): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorTipo(tipo).map { entities ->
            entities.map { entity ->
                enriquecerProducto(entity.toProducto())
            }
        }
    }

    fun obtenerProductosDisponibles(): Flow<List<Producto>> {
        return productoDao.obtenerProductosDisponibles().map { entities ->
            entities.map { entity ->
                enriquecerProducto(entity.toProducto())
            }
        }
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        val producto = productoDao.obtenerProductoPorId(id)?.toProducto() ?: return null
        return enriquecerProducto(producto)
    }

    suspend fun obtenerProductosTopVendidos(limit: Int = 20): List<Producto> {
        val estadisticas = estadisticaRepository.obtenerTopVendidos(limit)
        return estadisticas.mapNotNull { estadistica ->
            productoDao.obtenerProductoPorId(estadistica.productoId)?.toProducto()?.let {
                enriquecerProducto(it, estadistica)
            }
        }
    }

    suspend fun obtenerProductosTopVistos(limit: Int = 20): List<Producto> {
        val estadisticas = estadisticaRepository.obtenerTopVistos(limit)
        return estadisticas.mapNotNull { estadistica ->
            productoDao.obtenerProductoPorId(estadistica.productoId)?.toProducto()?.let {
                enriquecerProducto(it, estadistica)
            }
        }
    }

    suspend fun obtenerProductosTrending(limit: Int = 20): List<Producto> {
        val estadisticas = estadisticaRepository.obtenerTopTrending(limit)
        return estadisticas.mapNotNull { estadistica ->
            productoDao.obtenerProductoPorId(estadistica.productoId)?.toProducto()?.let {
                enriquecerProducto(it, estadistica)
            }
        }
    }

    suspend fun obtenerProductosTopFavoritos(limit: Int = 20): List<Producto> {
        val estadisticas = estadisticaRepository.obtenerTopFavoritos(limit)
        return estadisticas.mapNotNull { estadistica ->
            productoDao.obtenerProductoPorId(estadistica.productoId)?.toProducto()?.let {
                enriquecerProducto(it, estadistica)
            }
        }
    }

    suspend fun obtenerProductosMasRecientes(limit: Int = 20): List<Producto> {
        val entities = productoDao.obtenerProductosMasRecientes(limit)
        return entities.map { entity ->
            enriquecerProducto(entity.toProducto())
        }
    }

    suspend fun insertarProducto(producto: Producto): Long {
        val id = productoDao.insertarProducto(producto.toEntity())
        // Inicializar estadísticas para el nuevo producto
        estadisticaRepository.inicializarEstadistica(id.toInt())
        return id
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto.toEntity())
    }

    suspend fun eliminarProducto(producto: Producto) {
        productoDao.eliminarProducto(producto.toEntity())
    }

    suspend fun eliminarTodosLosProductos() {
        productoDao.eliminarTodosLosProductos()
    }

    suspend fun registrarVista(productoId: Int) {
        estadisticaRepository.incrementarVista(productoId)
    }

    private suspend fun enriquecerProducto(
        producto: Producto,
        estadistica: com.example.appresina.model.EstadisticaProducto? = null
    ): Producto {
        val estadisticaActual = estadistica ?: estadisticaRepository.obtenerEstadisticaPorProducto(producto.id) 
            ?: com.example.appresina.model.EstadisticaProducto(productoId = producto.id)
        
        val valoracionPromedio = valoracionRepository.obtenerPromedioValoracion(producto.id)
        val cantidadValoraciones = valoracionRepository.obtenerCantidadValoraciones(producto.id)
        val cantidadFavoritos = favoritoRepository.obtenerCantidadFavoritos(producto.id)
        val esFavorito = favoritoRepository.esFavorito(usuarioActualId, producto.id)

        return producto.copy(
            valoracionPromedio = valoracionPromedio,
            cantidadValoraciones = cantidadValoraciones,
            cantidadFavoritos = cantidadFavoritos,
            vistas = estadisticaActual.vistas,
            esFavorito = esFavorito,
            usuarioId = producto.usuarioId
        )
    }
}

/**
 * Extensión para convertir ProductoEntity a Producto
 */
private fun ProductoEntity.toProducto(): Producto {
    return Producto(
        id = this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        precio = this.precio,
        cantidad = this.cantidad,
        descripcion = this.descripcion,
        imagenUrl = this.imagenUrl,
        fechaCreacion = this.fechaCreacion,
        disponible = this.disponible,
        usuarioId = 1 // Por defecto, se puede obtener del contexto
    )
}

/**
 * Extensión para convertir Producto a ProductoEntity
 */
private fun Producto.toEntity(): ProductoEntity {
    return ProductoEntity(
        id = this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        precio = this.precio,
        cantidad = this.cantidad,
        descripcion = this.descripcion,
        imagenUrl = this.imagenUrl,
        fechaCreacion = this.fechaCreacion,
        disponible = this.disponible
    )
}

