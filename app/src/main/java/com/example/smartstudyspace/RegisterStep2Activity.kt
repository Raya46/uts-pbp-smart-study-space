package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityRegisterStep2Binding

class RegisterStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Tombol Back
        binding.btnBack.setOnClickListener {
            finish() // Kembali ke Register Step 1
        }

        // Upload Avatar Click
        binding.layoutAvatar.setOnClickListener {
            Toast.makeText(this, "Membuka galeri untuk pilih avatar...", Toast.LENGTH_SHORT).show()
        }

        // University Dropdown Mock
        binding.etUniversity.setOnClickListener {
            Toast.makeText(this, "Tampilkan BottomSheet Universitas", Toast.LENGTH_SHORT).show()
        }

        // Tombol Continue -> Lanjut ke Step 3 (Success)
        binding.btnContinueStep2.setOnClickListener {
            val major = binding.etMajor.text.toString()

            // Mengambil daftar chip yang dipilih
            val selectedChipIds = binding.chipGroupPreferences.checkedChipIds

            if (major.isNotEmpty() && selectedChipIds.isNotEmpty()) {
                 val intent = Intent(this, RegisterStep3Activity::class.java)
                 startActivity(intent)
            } else {
                Toast.makeText(this, "Isi jurusan dan pilih minimal 1 preferensi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}