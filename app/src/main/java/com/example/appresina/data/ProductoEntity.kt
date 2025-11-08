package com.example.appresina.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val precio: Double,
    val cantidad: Int,
    val descripcion: String,
    val imagenUrl: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val disponible: Boolean = true
)