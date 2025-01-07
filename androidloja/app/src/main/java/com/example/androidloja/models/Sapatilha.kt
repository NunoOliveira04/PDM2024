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
    val genero: String = ""
) : Parcelable

