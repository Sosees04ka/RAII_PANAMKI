package com.example.mobileapp.api

import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothListResponse
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.cloth.ClothRequest
import com.example.mobileapp.models.cloth.ClothResponse
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ClothService {
    @POST("/api/cloth/add")
    fun addCloth(@Body request:ClothRequest):Call<ClothResponse>

    @GET("/api/cloth/{id}")
    fun getCloth(@Path("id") clothId: Long): Call<ClothInfo>

    @DELETE("/api/cloth/delete/{id}")
    fun deleteProduct(@Path("id") clothId: Long): Call<Unit>

    @GET("/api/cloth/preview")
    fun getClothPreviewList():Call<MutableList<ClothPreview>>

    @GET("/api/look/cloth/{id}")
    fun getOutfitWithCloth(@Path("id") clothId: Long):Call<MutableList<GeneratedOutfitsInfo>>
}