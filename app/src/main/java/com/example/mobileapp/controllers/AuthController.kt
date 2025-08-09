package com.example.mobileapp.controllers.listeners

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.mobileapp.LoginActivity
import com.example.mobileapp.MainActivity
import com.example.mobileapp.RegisterActivity
import com.example.mobileapp.api.ApiClient
import com.example.mobileapp.api.AuthService
import com.example.mobileapp.models.auth.LoginResponse
import com.example.mobileapp.models.auth.RegisterRequest
import com.example.mobileapp.models.auth.RegisterResponse
import com.example.mobileapp.models.auth.UserRequest
import com.example.mobileapp.userStorage.UserPreferences
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthController(private val context: Context, private val authListener: AuthListener) {
    private val userPreferences = UserPreferences(context)
    private val apiService = ApiClient.getClient(context).create(AuthService::class.java)

    fun isUserLoggedIn():Boolean{
        return userPreferences.isLoggedIn()
    }

    fun checkAuthorization():Boolean {
        return !userPreferences.isLoggedIn()
    }

    fun loginUser(login:String,password: String){
        val errorMessage = validateLoginData(login, password)
        if (errorMessage != null) {
            authListener.onMessage(errorMessage)
            return
        }

        val user = UserRequest(
            email = login,
            password = password
        )
        val call = apiService.login(user)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.token != null) {
                        userPreferences.saveToken(responseBody.token)
                        userPreferences.saveLoginStatus(true)

                        authListener.onMessage("Успешный вход")

                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                    } else {
                        authListener.onMessage(responseBody?.error ?: "Ошибка авторизации")
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                authListener.onMessage("Ошибка сети: ${t.message}")
            }
        })
    }
    private fun validateLoginData(
        email: String,
        password: String,
    ): String? {
        return when {
            email.isEmpty() -> "Введите email"
            password.isEmpty() -> "Введите пароль"
            else -> null
        }
    }

    private fun validateRegistrationData(
        email: String,
        name: String,
        gender:Int,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            email.isEmpty() -> "Введите email"
            name.isEmpty() -> "Введите логин"
            gender != 0 && gender != 1 -> "Выберите пол"
            password.isEmpty() -> "Введите пароль"
            confirmPassword.isEmpty() -> "Повторите пароль"
            password != confirmPassword -> "Пароли не совпадают"
            else -> null
        }
    }


    fun registerUser(email: String, name: String, password: String,gender:Int, confirmPassword: String) {
        val errorMessage = validateRegistrationData(email, name,gender, password, confirmPassword)
        if (errorMessage != null) {
            authListener.onMessage(errorMessage)
            return
        }

        val newUser = RegisterRequest(
            name = name,
            gender=gender,
            email = email,
            password = password
        )

        val call = apiService.register(newUser)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.error == null) {
                        authListener.onMessage("Регистрация успешна")
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        if (context is RegisterActivity) {
                            context.finish()
                        }
                    } else {
                        authListener.onMessage("Ошибка: ${responseBody.error}")
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                t.message?.let { Log.d("oisya", it) }
                authListener.onMessage("Ошибка сети: ${t.message}")
            }
        })
    }

    fun logout() {
        val call = apiService.logout()

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    authListener.onMessage("Успешный выход")
                    userPreferences.logout()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                authListener.onMessage("Ошибка сети: ${t.message}")
            }
        })
    }

    private fun handleErrorResponse(response: Response<*>) {
        val errorResponse = response.errorBody()?.string()
        val jsonObject = JSONObject(errorResponse!!)
        val errorMessage = jsonObject.optString("message", "Неизвестная ошибка")
        authListener.onMessage(errorMessage)
    }

}