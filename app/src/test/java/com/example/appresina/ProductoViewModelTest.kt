package com.example.appresina

import com.example.appresina.data.FavoritoRepository
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository
import com.example.appresina.model.Producto
import com.example.appresina.viewmodel.ProductoViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest : StringSpec({

    "productoList debe contener los datos esperados después de fetchProductos()" {
        val fakeProductos = listOf(
            Producto(
                id = 1,
                nombre = "Título 1",
                descripcion = "Contenido 1",
                tipo = "Resina Epoxi",
                precio = 25.99,
                cantidad = 10,
                usuarioId = 1
            ),
            Producto(
                id = 2,
                nombre = "Título 2",
                descripcion = "Contenido 2",
                tipo = "Pigmento",
                precio = 5.50,
                cantidad = 50,
                usuarioId = 2
            )
        )

        val mockProductoRepo = mockk<ProductoRepository>(relaxed = true)
        val mockValoracionRepo = mockk<ValoracionRepository>(relaxed = true)
        val mockFavoritoRepo = mockk<FavoritoRepository>(relaxed = true)

        val productoViewModel = object : ProductoViewModel(
            mockProductoRepo, mockValoracionRepo, mockFavoritoRepo
        ) {
            override fun fetchProductos() {
                _productos.value = fakeProductos
            }
        }

        runTest {
            productoViewModel.fetchProductos()
            productoViewModel.productoList.value shouldContainExactly fakeProductos
        }
    }
})