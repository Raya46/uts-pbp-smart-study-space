package com.example.smartstudyspace

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.data.SessionManager
import com.example.smartstudyspace.databinding.ActivityRegisterStep3Binding

class RegisterStep3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStep3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val name = intent.getStringExtra("USER_NAME") ?: "Rafi"
        val email = intent.getStringExtra("USER_EMAIL") ?: ""
        val token = intent.getStringExtra("TOKEN") ?: ""
        val userId = intent.getIntExtra("USER_ID", 0)

        SessionManager.saveLogin(userId, name, email, token)

        binding.tvTitleSuccess.text = "You're all set,\n$name!"

        binding.btnExplore.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        binding.btnSetupNotif.setOnClickListener {
            openNotificationSettings()
        }
    }

    private fun openNotificationSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.parse("package:$packageName")
                }
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka pengaturan notifikasi", Toast.LENGTH_SHORT).show()
        }
    }
}
