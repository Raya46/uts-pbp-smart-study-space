package com.example.smartstudyspace

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.smartstudyspace.adapter.OnboardingAdapter
import com.example.smartstudyspace.data.OnboardingItem
import com.example.smartstudyspace.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // Sembunyikan Action Bar jika belum di-set no action bar

        setupOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

        // Callback ketika halaman digeser
        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

                // Ubah UI berdasarkan halaman aktif
                if (position == onboardingAdapter.itemCount - 1) {
                    binding.btnNext.text = getString(R.string.get_started)
                    binding.tvSkip.alpha = 0f // Sembunyikan tombol skip dengan mulus
                    binding.tvSkip.isEnabled = false
                } else {
                    binding.btnNext.text = getString(R.string.next)
                    binding.tvSkip.alpha = 1f
                    binding.tvSkip.isEnabled = true
                }
            }
        })

        // Aksi Tombol Next / Get Started
        binding.btnNext.setOnClickListener {
            if (binding.viewPagerOnboarding.currentItem + 1 < onboardingAdapter.itemCount) {
                binding.viewPagerOnboarding.currentItem += 1
            } else {
                navigateToHome()
            }
        }

        // Aksi Tombol Skip
        binding.tvSkip.setOnClickListener {
            navigateToHome()
        }
    }

    private fun setupOnboardingItems() {
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.img_1,
                getString(R.string.ob_title_1),
                getString(R.string.ob_desc_1)
            ),
            OnboardingItem(
                R.drawable.img_2,
                getString(R.string.ob_title_2),
                getString(R.string.ob_desc_2)
            ),
            OnboardingItem(
                R.drawable.img_3,
                getString(R.string.ob_title_3),
                getString(R.string.ob_desc_3)
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPagerOnboarding.adapter = onboardingAdapter
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive // Kita buat XML ini nanti
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.layoutIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_active) // XML indikator aktif
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
                )
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Tutup onboarding agar tidak bisa di-back
    }
}