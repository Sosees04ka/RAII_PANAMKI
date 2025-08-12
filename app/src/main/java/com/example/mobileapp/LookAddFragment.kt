package com.example.mobileapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mobileapp.controllers.LookController
import com.example.mobileapp.controllers.listeners.LookListener
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.ByteArrayOutputStream

class LookAddFragment : Fragment(),LookListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var lookController: LookController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lookController = LookController(requireContext(), this)
        return inflater.inflate(R.layout.fragment_look_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (lookController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val buttonLookWeather = view.findViewById<FrameLayout>(R.id.buttonLookWeather)
        buttonLookWeather.setOnClickListener {
            buttonLookWeather.isEnabled=false
            buttonLookWeather.isClickable=false
            getLocation()
        }
        val buttonPhoto = view.findViewById<FrameLayout>(R.id.buttonLookPhoto)
        val buttonLookWithCloth = view.findViewById<FrameLayout>(R.id.buttonLookWithCloth)
        buttonLookWithCloth.setOnClickListener {
            buttonLookWithCloth.isEnabled=false
            buttonLookWithCloth.isClickable=false
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ClothFragmentList())
                .addToBackStack(null)
                .commit()
        }
        buttonPhoto.setOnClickListener{
            buttonPhoto.isEnabled=false
            buttonPhoto.isClickable=false
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PhotoLookFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getLocation() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("coords",location.latitude.toString() + " " + location.longitude)
                    lookController.getLooksByWeather(location.latitude,location.longitude)
                } else {
                    requestNewLocationData()
                }
            }
        }
    }

    private fun requestNewLocationData() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        )
            .setMaxUpdates(1)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        Log.d("coords",location.latitude.toString() + " " + location.longitude)
                        lookController.getLooksByWeather(location.latitude,location.longitude)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось получить новую локацию",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            null
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                getLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Разрешение на геолокацию отклонено",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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
        TODO("Not yet implemented")
    }

    override fun onLookReceived(list: MutableList<GeneratedOutfitsInfo>) {
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

    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
