package com.example.smartstudyspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstudyspace.adapter.BookingAdapter
import com.example.smartstudyspace.data.Booking
import com.example.smartstudyspace.data.ImageResolver
import com.example.smartstudyspace.data.SessionManager
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.databinding.FragmentBookingsBinding
import kotlinx.coroutines.launch

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookingAdapter: BookingAdapter
    private var allBookings: List<Booking> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs()
        fetchBookings("active")
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(emptyList())
        binding.rvBookings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookingAdapter
        }
    }

    private fun setupTabs() {
        binding.tabActiveOrders.setOnClickListener {
            updateTabUI(isActiveSelected = true)
            fetchBookings("active")
        }

        binding.tabPreviousOrders.setOnClickListener {
            updateTabUI(isActiveSelected = false)
            fetchBookings("previous")
        }
    }

    private fun updateTabUI(isActiveSelected: Boolean) {
        binding.apply {
            if (isActiveSelected) {
                tabActiveOrders.setBackgroundResource(R.drawable.bg_item_selected)
                tabActiveOrders.setTextColor(requireContext().getColor(R.color.white))
                tabPreviousOrders.background = null
                tabPreviousOrders.setTextColor(requireContext().getColor(R.color.text_muted))
            } else {
                tabPreviousOrders.setBackgroundResource(R.drawable.bg_item_selected)
                tabPreviousOrders.setTextColor(requireContext().getColor(R.color.white))
                tabActiveOrders.background = null
                tabActiveOrders.setTextColor(requireContext().getColor(R.color.text_muted))
            }
        }
    }

    private fun fetchBookings(status: String) {
        if (!SessionManager.isLoggedIn()) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getBookings(status)
                if (response.isSuccessful) {
                    val bookingDtos = response.body()!!.bookings
                    allBookings = bookingDtos.map { dto ->
                        Booking(
                            id = dto.id,
                            spotId = dto.spotId,
                            spotName = dto.spotName,
                            category = dto.category,
                            date = dto.date,
                            timeSlot = dto.timeSlot,
                            seats = dto.seats,
                            status = dto.status,
                            imageResId = ImageResolver.resolveDrawable(requireContext(), dto.imageUrl),
                            tag = dto.tag
                        )
                    }
                    bookingAdapter.updateList(allBookings)
                } else {
                    Toast.makeText(context, "Gagal memuat booking", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
