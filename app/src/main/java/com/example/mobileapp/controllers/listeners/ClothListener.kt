package com.example.mobileapp.controllers.listeners

import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothListResponse
import com.example.mobileapp.models.cloth.ClothPreview

interface ClothListener {
    fun onMessage(message: String)
    fun onUnauthorized()
    fun onClothInfoReceived(clothInfo: ClothInfo?)
    fun onListCloth(clothes: MutableList<ClothPreview>)
    fun onClothRemoved()
}