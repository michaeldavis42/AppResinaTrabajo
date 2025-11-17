package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appresina.data.FavoritoRepository
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository

class ProductoViewModelFactory(
    private val productoRepository: ProductoRepository,
    private val valoracionRepository: ValoracionRepository,
    private val favoritoRepository: FavoritoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(productoRepository, valoracionRepository, favoritoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}