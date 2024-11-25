package com.example.fotoflutuante

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FullScreenImageActivity : AppCompatActivity() {

    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)  // Certifique-se de que activity_full_screen_image existe
        // Esconde o teclado ao iniciar a Activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        val imageView = findViewById<ImageView>(R.id.imageViewFullScreen)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnDownloadAsPDF = findViewById<Button>(R.id.btnDownloadAsPDF)

        val filePath = intent.getStringExtra("filePath")
        val file = File(filePath)

        // Carrega a imagem em tela cheia
        Glide.with(this)
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

        // Configura o botão de exclusão
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(file)
        }

        // Configura o botão de download como PDF
        btnDownloadAsPDF.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                saveImageAsPDF(file)
            } else {
                // Solicita permissão de gravação
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val filePath = intent.getStringExtra("filePath")
                val file = File(filePath)
                saveImageAsPDF(file)
            } else {
                Toast.makeText(this, "Permissão negada para salvar PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Confirmação de Exclusão")
            .setMessage("Tem certeza que deseja excluir esta foto?")
            .setPositiveButton("Sim") { _, _ ->
                deletePhoto(file)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deletePhoto(file: File) {
        if (file.exists() && file.delete()) {
            Toast.makeText(this, "Foto excluída com sucesso", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Erro ao excluir a foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageAsPDF(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(2480, 3508, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Carrega a imagem como bitmap em segundo plano
                val bitmap = Glide.with(this@FullScreenImageActivity)
                    .asBitmap()
                    .load(file)
                    .submit()
                    .get()
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)

                // Define o caminho para salvar o PDF na pasta Downloads
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val pdfFile = File(downloadsDir, "image_${System.currentTimeMillis()}.pdf")

                pdfDocument.writeTo(FileOutputStream(pdfFile))
                pdfDocument.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FullScreenImageActivity, "Imagem salva como PDF em Downloads", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FullScreenImageActivity, "Erro ao salvar PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
