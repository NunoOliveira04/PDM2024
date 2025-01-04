package com.example.androidnews.data.network

import com.example.androidnews.data.model.NewsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Interface que define as chamadas para a API
interface RetrofitService {

    // Procura as notícias principais
    @GET("topstories/v2/home.json?api-key=QaJECR5rSIkOGMAoYyqaeYQYUzkE7gI0")
    suspend fun getTopStories(): NewsResponse

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/"

        // Configuração do Retrofit para comunicar com a API
        fun create(): RetrofitService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Converte JSON para Kotlin
                .build()
                .create(RetrofitService::class.java) // Cria a implementação da interface
        }
    }
}
