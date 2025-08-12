package com.example.mobileapp.models.look

data class LookInfo (
    val id:Long,
    val user_id:Long,
    val like_status: Int,
    val description:String,
    val score:Float,

)