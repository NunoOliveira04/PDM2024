package com.example.androidloja.autenticacao

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidloja.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Função de registo (implementação futura)
        binding.btnRegister.setOnClickListener {
            // Lógica para registar o utilizador
        }
    }
}
