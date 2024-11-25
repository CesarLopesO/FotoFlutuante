package com.example.fotoflutuante

import android.content.Intent // Adicione esta linha para importar o Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fotoflutuante.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Esconde o teclado ao iniciar a Activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configura o botão de registro
        binding.btnRegister.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()
            val confirmPassword: String = binding.etConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password.length < 6) {
                    Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                } else if (password == confirmPassword) {
                    createUserWithEmailAndPassword(email, password)
                } else {
                    Toast.makeText(this, "Senhas incompatíveis", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para criar usuário com email e senha
    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmailAndPassword: Success")
                    val user = auth.currentUser

                    // Retorna para a tela de login
                    Toast.makeText(this, "Conta criada com sucesso! Faça login para continuar.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Encerra a RegisterActivity para que o usuário não volte ao registro ao pressionar 'voltar'

                } else {
                    // Verifica se o erro é devido ao email já estar em uso
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Esse email já está cadastrado. Tente outro ou faça login.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "createUserWithEmailAndPassword: Failure", task.exception)
                        Toast.makeText(this, "Falha na autenticação: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    companion object {
        private const val TAG = "EmailAndPassword"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Aqui não é necessário definir o binding como null, pois estamos usando lateinit
    }
}
