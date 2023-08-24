package com.example.permissionmanage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.permissionaccessmanager.PermissionAccess
import com.example.permissionmanage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var permissionAccess: PermissionAccess

    private val getImg = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
        imageUri?.let {
            binding.circleImageView.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                binding.circleImageView.setImageBitmap(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionAccess = PermissionAccess.with(this@MainActivity,this)

        binding.selectImage.setOnClickListener {
            permissionAccess.sdkIntAboveOreo {
                permissionAccess.isPermissionGranted(this, android.Manifest.permission.CAMERA) { granted ->
                    if (granted) {
                        showImageSelectionDialog()
                        Log.d("MainActivity", "Permission granted")
                    } else {
                        // Ask for permission if denied
                        permissionAccess.registerActivityForResult?.launch(android.Manifest.permission.CAMERA)
                    }
                }
            }
        }
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("Gallery", "Camera")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (options[which]) {
                "Gallery" -> getImg.launch("image/*")
                "Camera" -> openCamera()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }
}