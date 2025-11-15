package com.example.appresina.repository

import com.example.appresina.model.Producto
import com.example.appresina.remote.RetrofitInstance

class ProductosRepository {

    suspend fun getProductos(): List<Producto> {
        return RetrofitInstance.api.getProductos()

    }
}