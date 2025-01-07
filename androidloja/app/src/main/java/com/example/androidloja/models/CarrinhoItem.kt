package com.example.androidloja.models

data class CarrinhoItem(
    val sapatilhaId: String,
    val marca: String,
    val quantidade: Int,
    val imagemUrl: String,
    val modelo: String,
    val tamanho: Int? = null,
    val preco: Double = 0.0
)