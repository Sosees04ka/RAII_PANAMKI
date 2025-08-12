package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobileapp.controllers.LookController
import com.example.mobileapp.controllers.listeners.LookListener
import com.example.mobileapp.models.cloth.ClothWardrobeInfo
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LookFragment : Fragment(),LookListener {

    private lateinit var fabAdd: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private var lookList:MutableList<GeneratedOutfitsInfo> = mutableListOf()
    private lateinit var lookController: LookController
    private lateinit var productAdapter: LooksAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lookController = LookController(requireContext(), this)
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list2, container, false)
        if (lookController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        productAdapter = LooksAdapter(lookList)

        emptyView = view.findViewById(R.id.empty_view)
        fabAdd = view.findViewById(R.id.fab_add_look)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = productAdapter
        updateEmptyView()

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnChildScrollUpCallback { _, _ ->
            recyclerView.canScrollVertically(-1)
        }

        swipeRefreshLayout.setOnRefreshListener {
            lookController.getLooks()
            swipeRefreshLayout.isRefreshing = false
        }
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
            lookList.remove(product)
            productAdapter.notifyDataSetChanged()
            updateEmptyView()

            // Отправляем на сервер
            lookController.dislikeLook(product.id)
        }
        updateList()
        return view
    }

    private fun updateEmptyView() {
        val adapter = recyclerView.adapter
        if (adapter == null || adapter.itemCount == 0) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    private fun updateList() {
        lookController.getLooks()
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    override fun onUnauthorized() {
        requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    override fun onOutfitInfoReceived(outfit: GeneratedOutfitsInfo?) {

    }

    override fun onLookLiked(lookId: Long) {
        updateList()
        updateEmptyView()
        onMessage("Успешно удалено")
    }

    override fun onLookReceived(list: MutableList<GeneratedOutfitsInfo>) {
        productAdapter.updateItems(list)
        updateEmptyView()
    }

    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

}
