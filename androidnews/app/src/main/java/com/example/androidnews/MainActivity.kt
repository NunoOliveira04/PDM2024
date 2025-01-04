package com.example.androidnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidnews.ui.screens.NewsDetailScreen
import com.example.androidnews.ui.screens.NewsListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent() // Chama a função principal da UI
        }
    }
}

@Composable
fun AppContent() {
    // Navegar entre as telas
    val navController: NavHostController = rememberNavController()

    MaterialTheme {
        NavHost(navController, startDestination = "news_list") {
            // Tela inicial que mostra a lista de notícias
            composable("news_list") {
                NewsListScreen(navController)
            }
            // Tela de detalhes para uma notícia específica
            composable("news_detail/{articleUrl}") { backStackEntry ->
                val articleUrl = backStackEntry.arguments?.getString("articleUrl")
                NewsDetailScreen(articleUrl) // Passa a URL como argumento
            }
        }
    }
}
