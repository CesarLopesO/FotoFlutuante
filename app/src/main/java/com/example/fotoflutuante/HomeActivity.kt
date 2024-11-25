package com.example.fotoflutuante

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Esconde o teclado ao iniciar a Activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Verifica se o usuário está autenticado
        if (auth.currentUser == null) {
            redirectToLogin()
            return
        }

        // Exibe uma saudação com o e-mail do usuário autenticado
        val userEmail = auth.currentUser?.email
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        welcomeTextView.text = "Oi, $userEmail! Bem-vindo(a)!"

        // Configura o botão de logout
        // Configura o botão de logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Limpa o estado de login salvo nas SharedPreferences
            getSharedPreferences("userPreferences", MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply()

            // Desloga o usuário do Firebase
            auth.signOut()

            // Redireciona para a tela de login
            redirectToLogin()
        }


        // Configura o botão da câmera
        val fabCamera = findViewById<FloatingActionButton>(R.id.fabCamera)
        fabCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        // Configura o botão da galeria
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        btnGallery.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    // Função para redirecionar o usuário para a tela de login
    private fun redirectToLogin() {
        val intent = Intent(this@HomeActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
