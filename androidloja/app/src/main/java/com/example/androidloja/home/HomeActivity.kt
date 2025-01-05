package com.example.androidloja.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidloja.MainActivity
import com.example.androidloja.databinding.ActivityHomeBinding
import com.example.androidloja.home.adapters.MarcaAdapter
import com.example.androidloja.models.Marca
import com.example.androidloja.models.Sapatilha
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.example.androidloja.R

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        binding.rvMarcas.layoutManager = LinearLayoutManager(this)

        // Configurar funcionalidade do menu lateral
        configureDrawer()

        // Buscar dados do Firestore
        fetchMarcasFromFirestore()
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
                    // Fechar o menu lateral ao clicar na página principal
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
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

    private fun fetchMarcasFromFirestore() {
        firestore.collection("Marcas")
            .get()
            .addOnSuccessListener { result ->
                val marcas = mutableListOf<Marca>()

                for (document in result) {
                    val nomeMarca = document.getString("nome") ?: ""
                    val sapatilhasList = mutableListOf<Sapatilha>()

                    // Obter lista de sapatilhas dentro da marca
                    firestore.collection("Marcas")
                        .document(document.id)
                        .collection("Sapatilhas")
                        .get()
                        .addOnSuccessListener { sapatilhasResult ->
                            for (sapatilhaDoc in sapatilhasResult) {
                                val sapatilha = Sapatilha(
                                    nome = sapatilhaDoc.getString("modelo") ?: "",
                                    preco = sapatilhaDoc.getDouble("preco") ?: 0.0,
                                    imagem = sapatilhaDoc.getString("imagem") ?: "",
                                    categoria = sapatilhaDoc.getString("categoria") ?: "",
                                    genero = sapatilhaDoc.getString("genero") ?: ""
                                )
                                sapatilhasList.add(sapatilha)
                            }

                            // Adicionar marca com suas sapatilhas
                            val marca = Marca(nome = nomeMarca, sapatilhas = sapatilhasList)
                            marcas.add(marca)

                            // Atualizar RecyclerView com os dados
                            binding.rvMarcas.adapter = MarcaAdapter(marcas)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erro ao buscar sapatilhas: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar marcas: ${e.message}")
            }
    }
}
