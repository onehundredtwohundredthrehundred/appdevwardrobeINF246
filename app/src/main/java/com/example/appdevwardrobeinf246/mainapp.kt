package com.example.appdevwardrobeinf246

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainApp : AppCompatActivity() {

    private lateinit var gridWardrobe: GridLayout
    private var cameraImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainapp)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        gridWardrobe = findViewById(R.id.gridWardrobe)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        val username = intent.getStringExtra("username") ?: "User"
        tvWelcome.text = "Welcome back, $username!"

        fabAdd.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Image")
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val uri = cameraImageUri
                if (uri != null) {
                    addImageToGrid(uri)
                }
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                addImageToGrid(it)
            }
        }

    private fun openCamera() {
        try {
            val imageFile = File.createTempFile("wardrobe_", ".jpg", cacheDir)
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                imageFile
            )
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun addImageToGrid(uri: Uri) {
        val imageView = ImageView(this)
        imageView.setImageURI(uri)
        imageView.layoutParams = GridLayout.LayoutParams().apply {
            width = 300
            height = 300
            marginEnd = 16
            bottomMargin = 16
        }
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        gridWardrobe.addView(imageView)
    }
}