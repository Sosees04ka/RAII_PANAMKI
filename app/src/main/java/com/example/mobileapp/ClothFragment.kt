package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobileapp.controllers.ClothController
import com.example.mobileapp.controllers.listeners.ClothListener
import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.placeholder.PlaceholderContent
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ClothFragment : Fragment(),ClothListener {

    private lateinit var fabAdd: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var clothController: ClothController
    private lateinit var productList: MutableList<ClothPreview>
    private lateinit var productAdapter: MyClothRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        recyclerView=view.findViewById(R.id.recyclerView)
        productList = mutableListOf()
        productAdapter = MyClothRecyclerViewAdapter(productList)
        recyclerView.adapter = productAdapter
        emptyView = view.findViewById(R.id.empty_view)
        fabAdd= view.findViewById(R.id.fab_add)
        clothController = ClothController(requireActivity(), this)
        if (clothController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ClothAddFragment())
                .addToBackStack(null)
                .commit()
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        updateList()

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        // ✅ Ограничиваем срабатывание refresh только на самом верху списка
        swipeRefreshLayout.setOnChildScrollUpCallback { _, _ ->
            recyclerView.canScrollVertically(-1)
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateList()
            updateEmptyView()
            swipeRefreshLayout.isRefreshing = false
        }

        productAdapter.onItemClickListener = { product ->
            val bundle = Bundle().apply {
                putLong("clothId", product.id)
            }

            val fragment = ClothFragmentOne().apply {
                arguments = bundle
            }

            val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

            fragmentManager?.popBackStack()

            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.commit()
        }

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

    fun updateList(){
        clothController.getPromoCloth()
    }
    override fun onMessage(message: String) {
        showToast(message)
    }

    override fun onUnauthorized() {
        TODO("Not yet implemented")
    }

    override fun onClothInfoReceived(clothInfo: ClothInfo?) {
        TODO("Not yet implemented")
    }

    override fun onListCloth(clothes: MutableList<ClothPreview>) {
        productAdapter.updateItems(clothes)
        updateEmptyView()
    }

    override fun onClothRemoved() {
        TODO("Not yet implemented")
    }
    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
