package com.example.appresina

import com.example.appresina.model.Producto
import com.example.appresina.remote.ApiService
import com.example.appresina.repository.ProductosRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class TestableProductoRepository(private val testApi: ApiService) : ProductosRepository() {
    override suspend fun getProductos(): List<Producto> {
        return testApi.getProductos()
    }
}

class ProductoRepositoryTest : StringSpec({
    "getProductos() debe retornar una lista de productos simulada" {
        val fakeProductos = listOf(
            Producto(id = 1, nombre = "Título 1", descripcion = "Cuerpo 1", tipo = "Resina Epoxi", precio = 25.99, cantidad = 10, usuarioId = 1),
            Producto(id = 2, nombre = "Título 2", descripcion = "Cuerpo 2", tipo = "Pigmento", precio = 5.50, cantidad = 50, usuarioId = 2)
        )

        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getProductos() } returns fakeProductos

        val repo = TestableProductoRepository(testApi = mockApi)

        runTest {
            val result = repo.getProductos()
            result shouldContainExactly fakeProductos
        }
    }
})
