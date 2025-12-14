package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appresina.data.AuthRepository
import com.example.appresina.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private val _fechaNacimiento = MutableStateFlow("")
    val fechaNacimiento: StateFlow<String> = _fechaNacimiento.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    // Common State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        sessionManager.isLoggedIn
            .onEach { _isAuthenticated.value = it }
            .launchIn(viewModelScope)
    }

    fun actualizarEmailLogin(email: String) { _emailLogin.value = email }
    fun actualizarContrasenaLogin(contrasena: String) { _contrasenaLogin.value = contrasena }
    fun actualizarNombreRegistro(nombre: String) { _nombreRegistro.value = nombre }
    fun actualizarEmailRegistro(email: String) { _emailRegistro.value = email }
    fun actualizarContrasenaRegistro(contrasena: String) { _contrasenaRegistro.value = contrasena }
    fun actualizarConfirmarContrasenaRegistro(contrasena: String) { _confirmarContrasenaRegistro.value = contrasena }
    fun actualizarFechaNacimiento(fecha: String) { _fechaNacimiento.value = fecha }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.login(_emailLogin.value, _contrasenaLogin.value)
            result.onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun registrarUsuario() {
        viewModelScope.launch {
            if (_contrasenaRegistro.value != _confirmarContrasenaRegistro.value) {
                _errorMessage.value = "Las contraseñas no coinciden"
                return@launch
            }
            val fechaNacimientoLong = stringToDateLong(_fechaNacimiento.value)
            if (fechaNacimientoLong == null || !esMayorDeEdad(fechaNacimientoLong)) {
                _errorMessage.value = "Fecha inválida o no eres mayor de 18 años"
                return@launch
            }
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.registrarUsuario(
                _nombreRegistro.value,
                _emailRegistro.value,
                _contrasenaRegistro.value,
                fechaNacimientoLong
            )
            result.onSuccess { _registrationSuccess.value = true }.onFailure { _errorMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun onRegistrationHandled() { _registrationSuccess.value = false }
    fun logout() { viewModelScope.launch { authRepository.logout() } }
    fun limpiarError() { _errorMessage.value = null }

    private fun esMayorDeEdad(fechaNacimiento: Long): Boolean {
        val calNacimiento = Calendar.getInstance().apply { timeInMillis = fechaNacimiento }
        val calHoy = Calendar.getInstance()
        var edad = calHoy.get(Calendar.YEAR) - calNacimiento.get(Calendar.YEAR)
        if (calHoy.get(Calendar.DAY_OF_YEAR) < calNacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }
        return edad >= 18
    }

    private fun stringToDateLong(dateStr: String): Long? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }
}