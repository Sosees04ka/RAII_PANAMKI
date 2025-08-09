package com.example.mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.controllers.listeners.AuthController
import com.example.mobileapp.controllers.listeners.AuthListener


class HomeFragment : Fragment(),AuthListener {
    private lateinit var exitButton: ImageButton
    private lateinit var authController: AuthController
    private lateinit var buttonCreateCloth: FrameLayout
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
        buttonCreateCloth = view.findViewById<FrameLayout>(R.id.buttonAddCloth)
        buttonCreateCloth.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ClothAddFragment())
                .addToBackStack(null)
                .commit()
        }

        exitButton=view.findViewById(R.id.fab_exit)
        exitButton.setOnClickListener {
            authController.logout()
        }
        return view

    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
