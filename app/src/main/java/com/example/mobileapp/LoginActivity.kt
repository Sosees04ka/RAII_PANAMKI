package com.example.mobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.mobileapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Вход выполнен: $email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerText.setOnClickListener {
            Toast.makeText(this, "Переход к регистрации", Toast.LENGTH_SHORT).show()
        }

        val registerLink: TextView = findViewById(R.id.registerText)
        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}