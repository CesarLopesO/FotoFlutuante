package com.example.fotoflutuante

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fotoflutuante.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true// Remova o foco inicial de EditTexts


        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)







        // Inicializar o Firebase Auth e SharedPreferences
        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("userPreferences", MODE_PRIVATE)

        // Verifica se o usuário já está logado e redireciona para HomeActivity se sim
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        // Configura o Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configura o botão de login com Google
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        // Configura o botão de login normal com email/senha
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o botão para criar uma nova conta
        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmailAndPassword: Success")
                setLoginState(true)
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            } else {
                Log.w(TAG, "signInWithEmailAndPassword: Failure", task.exception)
                Toast.makeText(this, "Falha na autenticação: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: Exception) {
            Log.w(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Login com Google falhou", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                setLoginState(true)
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoginState(isLoggedIn: Boolean) {
        sharedPref.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
