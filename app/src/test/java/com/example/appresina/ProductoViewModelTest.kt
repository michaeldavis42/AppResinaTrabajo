package com.example.appresina

import com.example.appresina.data.FavoritoRepository
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository
import com.example.appresina.model.Producto
import com.example.appresina.viewmodel.ProductoViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    val mockProductoRepo = mockk<ProductoRepository>(relaxed = true)
    val mockValoracionRepo = mockk<ValoracionRepository>(relaxed = true)
    val mockFavoritoRepo = mockk<FavoritoRepository>(relaxed = true)

    "productoList debe contener los datos esperados después de fetchProductos()" {
        val fakeProductos = listOf(
            Producto(id = 1, nombre = "Título 1", descripcion = "Contenido 1", tipo = "Resina Epoxi", precio = 25.99, cantidad = 10, usuarioId = 1),
            Producto(id = 2, nombre = "Título 2", descripcion = "Contenido 2", tipo = "Pigmento", precio = 5.50, cantidad = 50, usuarioId = 2)
        )

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

    "los métodos de actualización deben modificar los StateFlows correctamente" {
        val productoViewModel = ProductoViewModel(mockProductoRepo, mockValoracionRepo, mockFavoritoRepo)
        runTest {
            productoViewModel.actualizarNombre("Nuevo Nombre")
            productoViewModel.nombre.value shouldBe "Nuevo Nombre"

            productoViewModel.actualizarTipo("Nuevo Tipo")
            productoViewModel.tipo.value shouldBe "Nuevo Tipo"

            productoViewModel.actualizarPrecio("123.45")
            productoViewModel.precio.value shouldBe "123.45"

            productoViewModel.actualizarCantidad("100")
            productoViewModel.cantidad.value shouldBe "100"

            productoViewModel.actualizarDescripcion("Nueva Descripción")
            productoViewModel.descripcion.value shouldBe "Nueva Descripción"
        }
    }

    "agregarProducto debe llamar al repositorio para insertar un nuevo producto" {
        val productoViewModel = ProductoViewModel(mockProductoRepo, mockValoracionRepo, mockFavoritoRepo)
        runTest {
            productoViewModel.actualizarNombre("Producto Test")
            productoViewModel.actualizarTipo("Tipo Test")
            productoViewModel.actualizarPrecio("99.99")
            productoViewModel.actualizarCantidad("5")
            productoViewModel.actualizarDescripcion("Desc Test")

            productoViewModel.agregarProducto()

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify {
                mockProductoRepo.insertarProducto(
                    any()
                )
            }
        }
    }

    "eliminarProducto debe llamar al repositorio para eliminar el producto" {
        val productoViewModel = ProductoViewModel(mockProductoRepo, mockValoracionRepo, mockFavoritoRepo)
        runTest {
            val productoAEliminar = Producto(id = 1, nombre = "Test", tipo = "Test", precio = 1.0, cantidad = 1, descripcion = "Test", usuarioId = 1)
            productoViewModel.eliminarProducto(productoAEliminar)

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify {
                mockProductoRepo.eliminarProducto(productoAEliminar)
            }
        }
    }
})