package com.example.androidloja.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.androidloja.MainActivity
import com.example.androidloja.R
import com.example.androidloja.databinding.ActivityDetalhesSapatilhaBinding
import com.example.androidloja.home.adapters.CarrinhoAdapter
import com.example.androidloja.home.adapters.SapatilhaAdapter
import com.example.androidloja.models.CarrinhoItem
import com.example.androidloja.models.Sapatilha
import com.example.androidloja.models.TamanhosSapatilhas
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetalhesSapatilhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesSapatilhaBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var carrinhoAdapter: CarrinhoAdapter
    private val carrinhoItems = mutableListOf<CarrinhoItem>()
    private var tamanhoSelecionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesSapatilhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView para o carrinho
        binding.rvCarrinho.layoutManager = LinearLayoutManager(this)
        carrinhoAdapter = CarrinhoAdapter(carrinhoItems) { item -> removeFromCart(item) }
        binding.rvCarrinho.adapter = carrinhoAdapter

        // Configurar menu
        binding.ivMenuDetalhes.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configurar NavigationView
        binding.navigationViewDetalhes.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // Botão do carrinho
        binding.fabCarrinhoDetalhes.setOnClickListener { openCartDrawer() }

        // Dados da sapatilha
        val sapatilha = intent.getParcelableExtra<Sapatilha>("sapatilha")
        val marcaNome = intent.getStringExtra("marcaNome") ?: "Marca não encontrada"

        sapatilha?.let {
            setupSapatilhaDetails(it, marcaNome)
            setupTamanhosChips()
            setupAddToCartButton(it)
            fetchSugestoes(it.categoria, it.nome)
        }

        fetchCarrinhoItems()
    }

    private fun setupSapatilhaDetails(sapatilha: Sapatilha, marcaNome: String) {
        Glide.with(this).load(sapatilha.imagem).into(binding.ivSapatilha)
        binding.tvModeloMarca.text = "$marcaNome ${sapatilha.nome}"
        binding.tvPreco.text = "€ ${sapatilha.preco}"
        binding.tvCategoriaGenero.text = "${sapatilha.categoria} | ${sapatilha.genero}"
    }

    private fun setupTamanhosChips() {
        binding.tamanhoChipGroup.removeAllViews()

        TamanhosSapatilhas.tamanhosDisponiveis.forEach { tamanho ->
            val chip = Chip(this).apply {
                text = tamanho.toString()
                isCheckable = true
                setTextColor(getColor(android.R.color.white))
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tamanhoSelecionado = tamanho
                    }
                }
            }
            binding.tamanhoChipGroup.addView(chip)
        }
    }

    private fun setupAddToCartButton(sapatilha: Sapatilha) {
        binding.btnAdicionarCarrinho.setOnClickListener {
            if (tamanhoSelecionado == null) {
                Toast.makeText(this, "Por favor, selecione um tamanho", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            // Verificar se já existe este item no carrinho
            firestore.collection("carrinho")
                .whereEqualTo("userId", userId)
                .whereEqualTo("sapatilhaId", sapatilha.nome)
                .whereEqualTo("tamanho", tamanhoSelecionado)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Se não existe, criar novo item
                        val carrinhoItem = mapOf(
                            "userId" to userId,
                            "sapatilhaId" to sapatilha.nome,
                            "marca" to sapatilha.marca,
                            "modelo" to sapatilha.nome,
                            "imagemUrl" to sapatilha.imagem,
                            "quantidade" to 1,
                            "tamanho" to tamanhoSelecionado,
                            "preco" to sapatilha.preco
                        )

                        firestore.collection("carrinho")
                            .add(carrinhoItem)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show()
                                fetchCarrinhoItems()
                            }
                    } else {
                        // Se existe, aumentar a quantidade
                        val document = documents.documents[0]
                        val currentQuantity = document.getLong("quantidade")?.toInt() ?: 1
                        document.reference.update("quantidade", currentQuantity + 1)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Quantidade aumentada", Toast.LENGTH_SHORT).show()
                                fetchCarrinhoItems()
                            }
                    }
                }
        }
    }

    private fun fetchCarrinhoItems() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("carrinho")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                carrinhoItems.clear()
                val itemsMap = mutableMapOf<String, CarrinhoItem>()

                for (document in result) {
                    val sapatilhaId = document.getString("sapatilhaId") ?: ""
                    val marca = document.getString("marca") ?: ""
                    val modelo = document.getString("modelo") ?: ""
                    val quantidade = document.getLong("quantidade")?.toInt() ?: 1
                    val imagemUrl = document.getString("imagemUrl") ?: ""
                    val tamanho = document.getLong("tamanho")?.toInt()
                    val preco = document.getDouble("preco") ?: 0.0

                    val key = "$sapatilhaId-$tamanho"
                    if (itemsMap.containsKey(key)) {
                        val existingItem = itemsMap[key]!!
                        itemsMap[key] = existingItem.copy(quantidade = existingItem.quantidade + quantidade)
                    } else {
                        itemsMap[key] = CarrinhoItem(
                            sapatilhaId = sapatilhaId,
                            marca = marca,
                            modelo = modelo,
                            quantidade = quantidade,
                            imagemUrl = imagemUrl,
                            tamanho = tamanho,
                            preco = preco
                        )
                    }
                }

                carrinhoItems.addAll(itemsMap.values)
                carrinhoAdapter.notifyDataSetChanged()

                // Atualizar total
                val total = carrinhoItems.sumOf { it.preco * it.quantidade }
                binding.tvTotalCarrinho.text = String.format("Total: € %.2f", total)
            }
    }

    private fun fetchSugestoes(categoria: String, modeloAtual: String) {
        binding.rvSugestoes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Buscar todas as marcas primeiro
        firestore.collection("Marcas")
            .get()
            .addOnSuccessListener { marcasSnapshot ->
                val sugestoes = mutableListOf<Sapatilha>()
                var completedQueries = 0

                for (marcaDoc in marcasSnapshot.documents) {
                    val marcaNome = marcaDoc.id

                    // Para cada marca, buscar sapatilhas da mesma categoria
                    marcaDoc.reference.collection("Sapatilhas")
                        .whereEqualTo("categoria", categoria)
                        .get()
                        .addOnSuccessListener { sapatilhasSnapshot ->
                            for (sapatilhaDoc in sapatilhasSnapshot.documents) {
                                val modelo = sapatilhaDoc.getString("modelo") ?: ""
                                if (modelo != modeloAtual) {  // Não incluir a sapatilha atual
                                    val sapatilha = Sapatilha(
                                        nome = modelo,
                                        marca = marcaNome,
                                        preco = sapatilhaDoc.getDouble("preco") ?: 0.0,
                                        imagem = sapatilhaDoc.getString("imagem") ?: "",
                                        categoria = sapatilhaDoc.getString("categoria") ?: "",
                                        genero = sapatilhaDoc.getString("genero") ?: ""
                                    )
                                    sugestoes.add(sapatilha)
                                }
                            }

                            completedQueries++
                            // Quando todas as queries terminarem, atualizar o RecyclerView
                            if (completedQueries == marcasSnapshot.size()) {
                                val sugestoesLimitadas = sugestoes.shuffled().take(5)
                                if (sugestoesLimitadas.isNotEmpty()) {
                                    binding.rvSugestoes.adapter = SapatilhaAdapter(sugestoesLimitadas, "")
                                }
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun openCartDrawer() {
        fetchCarrinhoItems()
        binding.drawerLayout.openDrawer(binding.carrinhoDrawer)
    }

    private fun removeFromCart(item: CarrinhoItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("carrinho")
            .whereEqualTo("userId", userId)
            .whereEqualTo("sapatilhaId", item.sapatilhaId)
            .whereEqualTo("tamanho", item.tamanho)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val currentQuantity = document.getLong("quantidade")?.toInt() ?: 1

                    when {
                        currentQuantity <= 1 -> {
                            // Se quantidade é 1, remove o item
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Item removido do carrinho", Toast.LENGTH_SHORT).show()
                                    fetchCarrinhoItems()
                                }
                        }
                        else -> {
                            // Se quantidade > 1, diminui em 1
                            document.reference.update("quantidade", currentQuantity - 1)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Quantidade reduzida", Toast.LENGTH_SHORT).show()
                                    fetchCarrinhoItems()
                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao remover item", Toast.LENGTH_SHORT).show()
            }
    }
}