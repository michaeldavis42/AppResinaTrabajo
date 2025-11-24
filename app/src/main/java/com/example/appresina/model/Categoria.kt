package com.example.appresina.model

data class Categoria(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val icono: String = "",
    val padreId: Int? = null
)

data class Valoracion(
    val id: Int = 0,
    val productoId: Int,
    val usuarioId: Int,
    val calificacion: Int,
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val usuario: Usuario? = null
)

data class Favorito(
    val id: Int = 0,
    val usuarioId: Int,
    val productoId: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
)

data class EstadisticaProducto(
    val productoId: Int,
    val vistas: Int = 0,
    val descargas: Int = 0,
    val ventas: Int = 0,
    val favoritos: Int = 0,
    val scoreTrending: Double = 0.0,
    val fechaUltimaActualizacion: Long = System.currentTimeMillis()
)
