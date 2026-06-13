package com.example.smartstudyspace

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smartstudyspace.data.SessionManager
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.data.model.UpdateProfileRequest
import com.example.smartstudyspace.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var avatarUri: String? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            avatarUri = it.toString()
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.imgAvatarPlaceholder)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loadProfile()

        binding.btnBack.setOnClickListener { finish() }

        binding.layoutAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener { saveProfile() }
    }

    private fun loadProfile() {
        if (!SessionManager.isLoggedIn()) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()!!.user
                    binding.etFullName.setText(user.name)
                    binding.etEmail.setText(user.email)
                    binding.etUniversity.setText(user.university)
                    binding.etMajor.setText(user.major)
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfile() {
        val name = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val university = binding.etUniversity.text.toString()
        val major = binding.etMajor.text.toString()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updateProfile(
                    UpdateProfileRequest(
                        name = name,
                        email = email,
                        avatar = avatarUri,
                        university = university,
                        major = major
                    )
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfileActivity, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProfileActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Koneksi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
