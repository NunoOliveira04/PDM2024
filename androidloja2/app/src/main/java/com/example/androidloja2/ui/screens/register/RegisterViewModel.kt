package com.example.androidloja2.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private suspend fun getNextUserId(): Int {
        val snapshot = db.collection("Usuarios")
            .orderBy("id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return if (snapshot.isEmpty) {
            1
        } else {
            (snapshot.documents[0].getLong("id")?.toInt() ?: 0) + 1
        }
    }

    fun register(email: String, password: String, nome: String, telefone: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Criar autenticação
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = getNextUserId()

                // Salvar dados do usuário
                val userData = hashMapOf(
                    "id" to userId,
                    "email" to email,
                    "nome" to nome,
                    "telefone" to telefone
                )

                db.collection("Usuarios")
                    .document(userId.toString())
                    .set(userData)
                    .await()

                // Criar carrinho vazio para o usuário
                val carrinhoData = hashMapOf(
                    "items" to listOf<Map<String, Any>>()
                )

                db.collection("Carrinho")
                    .document(userId.toString())
                    .set(carrinhoData)
                    .await()

                onResult(true, null)
            } catch (e: Exception) {
                val message = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Senha muito fraca"
                    is FirebaseAuthInvalidCredentialsException -> "Email inválido"
                    is FirebaseAuthUserCollisionException -> "Email já cadastrado"
                    else -> "Erro ao registrar. Tente novamente."
                }
                onResult(false, message)
            }
        }
    }
}