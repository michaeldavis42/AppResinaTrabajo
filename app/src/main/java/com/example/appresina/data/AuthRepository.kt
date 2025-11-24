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

// Repositorio para gestionar autenticación de usuarios
class AuthRepository(
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager
) {

    /**
     * Registra un nuevo usuario
     * @param nombre Nombre del usuario
     * @param email Email del usuario (debe ser único)
     * @param contrasena Contraseña en texto plano (se hashea internamente)
     * @return Resultado del registro: éxito o error
     */
    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        contrasena: String
    ): Result<Usuario> {
        return try {
            // Validar campos
            if (nombre.isBlank()) {
                return Result.failure(kotlin.IllegalArgumentException("El nombre no puede estar vacío"))
            }
            if (email.isBlank() || !isValidEmail(email)) {
                return Result.failure(kotlin.IllegalArgumentException("Email inválido"))
            }
            if (contrasena.isBlank() || contrasena.length < 6) {
                return Result.failure(kotlin.IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
            }

            // Verificar si el email ya existe
            val usuarioExistente = usuarioRepository.obtenerUsuarioPorEmail(email)
            if (usuarioExistente != null) {
                return Result.failure(kotlin.IllegalArgumentException("El email ya está registrado"))
            }

            // Hashear contraseña
            val contrasenaHash = hashPassword(contrasena)

            // Crear nuevo usuario
            val nuevoUsuario = Usuario(
                nombre = nombre.trim(),
                email = email.trim().lowercase(),
                password_hash = contrasenaHash,
                esCreador = false
            )

            // Guardar usuario
            val userId = usuarioRepository.insertarUsuario(nuevoUsuario)

            // Obtener usuario guardado
            val usuarioGuardado = usuarioRepository.obtenerUsuarioPorId(userId.toInt())
                ?: return Result.failure(kotlin.Exception("Error al crear el usuario"))

            Result.success(usuarioGuardado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     * @param email Email del usuario
     * @param contrasena Contraseña en texto plano
     * @return Resultado del login: usuario o error
     */
    suspend fun login(email: String, contrasena: String): Result<Usuario> {
        return try {
            // Validar campos
            if (email.isBlank() || contrasena.isBlank()) {
                return Result.failure(kotlin.IllegalArgumentException("Email y contraseña son requeridos"))
            }

            // Buscar usuario por email
            val usuario = usuarioRepository.obtenerUsuarioPorEmail(email.trim().lowercase())
                ?: return Result.failure(kotlin.IllegalArgumentException("Email o contraseña incorrectos"))

            // Verificar contraseña
            val contrasenaHash = hashPassword(contrasena)
            if (usuario.password_hash != contrasenaHash) {
                return Result.failure(kotlin.IllegalArgumentException("Email o contraseña incorrectos"))
            }

            // Iniciar sesión en SessionManager
            sessionManager.login(usuario.id, usuario.email, usuario.nombre)

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cierra la sesión del usuario actual
    suspend fun logout() {
        sessionManager.logout()
    }

    // Verifica si un email está disponible para registro
    suspend fun isEmailDisponible(email: String): Boolean {
        return usuarioRepository.obtenerUsuarioPorEmail(email.trim().lowercase()) == null
    }

    // Obtiene el usuario actual desde la sesión
    suspend fun getCurrentUser(): Usuario? {
        val userId = sessionManager.getCurrentUserId() ?: return null
        return usuarioRepository.obtenerUsuarioPorId(userId)
    }

    // Hashea una contraseña usando SHA-256
    //  En producción, usar bcrypt o scrypt es más seguro
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    // Valida formato de email básico
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}
