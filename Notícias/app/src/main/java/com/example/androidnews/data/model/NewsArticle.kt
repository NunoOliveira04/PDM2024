package com.example.apinoticias.data.model

import com.google.gson.annotations.SerializedName

// Classe para armazenar a resposta da API
data class NewsResponse(
    @SerializedName("results") val results: List<NewsArticle>
)

// Classe para armazenar os dados de cada artigo de notícia
data class NewsArticle(
    @SerializedName("title") val title: String,
    @SerializedName("abstract") val summary: String,
    @SerializedName("url") val url: String,
    @SerializedName("multimedia") val multimedia: List<Multimedia>?
)

// Classe para armazenar informações sobre as multimídias do artigo
data class Multimedia(
    @SerializedName("url") val url: String,
    @SerializedName("type") val type: String
)
