package com.example.mobileapp.models.auth

data class LoginResponse(
    val token: String? = null,
    val error: String? = null
)