package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileapp.controllers.LookController
import com.example.mobileapp.controllers.listeners.LookListener
import com.example.mobileapp.models.look.GeneratedOutfitsInfo

class WardrobeGenByClothFragment : Fragment(), LookListener {

    private lateinit var productAdapter: MyWardrobeGenByClothRecyclerViewAdapter
    private var outfitsList: MutableList<GeneratedOutfitsInfo> = mutableListOf()
    private lateinit var lookController: LookController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем список из аргументов, если есть
        arguments?.let {
            @Suppress("UNCHECKED_CAST")
            val list = it.getSerializable("generatedOutfitsList") as? ArrayList<GeneratedOutfitsInfo>
            if (list != null) {
                outfitsList.clear()
                outfitsList.addAll(list)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lookController = LookController(requireActivity(), this)
        parentFragmentManager.setFragmentResultListener("likeStatusChanged", viewLifecycleOwner) { key, bundle ->
            val lookId = bundle.getLong("lookId")
            val likeStatus = bundle.getBoolean("likeStatus")
            updateLikeStatusInList(lookId, likeStatus)
        }
        // Проверка авторизации
        if (lookController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return null
        }

        val view = inflater.inflate(R.layout.fragment_item_list3, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // Создаём адаптер с уже имеющимся списком
        productAdapter = MyWardrobeGenByClothRecyclerViewAdapter(outfitsList)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.setHasFixedSize(true)

        // Обработка клика по карточке
        productAdapter.onItemClickListener = { product ->
            val bundle = Bundle().apply {
                putLong("lookId", product.id)
            }
            val fragment = LookOneFragment().apply {
                arguments = bundle
            }
            val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)    // <- добавь сюда
                ?.commit()
        }
        productAdapter.onFavClickListener = {product ->
            if(!product.like_status){
                lookController.likeLook(product.id)
            }
            else{
                lookController.dislikeLook(product.id)
            }

        }


        return view
    }
    private fun updateLikeStatusInList(lookId: Long, likeStatus: Boolean) {
        val index = outfitsList.indexOfFirst { it.id == lookId }
        if (index != -1) {
            // Создаем копию элемента с обновленным статусом
            val updatedItem = outfitsList[index].copy(like_status = likeStatus)
            outfitsList[index] = updatedItem
            // Убедитесь, что адаптер обновляется на главном потоке
            requireActivity().runOnUiThread {
                productAdapter.notifyItemChanged(index)
            }
        }
    }
    /** Метод для обновления списка из контроллера/сети */
    fun updateOutfits(newList: List<GeneratedOutfitsInfo>) {
        outfitsList.clear()
        outfitsList.addAll(newList)
        productAdapter.notifyDataSetChanged()
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    override fun onUnauthorized() {
        requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    override fun onOutfitInfoReceived(outfit: GeneratedOutfitsInfo?) {
        // Если надо обновить UI с новым элементом
        outfit?.let {
            outfitsList.add(it)
            productAdapter.notifyItemInserted(outfitsList.size - 1)
        }
    }

    override fun onLookLiked(lookId: Long) {
        val index = outfitsList.indexOfFirst { it.id == lookId }
        if (index != -1) {
            val oldItem = outfitsList[index]
            val newStatus = !oldItem.like_status
            // Создаем новый объект с обновленным like_status
            val newItem = oldItem.copy(like_status = newStatus)
            outfitsList[index] = newItem
            productAdapter.notifyItemChanged(index)
        } else {
            showToast("Элемент с id $lookId не найден")
        }
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
