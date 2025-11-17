package com.example.appresina.remote

import com.example.appresina.model.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/productos")
    suspend fun getProductos(): List<Producto>

    @GET("/productos/{id}")
    suspend fun getProductoById(@Path("id") id: Int): Producto

    @GET("/productos")
    suspend fun getProductosPorCategoria(@Query("categoria") categoria: String): List<Producto>

    @POST("/productos")
    suspend fun createProducto(@Body producto: Producto): Response<Producto>

    @PUT("/productos/{id}")
    suspend fun updateProducto(@Path("id") id: Int, @Body producto: Producto): Response<Producto>

    @PATCH("/productos/{id}")
    suspend fun patchProducto(@Path("id") id: Int, @Body updates: Map<String, Any>): Response<Producto>

    @DELETE("/productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Int): Response<Unit>
}