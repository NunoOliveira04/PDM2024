package com.example.androidloja2.ui.screens.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.example.androidloja2.ui.screens.main.MainViewModel

class LoginViewModel(private val mainViewModel: MainViewModel) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                mainViewModel.reloadUserCart()
                onResult(true, null)
            }
            .addOnFailureListener {
                val message = when {
                    it is FirebaseAuthInvalidCredentialsException -> "Email ou senha incorretos"
                    it is FirebaseAuthInvalidUserException -> "Usuário não encontrado"
                    else -> "Erro ao fazer login. Tente novamente."
                }
                onResult(false, message)
            }
    }
}