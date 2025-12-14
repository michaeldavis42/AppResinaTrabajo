package com.example.appresina.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert
    suspend fun insertarProducto(producto: ProductoEntity): Long

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos ORDER BY fechaCreacion DESC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity?

    @Query("SELECT * FROM productos WHERE tipo = :tipo ORDER BY fechaCreacion DESC")
    fun obtenerProductosPorTipo(tipo: String): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE disponible = 1 ORDER BY fechaCreacion DESC")
    fun obtenerProductosDisponibles(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos ORDER BY fechaCreacion DESC LIMIT :limit")
    suspend fun obtenerProductosMasRecientes(limit: Int): List<ProductoEntity>

    @Query("DELETE FROM productos")
    suspend fun eliminarTodosLosProductos()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Transaction
    suspend fun eliminarYReemplazar(productos: List<ProductoEntity>) {
        eliminarTodosLosProductos()
        insertarProductos(productos)
    }
}
