package com.example.androidloja.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidloja.databinding.ActivityHomeBinding
import com.example.androidloja.home.adapters.MarcaAdapter
import com.example.androidloja.models.Marca
import com.example.androidloja.models.Sapatilha
import com.google.firebase.firestore.FirebaseFirestore

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

        // Buscar dados do Firestore
        fetchMarcasFromFirestore()
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