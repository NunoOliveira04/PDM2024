package com.example.androidnews.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidnews.data.model.NewsArticle
import com.example.androidnews.data.network.RetrofitService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    // Guarda a lista de artigos (estado)
    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> get() = _newsArticles

    // Quando o ViewModel é criado, puxa as notícias
    init {
        fetchNewsArticles()
    }

    private fun fetchNewsArticles() {
        viewModelScope.launch {
            try {
                // Chama a API e atualiza a lista
                val response = RetrofitService.create().getTopStories()
                _newsArticles.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
