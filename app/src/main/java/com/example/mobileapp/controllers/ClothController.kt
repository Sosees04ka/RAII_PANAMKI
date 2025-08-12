package com.example.mobileapp.controllers

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.mobileapp.ClothFragmentOne
import com.example.mobileapp.R
import com.example.mobileapp.api.ApiClient
import com.example.mobileapp.api.AuthService
import com.example.mobileapp.api.ClothService
import com.example.mobileapp.controllers.listeners.AuthListener
import com.example.mobileapp.controllers.listeners.ClothListener
import com.example.mobileapp.models.auth.UserRequest
import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothListResponse
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.cloth.ClothRequest
import com.example.mobileapp.models.cloth.ClothResponse
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import com.example.mobileapp.userStorage.UserPreferences
import com.example.mobileapp.utils.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ClothController(private val context: Context, private val listener: ClothListener) {
    private val userPreferences = UserPreferences(context)
    private val apiService = ApiClient.getClient(context).create(ClothService::class.java)

    fun checkAuthorization():Boolean {
        return !userPreferences.isLoggedIn()
    }

    fun fetchClothInfo(clothId:Long) {
        val call = apiService.getCloth(clothId)

        call.enqueue(object : Callback<ClothInfo> {
            override fun onResponse(call: Call<ClothInfo>, response: Response<ClothInfo>) {
                if (response.isSuccessful) {
                    val clothBody = response.body()
                    listener.onClothInfoReceived(clothBody)
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ClothInfo>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
                Log.d("аваыаыва",t.message.toString())
            }
        })
    }

    fun removeCloth(id:Long){
        val call = apiService.deleteProduct(id)

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    listener.onClothRemoved()
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
            }
        })
    }

    fun getGeneratedOutfitByCloth(clothId: Long) {
        val call = apiService.getOutfitWithCloth(clothId)

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
                        listener.onGeneratedOutfitsInfo(productResponse)
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

    fun addCloth(name: String, image: Bitmap?) {
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

                apiService.addCloth(cloth).enqueue(object : Callback<ClothResponse> {
                    override fun onResponse(
                        call: Call<ClothResponse>,
                        response: Response<ClothResponse>
                    ) {
                        when {
                            !response.isSuccessful -> {
                                handleErrorResponse(response)
                            }
                            response.body() == null -> {
                                listener.onMessage("Пустой ответ от сервера")
                            }
                            else -> {
                                listener.onMessage("Вещь успешно добавлена")
                                Log.d("id", response.body()!!.clothId.toString())
                                val clothId = response.body()!!.clothId

                                listener.onMessage("Вещь успешно добавлена")

                                val bundle = Bundle().apply {
                                    if (clothId != null) {
                                        putLong("clothId", clothId)
                                    }
                                }

                                val fragment = ClothFragmentOne().apply {
                                    arguments = bundle
                                }

                                val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

                                fragmentManager?.popBackStack()

                                fragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, fragment)
                                    ?.commit()

                            }
                        }
                    }

                    override fun onFailure(call: Call<ClothResponse>, t: Throwable) {
                        listener.onMessage("Ошибка сети: ${t.message}")
                        Log.d("аваыаыва", t.message.toString())
                    }
                })
            }
        }
    }

    fun getPromoCloth(){
        val call = apiService.getClothPreviewList()

        call.enqueue(object : Callback<MutableList<ClothPreview>> {
            override fun onResponse(call: Call<MutableList<ClothPreview>>, response: Response<MutableList<ClothPreview>>) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        listener.onListCloth(productResponse)
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<MutableList<ClothPreview>>, t: Throwable) {
                listener.onMessage("Ошибка сети: ${t.message}")
                Log.d("prikol",t.message.toString())
            }
        })
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