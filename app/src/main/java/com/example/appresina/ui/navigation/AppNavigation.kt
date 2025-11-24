package com.example.appresina.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appresina.data.SessionManager
import com.example.appresina.model.Producto
import com.example.appresina.ui.screens.HomeScreen
import com.example.appresina.ui.screens.AddEditProductScreen
import com.example.appresina.ui.screens.SettingsScreen
import com.example.appresina.ui.screens.LoginScreen
import com.example.appresina.ui.screens.RegisterScreen
import kotlinx.coroutines.flow.first

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    
    // Verificar estado de autenticación solo para usuarios ya autenticados
    // El login permanecerá visible hasta que se complete exitosamente
    var hasCheckedAuth by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        val loggedIn = sessionManager.isLoggedIn.first()
        hasCheckedAuth = true
        
        // Solo navegar a Home si el usuario ya está autenticado de una sesión anterior
        // y solo después de que el NavHost se haya inicializado completamente
        // El login no desaparecerá hasta que el usuario complete el login exitosamente
        if (loggedIn) {
            // Esperar un momento para que NavHost se inicialice completamente
            kotlinx.coroutines.delay(100)
            try {
                // Verificar que estamos en la pantalla de login antes de navegar
                val currentRoute = navController.currentDestination?.route
                if (currentRoute == Screen.Login.route || currentRoute == null) {
                    navController.navigate(Screen.Home.route) {
                        // Limpiar todo el back stack
                        popUpTo(0) { inclusive = true }
                        // Evitar múltiples navegaciones
                        launchSingleTop = true
                    }
                }
            } catch (e: Exception) {
                // Si hay error de navegación, ignorar - el login permanecerá visible
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
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
            
            val producto = Producto(
                id = productoId ?: 0,
                nombre = "Producto de ejemplo",
                tipo = "Epoxi",
                precio = 15000.0,
                cantidad = 10,
                descripcion = "Descripción del producto",
                usuarioId = 1
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

        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddProduct : Screen("add_product")
    object EditProduct : Screen("edit_product/{productoId}")
    object ProductDetail : Screen("product_detail")
    object Settings : Screen("settings")
}
