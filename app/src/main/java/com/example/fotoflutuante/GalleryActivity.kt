package com.example.fotoflutuante

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        // Esconde o teclado ao iniciar a Activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        val monthContainer = findViewById<LinearLayout>(R.id.monthContainer)

        // Carregar fotos e agrupar por mês
        val photos = loadPhotosFromStorage()
        val photosByMonth = photos.groupBy { getMonthFromFile(it) }

        // Criar um botão para cada mês
        photosByMonth.keys.forEach { month ->
            val monthButton = Button(this).apply {
                text = month
                setOnClickListener {
                    Log.d("GalleryActivity", "Mês selecionado: $month")
                    val intent = Intent(this@GalleryActivity, MonthlyGalleryActivity::class.java)
                    intent.putExtra("month", month)
                    startActivity(intent)
                }
            }
            monthContainer.addView(monthButton)
        }
    }

    private fun loadPhotosFromStorage(): List<File> {
        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return picturesDir?.listFiles()?.toList() ?: emptyList()
    }

    private fun getMonthFromFile(file: File): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val lastModified = Date(file.lastModified())
        return dateFormat.format(lastModified)
    }
}
