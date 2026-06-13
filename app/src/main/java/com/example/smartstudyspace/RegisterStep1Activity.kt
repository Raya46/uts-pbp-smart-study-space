package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.data.model.RegisterStep1Request
import com.example.smartstudyspace.databinding.ActivityRegisterStep1Binding
import kotlinx.coroutines.launch

class RegisterStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && pass == confirmPass) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.apiService.registerStep1(
                            RegisterStep1Request(name, email, pass)
                        )
                        if (response.isSuccessful) {
                            val result = response.body()!!
                            val intent = Intent(this@RegisterStep1Activity, RegisterStep2Activity::class.java).apply {
                                putExtra("USER_ID", result.userId)
                                putExtra("USER_NAME", name)
                                putExtra("USER_EMAIL", email)
                            }
                            startActivity(intent)
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "Registrasi gagal"
                            Toast.makeText(this@RegisterStep1Activity, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterStep1Activity, "Koneksi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Periksa kembali data Anda", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
