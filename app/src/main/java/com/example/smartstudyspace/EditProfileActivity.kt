package com.example.smartstudyspace

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Tombol Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Tombol Edit Avatar
        binding.layoutAvatar.setOnClickListener {
            Toast.makeText(this, "Pilih foto profil baru...", Toast.LENGTH_SHORT).show()
        }

        // Tombol Save Changes
        binding.btnSaveChanges.setOnClickListener {
            Toast.makeText(this, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke halaman sebelumnya
        }
    }
}