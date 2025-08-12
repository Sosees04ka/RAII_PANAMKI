package com.example.mobileapp.controllers.listeners

import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothListResponse
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.look.GeneratedOutfitsInfo

interface ClothListener {
    fun onMessage(message: String)
    fun onUnauthorized()
    fun onClothInfoReceived(clothInfo: ClothInfo?)
    fun onListCloth(clothes: MutableList<ClothPreview>)
    fun onGeneratedOutfitsInfo(list:MutableList<GeneratedOutfitsInfo>)
    fun onClothRemoved()
}