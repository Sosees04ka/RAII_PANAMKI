package com.example.mobileapp.models.cloth

import java.io.Serializable

data class ClothWardrobeInfo (
    val id:Long,
    val user_id:Long,
    val product_display_name:String,
    val master_category: String,
    val sub_category:String,
    val base_color: List<Int>,
    val usage: String,
    val picture: String
): Serializable