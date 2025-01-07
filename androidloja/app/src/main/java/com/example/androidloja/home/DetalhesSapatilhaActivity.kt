package com.example.androidloja.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.androidloja.databinding.ActivityDetalhesSapatilhaBinding
import com.example.androidloja.home.adapters.CarrinhoAdapter
import com.example.androidloja.models.CarrinhoItem
import com.example.androidloja.models.Sapatilha
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetalhesSapatilhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesSapatilhaBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var carrinhoAdapter: CarrinhoAdapter
    private val carrinhoItems = mutableListOf<CarrinhoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesSapatilhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView para o carrinho
        binding.rvCarrinhoDrawer.layoutManager = LinearLayoutManager(this)
        carrinhoAdapter = CarrinhoAdapter(carrinhoItems)
        binding.rvCarrinhoDrawer.adapter = carrinhoAdapter

        // Botão do carrinho
        binding.fabCarrinho.setOnClickListener { openCartDrawer() }

        // Dados da sapatilha
        val sapatilha = intent.getParcelableExtra<Sapatilha>("sapatilha")
        val marcaNome = intent.getStringExtra("marcaNome") ?: "Marca não encontrada"

        sapatilha?.let {
            setupSapatilhaDetails(it, marcaNome)
            setupAddToCartButton(it)
        }

        fetchCarrinhoItems()
    }

    private fun setupSapatilhaDetails(sapatilha: Sapatilha, marcaNome: String) {
        Glide.with(this).load(sapatilha.imagem).into(binding.ivSapatilha)
        binding.tvModeloMarca.text = "$marcaNome ${sapatilha.nome}"
        binding.tvPreco.text = "€ ${sapatilha.preco}"
        binding.tvCategoriaGenero.text = "Categoria: ${sapatilha.categoria}"
    }

    private fun setupAddToCartButton(sapatilha: Sapatilha) {
        binding.btnAdicionarCarrinho.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val carrinhoItem = mapOf(
                "userId" to userId,
                "sapatilhaId" to sapatilha.nome,
                "marca" to sapatilha.marca,
                "modelo" to sapatilha.nome,
                "imagemUrl" to sapatilha.imagem,
                "quantidade" to 1
            )
            firestore.collection("carrinho").add(carrinhoItem).addOnSuccessListener { fetchCarrinhoItems() }
        }
    }

    private fun fetchCarrinhoItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("carrinho").whereEqualTo("userId", userId).get().addOnSuccessListener { result ->
            carrinhoItems.clear()
            for (document in result) {
                carrinhoItems.add(
                    CarrinhoItem(
                        sapatilhaId = document.getString("sapatilhaId") ?: "",
                        marca = document.getString("marca") ?: "",
                        modelo = document.getString("modelo") ?: "",
                        quantidade = document.getLong("quantidade")?.toInt() ?: 1,
                        imagemUrl = document.getString("imagemUrl") ?: ""
                    )
                )
            }
            carrinhoAdapter.notifyDataSetChanged()
        }
    }

    private fun openCartDrawer() {
        fetchCarrinhoItems()
        binding.drawerLayout.openDrawer(binding.rightDrawer)
    }
}
