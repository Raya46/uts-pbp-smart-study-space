package com.example.smartstudyspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityStudySpotDetailBinding
import com.example.smartstudyspace.databinding.ItemDateSelectorBinding

class StudySpotDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudySpotDetailBinding

    companion object {
        const val EXTRA_SPOT_NAME = "SPOT_NAME"
        const val EXTRA_SPOT_CATEGORY = "SPOT_CATEGORY"
        const val EXTRA_SPOT_DISTANCE = "SPOT_DISTANCE"
        const val EXTRA_SPOT_RATING = "SPOT_RATING"
        const val EXTRA_SPOT_IMAGE = "SPOT_IMAGE"
        const val EXTRA_SPOT_TAG = "SPOT_TAG"
        
        private const val DEFAULT_SEAT_COUNT = 2
    }

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
        val name = intent.getStringExtra(EXTRA_SPOT_NAME) ?: getString(R.string.cat_library)
        val category = intent.getStringExtra(EXTRA_SPOT_CATEGORY) ?: getString(R.string.cat_library)
        val distance = intent.getStringExtra(EXTRA_SPOT_DISTANCE) ?: ""
        val rating = intent.getDoubleExtra(EXTRA_SPOT_RATING, 0.0)
        val imageRes = intent.getIntExtra(EXTRA_SPOT_IMAGE, R.drawable.bg_library)
        val tag = intent.getStringExtra(EXTRA_SPOT_TAG) ?: ""

        binding.apply {
            tvDetailName.text = name
            tvDetailCategoryDistance.text = getString(R.string.category_distance_format, category, distance)
            tvDetailRating.text = getString(R.string.rating_format, rating)
            ivDetailImage.setImageResource(imageRes)
            tvDetailTag.text = tag

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun setupTabs() {
        binding.tabReserve.setOnClickListener {
            updateTabUI(isReserveSelected = true)
        }

        binding.tabDetail.setOnClickListener {
            updateTabUI(isReserveSelected = false)
        }
    }
    
    private fun updateTabUI(isReserveSelected: Boolean) {
        binding.apply {
            if (isReserveSelected) {
                tabReserve.setBackgroundResource(R.drawable.bg_item_selected)
                tabReserve.setTextColor(getColor(R.color.white))
                tabDetail.background = null
                tabDetail.setTextColor(getColor(R.color.text_muted))

                llReserveContent.visibility = View.VISIBLE
                llDetailContent.visibility = View.GONE
                btnReserveNowDetail.visibility = View.VISIBLE
            } else {
                tabDetail.setBackgroundResource(R.drawable.bg_item_selected)
                tabDetail.setTextColor(getColor(R.color.white))
                tabReserve.background = null
                tabReserve.setTextColor(getColor(R.color.text_muted))

                llReserveContent.visibility = View.GONE
                llDetailContent.visibility = View.VISIBLE
                btnReserveNowDetail.visibility = View.GONE
            }
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
        var count = DEFAULT_SEAT_COUNT
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
