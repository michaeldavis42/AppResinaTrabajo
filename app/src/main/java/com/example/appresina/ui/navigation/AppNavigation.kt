package com.example.appresina.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appresina.data.AuthRepository
import com.example.appresina.data.AppDatabase
import com.example.appresina.data.SessionManager
import com.example.appresina.data.UsuarioRepository
import com.example.appresina.model.Producto
import com.example.appresina.ui.screens.AddEditProductScreen
import com.example.appresina.ui.screens.HomeScreen
import com.example.appresina.ui.screens.LoginScreen
import com.example.appresina.ui.screens.ProductDetailScreen
import com.example.appresina.ui.screens.RegisterScreen
import com.example.appresina.ui.screens.SettingsScreen
import com.example.appresina.viewmodel.AuthViewModel
import com.example.appresina.viewmodel.AuthViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            AuthRepository(
                UsuarioRepository(AppDatabase.getDatabase(context).usuarioDao()),
                SessionManager(context)
            ),
            SessionManager(context)
        )
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
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
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
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
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
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

        composable("${Screen.ProductDetail.route}/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull() ?: 0
            ProductDetailScreen(
                productoId = productoId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { producto ->
                    navController.navigate("edit_product/${producto.id}")
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