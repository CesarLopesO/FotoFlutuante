package com.example.fotoflutuante

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MonthlyGalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var photos: List<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_gallery)
        // Esconde o teclado ao iniciar a Activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        val month = intent.getStringExtra("month")
        photos = loadPhotosForMonth(month)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        photoAdapter = PhotoAdapter(photos) { photo ->
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("filePath", photo.absolutePath)
            startActivityForResult(intent, REQUEST_CODE_DELETE)
        }
        recyclerView.adapter = photoAdapter
    }

    private fun loadPhotosForMonth(month: String?): List<File> {
        // Carrega e retorna as fotos do mês especificado em ordem decrescente
        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return picturesDir?.listFiles()?.filter {
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(it.lastModified())) == month
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DELETE && resultCode == RESULT_OK) {
            // Recarrega a lista de fotos e atualiza o adaptador após exclusão
            photos = loadPhotosForMonth(intent.getStringExtra("month"))
            photoAdapter.updatePhotos(photos)
        }
    }

    companion object {
        private const val REQUEST_CODE_DELETE = 1
    }
}
