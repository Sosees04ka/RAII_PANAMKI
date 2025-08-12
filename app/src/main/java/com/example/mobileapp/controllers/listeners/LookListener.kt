package com.example.mobileapp.controllers.listeners

import com.example.mobileapp.models.look.GeneratedOutfitsInfo

interface LookListener {

    fun onMessage(message: String)
    fun onUnauthorized()
    fun onOutfitInfoReceived(outfit:GeneratedOutfitsInfo?)
    fun onLookLiked(lookId: Long)

    fun onLookReceived(list:MutableList<GeneratedOutfitsInfo>)
}