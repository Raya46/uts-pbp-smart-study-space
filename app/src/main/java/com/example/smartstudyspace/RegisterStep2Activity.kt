package com.example.smartstudyspace

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.data.model.RegisterStep2Request
import com.example.smartstudyspace.databinding.ActivityRegisterStep2Binding
import kotlinx.coroutines.launch

class RegisterStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep2Binding
    private var userId: Int = 0
    private var userName: String = ""
    private var userEmail: String = ""
    private var avatarUri: String = ""

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            avatarUri = it.toString()
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.imgAvatarPlaceholder)
        }
    }

    private val universities = listOf(
        "UPN Veteran Jakarta", "Universitas Indonesia", "Universitas Gadjah Mada",
        "Institut Teknologi Bandung", "Institut Teknologi Sepuluh Nopember",
        "Universitas Brawijaya", "Universitas Padjadjaran", "Universitas Diponegoro",
        "Universitas Airlangga", "Universitas Hasanuddin", "Universitas Sumatera Utara",
        "Universitas Negeri Jakarta", "Universitas Trisakti", "Universitas Tarumanagara",
        "BINUS University", "Universitas Pelita Harapan", "Universitas Gunadarma",
        "Universitas Kristen Krida Wacana", "Universitas Mercu Buana"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userId = intent.getIntExtra("USER_ID", 0)
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.layoutAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.etUniversity.setOnClickListener {
            showUniversityPicker()
        }

        binding.btnContinueStep2.setOnClickListener {
            val major = binding.etMajor.text.toString()
            val selectedChipIds = binding.chipGroupPreferences.checkedChipIds

            if (major.isNotEmpty() && selectedChipIds.isNotEmpty()) {
                val preferences = mutableListOf<String>()
                selectedChipIds.forEach { id ->
                    val chip = findViewById<com.google.android.material.chip.Chip>(id)
                    chip?.let { preferences.add(it.text.toString()) }
                }

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.apiService.registerStep2(
                            userId,
                            RegisterStep2Request(
                                avatar = avatarUri,
                                university = binding.etUniversity.text.toString(),
                                major = major,
                                preferences = preferences
                            )
                        )
                        if (response.isSuccessful) {
                            val result = response.body()!!
                            val intent = Intent(this@RegisterStep2Activity, RegisterStep3Activity::class.java).apply {
                                putExtra("USER_NAME", userName)
                                putExtra("USER_EMAIL", userEmail)
                                putExtra("TOKEN", result.token)
                                putExtra("USER_ID", result.user.id)
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegisterStep2Activity, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterStep2Activity, "Koneksi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Isi jurusan dan pilih minimal 1 preferensi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUniversityPicker() {
        AlertDialog.Builder(this)
            .setTitle("Pilih Universitas")
            .setItems(universities.toTypedArray()) { _, which ->
                binding.etUniversity.setText(universities[which])
            }
            .show()
    }
}
