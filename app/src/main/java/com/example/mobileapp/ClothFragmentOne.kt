package com.example.mobileapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatButton
import com.example.mobileapp.controllers.ClothController
import com.example.mobileapp.controllers.LookController
import com.example.mobileapp.controllers.listeners.ClothListener
import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import com.example.mobileapp.utils.Utils

class ClothFragmentOne : Fragment(), ClothListener {
    private lateinit var buttonRemove: AppCompatButton
    private lateinit var buttonWardrobe:AppCompatButton
    interface OnBackToWardrobeListener {
        fun onBackToWardrobe()
    }

    private var backListener: OnBackToWardrobeListener? = null

    private lateinit var clothController: ClothController
    private var clothId: Long = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBackToWardrobeListener) {
            backListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        backListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clothController = ClothController(requireContext(), this)
        clothId = arguments?.getLong("clothId", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (clothController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        return inflater.inflate(R.layout.fragment_cloth_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ClothFragment())
                .commit()
            backListener?.onBackToWardrobe()
        }
        buttonRemove=view.findViewById(R.id.deleteCloth)
        buttonWardrobe = view.findViewById(R.id.addWardrobe)
        buttonRemove.setOnClickListener{
            if (clothId != -1L) {
                clothController.removeCloth(clothId)
            } else {
                showToast("ID вещи не передан")
            }
        }
        buttonWardrobe.setOnClickListener{
            if (clothId != -1L) {
                clothController.getGeneratedOutfitByCloth(clothId)
            } else {
                showToast("ID вещи не передан")
            }
        }
        if (clothId != -1L) {
            clothController.fetchClothInfo(clothId)
        } else {
            showToast("ID вещи не передан")
        }
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    override fun onUnauthorized() {
        requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    override fun onClothInfoReceived(clothInfo: ClothInfo?) {
        Log.d("ClothFragmentOne", clothInfo?.master_category.toString())
        clothInfo?.let {
            try {
                val r = it.base_color.getOrNull(0) ?: 0
                val g = it.base_color.getOrNull(1) ?: 0
                val b = it.base_color.getOrNull(2) ?: 0


                val color = android.graphics.Color.rgb(r, g, b)

                val colorView: View? = view?.findViewById(R.id.color)
                colorView?.setBackgroundColor(color)
                if (it.picture != null && it.picture != "none") {
                    val imageBytes = Utils.decodeBase64(it.picture)
                    view?.findViewById<ImageView>(R.id.picture)?.setImageBitmap(imageBytes)
                } else {
                    view?.findViewById<ImageView>(R.id.picture)?.setImageResource(R.drawable.photo)
                }
                view?.findViewById<TextView>(R.id.nameCloth)?.text = it.product_display_name
                view?.findViewById<TextView>(R.id.subCategory)?.text = it.sub_category
                view?.findViewById<TextView>(R.id.masterCategory)?.text = it.master_category
                view?.findViewById<TextView>(R.id.usage)?.text = it.usage
            } catch (e: Exception) {
                showToast("Ошибка обработки цвета: ${e.message}")
            }
        }
    }

    override fun onListCloth(clothes: MutableList<ClothPreview>) {
        TODO("Not yet implemented")
    }

    override fun onGeneratedOutfitsInfo(list: MutableList<GeneratedOutfitsInfo>) {
        val fragment = WardrobeGenByClothFragment().apply {
            arguments = Bundle().apply {
                putSerializable("generatedOutfitsList", ArrayList(list))
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onClothRemoved() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ClothFragment())
            .commit()
        showToast("Удаление успешно!")
    }


    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
