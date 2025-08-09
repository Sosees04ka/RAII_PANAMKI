package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileapp.controllers.listeners.AuthController
import com.example.mobileapp.controllers.listeners.AuthListener

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,AuthListener {

    private lateinit var spinner: Spinner
    private val paths = arrayOf("Мужчина", "Женщина")
    var selectedGender: Int = -1
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        authController = AuthController(this,this)
        if (authController.isUserLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        spinner = findViewById(R.id.genderSpinner)
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_dropdown_item,
            paths
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        findViewById<Spinner>(R.id.genderSpinner).adapter = adapter
        spinner.onItemSelectedListener = this

        val registerButton: Button = findViewById(R.id.regButton)
        registerButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailLoginText).text.toString().trim()
            val login = findViewById<EditText>(R.id.loginLoginText).text.toString().trim()
            val password = findViewById<EditText>(R.id.passwordText).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.passwordConfirmText).text.toString().trim()

            authController.registerUser(email, login, password,selectedGender, confirmPassword)
        }

        val loginLink: TextView = findViewById(R.id.registerText)
        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemSelected(
        parent: AdapterView<*>?, view: View?, position: Int, id: Long
    ) {
        selectedGender = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedGender = -1
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
