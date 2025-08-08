package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var spinner: Spinner
    private val paths = arrayOf("Мужчина", "Женщина")
    var selectedGender: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        spinner = findViewById(R.id.genderSpinner)

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_dropdown_item, // твой кастомный элемент
            paths
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        findViewById<Spinner>(R.id.genderSpinner).adapter = adapter

        spinner.onItemSelectedListener = this

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
}
