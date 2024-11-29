package com.example.apinoticias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.apinoticias.ui.screens.NewsListScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

// Função que monta o conteúdo da aplicação
@Composable
fun AppContent() {
    MaterialTheme {
        Scaffold {
            NewsListScreen()
        }
    }
}

// Essa função é só para exibir um preview da tela na IDE
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppContent()
}
