package com.example.apinoticias.data.network

import com.example.apinoticias.data.model.NewsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Interface para definir as chamadas à API
interface NewsApiService {

    @GET("topstories/v2/home.json?api-key=QaJECR5rSIkOGMAoYyqaeYQYUzkE7gI0")
    suspend fun getTopStories(): NewsResponse

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/"

        // Função para criar a instância do Retrofit e configurar a comunicação com a API
        fun create(): NewsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApiService::class.java)
        }
    }
}
