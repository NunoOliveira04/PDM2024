package com.example.androidloja2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidloja2.ui.screens.home.HomeScreen
import com.example.androidloja2.ui.screens.login.LoginScreen
import com.example.androidloja2.ui.screens.login.LoginViewModel
import com.example.androidloja2.ui.screens.register.RegisterScreen
import com.example.androidloja2.ui.screens.register.RegisterViewModel
import com.example.androidloja2.ui.screens.main.MainScreen
import com.example.androidloja2.ui.screens.main.MainViewModel
import com.example.androidloja2.ui.screens.detail.ProductDetailScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.androidloja2.model.Product


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val mainViewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(mainViewModel),
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = RegisterViewModel(),
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                viewModel = mainViewModel,
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onProductClick = { product ->
                    mainViewModel.setSelectedProduct(product)
                    navController.navigate("product_detail")
                }
            )
        }

        composable("product_detail") {
            val product by mainViewModel.selectedProduct.collectAsState()

            product?.let { prod ->
                ProductDetailScreen(
                    product = prod,
                    onBackClick = { navController.popBackStack() },
                    onAddToCart = { product, size ->
                        navController.popBackStack()
                    },
                    viewModel = mainViewModel
                )
            }
        }
    }
}