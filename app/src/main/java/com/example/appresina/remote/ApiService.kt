package com.example.appresina.remote

import com.example.appresina.model.Producto
import retrofit2.http.GET

interface ApiService {

    @GET("/productos")
    suspend fun getProductos(): List<Producto>


}