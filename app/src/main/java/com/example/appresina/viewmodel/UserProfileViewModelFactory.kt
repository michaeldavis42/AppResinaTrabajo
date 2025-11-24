package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appresina.data.AuthRepository
import com.example.appresina.data.UsuarioRepository
import com.example.appresina.data.SessionManager

class UserProfileViewModelFactory(
    private val usuarioRepository: UsuarioRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserProfileViewModel(usuarioRepository, authRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
