package com.example.mobileapp

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.mobileapp.controllers.ClothController
import com.example.mobileapp.controllers.LookController
import com.example.mobileapp.controllers.listeners.AuthController
import com.example.mobileapp.controllers.listeners.ClothListener
import com.example.mobileapp.controllers.listeners.LookListener
import com.example.mobileapp.models.cloth.ClothInfo
import com.example.mobileapp.models.cloth.ClothPreview
import com.example.mobileapp.models.look.GeneratedOutfitsInfo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class PhotoLookFragment : Fragment(),LookListener {
    private lateinit var imageViewPhoto: ImageView
    private var cameraImageUri: Uri? = null
    private lateinit var buttonAddClothTo:AppCompatButton
    private lateinit var lookController: LookController
    private lateinit var textView:EditText
    private val galleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageViewPhoto.setImageURI(uri)
        }
    }

    private val cameraResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraImageUri != null) {
            imageViewPhoto.setImageURI(cameraImageUri)
        } else {
            Toast.makeText(requireContext(), "Фото не было сделано", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showImagePickerDialog()
        } else {
            Toast.makeText(
                requireContext(),
                "Для работы с камерой необходимо разрешение",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_photo_look, container, false)
        lookController = LookController(requireActivity(), this)
        if (lookController.checkAuthorization()) {
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        textView=view.findViewById(R.id.nameCloth)
        buttonAddClothTo=view.findViewById(R.id.buttonAddClothTo)
        buttonAddClothTo.setOnClickListener{
            val defaultDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.photo)
            val currentDrawable = imageViewPhoto.drawable

            val imageBitmap = if (currentDrawable != null && currentDrawable.constantState == defaultDrawable?.constantState) {
                null
            } else {
                val originalBitmap = (currentDrawable as? BitmapDrawable)?.bitmap
                originalBitmap?.let { compressBitmap(it, 60) } // сжатие до 70%
            }

            if(imageBitmap!=null){
                buttonAddClothTo.isEnabled = false
                buttonAddClothTo.isClickable = false
            }

            lookController.getLooksByPhoto(textView.text.toString(), imageBitmap)

        }
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto)
        view.findViewById<View>(R.id.buttonSelectPhoto).setOnClickListener {
            checkCameraPermissionAndShowDialog()
        }

        return view
    }

    fun compressBitmap(bitmap: Bitmap, quality: Int = 80): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    private fun checkCameraPermissionAndShowDialog() {
        when {
            requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED -> {
                showImagePickerDialog()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Требуется разрешение")
                    .setMessage("Для съемки фото необходимо разрешение на использование камеры")
                    .setPositiveButton("OK") { _, _ ->
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = mutableListOf("Галерея")
        val hasCamera = requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

        if (hasCamera) {
            options.add(0, "Камера")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите источник")
            .setItems(options.toTypedArray()) { _, which ->
                when {
                    hasCamera && which == 0 -> openCamera()
                    else -> openGallery()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun openCamera() {
        try {
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val photoFile = File.createTempFile(
                "IMG_${System.currentTimeMillis()}",
                ".jpg",
                storageDir
            ).apply {
                cameraImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    this
                )
            }

            cameraResult.launch(cameraImageUri)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Ошибка при создании файла", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(requireContext(), "Ошибка FileProvider", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openGallery() {
        galleryResult.launch("image/*")
    }

    override fun onMessage(message: String) {
        showToast(message)
    }

    override fun onUnauthorized() {
        requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    override fun onOutfitInfoReceived(outfit: GeneratedOutfitsInfo?) {
        TODO("Not yet implemented")
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


