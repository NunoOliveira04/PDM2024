package com.example.androidloja.autenticacao

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidloja.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.androidloja.home.HomeActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val name = binding.etName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || phone.isEmpty() || name.isEmpty()) {
                Snackbar.make(binding.root, "Por favor, preencha todos os campos.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser!!.uid
                        val user = mapOf(
                            "nome" to name,
                            "telefone" to phone,
                            "email" to email
                        )

                        firestore.collection("Usuarios")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Snackbar.make(binding.root, "Registo efetuado com sucesso!", Snackbar.LENGTH_LONG).show()
                                // Enviar o ID do utilizador para a HomeActivity
                                val intent = Intent(this, HomeActivity::class.java).apply {
                                    putExtra("USER_ID", userId)
                                }
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Snackbar.make(binding.root, "Erro ao salvar os dados do utilizador.", Snackbar.LENGTH_LONG).show()
                            }
                    } else {
                        val errorMessage = when (task.exception?.message) {
                            "The email address is badly formatted." -> "O formato do email está incorreto."
                            "The email address is already in use by another account." -> "O email já está em uso."
                            else -> "Erro no registo. Por favor, tente novamente."
                        }
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }
}
