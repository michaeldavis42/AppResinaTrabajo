package com.example.appresina.model

data class Producto(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val precio: Double,
    val cantidad: Int,
    val descripcion: String,
    val imagenUrl: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val disponible: Boolean = true,
    val usuarioId: Int,
    // Campos calculados (no se persisten en BD)
    val valoracionPromedio: Double = 0.0,
    val cantidadValoraciones: Int = 0,
    val cantidadFavoritos: Int = 0,
    val vistas: Int = 0,
    val esFavorito: Boolean = false
)