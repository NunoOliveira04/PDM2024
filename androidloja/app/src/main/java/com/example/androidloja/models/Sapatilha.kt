package com.example.androidloja.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sapatilha(
    val nome: String,
    val preco: Double,
    val imagem: String,
    val categoria: String,
    val genero: String
) : Parcelable

