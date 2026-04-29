package com.example.smartstudyspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityStudySpotDetailBinding
import com.example.smartstudyspace.databinding.ItemDateSelectorBinding

class StudySpotDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudySpotDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudySpotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupData()
        setupDateSelector()
        setupCounter()
        setupTabs()
    }

    private fun setupData() {
        val name = intent.getStringExtra("SPOT_NAME") ?: "Kampus Café"
        val category = intent.getStringExtra("SPOT_CATEGORY") ?: "Café"
        val distance = intent.getStringExtra("SPOT_DISTANCE") ?: "0.8 km"
        val rating = intent.getDoubleExtra("SPOT_RATING", 4.6)
        val imageRes = intent.getIntExtra("SPOT_IMAGE", R.drawable.img_1)
        val tag = intent.getStringExtra("SPOT_TAG") ?: "Moderate"

        binding.apply {
            tvDetailName.text = name
            tvDetailCategoryDistance.text = getString(R.string.category_distance_format, category, distance)
            tvDetailRating.text = rating.toString()
            ivDetailImage.setImageResource(imageRes)
            tvDetailTag.text = tag

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun setupTabs() {
        binding.tabReserve.setOnClickListener {
            // Switch to Reserve Tab
            binding.tabReserve.setBackgroundResource(R.drawable.bg_item_selected)
            binding.tabReserve.setTextColor(getColor(R.color.white))
            binding.tabDetail.background = null
            binding.tabDetail.setTextColor(getColor(R.color.text_muted))

            binding.llReserveContent.visibility = View.VISIBLE
            binding.llDetailContent.visibility = View.GONE
            binding.btnReserveNowDetail.visibility = View.VISIBLE
        }

        binding.tabDetail.setOnClickListener {
            // Switch to Detail Tab
            binding.tabDetail.setBackgroundResource(R.drawable.bg_item_selected)
            binding.tabDetail.setTextColor(getColor(R.color.white))
            binding.tabReserve.background = null
            binding.tabReserve.setTextColor(getColor(R.color.text_muted))

            binding.llReserveContent.visibility = View.GONE
            binding.llDetailContent.visibility = View.VISIBLE
            binding.btnReserveNowDetail.visibility = View.GONE
        }
    }

    private fun setupDateSelector() {
        val dates = listOf(
            Triple(getString(R.string.today), "20", true),
            Triple("Tue", "21", false),
            Triple("Wed", "22", false),
            Triple("Thu", "23", false),
            Triple("Fri", "24", false),
            Triple("Sat", "25", false)
        )

        dates.forEach { date ->
            val dateBinding = ItemDateSelectorBinding.inflate(LayoutInflater.from(this), binding.llDateSelector, false)
            dateBinding.tvDayName.text = date.first
            dateBinding.tvDayDate.text = date.second

            if (date.third) {
                dateBinding.root.setBackgroundResource(R.drawable.bg_item_selected)
                dateBinding.tvDayName.setTextColor(getColor(R.color.white))
                dateBinding.tvDayDate.setTextColor(getColor(R.color.white))
            }

            binding.llDateSelector.addView(dateBinding.root)
        }
    }

    private fun setupCounter() {
        var count = 2
        binding.tvSeatCount.text = getString(R.string.format_seats, count)

        binding.btnPlus.setOnClickListener {
            count++
            binding.tvSeatCount.text = getString(R.string.format_seats, count)
        }

        binding.btnMinus.setOnClickListener {
            if (count > 1) {
                count--
                binding.tvSeatCount.text = getString(R.string.format_seats, count)
            }
        }
    }
}
