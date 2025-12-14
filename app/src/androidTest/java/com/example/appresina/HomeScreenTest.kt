package com.example.appresina

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.appresina.data.FavoritoRepository
import com.example.appresina.data.ProductoRepository
import com.example.appresina.data.ValoracionRepository
import com.example.appresina.model.Producto
import com.example.appresina.ui.screens.HomeScreen
import com.example.appresina.viewmodel.ProductoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_producto_debe_aparecer_en_pantalla() {
        val fakeProductos = listOf(
            Producto(
                id = 1,
                nombre = "Producto 1",
                descripcion = "Descripción 1",
                tipo = "A",
                precio = 10.0,
                cantidad = 1,
                usuarioId = 1
            ),
            Producto(
                id = 2,
                nombre = "Producto 2",
                descripcion = "Descripción 2",
                tipo = "B",
                precio = 20.0,
                cantidad = 2,
                usuarioId = 1
            )
        )

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
                onNavigateToProductDetail = {},
                onNavigateToSettings = {},
                onLogout = {},
                viewModel = fakeViewModel
            )
        }

        composeTestRule.onNodeWithText("Producto 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Producto 2").assertIsDisplayed()
    }
}