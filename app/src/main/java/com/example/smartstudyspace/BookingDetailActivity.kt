package com.example.smartstudyspace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartstudyspace.databinding.ActivityBookingDetailBinding
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.smartstudyspace.data.api.RetrofitClient
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog

class BookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailBinding
    private var bookingId = -1

    companion object {

        const val EXTRA_BOOKING_ID = "BOOKING_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityBookingDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()

        bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, -1)

        if (bookingId == -1) {
            Toast.makeText(this, "Booking tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchBookingDetail()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCancelBooking.setOnClickListener {
            showCancelDialog()
        }

        binding.ivQrCode.setOnClickListener {
            showCheckInDialog()
        }

        binding.btnEndReservation.setOnClickListener {
            showEndReservationDialog()
        }
    }

    private fun showEndReservationDialog() {

        AlertDialog.Builder(this)
            .setTitle("End Reservation")
            .setMessage("Are you sure you want to end this reservation?")
            .setPositiveButton("End") { _, _ ->
                endReservation()
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    private fun endReservation() {
        lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.apiService.endBooking(bookingId)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@BookingDetailActivity,
                        response.body()?.message ?: "Reservation completed",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchBookingDetail()

                } else {
                    Toast.makeText(
                        this@BookingDetailActivity,
                        "Failed to end reservation",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@BookingDetailActivity,
                    e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCheckInDialog() {

        AlertDialog.Builder(this)
            .setTitle("Check In")
            .setMessage("Confirm check in for this booking?")
            .setPositiveButton("Check In") { _, _ ->
                checkInBooking()
            }
            .setNegativeButton("Cancel", null)
            .show()

    }
    private fun checkInBooking() {
        lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.apiService.checkInBooking(bookingId)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@BookingDetailActivity,
                        response.body()?.message ?: "Check in successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchBookingDetail()

                } else {
                    Toast.makeText(
                        this@BookingDetailActivity,
                        "Failed to check in",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@BookingDetailActivity,
                    e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchBookingDetail() {

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService.getBookingDetail(bookingId)

                if (response.isSuccessful) {

                    val booking = response.body()!!.booking

                    binding.tvBookingId.text =
                        booking.id.toString()

                    binding.tvSpotName.text =
                        booking.spotName

                    binding.tvDate.text =
                        booking.date

                    binding.tvTime.text =
                        booking.timeSlot

                    binding.tvSeats.text =
                        booking.seats.toString()

                    binding.tvStatus.text =
                        booking.status

                    updateStatusUI(booking.status)

                } else {

                    Toast.makeText(
                        this@BookingDetailActivity,
                        "Gagal mengambil data booking",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@BookingDetailActivity,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    private fun updateStatusUI(status: String) {

        when (status) {
            "Upcoming" -> {
                binding.cardQr.visibility = View.VISIBLE
                binding.tvWarning.visibility = View.VISIBLE
                binding.btnCancelBooking.visibility = View.VISIBLE
                binding.btnEndReservation.visibility = View.GONE
                binding.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange)
            }

            "Active" -> {
                binding.cardQr.visibility = View.GONE
                binding.tvWarning.visibility = View.GONE
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnEndReservation.visibility = View.VISIBLE

                binding.tvStatus.setBackgroundResource(R.drawable.bg_item_selected)
                binding.tvStatus.setTextColor(getColor(R.color.white))
            }

            "Completed" -> {
                binding.cardQr.visibility = View.GONE
                binding.tvWarning.visibility = View.GONE
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnEndReservation.visibility = View.GONE
                binding.tvStatus.setBackgroundResource(R.drawable.bg_tab_container)
            }

            "Cancelled" -> {
                binding.cardQr.visibility = View.GONE
                binding.tvWarning.visibility = View.GONE
                binding.btnCancelBooking.visibility = View.GONE
                binding.btnEndReservation.visibility = View.GONE
                binding.tvStatus.setBackgroundResource(R.drawable.bg_tab_container)
            }
        }
    }

    private fun showCancelDialog() {

        AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes") { _, _ ->
                cancelBooking()
            }
            .setNegativeButton("No", null)
            .show()

    }

    private fun cancelBooking() {

        lifecycleScope.launch {

            try {
                val response =
                    RetrofitClient.apiService.cancelBooking(bookingId)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@BookingDetailActivity,
                        response.body()?.message ?: "Booking cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()

                } else {
                    Toast.makeText(
                        this@BookingDetailActivity,
                        "Failed to cancel booking",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@BookingDetailActivity,
                    e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}