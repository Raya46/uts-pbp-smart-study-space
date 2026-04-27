package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityRegisterStep3Binding

class RegisterStep3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Ambil nama dari Intent jika dikirim dari Step 1/Step 2 (Opsional)
         val name = intent.getStringExtra("USER_NAME") ?: "Rafi"
         binding.tvTitleSuccess.text = "You're all set,\n$name! 🎉"

        // Tombol Explore (Masuk ke MainActivity)
        binding.btnExplore.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Menghapus semua tumpukan activity (riwayat) agar tidak bisa di-back ke layar register
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Tombol Set Up Notifications
        binding.btnSetupNotif.setOnClickListener {
            Toast.makeText(this, "Membuka pengaturan notifikasi sistem...", Toast.LENGTH_SHORT).show()
        }
    }
}