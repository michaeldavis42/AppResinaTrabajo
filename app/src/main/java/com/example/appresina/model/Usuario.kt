package com.example.appresina.model

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password_hash: String,
    val fechaNacimiento: Long = 0,
    val avatarUrl: String = "",
    val biografia: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val esCreador: Boolean = false
)
