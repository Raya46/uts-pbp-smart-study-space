package com.example.smartstudyspace

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.smartstudyspace.databinding.BottomSheetConfirmBookingBinding
import com.example.smartstudyspace.data.SessionManager
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.data.model.CreateBookingRequest
import com.example.smartstudyspace.data.model.CreateReviewRequest
import com.example.smartstudyspace.data.model.FavoriteToggleRequest
import com.example.smartstudyspace.databinding.ActivityStudySpotDetailBinding
import com.example.smartstudyspace.databinding.ItemDateSelectorBinding
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudySpotDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudySpotDetailBinding

    private var spotId: Int = 0
    private var spotLatitude: Double = 0.0
    private var spotLongitude: Double = 0.0
    private var selectedDate = ""
    private var selectedTime = ""
    private var seatCount = 2
    private var mapInitialized = false
    private var confirmDialog: Dialog? = null

    companion object {
        const val EXTRA_SPOT_ID = "SPOT_ID"
        const val EXTRA_SPOT_NAME = "SPOT_NAME"
        const val EXTRA_SPOT_CATEGORY = "SPOT_CATEGORY"
        const val EXTRA_SPOT_DISTANCE = "SPOT_DISTANCE"
        const val EXTRA_SPOT_RATING = "SPOT_RATING"
        const val EXTRA_SPOT_IMAGE = "SPOT_IMAGE"
        const val EXTRA_SPOT_TAG = "SPOT_TAG"
        const val EXTRA_SPOT_IMAGE_URL = "SPOT_IMAGE_URL"
        const val EXTRA_SPOT_LATITUDE = "SPOT_LATITUDE"
        const val EXTRA_SPOT_LONGITUDE = "SPOT_LONGITUDE"

        private const val DEFAULT_SEAT_COUNT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidTileCache = cacheDir.resolve("tiles")
        }

        binding = ActivityStudySpotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        spotId = intent.getIntExtra(EXTRA_SPOT_ID, 0)
        spotLatitude = intent.getDoubleExtra(EXTRA_SPOT_LATITUDE, 0.0)
        spotLongitude = intent.getDoubleExtra(EXTRA_SPOT_LONGITUDE, 0.0)
        setupData()
        setupDateSelector()
        setupCounter()
        setupTabs()
        setupTimeSlot()
        initMap()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnReserveNowDetail.setOnClickListener {showConfirmBookingDialog() }
        binding.btnFavorite.setOnClickListener { toggleFavorite() }
        binding.btnAddReview.setOnClickListener { showReviewDialog() }
        binding.tvViewOnMap.setOnClickListener { openMap() }

        checkFavoriteStatus()
        loadReviews()
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
        }
    }

    private fun setupTabs() {
        binding.tabReserve.setOnClickListener { updateTabUI(isReserveSelected = true) }
        binding.tabDetail.setOnClickListener { updateTabUI(isReserveSelected = false) }
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
                favoriteReviewBar.visibility = View.GONE
            } else {
                tabDetail.setBackgroundResource(R.drawable.bg_item_selected)
                tabDetail.setTextColor(getColor(R.color.white))
                tabReserve.background = null
                tabReserve.setTextColor(getColor(R.color.text_muted))

                llReserveContent.visibility = View.GONE
                llDetailContent.visibility = View.VISIBLE
                btnReserveNowDetail.visibility = View.GONE
                favoriteReviewBar.visibility = View.VISIBLE

                mapView.onResume()
                if (!mapInitialized && spotId > 0) {
                    fetchSpotAndUpdateMap()
                    mapInitialized = true
                }
            }
        }
    }


    private fun setupDateSelector() {
        val calendar = Calendar.getInstance()
        val displayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val fullFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        for (i in 0 until 6) {
            val currentDate = calendar.time
            val dayName = if (i == 0) {
                getString(R.string.today)
            } else {
                dayFormat.format(currentDate)
            }
            val dayNumber = displayFormat.format(currentDate)
            val fullDate = fullFormat.format(currentDate)
            val dateBinding = ItemDateSelectorBinding.inflate(
                LayoutInflater.from(this),
                binding.llDateSelector,
                false
            )
            dateBinding.tvDayName.text = dayName
            dateBinding.tvDayDate.text = dayNumber
            if (i == 0) {
                selectedDate = fullDate
            }
            dateBinding.root.setOnClickListener {
                selectedDate = fullDate
            }
            binding.llDateSelector.addView(dateBinding.root)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun setupCounter() {
        seatCount = DEFAULT_SEAT_COUNT
        binding.tvSeatCount.text = getString(R.string.format_seats, seatCount)

        binding.btnPlus.setOnClickListener {
            seatCount++
            binding.tvSeatCount.text = getString(R.string.format_seats, seatCount)
        }

        binding.btnMinus.setOnClickListener {
            if (seatCount > 1) {
                seatCount--
                binding.tvSeatCount.text = getString(R.string.format_seats, seatCount)
            }
        }
    }

    private fun showConfirmBookingDialog() {

        confirmDialog = Dialog(this)

        val dialogBinding =
            BottomSheetConfirmBookingBinding.inflate(layoutInflater)

        confirmDialog?.setContentView(dialogBinding.root)

        confirmDialog?.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        confirmDialog?.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        confirmDialog?.window?.attributes?.windowAnimations =
            android.R.style.Animation_Dialog

        confirmDialog?.window?.setGravity(android.view.Gravity.BOTTOM)

        val imageRes = intent.getIntExtra(
            EXTRA_SPOT_IMAGE,
            R.drawable.bg_library
        )

        dialogBinding.ivSpotImage.setImageResource(imageRes)

        dialogBinding.tvSpotName.text =
            intent.getStringExtra(EXTRA_SPOT_NAME)
                ?: "Study Spot"

        dialogBinding.tvCategory.text =
            intent.getStringExtra(EXTRA_SPOT_CATEGORY)
                ?: ""

        dialogBinding.tvBookingDate.text = selectedDate

        dialogBinding.tvBookingTime.text = selectedTime

        dialogBinding.tvBookingSeat.text =
            getString(R.string.format_seats, seatCount)

        dialogBinding.btnCancel.setOnClickListener {
            confirmDialog?.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            confirmDialog?.dismiss()
            createBooking()
        }
        confirmDialog?.show()

    }

    private fun createBooking() {

        if (!SessionManager.isLoggedIn()) {
            Toast.makeText(
                this,
                "Silakan login terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(
                this,
                "Pilih tanggal dan waktu terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.apiService.createBooking(
                        CreateBookingRequest(
                            spotId,
                            selectedDate,
                            selectedTime,
                            seatCount
                        )
                    )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@StudySpotDetailActivity,
                        "Booking berhasil dibuat",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent =
                        Intent(
                            this@StudySpotDetailActivity,
                            MainActivity::class.java
                        )
                    intent.putExtra(
                        MainActivity.EXTRA_OPEN_BOOKINGS,
                        true
                    )
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@StudySpotDetailActivity,
                        "Gagal membuat booking",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@StudySpotDetailActivity,
                    e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupTimeSlot() {
        val timeViews = listOf(binding.time1, binding.time2, binding.time3, binding.time4)
        timeViews.forEach { view ->
            view.setOnClickListener {
                timeViews.forEach {
                    it.setBackgroundResource(R.drawable.bg_tab_container)
                    it.setTextColor(getColor(R.color.text_muted))
                }
                view.setBackgroundResource(R.drawable.bg_item_selected)
                view.setTextColor(getColor(R.color.white))
                selectedTime = view.text.toString()
            }
        }
    }

    private fun checkFavoriteStatus() {
        if (!SessionManager.isLoggedIn() || spotId == 0) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.checkFavorite(spotId)
                if (response.isSuccessful) {
                    val favorited = response.body()!!.favorited
                    updateFavoriteIcon(favorited)
                }
            } catch (_: Exception) { }
        }
    }

    private fun toggleFavorite() {
        if (!SessionManager.isLoggedIn()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (spotId == 0) {
            Toast.makeText(this, "Spot ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.toggleFavorite(FavoriteToggleRequest(spotId))
                if (response.isSuccessful) {
                    val result = response.body()!!
                    updateFavoriteIcon(result.favorited)
                    Toast.makeText(this@StudySpotDetailActivity, result.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@StudySpotDetailActivity, "Gagal mengubah favorite", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudySpotDetailActivity, "Koneksi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteIcon(favorited: Boolean) {
        binding.btnFavorite.setImageResource(
            if (favorited) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }

    private fun showReviewDialog() {
        if (!SessionManager.isLoggedIn()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (spotId == 0) {
            Toast.makeText(this, "Spot ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val input = LayoutInflater.from(this).inflate(R.layout.dialog_review, null)
        val ratingBar = input.findViewById<android.widget.RatingBar>(R.id.rbReviewRating)
        val etComment = input.findViewById<android.widget.EditText>(R.id.etReviewComment)

        AlertDialog.Builder(this)
            .setTitle("Add Review")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating.toInt()
                if (rating < 1) {
                    Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                submitReview(rating, etComment.text.toString().trim())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun submitReview(rating: Int, comment: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createReview(
                    CreateReviewRequest(spotId, rating, comment)
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@StudySpotDetailActivity, "Review submitted!", Toast.LENGTH_SHORT).show()
                    loadReviews()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = if (errorBody?.contains("already reviewed") == true) {
                        "You have already reviewed this spot"
                    } else {
                        "Gagal mengirim review"
                    }
                    Toast.makeText(this@StudySpotDetailActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudySpotDetailActivity, "Koneksi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadReviews() {
        if (spotId == 0) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSpotReviews(spotId)
                if (response.isSuccessful) {
                    val reviews = response.body()!!.reviews
                    binding.tvReviewsLabel.text = "Reviews (${reviews.size})"
                }
            } catch (_: Exception) { }
        }
    }

    private fun openMap() {
        if (spotLatitude == 0.0 && spotLongitude == 0.0) {
            fetchSpotAndOpenMap()
            return
        }
        launchMapIntent()
    }

    private fun fetchSpotAndOpenMap() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSpotDetailRaw(spotId)
                if (response.isSuccessful) {
                    val spot = response.body()!!.getAsJsonObject("spot")
                    spotLatitude = spot.get("latitude")?.asDouble ?: 0.0
                    spotLongitude = spot.get("longitude")?.asDouble ?: 0.0
                    launchMapIntent()
                } else {
                    Toast.makeText(this@StudySpotDetailActivity, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudySpotDetailActivity, "Lokasi tidak tersedia: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchMapIntent() {
        val uri = Uri.parse("https://www.google.com/maps?q=$spotLatitude,$spotLongitude")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun initMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(16.0)
    }

    private fun fetchSpotAndUpdateMap() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSpotDetailRaw(spotId)
                if (response.isSuccessful) {
                    val spot = response.body()!!.getAsJsonObject("spot")
                    spotLatitude = spot.get("latitude")?.asDouble ?: 0.0
                    spotLongitude = spot.get("longitude")?.asDouble ?: 0.0
                    if (spotLatitude != 0.0 || spotLongitude != 0.0) {
                        updateMapMarker()
                    } else {
                        Toast.makeText(this@StudySpotDetailActivity, "Lokasi spot tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@StudySpotDetailActivity, "Gagal memuat lokasi spot", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@StudySpotDetailActivity, "Gagal memuat lokasi spot: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMapMarker() {
        val point = GeoPoint(spotLatitude, spotLongitude)
        binding.mapView.controller.setZoom(16.0)
        binding.mapView.controller.setCenter(point)
        binding.mapView.overlays.clear()
        val marker = Marker(binding.mapView)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = binding.tvDetailName.text.toString()
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}
