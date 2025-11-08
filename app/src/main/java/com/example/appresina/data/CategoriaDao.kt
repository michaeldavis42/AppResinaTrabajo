package com.example.appresina.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCategoria(categoria: CategoriaEntity): Long

    @Query("SELECT * FROM categorias WHERE padreId IS NULL ORDER BY nombre")
    fun obtenerCategoriasPrincipales(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE padreId = :padreId ORDER BY nombre")
    fun obtenerSubcategorias(padreId: Int): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun obtenerCategoriaPorId(id: Int): CategoriaEntity?

    @Query("SELECT c.* FROM categorias c INNER JOIN producto_categoria pc ON c.id = pc.categoriaId WHERE pc.productoId = :productoId")
    suspend fun obtenerCategoriasPorProducto(productoId: Int): List<CategoriaEntity>
}

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE esCreador = 1 ORDER BY nombre")
    fun obtenerCreadores(): Flow<List<UsuarioEntity>>
}

@Dao
interface ValoracionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarValoracion(valoracion: ValoracionEntity): Long

    @Query("SELECT * FROM valoraciones WHERE productoId = :productoId ORDER BY fecha DESC")
    fun obtenerValoracionesPorProducto(productoId: Int): Flow<List<ValoracionEntity>>

    @Query("SELECT AVG(calificacion) FROM valoraciones WHERE productoId = :productoId")
    suspend fun obtenerPromedioValoracion(productoId: Int): Double?

    @Query("SELECT COUNT(*) FROM valoraciones WHERE productoId = :productoId")
    suspend fun obtenerCantidadValoraciones(productoId: Int): Int

    @Query("SELECT * FROM valoraciones WHERE usuarioId = :usuarioId AND productoId = :productoId LIMIT 1")
    suspend fun obtenerValoracionUsuario(productoId: Int, usuarioId: Int): ValoracionEntity?

    @Query("DELETE FROM valoraciones WHERE id = :id")
    suspend fun eliminarValoracion(id: Int)
}

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarFavorito(favorito: FavoritoEntity): Long

    @Query("SELECT * FROM favoritos WHERE usuarioId = :usuarioId ORDER BY fechaAgregado DESC")
    fun obtenerFavoritosPorUsuario(usuarioId: Int): Flow<List<FavoritoEntity>>

    @Query("SELECT COUNT(*) FROM favoritos WHERE productoId = :productoId")
    suspend fun obtenerCantidadFavoritos(productoId: Int): Int

    @Query("SELECT * FROM favoritos WHERE usuarioId = :usuarioId AND productoId = :productoId LIMIT 1")
    suspend fun esFavorito(usuarioId: Int, productoId: Int): FavoritoEntity?

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun eliminarFavorito(usuarioId: Int, productoId: Int)

    @Query("SELECT COUNT(*) FROM favoritos WHERE productoId = :productoId")
    fun obtenerCantidadFavoritosFlow(productoId: Int): Flow<Int>
}

@Dao
interface EstadisticaProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEstadistica(estadistica: EstadisticaProductoEntity)

    @Query("SELECT * FROM estadisticas_producto WHERE productoId = :productoId")
    suspend fun obtenerEstadisticaPorProducto(productoId: Int): EstadisticaProductoEntity?

    @Query("UPDATE estadisticas_producto SET vistas = vistas + 1, fechaUltimaActualizacion = :timestamp WHERE productoId = :productoId")
    suspend fun incrementarVista(productoId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE estadisticas_producto SET descargas = descargas + 1, fechaUltimaActualizacion = :timestamp WHERE productoId = :productoId")
    suspend fun incrementarDescarga(productoId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE estadisticas_producto SET ventas = ventas + 1, fechaUltimaActualizacion = :timestamp WHERE productoId = :productoId")
    suspend fun incrementarVenta(productoId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE estadisticas_producto SET favoritos = favoritos + 1, fechaUltimaActualizacion = :timestamp WHERE productoId = :productoId")
    suspend fun incrementarFavorito(productoId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE estadisticas_producto SET favoritos = favoritos - 1, fechaUltimaActualizacion = :timestamp WHERE productoId = :productoId")
    suspend fun decrementarFavorito(productoId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM estadisticas_producto ORDER BY scoreTrending DESC LIMIT :limit")
    suspend fun obtenerTopTrending(limit: Int = 20): List<EstadisticaProductoEntity>

    @Query("SELECT * FROM estadisticas_producto ORDER BY ventas DESC LIMIT :limit")
    suspend fun obtenerTopVendidos(limit: Int = 20): List<EstadisticaProductoEntity>

    @Query("SELECT * FROM estadisticas_producto ORDER BY vistas DESC LIMIT :limit")
    suspend fun obtenerTopVistos(limit: Int = 20): List<EstadisticaProductoEntity>

    @Query("SELECT * FROM estadisticas_producto ORDER BY favoritos DESC LIMIT :limit")
    suspend fun obtenerTopFavoritos(limit: Int = 20): List<EstadisticaProductoEntity>

    @Query("UPDATE estadisticas_producto SET scoreTrending = :score WHERE productoId = :productoId")
    suspend fun actualizarScoreTrending(productoId: Int, score: Double)
}

@Dao
interface ProductoCategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRelacion(productoCategoria: ProductoCategoriaEntity)

    @Query("SELECT categoriaId FROM producto_categoria WHERE productoId = :productoId")
    suspend fun obtenerCategoriasPorProducto(productoId: Int): List<Int>

    @Query("SELECT productoId FROM producto_categoria WHERE categoriaId = :categoriaId")
    suspend fun obtenerProductosPorCategoria(categoriaId: Int): List<Int>

    @Query("DELETE FROM producto_categoria WHERE productoId = :productoId")
    suspend fun eliminarCategoriasPorProducto(productoId: Int)
}

