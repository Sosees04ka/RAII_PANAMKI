package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.controllers.listeners.AuthController
import com.example.mobileapp.controllers.listeners.AuthListener


class HomeFragment : Fragment(),AuthListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var exitButton: ImageButton
    private lateinit var authController: AuthController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authController = AuthController(requireActivity(), this)
        if (authController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = MyLookRecyclerViewAdapter(generateLooks())
        exitButton=view.findViewById(R.id.fab_exit)
        exitButton.setOnClickListener {
            authController.logout()
        }
        updateEmptyView()
        return view

    }
    private fun updateEmptyView() {
        val adapter = recyclerView.adapter
        if (adapter == null || adapter.itemCount == 0) {
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun generateLooks(): List<LookItem> {
        return List(4) { LookItem(R.drawable.tshirt_example) }
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
