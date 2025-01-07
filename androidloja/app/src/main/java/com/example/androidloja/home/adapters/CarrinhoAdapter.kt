package com.example.androidloja.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidloja.databinding.ItemCarrinhoBinding
import com.example.androidloja.models.CarrinhoItem

class CarrinhoAdapter(
    private val itens: List<CarrinhoItem>
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
                .load(item.imagemUrl) // URL da imagem
                .into(binding.ivProduto)

            // Exibir marca e modelo na mesma linha
            binding.tvNomeProduto.text = "${item.marca} ${item.modelo}"

            // Exibir quantidade
            binding.tvQuantidade.text = "Quantidade: ${item.quantidade}"
        }
    }
}
