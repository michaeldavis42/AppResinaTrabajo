package com.example.appresina.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appresina.model.Producto
import com.example.appresina.ui.screens.HomeScreen
import com.example.appresina.ui.screens.AddEditProductScreen
import com.example.appresina.ui.screens.ProductDetailScreen
import com.example.appresina.ui.screens.SettingsScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddProduct = {
                    navController.navigate(Screen.AddProduct.route)
                },
                onNavigateToProductDetail = { producto ->
                    navController.navigate("${Screen.ProductDetail.route}/${producto.id}")
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.AddProduct.route) {
            AddEditProductScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProductSaved = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.EditProduct.route) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull()
            
            // TODO: Obtener producto por ID desde el ViewModel
            val producto = Producto(
                id = productoId ?: 0,
                nombre = "Producto de ejemplo",
                tipo = "Epoxi",
                precio = 15000.0,
                cantidad = 10,
                descripcion = "Descripción del producto",
                usuarioId = 1 // Añadido el usuarioId que faltaba
            )
            
            AddEditProductScreen(
                producto = producto,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProductSaved = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("${Screen.ProductDetail.route}/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull() ?: 0
            
            ProductDetailScreen(
                productoId = productoId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditProduct = { producto ->
                    navController.navigate("${Screen.EditProduct.route}/${producto.id}")
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Definición de las pantallas de la aplicación
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddProduct : Screen("add_product")
    object EditProduct : Screen("edit_product/{productoId}")
    object ProductDetail : Screen("product_detail")
    object Settings : Screen("settings")
}
