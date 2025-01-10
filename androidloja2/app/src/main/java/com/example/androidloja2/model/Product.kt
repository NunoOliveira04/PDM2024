package com.example.androidloja2.model

data class Product(
    val modelo: String = "",
    val categoria: String = "",
    val genero: String = "",
    val imagem: String = "",
    val preco: Double = 0.0,
    val marca: String = "",
    val tamanhos: List<Int> = listOf(38, 39, 40, 41, 42, 43)
)