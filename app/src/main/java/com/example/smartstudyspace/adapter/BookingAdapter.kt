package com.example.smartstudyspace.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smartstudyspace.R
import com.example.smartstudyspace.StudySpotDetailActivity
import com.example.smartstudyspace.data.Booking
import com.example.smartstudyspace.databinding.ItemBookingBinding

class BookingAdapter(private var bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    fun updateList(newList: List<Booking>) {
        val diffResult = DiffUtil.calculateDiff(BookingDiffCallback(bookings, newList))
        bookings = newList
        diffResult.dispatchUpdatesTo(this)
    }

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

            // Handle Status UI
            when (booking.status) {
                "Active" -> {
                    tvBookingStatus.setBackgroundResource(R.drawable.bg_item_selected)
                    tvBookingStatus.setTextColor(context.getColor(R.color.white))
                }
                "Completed" -> {
                    tvBookingStatus.setBackgroundResource(R.drawable.bg_tab_container)
                    tvBookingStatus.setTextColor(context.getColor(R.color.text_muted))
                }
                "Cancelled" -> {
                    tvBookingStatus.setBackgroundResource(R.drawable.bg_tab_container)
                    tvBookingStatus.setTextColor(android.graphics.Color.RED)
                }
                else -> { // Upcoming
                    tvBookingStatus.setBackgroundResource(R.drawable.bg_tag_orange)
                    tvBookingStatus.setTextColor(context.getColor(R.color.text_dark_green))
                }
            }

            btnViewDetail.setOnClickListener {
                val intent = Intent(context, StudySpotDetailActivity::class.java).apply {
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_NAME, booking.spotName)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_CATEGORY, booking.category)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_IMAGE, booking.imageResId)
                    putExtra(StudySpotDetailActivity.EXTRA_SPOT_TAG, booking.tag)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = bookings.size

    class BookingDiffCallback(
        private val oldList: List<Booking>,
        private val newList: List<Booking>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
