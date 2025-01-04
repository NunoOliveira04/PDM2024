package com.example.androidnews.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun NewsDetailScreen(newsUrl: String?) {
    // Verifica se a URL é válida
    newsUrl?.let { url ->
        // Mostra a página da notícia usando WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true // Ativa JavaScript
                    webViewClient = WebViewClient()   // Abre a página dentro do app
                    loadUrl(url) // Carrega a URL
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    } ?: run {
        // Mensagem caso não haja URL
        androidx.compose.material3.Text(
            text = "Erro: URL não disponível.",
            modifier = Modifier.fillMaxSize()
        )
    }
}
