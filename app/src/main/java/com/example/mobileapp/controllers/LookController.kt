package com.example.mobileapp.controllers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.mobileapp.ClothFragmentOne
import com.example.mobileapp.MainActivity
import com.example.mobileapp.R
import com.example.mobileapp.api.ApiClient
import com.example.mobileapp.api.AuthService
import com.example.mobileapp.api.LookService
import com.example.mobileapp.controllers.listeners.AuthListener
import com.example.mobileapp.controllers.listeners.ClothListener
import com.example.mobileapp.controllers.listeners.LookListener
import com.example.mobileapp.models.auth.LoginResponse
import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothRequest
import com.example.mobileapp.models.cloth.ClothResponse
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import com.example.mobileapp.userStorage.UserPreferences
import com.example.mobileapp.utils.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookController(private val context: Context, private val listener: LookListener) {

    private val userPreferences = UserPreferences(context)
    private val apiService = ApiClient.getClient(context).create(LookService::class.java)

    fun checkAuthorization(): Boolean {
        return !userPreferences.isLoggedIn()
    }

    fun getLookById(lookId:Long){
        val call = apiService.getLook(lookId)

        call.enqueue(object : Callback<GeneratedOutfitsInfo> {
            override fun onResponse(call: Call<GeneratedOutfitsInfo>, response: Response<GeneratedOutfitsInfo>) {
                if (response.isSuccessful) {
                    val clothBody = response.body()
                    Log.d("аваыаыва",clothBody?.like_status.toString())
                    listener.onOutfitInfoReceived(clothBody)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<GeneratedOutfitsInfo>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
                Log.d("аваыаыва",t.message.toString())
            }
        })
    }

    fun likeLook(lookId: Long){
        val call = apiService.likeLook(lookId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    listener?.onLookLiked(lookId)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
            }
        })
    }

    fun dislikeLook(lookId: Long){
        val call = apiService.dislikeLook(lookId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    listener?.onLookLiked(lookId)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
            }
        })
    }

    fun getLooks(){
        val call = apiService.look()

        call.enqueue(object : Callback<MutableList<GeneratedOutfitsInfo>> {
            override fun onResponse(
                call: Call<MutableList<GeneratedOutfitsInfo>>,
                response: Response<MutableList<GeneratedOutfitsInfo>>
            ) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (!productResponse.isNullOrEmpty()) {
                        productResponse.forEach { outfit ->
                            Log.d("Scores", "Score: ${outfit.score}")
                        }
                        listener.onLookReceived(productResponse)
                    } else {
                        listener.onMessage("Образы не найдены")
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<MutableList<GeneratedOutfitsInfo>>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
                Log.d("аваыаыва", t.message.toString())
            }


        })
    }

    fun getLooksByWeather(lat:Double,lon:Double){
        val call = apiService.getWeatherLook(lat,lon)

        call.enqueue(object : Callback<MutableList<GeneratedOutfitsInfo>> {
            override fun onResponse(
                call: Call<MutableList<GeneratedOutfitsInfo>>,
                response: Response<MutableList<GeneratedOutfitsInfo>>
            ) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (!productResponse.isNullOrEmpty()) {
                        productResponse.forEach { outfit ->
                            Log.d("Scores", "Score: ${outfit.score}")
                        }
                        listener.onLookReceived(productResponse)
                    } else {
                        listener.onMessage("Образы не найдены")
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<MutableList<GeneratedOutfitsInfo>>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
                Log.d("аваыаыва", t.message.toString())
            }


        })
    }

    fun getLooksByPhoto(name: String, image: Bitmap?) {
        when {
            name.isEmpty() -> {
                listener.onMessage("Название пустое")
                return
            }

            image == null -> {
                listener.onMessage("Изображение не выбрано")
                return
            }

            else -> {
                val imageBase64 = Utils.imageToBase64(image) ?: run {
                    listener.onMessage("Ошибка конвертации изображения")
                    return
                }

                val cloth = ClothRequest(
                    picture = imageBase64,
                    product_display_name = name
                )

                apiService.getPhotoLook(cloth).enqueue(object : Callback<MutableList<GeneratedOutfitsInfo>> {
                    override fun onResponse(
                        call: Call<MutableList<GeneratedOutfitsInfo>>,
                        response: Response<MutableList<GeneratedOutfitsInfo>>
                    ) {
                        if (response.isSuccessful) {
                            val productResponse = response.body()
                            if (!productResponse.isNullOrEmpty()) {
                                productResponse.forEach { outfit ->
                                    Log.d("Scores", "Score: ${outfit.score}")
                                }
                                listener.onLookReceived(productResponse)
                            } else {
                                listener.onMessage("Образы не найдены")
                            }
                        } else {
                            handleErrorResponse(response)
                        }
                    }

                    override fun onFailure(call: Call<MutableList<GeneratedOutfitsInfo>>, t: Throwable) {
                        listener.onMessage("Ошибка сети: ${t.message}")
                        Log.d("аваыаыва", t.message.toString())
                    }
                })
            }
        }
    }

    private fun handleErrorResponse(response: Response<*>) {
        if (response.code() == 401) {
            handleUnauthorizedError()
            listener.onMessage("Время сеанса истекло. Пожалуйста, войдите снова.")
        } else {
            val errorResponse = response.errorBody()?.string()
            val jsonObject = JSONObject(errorResponse ?: "{}")
            val errorMessage = jsonObject.optString("message", "Неизвестная ошибка")
            listener.onMessage(errorMessage)
        }
    }

    private fun handleUnauthorizedError() {
        userPreferences.logout()
        listener.onUnauthorized()
    }
}
