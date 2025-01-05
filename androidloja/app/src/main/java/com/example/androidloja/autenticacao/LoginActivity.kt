package com.example.androidloja.autenticacao

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidloja.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.example.androidloja.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Botão de login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "Por favor, preencha todos os campos.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(binding.root, "Login efetuado com sucesso!", Snackbar.LENGTH_LONG).show()
                        // Redireciona para HomeActivity
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        // Mensagens personalizadas baseadas no erro
                        val errorMessage = when (task.exception?.message) {
                            "The email address is badly formatted." -> "O formato do email está incorreto."
                            "There is no user record corresponding to this identifier." -> "O utilizador não foi encontrado."
                            "The password is invalid or the user does not have a password." -> "Palavra-passe incorreta."
                            else -> "Erro no login. Por favor, tente novamente."
                        }
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        // Redirecionar para a tela de registo
        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
