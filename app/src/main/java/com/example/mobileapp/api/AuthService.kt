package com.example.mobileapp.api

import com.example.mobileapp.models.auth.LoginResponse
import com.example.mobileapp.models.auth.RegisterRequest
import com.example.mobileapp.models.auth.RegisterResponse
import com.example.mobileapp.models.auth.UserRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/api/auth/login")
    fun login(@Body request: UserRequest): Call<LoginResponse>

    @POST("/api/logout")
    fun logout(): Call<Void>
}