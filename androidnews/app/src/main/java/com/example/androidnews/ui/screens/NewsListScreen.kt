package com.example.androidnews.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.androidnews.data.model.NewsArticle
import java.net.URLEncoder

@Composable
fun NewsListScreen(navController: NavHostController, viewModel: NewsViewModel = viewModel()) {
    // Observa os artigos carregados pelo ViewModel
    val newsArticles by viewModel.newsArticles.collectAsState()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(newsArticles.size) { index ->
            val article = newsArticles[index]
            NewsCard(article, navController)
        }
    }
}

@Composable
fun NewsCard(article: NewsArticle, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Navega para a tela de detalhe com a URL codificada
                val encodedUrl = URLEncoder.encode(article.url, "UTF-8")
                navController.navigate("news_detail/$encodedUrl")
            }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Mostra a imagem
            if (!article.multimedia.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(article.multimedia[0].url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // Título do artigo
            Text(
                text = article.title,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis // Corta com "..." se for muito longo
            )
        }
    }
}
