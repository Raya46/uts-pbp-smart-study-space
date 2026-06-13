package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartstudyspace.data.SessionManager
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.data.model.UpdatePreferencesRequest
import com.example.smartstudyspace.databinding.ActivityStudyPreferencesBinding
import kotlinx.coroutines.launch

class StudyPreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSavePreferences.setOnClickListener { savePreferences() }

        loadExistingPreferences()
    }

    private fun loadExistingPreferences() {
        if (!SessionManager.isLoggedIn()) return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPreferences()
                if (response.isSuccessful) {
                    val prefs = response.body()!!.preferences
                    for (i in 0 until binding.cgSpaces.childCount) {
                        val chip = binding.cgSpaces.getChildAt(i) as? com.google.android.material.chip.Chip
                        chip?.isChecked = prefs.contains(chip.text.toString())
                    }
                    for (i in 0 until binding.cgAmenities.childCount) {
                        val chip = binding.cgAmenities.getChildAt(i) as? com.google.android.material.chip.Chip
                        chip?.isChecked = prefs.contains(chip.text.toString())
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun savePreferences() {
        val preferences = mutableListOf<String>()
        binding.cgSpaces.checkedChipIds.forEach { id ->
            val chip = findViewById<com.google.android.material.chip.Chip>(id)
            chip?.let { preferences.add(it.text.toString()) }
        }
        binding.cgAmenities.checkedChipIds.forEach { id ->
            val chip = findViewById<com.google.android.material.chip.Chip>(id)
            chip?.let { preferences.add(it.text.toString()) }
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updatePreferences(
                    UpdatePreferencesRequest(preferences)
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@StudyPreferencesActivity, "Preferences Saved!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@StudyPreferencesActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                } else {
                    Toast.makeText(this@StudyPreferencesActivity, "Gagal menyimpan preferensi", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudyPreferencesActivity, "Koneksi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
