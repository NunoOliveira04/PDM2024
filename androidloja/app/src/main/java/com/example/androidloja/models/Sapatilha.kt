package com.example.androidloja.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sapatilha(
    val nome: String = "",
    val marca: String = "",
    val imagem: String = "",
    val preco: Double = 0.0,
    val categoria: String = "",
    val genero: String = "",
    var tamanhoSelecionado: Int? = null
) : Parcelable

// Classe auxiliar para os tamanhos disponíveis
object TamanhosSapatilhas {
    val tamanhosDisponiveis = listOf(35, 36, 37, 38, 39, 40, 41, 42, 43, 44)
}