package com.example.appresina.ui.screens

/**
 * Objeto singleton que mantiene el estado del diálogo de seguridad durante la sesión de la app.
 * Se resetea cuando la app se reinicia completamente.
 */
object SeguridadDialogState {
    private var yaMostrado = false
    
    fun debeMostrarse(): Boolean {
        return !yaMostrado
    }
    
    fun marcarComoVisto() {
        yaMostrado = true
    }
    
    fun resetear() {
        yaMostrado = false
    }
}

