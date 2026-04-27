package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sembunyikan ActionBar agar full screen
        supportActionBar?.hide()

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Arahkan ke MainActivity jika diisi
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cegah user kembali ke halaman login dengan tombol back
            } else {
                Toast.makeText(this, "Silakan isi Email dan Password", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Register
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterStep1Activity::class.java)
            startActivity(intent)
        }

        // Tombol Google
        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Login dengan Google diklik", Toast.LENGTH_SHORT).show()
        }
    }
}