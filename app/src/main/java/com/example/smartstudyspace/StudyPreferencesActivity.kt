package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityStudyPreferencesBinding

class StudyPreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Tombol Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Tombol Save Preferences
        binding.btnSavePreferences.setOnClickListener {
            Toast.makeText(this, "Preferences Saved!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}