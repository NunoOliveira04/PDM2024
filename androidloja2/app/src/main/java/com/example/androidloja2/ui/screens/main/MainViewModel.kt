package com.example.androidloja2.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidloja2.model.Product
import com.example.androidloja2.ui.components.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log

class MainViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _productsByBrand = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
    val productsByBrand: StateFlow<Map<String, List<Product>>> = _productsByBrand

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _isCartOpen = MutableStateFlow(false)
    val isCartOpen: StateFlow<Boolean> = _isCartOpen

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    init {
        loadProducts()
        loadUserCart()
    }

    fun setSelectedProduct(product: Product) {
        Log.d("MainViewModel", "Setting selected product: ${product.modelo}")
        _selectedProduct.value = product
    }

    fun toggleCart() {
        _isCartOpen.value = !_isCartOpen.value
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val brands = listOf("Nike", "Adidas", "Newbalance")
                val productsMap = mutableMapOf<String, List<Product>>()

                for (brand in brands) {
                    val snapshot = db.collection("Marcas").document(brand)
                        .collection("Sapatilhas")
                        .get()
                        .await()

                    val products = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(marca = brand)
                    }

                    if (products.isNotEmpty()) {
                        productsMap[brand] = products
                    }
                }

                _productsByBrand.value = productsMap
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading products", e)
            }
        }
    }

    fun addToCart(product: Product, selectedSize: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find {
            it.marca == product.marca &&
                    it.modelo == product.modelo &&
                    it.tamanho == selectedSize
        }

        if (existingItem != null) {
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(quantidade = existingItem.quantidade + 1)
        } else {
            currentItems.add(
                CartItem(
                    marca = product.marca,
                    modelo = product.modelo,
                    tamanho = selectedSize,
                    quantidade = 1,
                    preco = product.preco,
                    imagem = product.imagem
                )
            )
        }

        _cartItems.value = currentItems
        saveCartToFirestore()
    }


    fun clearCart() {
        viewModelScope.launch {
            try {
                _cartItems.value = emptyList()
                saveCartToFirestore()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error clearing cart", e)
            }
        }
    }

    fun reloadUserCart() {
        loadUserCart()
    }

    private fun loadUserCart() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: return@launch
                val userDoc = db.collection("Usuarios")
                    .whereEqualTo("email", user.email)
                    .get()
                    .await()

                val userId = userDoc.documents.firstOrNull()?.id ?: return@launch
                val cartRef = db.collection("Carrinho").document(userId)
                val snapshot = cartRef.get().await()

                val cartItems = snapshot.get("items") as? List<Map<String, Any>> ?: return@launch
                _cartItems.value = cartItems.map { item ->
                    CartItem(
                        marca = item["marca"] as String,
                        modelo = item["modelo"] as String,
                        tamanho = (item["tamanho"] as Long).toInt(),
                        quantidade = (item["quantidade"] as Long).toInt(),
                        preco = (item["preco"] as Number).toDouble(),
                        imagem = item["imagem"] as String
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading cart", e)
            }
        }
    }

    private fun saveCartToFirestore() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: return@launch
                val userDoc = db.collection("Usuarios")
                    .whereEqualTo("email", user.email)
                    .get()
                    .await()

                val userId = userDoc.documents.firstOrNull()?.id ?: return@launch
                Log.d("SaveCart", "Salvando carrinho para usuário: $userId")
                Log.d("SaveCart", "Número de itens a salvar: ${_cartItems.value.size}")

                val cartRef = db.collection("Carrinho").document(userId)
                val cartData = hashMapOf(
                    "items" to _cartItems.value.map { item ->
                        hashMapOf(
                            "marca" to item.marca,
                            "modelo" to item.modelo,
                            "tamanho" to item.tamanho,
                            "quantidade" to item.quantidade,
                            "preco" to item.preco,
                            "imagem" to item.imagem
                        )
                    }
                )

                cartRef.set(cartData).await()
                Log.d("SaveCart", "Carrinho salvo com sucesso")
            } catch (e: Exception) {
                Log.e("SaveCart", "Erro ao salvar carrinho", e)
            }
        }
    }


    fun removeFromCart(item: CartItem) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find {
            it.marca == item.marca &&
                    it.modelo == item.modelo &&
                    it.tamanho == item.tamanho
        }

        existingItem?.let {
            val index = currentItems.indexOf(it)
            if (it.quantidade > 1) {
                currentItems[index] = it.copy(quantidade = it.quantidade - 1)
            } else {
                currentItems.removeAt(index)
            }
            _cartItems.value = currentItems
            saveCartToFirestore()
        }
    }

    fun importCart(carrinhoId: String) {
        viewModelScope.launch {
            try {
                Log.d("ImportCart", "Tentando importar carrinho ID: $carrinhoId")

                // Verificar se o carrinho que queremos importar existe
                val cartRef = db.collection("Carrinho").document(carrinhoId)
                val cartSnapshot = cartRef.get().await()

                if (cartSnapshot.exists()) {
                    Log.d("ImportCart", "Carrinho encontrado")
                    val importedItems = cartSnapshot.get("items") as? List<Map<String, Any>>
                    Log.d("ImportCart", "Itens importados: ${importedItems?.size ?: 0}")

                    if (importedItems == null || importedItems.isEmpty()) {
                        Log.d("ImportCart", "Carrinho vazio ou formato inválido")
                        return@launch
                    }

                    // Pegar os itens atuais do carrinho
                    val currentItems = _cartItems.value.toMutableList()
                    Log.d("ImportCart", "Itens atuais no carrinho: ${currentItems.size}")

                    // Converter itens importados
                    val importedCartItems = importedItems.map { item ->
                        CartItem(
                            marca = item["marca"] as String,
                            modelo = item["modelo"] as String,
                            tamanho = (item["tamanho"] as Long).toInt(),
                            quantidade = (item["quantidade"] as Long).toInt(),
                            preco = (item["preco"] as Number).toDouble(),
                            imagem = item["imagem"] as String
                        )
                    }
                    Log.d("ImportCart", "Itens convertidos: ${importedCartItems.size}")

                    // Adicionar ou atualizar itens
                    importedCartItems.forEach { importedItem ->
                        val existingItem = currentItems.find {
                            it.marca == importedItem.marca &&
                                    it.modelo == importedItem.modelo &&
                                    it.tamanho == importedItem.tamanho
                        }

                        if (existingItem != null) {
                            Log.d("ImportCart", "Item existente encontrado: ${importedItem.modelo}")
                            val index = currentItems.indexOf(existingItem)
                            currentItems[index] = existingItem.copy(
                                quantidade = existingItem.quantidade + importedItem.quantidade
                            )
                        } else {
                            Log.d("ImportCart", "Adicionando novo item: ${importedItem.modelo}")
                            currentItems.add(importedItem)
                        }
                    }

                    Log.d("ImportCart", "Total de itens após importação: ${currentItems.size}")

                    // Atualizar o estado local
                    _cartItems.value = currentItems

                    // Salvar no Firestore
                    saveCartToFirestore()

                    Log.d("ImportCart", "Importação concluída com sucesso")
                } else {
                    Log.d("ImportCart", "Carrinho não encontrado")
                }
            } catch (e: Exception) {
                Log.e("ImportCart", "Erro ao importar carrinho", e)
            }
        }
    }

    private suspend fun getUserIdNumber(): String {
        val user = auth.currentUser ?: return ""
        val userSnapshot = db.collection("Usuarios")
            .whereEqualTo("email", user.email)
            .get()
            .await()

        return userSnapshot.documents.firstOrNull()?.id ?: ""
    }

    suspend fun getCurrentUserIdForCart(): String {
        val user = auth.currentUser ?: return ""
        val userSnapshot = db.collection("Usuarios")
            .whereEqualTo("email", user.email)
            .get()
            .await()

        return userSnapshot.documents.firstOrNull()?.id ?: ""
    }

    fun getUserId(): String {
        var userId = ""
        viewModelScope.launch {
            userId = getCurrentUserIdForCart()
        }
        return userId
    }

    private suspend fun getCurrentUserId(): String? {
        val user = auth.currentUser ?: return null
        val userDoc = db.collection("Usuarios")
            .whereEqualTo("email", user.email)
            .get()
            .await()

        return userDoc.documents.firstOrNull()?.id
    }

}