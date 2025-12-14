package com.example.appresina.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appresina.data.AuthRepository
import com.example.appresina.data.UsuarioRepository
import com.example.appresina.data.SessionManager
import com.example.appresina.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _biografia = MutableStateFlow("")
    val biografia: StateFlow<String> = _biografia.asStateFlow()

    private val _avatarUrl = MutableStateFlow("")
    val avatarUrl: StateFlow<String> = _avatarUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _contrasenaActual = MutableStateFlow("")
    val contrasenaActual: StateFlow<String> = _contrasenaActual.asStateFlow()

    private val _nuevaContrasena = MutableStateFlow("")
    val nuevaContrasena: StateFlow<String> = _nuevaContrasena.asStateFlow()

    private val _confirmarNuevaContrasena = MutableStateFlow("")
    val confirmarNuevaContrasena: StateFlow<String> = _confirmarNuevaContrasena.asStateFlow()

    init {
        sessionManager.isLoggedIn.onEach { isLoggedIn ->
            if (isLoggedIn) {
                cargarUsuarioActual()
            }
        }.launchIn(viewModelScope)
    }

    fun cargarUsuarioActual() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authRepository.getCurrentUser()
            _usuario.value = currentUser
            _nombre.value = currentUser?.nombre ?: ""
            _biografia.value = currentUser?.biografia ?: ""
            _avatarUrl.value = currentUser?.avatarUrl ?: ""
            _isLoading.value = false
        }
    }

    fun actualizarNombre(nombre: String) {
        _nombre.value = nombre
    }

    fun actualizarBiografia(biografia: String) {
        _biografia.value = biografia
    }

    fun actualizarAvatarUrl(avatarUrl: String) {
        _avatarUrl.value = avatarUrl
    }

    fun actualizarAvatarUri(uri: Uri?) {
        _avatarUrl.value = uri?.toString() ?: ""
    }

    fun actualizarContrasenaActual(contrasena: String) {
        _contrasenaActual.value = contrasena
    }

    fun actualizarNuevaContrasena(contrasena: String) {
        _nuevaContrasena.value = contrasena
    }

    fun actualizarConfirmarNuevaContrasena(contrasena: String) {
        _confirmarNuevaContrasena.value = contrasena
    }

    fun actualizarPerfil() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val currentUser = _usuario.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        nombre = _nombre.value,
                        biografia = _biografia.value,
                        avatarUrl = _avatarUrl.value
                    )
                    usuarioRepository.actualizarUsuario(updatedUser)
                    _successMessage.value = "Perfil actualizado con éxito"
                    cargarUsuarioActual() // Recargar datos
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun cambiarContrasena() {
        viewModelScope.launch {
            if (_nuevaContrasena.value != _confirmarNuevaContrasena.value) {
                _errorMessage.value = "Las contraseñas no coinciden"
                return@launch
            }
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(password_hash = authRepository.hashPassword(_nuevaContrasena.value))
                    usuarioRepository.actualizarUsuario(updatedUser)
                    _successMessage.value = "Contraseña cambiada con éxito"
                    _contrasenaActual.value = ""
                    _nuevaContrasena.value = ""
                    _confirmarNuevaContrasena.value = ""
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun limpiarSuccess() {
        _successMessage.value = null
    }
}
