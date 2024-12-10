package com.example.apinoticias.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apinoticias.data.model.NewsArticle
import com.example.apinoticias.data.network.NewsApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// O ViewModel que vai gerenciar as notícias
class NewsViewModel : ViewModel() {
    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> get() = _newsArticles

    init {
        fetchNewsArticles()
    }


    private fun fetchNewsArticles() {
        viewModelScope.launch {
            try {
                val response = NewsApiService.create().getTopStories()
                _newsArticles.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
