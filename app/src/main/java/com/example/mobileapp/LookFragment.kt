package com.example.mobileapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LookFragment : Fragment() {

    private lateinit var fabAdd: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list2, container, false)

        emptyView = view.findViewById(R.id.empty_view)
        fabAdd = view.findViewById(R.id.fab_add_look)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = MyLookRecyclerViewAdapter(generateLooks())
        updateEmptyView()

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnChildScrollUpCallback { _, _ ->
            recyclerView.canScrollVertically(-1)
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateEmptyView()
            swipeRefreshLayout.isRefreshing = false
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

    private fun generateLooks(): List<LookItem> {
        // Заглушка — 10 одинаковых футболок
        return List(0) { LookItem(R.drawable.tshirt_example) }
    }
}
