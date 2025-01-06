package com.example.androidloja.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.androidloja.databinding.ActivityDetalhesSapatilhaBinding
import com.example.androidloja.home.adapters.SapatilhaAdapter
import com.example.androidloja.models.Sapatilha
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.androidloja.MainActivity
import com.example.androidloja.R

class DetalhesSapatilhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesSapatilhaBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesSapatilhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Configurar o menu lateral (Drawer)
        configureDrawer()

        // Obter dados passados da sapatilha selecionada
        val sapatilha = intent.getParcelableExtra<Sapatilha>("sapatilha")!!
        val marcaNome = intent.getStringExtra("marcaNome") ?: "Marca não encontrada"

        // Configurar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Preencher detalhes da sapatilha
        setupSapatilhaDetails(sapatilha, marcaNome)

        // Configurar clique do botão "Adicionar ao carrinho"
        setupAddToCartButton()

        // Buscar sapatilhas relacionadas
        fetchRelatedSapatilhas(sapatilha)
    }

    private fun configureDrawer() {
        // Configurar clique nas 3 barras para abrir o menu lateral
        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configurar cliques no menu lateral
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Redirecionar para HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_logout -> {
                    // Redirecionar para MainActivity e finalizar a HomeActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSapatilhaDetails(sapatilha: Sapatilha, marcaNome: String) {
        Glide.with(this)
            .load(sapatilha.imagem)
            .into(binding.ivSapatilha)

        binding.tvModeloMarca.text = "$marcaNome ${sapatilha.nome}"
        binding.tvPreco.text = "€ ${sapatilha.preco}"
        binding.tvCategoriaGenero.text =
            "Categoria: ${sapatilha.categoria} | Gênero: ${sapatilha.genero}"
    }

    private fun setupAddToCartButton() {
        binding.btnAdicionarCarrinho.setOnClickListener {
            // Aqui você pode adicionar a lógica de adicionar ao carrinho
            // Exemplo de exibição de mensagem:
            // Toast.makeText(this, "Adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRelatedSapatilhas(sapatilha: Sapatilha) {
        firestore.collection("Marcas")
            .get()
            .addOnSuccessListener { result ->
                val relatedSapatilhas = mutableListOf<Sapatilha>()

                for (document in result) {
                    firestore.collection("Marcas")
                        .document(document.id)
                        .collection("Sapatilhas")
                        .get()
                        .addOnSuccessListener { sapatilhasResult ->
                            for (sapatilhaDoc in sapatilhasResult) {
                                val relatedSapatilha = Sapatilha(
                                    nome = sapatilhaDoc.getString("modelo") ?: "",
                                    preco = sapatilhaDoc.getDouble("preco") ?: 0.0,
                                    imagem = sapatilhaDoc.getString("imagem") ?: "",
                                    categoria = sapatilhaDoc.getString("categoria") ?: "",
                                    genero = sapatilhaDoc.getString("genero") ?: ""
                                )

                                // Adicionar apenas sapatilhas do mesmo gênero e/ou categoria
                                if (relatedSapatilha.genero == sapatilha.genero &&
                                    relatedSapatilha.nome != sapatilha.nome
                                ) {
                                    relatedSapatilhas.add(relatedSapatilha)
                                }
                            }

                            // Atualizar RecyclerView com sapatilhas relacionadas
                            setupRecyclerView(relatedSapatilhas)
                        }
                }
            }
    }

    private fun setupRecyclerView(sapatilhasRelacionadas: List<Sapatilha>) {
        binding.rvSapatilhasRelacionadas.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSapatilhasRelacionadas.adapter = SapatilhaAdapter(sapatilhasRelacionadas, "")
    }
}
