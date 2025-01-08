package com.example.androidloja.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidloja.databinding.ActivityCheckoutBinding
import com.example.androidloja.home.adapters.CarrinhoAdapter
import com.example.androidloja.models.CarrinhoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var firestore: FirebaseFirestore
    private var totalCompra: Double = 0.0
    private lateinit var carrinhoItems: List<CarrinhoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Receber os itens do carrinho e total
        @Suppress("DEPRECATION")
        carrinhoItems = intent.getParcelableArrayListExtra("itens_carrinho") ?: listOf()
        totalCompra = intent.getDoubleExtra("total_compra", 0.0)

        // Configurar RecyclerView
        binding.rvResumo.layoutManager = LinearLayoutManager(this)
        binding.rvResumo.adapter = CarrinhoAdapter(carrinhoItems) { _ ->
        }

        // Mostrar total
        binding.tvTotalCheckout.text = String.format("Total: € %.2f", totalCompra)

        // Configurar botão de finalizar
        binding.btnFinalizarCompra.setOnClickListener {
            if (validarCampos()) {
                finalizarCompra()
            }
        }
    }


    private fun finalizarCompra() {
        if (!validarCampos()) {
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val metodoPagamento = when (binding.rgPagamento.checkedRadioButtonId) {
            binding.rbMBWay.id -> "MB WAY"
            binding.rbPayPal.id -> "PayPal"
            binding.rbCartao.id -> "Cartão de Débito"
            else -> "Desconhecido"
        }

        // Criar documento do pedido
        val pedido = hashMapOf(
            "userId" to userId,
            "dataPedido" to System.currentTimeMillis(),
            "nome" to binding.etNome.text.toString(),
            "morada" to binding.etMorada.text.toString(),
            "telefone" to binding.etTelefone.text.toString(),
            "metodoPagamento" to metodoPagamento,
            "valorTotal" to totalCompra,
            "itens" to carrinhoItems.map { item ->
                hashMapOf(
                    "marca" to item.marca,
                    "modelo" to item.modelo,
                    "quantidade" to item.quantidade,
                    "tamanho" to item.tamanho,
                    "preco" to item.preco,
                    "precoTotal" to (item.preco * item.quantidade)
                )
            }
        )

        // Salvar pedido no Firestore
        firestore.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener {
                // Limpar carrinho após salvar o pedido
                limparCarrinho(userId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao finalizar pedido: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun limparCarrinho(userId: String) {
        firestore.collection("carrinho")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val batch = firestore.batch()
                documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    Toast.makeText(this, "Pedido realizado com sucesso!", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao limpar carrinho: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun validarCampos(): Boolean {
        val nome = binding.etNome.text.toString()
        val morada = binding.etMorada.text.toString()
        val telefone = binding.etTelefone.text.toString()

        if (nome.isEmpty() || morada.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.rgPagamento.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Por favor, selecione um método de pagamento", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}