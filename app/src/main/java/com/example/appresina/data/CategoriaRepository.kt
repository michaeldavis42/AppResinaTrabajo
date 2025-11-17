package com.example.appresina.data

import com.example.appresina.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriaRepository(private val categoriaDao: CategoriaDao) {
    fun obtenerCategoriasPrincipales(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategoriasPrincipales().map { entities ->
            entities.map { it.toCategoria() }
        }
    }

    fun obtenerSubcategorias(padreId: Int): Flow<List<Categoria>> {
        return categoriaDao.obtenerSubcategorias(padreId).map { entities ->
            entities.map { it.toCategoria() }
        }
    }

    suspend fun obtenerCategoriaPorId(id: Int): Categoria? {
        return categoriaDao.obtenerCategoriaPorId(id)?.toCategoria()
    }

    suspend fun obtenerCategoriasPorProducto(productoId: Int): List<Categoria> {
        return categoriaDao.obtenerCategoriasPorProducto(productoId).map { it.toCategoria() }
    }

    suspend fun insertarCategoria(categoria: Categoria): Long {
        return categoriaDao.insertarCategoria(categoria.toEntity())
    }
}

class ValoracionRepository(
    private val valoracionDao: ValoracionDao,
    private val usuarioDao: UsuarioDao
) {
    fun obtenerValoracionesPorProducto(productoId: Int): Flow<List<Valoracion>> {
        return valoracionDao.obtenerValoracionesPorProducto(productoId).map { entities ->
            entities.map { entity ->
                val usuario = usuarioDao.obtenerUsuarioPorId(entity.usuarioId)?.toUsuario()
                entity.toValoracion(usuario)
            }
        }
    }

    suspend fun obtenerPromedioValoracion(productoId: Int): Double {
        return valoracionDao.obtenerPromedioValoracion(productoId) ?: 0.0
    }

    suspend fun obtenerCantidadValoraciones(productoId: Int): Int {
        return valoracionDao.obtenerCantidadValoraciones(productoId)
    }

    suspend fun insertarValoracion(valoracion: Valoracion): Long {
        return valoracionDao.insertarValoracion(valoracion.toEntity())
    }

    suspend fun eliminarValoracion(id: Int) {
        valoracionDao.eliminarValoracion(id)
    }

    suspend fun obtenerValoracionUsuario(productoId: Int, usuarioId: Int): Valoracion? {
        return valoracionDao.obtenerValoracionUsuario(productoId, usuarioId)?.toValoracion()
    }
}

class FavoritoRepository(
    private val favoritoDao: FavoritoDao,
    private val estadisticaDao: EstadisticaProductoDao
) {
    fun obtenerFavoritosPorUsuario(usuarioId: Int): Flow<List<Favorito>> {
        return favoritoDao.obtenerFavoritosPorUsuario(usuarioId).map { entities ->
            entities.map { it.toFavorito() }
        }
    }

    suspend fun esFavorito(usuarioId: Int, productoId: Int): Boolean {
        return favoritoDao.esFavorito(usuarioId, productoId) != null
    }

    suspend fun agregarFavorito(usuarioId: Int, productoId: Int) {
        val favorito = FavoritoEntity(usuarioId = usuarioId, productoId = productoId)
        favoritoDao.insertarFavorito(favorito)
        estadisticaDao.incrementarFavorito(productoId)
    }

    suspend fun eliminarFavorito(usuarioId: Int, productoId: Int) {
        favoritoDao.eliminarFavorito(usuarioId, productoId)
        estadisticaDao.decrementarFavorito(productoId)
    }

    suspend fun obtenerCantidadFavoritos(productoId: Int): Int {
        return favoritoDao.obtenerCantidadFavoritos(productoId)
    }

    fun obtenerCantidadFavoritosFlow(productoId: Int): Flow<Int> {
        return favoritoDao.obtenerCantidadFavoritosFlow(productoId)
    }
}

class EstadisticaProductoRepository(private val estadisticaDao: EstadisticaProductoDao) {
    suspend fun obtenerEstadisticaPorProducto(productoId: Int): EstadisticaProducto? {
        return estadisticaDao.obtenerEstadisticaPorProducto(productoId)?.toEstadistica()
    }

    suspend fun incrementarVista(productoId: Int) {
        estadisticaDao.incrementarVista(productoId)
    }

    suspend fun incrementarDescarga(productoId: Int) {
        estadisticaDao.incrementarDescarga(productoId)
    }

    suspend fun incrementarVenta(productoId: Int) {
        estadisticaDao.incrementarVenta(productoId)
    }

    suspend fun obtenerTopTrending(limit: Int = 20): List<EstadisticaProducto> {
        return estadisticaDao.obtenerTopTrending(limit).map { it.toEstadistica() }
    }

    suspend fun obtenerTopVendidos(limit: Int = 20): List<EstadisticaProducto> {
        return estadisticaDao.obtenerTopVendidos(limit).map { it.toEstadistica() }
    }

    suspend fun obtenerTopVistos(limit: Int = 20): List<EstadisticaProducto> {
        return estadisticaDao.obtenerTopVistos(limit).map { it.toEstadistica() }
    }

    suspend fun obtenerTopFavoritos(limit: Int = 20): List<EstadisticaProducto> {
        return estadisticaDao.obtenerTopFavoritos(limit).map { it.toEstadistica() }
    }

    suspend fun inicializarEstadistica(productoId: Int) {
        val estadistica = EstadisticaProductoEntity(
            productoId = productoId,
            vistas = 0,
            descargas = 0,
            ventas = 0,
            favoritos = 0,
            scoreTrending = 0.0
        )
        estadisticaDao.insertarEstadistica(estadistica)
    }

    suspend fun calcularScoreTrending(productoId: Int, estadistica: EstadisticaProducto) {
        val tiempoTranscurrido = (System.currentTimeMillis() - estadistica.fechaUltimaActualizacion) / (1000 * 60 * 60)
        val score = (estadistica.vistas * 0.3 + estadistica.favoritos * 0.4 + 
                    estadistica.ventas * 0.2 + estadistica.descargas * 0.1) / 
                    (1.0 + tiempoTranscurrido / 24.0)
        estadisticaDao.actualizarScoreTrending(productoId, score)
    }
}

class UsuarioRepository(private val usuarioDao: UsuarioDao) {
    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(id)?.toUsuario()
    }

    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorEmail(email)?.toUsuario()
    }

    suspend fun insertarUsuario(usuario: Usuario): Long {
        return usuarioDao.insertarUsuario(usuario.toEntity())
    }

    fun obtenerCreadores(): Flow<List<Usuario>> {
        return usuarioDao.obtenerCreadores().map { entities ->
            entities.map { it.toUsuario() }
        }
    }
}

private fun CategoriaEntity.toCategoria(): Categoria {
    return Categoria(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        icono = this.icono,
        padreId = this.padreId
    )
}

private fun Categoria.toEntity(): CategoriaEntity {
    return CategoriaEntity(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        icono = this.icono,
        padreId = this.padreId
    )
}

private fun UsuarioEntity.toUsuario(): Usuario {
    return Usuario(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        avatarUrl = this.avatarUrl,
        biografia = this.biografia,
        fechaRegistro = this.fechaRegistro,
        esCreador = this.esCreador
    )
}

private fun Usuario.toEntity(): UsuarioEntity {
    return UsuarioEntity(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        avatarUrl = this.avatarUrl,
        biografia = this.biografia,
        fechaRegistro = this.fechaRegistro,
        esCreador = this.esCreador
    )
}

private fun ValoracionEntity.toValoracion(usuario: Usuario? = null): Valoracion {
    return Valoracion(
        id = this.id,
        productoId = this.productoId,
        usuarioId = this.usuarioId,
        calificacion = this.calificacion,
        comentario = this.comentario,
        fecha = this.fecha,
        usuario = usuario
    )
}

private fun Valoracion.toEntity(): ValoracionEntity {
    return ValoracionEntity(
        id = this.id,
        productoId = this.productoId,
        usuarioId = this.usuarioId,
        calificacion = this.calificacion,
        comentario = this.comentario,
        fecha = this.fecha
    )
}

private fun FavoritoEntity.toFavorito(): Favorito {
    return Favorito(
        id = this.id,
        usuarioId = this.usuarioId,
        productoId = this.productoId,
        fechaAgregado = this.fechaAgregado
    )
}

private fun EstadisticaProductoEntity.toEstadistica(): EstadisticaProducto {
    return EstadisticaProducto(
        productoId = this.productoId,
        vistas = this.vistas,
        descargas = this.descargas,
        ventas = this.ventas,
        favoritos = this.favoritos,
        scoreTrending = this.scoreTrending,
        fechaUltimaActualizacion = this.fechaUltimaActualizacion
    )
}

