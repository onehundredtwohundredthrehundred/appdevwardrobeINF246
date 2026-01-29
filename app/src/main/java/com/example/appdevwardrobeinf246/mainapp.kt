package com.example.appdevwardrobeinf246

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class MainApp : AppCompatActivity() {

    private lateinit var containerWardrobe: LinearLayout
    private lateinit var tvWelcome: TextView
    private var cameraImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainapp)

        tvWelcome = findViewById(R.id.tvWelcome)
        containerWardrobe = findViewById(R.id.containerWardrobe)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        val username = intent.getStringExtra("username") ?: "User"
        tvWelcome.text = "Welcome back, $username!"

        fabAdd.setOnClickListener {
            showImageSourceDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshWardrobe()
    }

    private fun refreshWardrobe() {
        val welcomeText = tvWelcome.text.toString()

        val childCount = containerWardrobe.childCount
        if (childCount > 1) {
            containerWardrobe.removeViews(1, childCount - 1)
        }

        val itemsByArea = tempdb.items.groupBy { it.area }

        val areaOrder = listOf("Top", "Bottom", "Headwear", "Footwear", "Accessory")

        for (area in areaOrder) {
            val areaItems = itemsByArea[area]
            if (!areaItems.isNullOrEmpty()) {
                addSectionHeader(area, areaItems.size)

                val gridLayout = GridLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    columnCount = 2
                }

                for (item in areaItems) {
                    addItemToGrid(item, gridLayout, tempdb.items.indexOf(item))
                }

                containerWardrobe.addView(gridLayout)
            }
        }

        if (tempdb.items.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "Your wardrobe is empty.\nTap the + button to add items!"
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.white))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, dpToPx(32), 0, 0)
                }
            }
            containerWardrobe.addView(tvEmpty)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun addSectionHeader(area: String, count: Int) {
        val inflater = LayoutInflater.from(this)
        val headerView = inflater.inflate(R.layout.header, containerWardrobe, false)

        val tvSectionTitle = headerView.findViewById<TextView>(R.id.tvSectionTitle)

        val areaName = when(area) {
            "Top" -> if (count == 1) "Top" else "Tops"
            "Bottom" -> if (count == 1) "Bottom" else "Bottoms"
            "Headwear" -> if (count == 1) "Headwear" else "Headwear"
            "Footwear" -> if (count == 1) "Footwear" else "Footwear"
            "Accessory" -> if (count == 1) "Accessory" else "Accessories"
            else -> area
        }

        tvSectionTitle.text = "$areaName ($count)"

        containerWardrobe.addView(headerView)
    }

    private fun addItemToGrid(item: clothitem, gridLayout: GridLayout, index: Int) {
        val inflater = LayoutInflater.from(this)
        val itemView = inflater.inflate(R.layout.griditem, gridLayout, false)

        val imageView = itemView.findViewById<ImageView>(R.id.imgItem)
        val tvName = itemView.findViewById<TextView>(R.id.tvItemName)
        val tvType = itemView.findViewById<TextView>(R.id.tvItemType)
        val tvDesc = itemView.findViewById<TextView>(R.id.tvItemDesc)

        val uri = Uri.parse(item.imageUri)
        imageView.setImageURI(uri)

        tvName.text = item.name
        tvType.text = item.type
        tvDesc.text = item.description

        itemView.setOnClickListener {
            val intent = Intent(this, itemdetail::class.java)
            intent.putExtra("itemIndex", index)
            startActivity(intent)
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        }

        itemView.layoutParams = params
        gridLayout.addView(itemView)
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
                    openLabelScreen(uri)
                }
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                openLabelScreen(it)
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

    private fun openLabelScreen(uri: Uri) {
        val intent = Intent(this, Label::class.java)
        intent.putExtra("imageUri", uri.toString())
        startActivity(intent)
    }
}