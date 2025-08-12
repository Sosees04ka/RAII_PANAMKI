package com.example.mobileapp.api

import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.cloth.ClothRequest
import com.example.mobileapp.models.cloth.ClothResponse
import com.example.mobileapp.models.cloth.ClothWardrobeInfo
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LookService {

    @GET("/api/look/{id}")
    fun getLook(@Path("id") lookId: Long): Call<GeneratedOutfitsInfo>

    @POST("/api/look/like/{id}")
    fun likeLook(@Path("id") lookId: Long):Call<Void>


    @POST("/api/look/dislike/{id}")
    fun dislikeLook(@Path("id") lookId: Long):Call<Void>

    @GET("/api/look")
    fun look():Call<MutableList<GeneratedOutfitsInfo>>

    @GET("api/look/weather")
    fun getWeatherLook(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Call<MutableList<GeneratedOutfitsInfo>>


    @GET("api/look/picture")
    fun getPhotoLook(@Body request:ClothRequest):Call<MutableList<GeneratedOutfitsInfo>>
}