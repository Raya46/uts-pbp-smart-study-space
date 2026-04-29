package com.example.smartstudyspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstudyspace.adapter.BookingAdapter
import com.example.smartstudyspace.data.Booking
import com.example.smartstudyspace.data.MockDataProvider
import com.example.smartstudyspace.databinding.FragmentBookingsBinding

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

        allBookings = MockDataProvider.getBookings(requireContext())
        setupRecyclerView()
        setupTabs()
        
        // Default show Active Orders
        filterBookings(isActive = true)
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
            filterBookings(isActive = true)
        }

        binding.tabPreviousOrders.setOnClickListener {
            updateTabUI(isActiveSelected = false)
            filterBookings(isActive = false)
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

    private fun filterBookings(isActive: Boolean) {
        val filteredList = if (isActive) {
            allBookings.filter { it.status == "Upcoming" || it.status == "Active" }
        } else {
            allBookings.filter { it.status == "Completed" || it.status == "Cancelled" }
        }
        bookingAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
