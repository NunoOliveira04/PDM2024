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
import com.example.androidloja.models.ContadorCarrinho
import com.example.androidloja.models.TamanhosSapatilhas
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog


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

        // Configurar menu do carrinho
        binding.btnCarrinhoMenu.setOnClickListener { showCarrinhoOptions() }

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

        // Adicionar listener para o botão de checkout
        binding.btnCheckout.setOnClickListener {
            if (carrinhoItems.isEmpty()) {
                Toast.makeText(this, "O carrinho está vazio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            iniciarCheckout()
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

            // Obter o ID do carrinho do usuário atual
            getOrCreateCarrinhoId(userId) { carrinhoId ->
                val carrinhoItem = mapOf(
                    "userId" to userId,
                    "carrinhoId" to carrinhoId,
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
                        Toast.makeText(this, "Produto adicionado ao carrinho #$carrinhoId", Toast.LENGTH_SHORT).show()
                        fetchCarrinhoItems()
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

    private fun showCarrinhoOptions() {
        val popup = PopupMenu(this, binding.btnCarrinhoMenu)
        popup.menu.apply {
            add(0, 1, 0, "Limpar Carrinho")
            add(0, 2, 1, "Exportar Carrinho")
            add(0, 3, 2, "Importar Carrinho")
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> limparCarrinho()
                2 -> exportarCarrinho()
                3 -> showImportCarrinhoDialog()
            }
            true
        }

        popup.show()
    }

    private fun limparCarrinho() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("carrinho")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val batch = firestore.batch()
                documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    Toast.makeText(this, "Carrinho limpo com sucesso", Toast.LENGTH_SHORT).show()
                    fetchCarrinhoItems()
                }
            }
    }

    private fun exportarCarrinho() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("carrinho")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Carrinho vazio", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                val carrinhoId = documents.documents[0].getLong("carrinhoId")
                showCarrinhoIdDialog(carrinhoId.toString())
            }
    }

    private fun showCarrinhoIdDialog(carrinhoId: String) {
        AlertDialog.Builder(this)
            .setTitle("ID do Carrinho")
            .setMessage("O ID do seu carrinho é: $carrinhoId")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showImportCarrinhoDialog() {
        val input = EditText(this)
        input.hint = "Digite o ID do carrinho"

        AlertDialog.Builder(this)
            .setTitle("Importar Carrinho")
            .setView(input)
            .setPositiveButton("Importar") { _, _ ->
                val carrinhoId = input.text.toString()
                if (carrinhoId.isNotEmpty()) {
                    importarCarrinho(carrinhoId)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun importarCarrinho(carrinhoIdStr: String) {
        val carrinhoIdImportar = carrinhoIdStr.toLongOrNull()
        if (carrinhoIdImportar == null) {
            Toast.makeText(this, "ID de carrinho inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Obter o ID do carrinho do usuário atual
        getOrCreateCarrinhoId(userId) { carrinhoIdAtual ->
            // Buscar itens do carrinho a ser importado
            firestore.collection("carrinho")
                .whereEqualTo("carrinhoId", carrinhoIdImportar)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "Carrinho não encontrado", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Copiar itens para o carrinho do usuário atual
                    val batch = firestore.batch()

                    documents.forEach { doc ->
                        val newDocRef = firestore.collection("carrinho").document()
                        val data = doc.data.toMutableMap().apply {
                            // Manter o ID do carrinho atual e trocar o userId
                            this["carrinhoId"] = carrinhoIdAtual
                            this["userId"] = userId
                        }
                        batch.set(newDocRef, data)
                    }

                    batch.commit().addOnSuccessListener {
                        Toast.makeText(this, "Itens importados para seu carrinho", Toast.LENGTH_SHORT).show()
                        fetchCarrinhoItems()
                    }
                }
        }
    }

    private fun getNextCarrinhoId(callback: (Long) -> Unit) {
        val contadorRef = firestore.collection("carrinhos_contador").document("contador")

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(contadorRef)
            val currentId = snapshot.getLong("ultimoId") ?: 0
            val nextId = currentId + 1

            transaction.set(contadorRef, mapOf("ultimoId" to nextId))
            nextId
        }.addOnSuccessListener { novoId ->
            callback(novoId)
        }
    }

    private fun getOrCreateCarrinhoId(userId: String, callback: (Long) -> Unit) {
        val carrinhoRef = firestore.collection("usuarios_carrinho").document(userId)

        carrinhoRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Se o usuário já tem um ID de carrinho, usar o existente
                val carrinhoId = document.getLong("carrinhoId") ?: 0
                callback(carrinhoId)
            } else {
                // Se não tem, criar um novo ID
                val contadorRef = firestore.collection("carrinhos_contador").document("contador")

                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(contadorRef)
                    val currentId = snapshot.getLong("ultimoId") ?: 0
                    val nextId = currentId + 1

                    // Atualiza o contador
                    transaction.set(contadorRef, mapOf("ultimoId" to nextId))
                    // Associa o novo ID ao usuário
                    transaction.set(carrinhoRef, mapOf("carrinhoId" to nextId))

                    nextId
                }.addOnSuccessListener { novoId ->
                    callback(novoId)
                }
            }
        }
    }

    private fun iniciarCheckout() {
        val total = carrinhoItems.sumOf { it.preco * it.quantidade }
        val intent = Intent(this, CheckoutActivity::class.java).apply {
            putParcelableArrayListExtra("itens_carrinho", ArrayList(carrinhoItems))
            putExtra("total_compra", total)
        }
        startActivityForResult(intent, CHECKOUT_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
            binding.drawerLayout.closeDrawer(binding.carrinhoDrawer)
            fetchCarrinhoItems()
        }
    }

    companion object {
        private const val CHECKOUT_REQUEST_CODE = 100
    }
}