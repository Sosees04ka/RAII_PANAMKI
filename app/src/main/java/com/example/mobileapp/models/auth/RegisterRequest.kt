package com.example.mobileapp.models.auth

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val gender: Int
)