package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appresina.data.AuthRepository
import com.example.appresina.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Login State
    private val _emailLogin = MutableStateFlow("")
    val emailLogin: StateFlow<String> = _emailLogin.asStateFlow()

    private val _contrasenaLogin = MutableStateFlow("")
    val contrasenaLogin: StateFlow<String> = _contrasenaLogin.asStateFlow()

    // Register State
    private val _nombreRegistro = MutableStateFlow("")
    val nombreRegistro: StateFlow<String> = _nombreRegistro.asStateFlow()

    private val _emailRegistro = MutableStateFlow("")
    val emailRegistro: StateFlow<String> = _emailRegistro.asStateFlow()

    private val _contrasenaRegistro = MutableStateFlow("")
    val contrasenaRegistro: StateFlow<String> = _contrasenaRegistro.asStateFlow()

    private val _confirmarContrasenaRegistro = MutableStateFlow("")
    val confirmarContrasenaRegistro: StateFlow<String> = _confirmarContrasenaRegistro.asStateFlow()

    // Common State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        viewModelScope.launch {
            _isAuthenticated.value = sessionManager.isLoggedIn.first()
        }
    }

    fun actualizarEmailLogin(email: String) {
        _emailLogin.value = email
    }

    fun actualizarContrasenaLogin(contrasena: String) {
        _contrasenaLogin.value = contrasena
    }

    fun actualizarNombreRegistro(nombre: String) {
        _nombreRegistro.value = nombre
    }

    fun actualizarEmailRegistro(email: String) {
        _emailRegistro.value = email
    }

    fun actualizarContrasenaRegistro(contrasena: String) {
        _contrasenaRegistro.value = contrasena
    }

    fun actualizarConfirmarContrasenaRegistro(contrasena: String) {
        _confirmarContrasenaRegistro.value = contrasena
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.login(_emailLogin.value, _contrasenaLogin.value)
            result.onSuccess {
                _isAuthenticated.value = true
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun registrarUsuario() {
        viewModelScope.launch {
            if (_contrasenaRegistro.value != _confirmarContrasenaRegistro.value) {
                _errorMessage.value = "Las contraseñas no coinciden"
                return@launch
            }
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.registrarUsuario(
                _nombreRegistro.value,
                _emailRegistro.value,
                _contrasenaRegistro.value
            )
            result.onSuccess {
                // Después de registrar, intentar hacer login
                val loginResult = authRepository.login(_emailRegistro.value, _contrasenaRegistro.value)
                loginResult.onSuccess {
                    _isAuthenticated.value = true
                }.onFailure {
                    _errorMessage.value = it.message
                }
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isAuthenticated.value = false
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}
