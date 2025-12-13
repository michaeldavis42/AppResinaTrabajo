package com.example.appresina.data

import com.example.appresina.model.Usuario
import java.security.MessageDigest
import kotlin.collections.joinToString
import kotlin.text.format
import kotlin.text.isBlank
import kotlin.text.lowercase
import kotlin.text.matches
import kotlin.text.toByteArray
import kotlin.text.toRegex
import kotlin.text.trim

class AuthRepository(
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager
) {

    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        contrasena: String,
        fechaNacimiento: Long
    ): Result<Usuario> {
        return try {
            if (nombre.isBlank()) {
                return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
            }
            if (email.isBlank() || !isValidEmail(email)) {
                return Result.failure(IllegalArgumentException("Email inválido"))
            }
            if (contrasena.isBlank() || contrasena.length < 6) {
                return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
            }

            val usuarioExistente = usuarioRepository.obtenerUsuarioPorEmail(email)
            if (usuarioExistente != null) {
                return Result.failure(IllegalArgumentException("El email ya está registrado"))
            }

            val contrasenaHash = hashPassword(contrasena)

            val nuevoUsuario = Usuario(
                nombre = nombre.trim(),
                email = email.trim().lowercase(),
                password_hash = contrasenaHash,
                fechaNacimiento = fechaNacimiento,
                esCreador = false
            )

            val userId = usuarioRepository.insertarUsuario(nuevoUsuario)

            val usuarioGuardado = usuarioRepository.obtenerUsuarioPorId(userId.toInt())
                ?: return Result.failure(Exception("Error al crear el usuario"))

            // ¡Ya no iniciamos sesión aquí!
            Result.success(usuarioGuardado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, contrasena: String): Result<Usuario> {
        return try {
            if (email.isBlank() || contrasena.isBlank()) {
                return Result.failure(IllegalArgumentException("Email y contraseña son requeridos"))
            }

            val usuario = usuarioRepository.obtenerUsuarioPorEmail(email.trim().lowercase())
                ?: return Result.failure(IllegalArgumentException("Email o contraseña incorrectos"))

            val contrasenaHash = hashPassword(contrasena)
            if (usuario.password_hash != contrasenaHash) {
                return Result.failure(IllegalArgumentException("Email o contraseña incorrectos"))
            }

            sessionManager.login(usuario.id, usuario.email, usuario.nombre, usuario.esCreador)

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        sessionManager.logout()
    }

    suspend fun isEmailDisponible(email: String): Boolean {
        return usuarioRepository.obtenerUsuarioPorEmail(email.trim().lowercase()) == null
    }

    suspend fun getCurrentUser(): Usuario? {
        val userId = sessionManager.getCurrentUserId() ?: return null
        return usuarioRepository.obtenerUsuarioPorId(userId)
    }

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}