package com.example.mobileapp.models.cloth

import org.json.JSONObject

data class ClothInfo (
    val id:Long,
    val user_id:Int,
    val product_display_name:String,
    val sub_category:String,
    val master_category:String,
    val usage:String,
    val picture:String,
    val base_color: List<Int>
)