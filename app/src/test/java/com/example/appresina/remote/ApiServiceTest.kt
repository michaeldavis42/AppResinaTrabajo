package com.example.appresina.remote

import com.example.appresina.model.Producto
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getProductos debe retornar una lista de productos cuando la respuesta es 200`() = runTest {
        val productos = listOf(
            Producto(id = 1, nombre = "Producto 1", tipo = "A", precio = 10.0, cantidad = 1, descripcion = "Desc 1", usuarioId = 1),
            Producto(id = 2, nombre = "Producto 2", tipo = "B", precio = 20.0, cantidad = 2, descripcion = "Desc 2", usuarioId = 1)
        )
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(Gson().toJson(productos))

        mockWebServer.enqueue(mockResponse)

        val response = apiService.getProductos()

        assertEquals(2, response.size)
        assertEquals("Producto 1", response[0].nombre)

        val request = mockWebServer.takeRequest()
        assertEquals("/productos", request.path)
    }
}