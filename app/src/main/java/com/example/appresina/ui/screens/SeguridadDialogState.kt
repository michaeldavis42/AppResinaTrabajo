package com.example.appresina.ui.screens

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

