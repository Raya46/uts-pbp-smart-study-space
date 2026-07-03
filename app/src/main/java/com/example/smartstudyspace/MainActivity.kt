package com.example.smartstudyspace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartstudyspace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_OPEN_BOOKINGS = "OPEN_BOOKINGS"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val openBookings =
            intent.getBooleanExtra(EXTRA_OPEN_BOOKINGS, false)
        if (openBookings) {
            replaceFragment(BookingsFragment())
            binding.bottomNavigationView.selectedItemId =
                R.id.nav_bookings
        } else {
            replaceFragment(HomeFragment())
            binding.bottomNavigationView.selectedItemId =
                R.id.nav_home
        }
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_bookings -> {
                    replaceFragment(BookingsFragment())
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
