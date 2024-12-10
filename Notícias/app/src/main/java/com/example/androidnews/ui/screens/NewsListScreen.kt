package com.example.apinoticias.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apinoticias.data.model.NewsArticle
import coil.compose.rememberAsyncImagePainter

// Função para exibir a lista de notícias
@Composable
fun NewsListScreen(viewModel: NewsViewModel = viewModel()) {
    val newsArticles by viewModel.newsArticles.collectAsState()

    when {
        newsArticles.isEmpty() -> {
            // Mensagem exibida enquanto as notícias estão a carregar
            Text(
                "A carregar notícias...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
        else -> {
            // Se tiver notícias, exibe uma lista com elas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(newsArticles.size) { index ->
                    val article = newsArticles[index]
                    NewsCard(article)
                }
            }
        }
    }
}

// Função para exibir cada notícia individualmente
@Composable
fun NewsCard(article: NewsArticle) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Aqui pode-se adicionar uma ação ao clicar na notícia */ }
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Exibe a imagem da notícia, se existir
            if (!article.multimedia.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(article.multimedia[0].url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // Exibe o título e o resumo da notícia
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    fontSize = 18.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.summary ?: "Resumo não disponível",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
