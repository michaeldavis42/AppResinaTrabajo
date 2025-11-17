package com.example.appresina.repository

import com.example.appresina.model.Producto
import com.example.appresina.remote.RetrofitInstance

open class ProductosRepository {

    open suspend fun getProductos(): List<Producto> {
        return RetrofitInstance.api.getProductos()

    }
}