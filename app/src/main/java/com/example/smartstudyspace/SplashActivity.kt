package com.example.smartstudyspace // Sesuaikan dengan package Anda!

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 3000 // Jeda 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opsional: Menyembunyikan Action Bar khusus di Splash Screen
        supportActionBar?.hide()

        // Handler untuk menunda eksekusi Intent
        Handler(Looper.getMainLooper()).postDelayed({
            // Berpindah ke MainActivity setelah 3 detik
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Tutup SplashActivity agar tidak bisa di-back
            finish()
        }, splashTimeOut)
    }
}