package com.example.mobileapp.models.look

import com.example.mobileapp.models.cloth.ClothWardrobeInfo

import java.io.Serializable


data class GeneratedOutfitsInfo(
    val id: Long,
    val score: Float,
    val like_status: Boolean,
    val description:String,
    val items: MutableList<ClothWardrobeInfo>
) : Serializable
