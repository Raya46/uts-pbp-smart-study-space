package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartstudyspace.data.SessionManager
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()
        loadPreferences()
        loadStats()

        binding.btnPreferences.setOnClickListener {
            startActivity(Intent(requireContext(), StudyPreferencesActivity::class.java))
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    SessionManager.logout()
                    startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    requireActivity().finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadProfile() {
        if (!SessionManager.isLoggedIn()) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()!!.user
                    binding.tvProfileName.text = user.name
                    binding.tvProfileEmail.text = user.email
                    binding.tvProfileUniversity.text = user.university.ifEmpty { "Belum diisi" }
                    binding.tvProfileMajor.text = user.major.ifEmpty { "Belum diisi" }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPreferences() {
        if (!SessionManager.isLoggedIn()) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPreferences()
                if (response.isSuccessful) {
                    val prefs = response.body()!!.preferences
                    if (prefs.isNotEmpty()) {
                        binding.tvPreferencesValue.text = prefs.joinToString(", ")
                    } else {
                        binding.tvPreferencesValue.text = "No preferences set yet"
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun loadStats() {
        if (!SessionManager.isLoggedIn()) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfileStats()
                if (response.isSuccessful) {
                    val stats = response.body()!!
                    binding.tvTotalBookingsCount.text = stats.totalBookings.toString()
                    binding.tvFavoritesCount.text = stats.totalFavorites.toString()
                    binding.tvReviewsCount.text = stats.totalReviews.toString()
                }
            } catch (_: Exception) { }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
        loadPreferences()
        loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
