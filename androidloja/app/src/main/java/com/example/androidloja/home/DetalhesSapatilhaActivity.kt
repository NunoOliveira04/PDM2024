package com.example.androidloja.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidloja.databinding.ActivityDetalhesSapatilhaBinding

class DetalhesSapatilhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesSapatilhaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesSapatilhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber os dados da Sapatilha através do Intent
        val marca = intent.getStringExtra("marca") ?: ""
        val modelo = intent.getStringExtra("modelo") ?: ""
        val preco = intent.getDoubleExtra("preco", 0.0)
        val categoria = intent.getStringExtra("categoria") ?: ""
        val genero = intent.getStringExtra("genero") ?: ""
        val imagem = intent.getStringExtra("imagem") ?: ""

// Atualizar os TextViews
        binding.tvModeloMarca.text = "$marca $modelo"
        binding.tvPreco.text = "€ $preco"
        binding.tvCategoriaGenero.text = "Categoria: $categoria | Género: $genero"

        Glide.with(this)
            .load(imagem)
            .into(binding.ivSapatilha)

    }
}
