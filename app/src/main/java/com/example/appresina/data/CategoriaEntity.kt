package com.example.appresina.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val icono: String = "",
    val padreId: Int? = null
)

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val password_hash: String,
    val avatarUrl: String = "",
    val biografia: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val esCreador: Boolean = false
)

@Entity(tableName = "valoraciones")
data class ValoracionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val usuarioId: Int,
    val calificacion: Int,
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis()
)

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val productoId: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
)

@Entity(tableName = "estadisticas_producto")
data class EstadisticaProductoEntity(
    @PrimaryKey val productoId: Int,
    val vistas: Int = 0,
    val descargas: Int = 0,
    val ventas: Int = 0,
    val favoritos: Int = 0,
    val scoreTrending: Double = 0.0,
    val fechaUltimaActualizacion: Long = System.currentTimeMillis()
)


@Entity(tableName = "producto_categoria")
data class ProductoCategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val categoriaId: Int
)

