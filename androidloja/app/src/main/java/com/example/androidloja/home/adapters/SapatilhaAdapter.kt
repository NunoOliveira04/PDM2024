package com.example.androidloja.home.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidloja.databinding.ItemSapatilhaBinding
import com.example.androidloja.home.DetalhesSapatilhaActivity
import com.example.androidloja.models.Sapatilha

class SapatilhaAdapter(
    private val sapatilhas: List<Sapatilha>,
    private val marcaNome: String
) : RecyclerView.Adapter<SapatilhaAdapter.SapatilhaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SapatilhaViewHolder {
        val binding = ItemSapatilhaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SapatilhaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SapatilhaViewHolder, position: Int) {
        holder.bind(sapatilhas[position])
    }

    override fun getItemCount(): Int = sapatilhas.size

    inner class SapatilhaViewHolder(private val binding: ItemSapatilhaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sapatilha: Sapatilha) {
            binding.tvModelo.text = sapatilha.nome
            binding.tvGenero.text = sapatilha.genero.capitalize()
            binding.tvCategoria.text = sapatilha.categoria.capitalize()
            binding.tvPreco.text = "€ ${sapatilha.preco}"

            Glide.with(binding.root.context)
                .load(sapatilha.imagem)
                .into(binding.ivSapatilha)

            // Clique para abrir DetalhesSapatilhaActivity
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetalhesSapatilhaActivity::class.java).apply {
                    putExtra("marcaNome", marcaNome)
                    putExtra("sapatilha", sapatilha) // Passando o objeto Parcelable completo
                }
                context.startActivity(intent)
            }
        }
    }
}
