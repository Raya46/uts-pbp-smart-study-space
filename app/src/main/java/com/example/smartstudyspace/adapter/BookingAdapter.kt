package com.example.smartstudyspace.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstudyspace.R
import com.example.smartstudyspace.StudySpotDetailActivity
import com.example.smartstudyspace.data.Booking
import com.example.smartstudyspace.databinding.ItemBookingBinding

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvBookingSpotName.text = booking.spotName
            tvBookingCategory.text = booking.category
            tvBookingDate.text = booking.date
            tvBookingTime.text = booking.timeSlot
            tvBookingSeats.text = context.getString(R.string.format_booking_seats, booking.seats)
            tvBookingStatus.text = booking.status

            if (booking.status == "Active") {
                tvBookingStatus.setBackgroundResource(R.drawable.bg_item_selected)
                tvBookingStatus.setTextColor(context.getColor(R.color.white))
            } else {
                tvBookingStatus.setBackgroundResource(R.drawable.bg_tag_orange)
                tvBookingStatus.setTextColor(context.getColor(R.color.text_dark_green))
            }

            btnViewDetail.setOnClickListener {
                val intent = Intent(context, StudySpotDetailActivity::class.java).apply {
                    putExtra("SPOT_NAME", booking.spotName)
                    putExtra("SPOT_CATEGORY", booking.category)
                    putExtra("SPOT_IMAGE", booking.imageResId)
                    putExtra("SPOT_TAG", booking.tag)
                    // You can add more extras if needed
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = bookings.size
}
