package com.example.androidloja.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidloja.databinding.ItemCarrinhoBinding
import com.example.androidloja.models.CarrinhoItem

class CarrinhoAdapter(
    private val itens: List<CarrinhoItem>,
    private val onRemoveClick: (CarrinhoItem) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCarrinhoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        holder.bind(itens[position])
    }

    override fun getItemCount(): Int = itens.size

    inner class CarrinhoViewHolder(private val binding: ItemCarrinhoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarrinhoItem) {
            // Exibir imagem do produto
            Glide.with(binding.root.context)
                .load(item.imagemUrl)
                .into(binding.ivProduto)

            // Exibir marca e modelo
            binding.tvNomeProduto.text = "${item.marca} ${item.modelo}"

            // Exibir tamanho
            binding.tvTamanho.text = "Tamanho: ${item.tamanho ?: "Não especificado"}"

            // Exibir quantidade
            binding.tvQuantidade.text = "Quantidade: ${item.quantidade}"

            // Exibir preço
            binding.tvPrecoItem.text = String.format("€ %.2f", item.preco * item.quantidade)

            // Configurar botão de remover
            binding.btnRemover.setOnClickListener {
                onRemoveClick(item)
            }
        }
    }
}