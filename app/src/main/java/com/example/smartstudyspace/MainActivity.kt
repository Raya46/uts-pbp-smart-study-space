package com.example.smartstudyspace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartstudyspace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Atur halaman default saat pertama buka (Misal: langsung ke Profile)
        replaceFragment(ProfileFragment())
        binding.bottomNavigationView.selectedItemId = R.id.nav_profile

        // Listener untuk navigasi bawah
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // TODO: Ganti dengan HomeFragment()
//                     replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_bookings -> {
                    // TODO: Ganti dengan BookingsFragment()
//                     replaceFragment(BookingsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}