package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityRegisterStep1Binding

class RegisterStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Tombol Back
        binding.btnBack.setOnClickListener {
            finish() // Kembali ke halaman Login
        }

        // Teks Login di bawah
        binding.tvLogin.setOnClickListener {
            finish() // Sama, menutup activity ini dan kembali ke Login
        }

        // Tombol Continue -> Lanjut ke Step 2
        binding.btnContinue.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && pass == confirmPass) {
                 val intent = Intent(this, RegisterStep2Activity::class.java)
                 startActivity(intent)
            } else {
                Toast.makeText(this, "Periksa kembali data Anda", Toast.LENGTH_SHORT).show()
            }
        }
    }
}