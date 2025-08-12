package com.example.mobileapp

import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.mobileapp.controllers.ClothController
import com.example.mobileapp.controllers.LookController
import com.example.mobileapp.controllers.listeners.LookListener
import com.example.mobileapp.models.cloth.ClothWardrobeInfo
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import com.example.mobileapp.placeholder.PlaceholderContent

class LookOneFragment : Fragment(),LookListener {
    private lateinit var productAdapter: MylookOneRecyclerViewAdapter
    private var columnCount = 1
    private var isLiked = false
    private lateinit var outfitTitle:TextView
    private lateinit var lookController: LookController
    private var lookId:Long=-1
    private var lookList:MutableList<ClothWardrobeInfo> = mutableListOf()
    private lateinit var favButton:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lookController = LookController(requireContext(), this)
        lookId = arguments?.getLong("lookId", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (lookController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        val view = inflater.inflate(R.layout.fragment_item_list4, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        outfitTitle = view.findViewById(R.id.outfitTitle)// используйте правильный id
        lookList = mutableListOf()
        productAdapter = MylookOneRecyclerViewAdapter(lookList)
        favButton = view.findViewById(R.id.favButton)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        if (lookId != -1L) {
            lookController.getLookById(lookId)
        } else {
            showToast("ID вещи не передан")
        }
        favButton.setOnClickListener {
            if (isLiked) {
                lookController.dislikeLook(lookId)
            } else {
                lookController.likeLook(lookId)
            }
        }
        return view
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    override fun onUnauthorized() {
        requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun notifyLikeStatusChanged() {
        val result = Bundle().apply {
            putLong("lookId", lookId)
            putBoolean("likeStatus", isLiked)
        }
        parentFragmentManager.setFragmentResult("likeStatusChanged", result)
    }


    override fun onOutfitInfoReceived(outfit: GeneratedOutfitsInfo?) {
        if (outfit != null) {
            Log.d("LookOneFragment", "Like status: ${outfit.like_status}")
            isLiked = outfit.like_status

            if(outfit.like_status){
                favButton.setImageResource(R.drawable.ic_heart_filled)
            }
            else{
                favButton.setImageResource(R.drawable.ic_heart_outline)
            }
            val text = String.format("%.1f%%", outfit.score * 100)

            outfitTitle.text = text
            productAdapter.updateItems(outfit.items ?: mutableListOf())

            // Уведомляем об изменении статуса при загрузке
            notifyLikeStatusChanged()
        }
    }
    override fun onLookLiked(lookId: Long) {
        lookController.getLookById(lookId)
        notifyLikeStatusChanged()
    }

    override fun onLookReceived(list: MutableList<GeneratedOutfitsInfo>) {
        TODO("Not yet implemented")
    }


    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

}