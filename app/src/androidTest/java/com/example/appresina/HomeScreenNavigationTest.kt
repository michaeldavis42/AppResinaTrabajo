package com.example.appresina

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.activity.ComponentActivity
import com.example.appresina.data.FavoritoRepository
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository
import com.example.appresina.model.Producto
import com.example.appresina.ui.screens.HomeScreen
import com.example.appresina.viewmodel.ProductoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class HomeScreenNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeProductos = listOf(
        Producto(
            id = 1,
            nombre = "Producto 1",
            descripcion = "Descripción 1",
            tipo = "A",
            precio = 10.0,
            cantidad = 1,
            usuarioId = 1
        )
    )

    @Test
    fun la_navegacion_a_add_product_debe_llamarse() {
        val onNavigateToAddProduct: () -> Unit = mockk(relaxed = true)
        val mockProductoRepo = mockk<ProductoRepository> { 
            every { obtenerProductos() } returns flowOf(fakeProductos)
        }
        val mockValoracionRepo = mockk<ValoracionRepository>(relaxed = true)
        val mockFavoritoRepo = mockk<FavoritoRepository>(relaxed = true)

        val fakeViewModel = ProductoViewModel(
            mockProductoRepo, mockValoracionRepo, mockFavoritoRepo
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToAddProduct = onNavigateToAddProduct,
                onNavigateToProductDetail = {},
                onNavigateToSettings = {},
                onLogout = {},
                viewModel = fakeViewModel
            )
        }

        composeTestRule.onNodeWithContentDescription("Agregar Producto").performClick()

        verify { onNavigateToAddProduct() }
    }

    @Test
    fun la_navegacion_a_product_detail_debe_llamarse() {
        val onNavigateToProductDetail: (Producto) -> Unit = mockk(relaxed = true)
        val mockProductoRepo = mockk<ProductoRepository> {
            every { obtenerProductos() } returns flowOf(fakeProductos)
        }
        val mockValoracionRepo = mockk<ValoracionRepository>(relaxed = true)
        val mockFavoritoRepo = mockk<FavoritoRepository>(relaxed = true)

        val fakeViewModel = ProductoViewModel(
            mockProductoRepo, mockValoracionRepo, mockFavoritoRepo
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToAddProduct = {},
                onNavigateToProductDetail = onNavigateToProductDetail,
                onNavigateToSettings = {},
                onLogout = {},
                viewModel = fakeViewModel
            )
        }

        composeTestRule.onNodeWithText("Producto 1").performClick()

        verify { onNavigateToProductDetail(fakeProductos.first()) }
    }
}
