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
import com.example.mobileapp.controllers.listeners.ClothListener
import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.utils.Utils

class ClothFragmentOne : Fragment(), ClothListener {
    private lateinit var buttonRemove: AppCompatButton
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

        // Получаем clothId из аргументов
        clothId = arguments?.getLong("clothId", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cloth_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Заменяем текущий фрагмент на ClothFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ClothFragment())
                .commit()

            // Уведомляем активити обновить выдление в навбаре
            backListener?.onBackToWardrobe()
        }
        buttonRemove=view.findViewById(R.id.deleteCloth)
        buttonRemove.setOnClickListener{
            if (clothId != -1L) {
                clothController.removeCloth(clothId)
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

                val colorView: View = requireView().findViewById(R.id.color)
                colorView.setBackgroundColor(color)
                if (it.picture != null && it.picture != "none") {
                    val imageBytes = Utils.decodeBase64(it.picture)
                    requireView().findViewById<ImageView>(R.id.picture).setImageBitmap(imageBytes)
                } else {
                    requireView().findViewById<ImageView>(R.id.picture).setImageResource(R.drawable.photo)
                }
                requireView().findViewById<TextView>(R.id.nameCloth).text = it.product_display_name
                requireView().findViewById<TextView>(R.id.subCategory).text = it.sub_category
                requireView().findViewById<TextView>(R.id.masterCategory).text = it.master_category
                requireView().findViewById<TextView>(R.id.usage).text = it.usage
            } catch (e: Exception) {
                showToast("Ошибка обработки цвета: ${e.message}")
            }
        }
    }

    override fun onListCloth(clothes: MutableList<ClothPreview>) {
        TODO("Not yet implemented")
    }

    override fun onClothRemoved() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ClothFragment())
            .commit()
        showToast("Удаление успешно!")
    }


    private fun showToast(message: String) {
        Log.d("msg",message)
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
