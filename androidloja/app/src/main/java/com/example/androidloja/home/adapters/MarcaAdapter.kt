package com.example.androidloja.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidloja.databinding.ItemMarcaBinding
import com.example.androidloja.models.Marca

class MarcaAdapter(private val marcas: List<Marca>) :
    RecyclerView.Adapter<MarcaAdapter.MarcaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarcaViewHolder {
        val binding = ItemMarcaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MarcaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarcaViewHolder, position: Int) {
        holder.bind(marcas[position])
    }

    override fun getItemCount(): Int = marcas.size

    inner class MarcaViewHolder(private val binding: ItemMarcaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(marca: Marca) {
            binding.tvMarca.text = marca.nome
            binding.rvSapatilhas.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.rvSapatilhas.adapter = SapatilhaAdapter(marca.sapatilhas, marca.nome)
        }
    }
}