package com.example.appresina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appresina.model.Producto
import com.example.appresina.repository.ProductosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {

    private val repository = ProductosRepository()

    private val _productoList = MutableStateFlow<List<Producto>>(emptyList())

    val productoList: StateFlow<List<Producto>> = _productoList

    init {
        fetchProductos()
    }

    private fun fetchProductos() {
        viewModelScope.launch {
            try {
                _productoList.value = repository.getProductos()
            } catch (e: Exception) {
                println("Error al obtener los datos: ${e.localizedMessage}")

            }
        }
    }

}