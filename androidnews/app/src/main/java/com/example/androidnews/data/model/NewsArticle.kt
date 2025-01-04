package com.example.androidnews.data.model

import com.google.gson.annotations.SerializedName

// Resposta da API com a lista de notícias
data class NewsResponse(
    @SerializedName("results") val results: List<NewsArticle>
)

// Dados de cada notícia
data class NewsArticle(
    @SerializedName("title") val title: String,
    @SerializedName("abstract") val summary: String,
    @SerializedName("url") val url: String,
    @SerializedName("multimedia") val multimedia: List<Multimedia>?
)

// Dados de cada imagem
data class Multimedia(
    @SerializedName("url") val url: String // Link da imagem
)
