package com.example.smartstudyspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstudyspace.adapter.BookingAdapter
import com.example.smartstudyspace.data.MockDataProvider
import com.example.smartstudyspace.databinding.FragmentBookingsBinding

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupRecyclerView() {
        val bookings = MockDataProvider.getBookings(requireContext())

        binding.rvBookings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BookingAdapter(bookings)
        }
    }

    private fun setupTabs() {
        binding.tabActiveOrders.setOnClickListener {
            binding.tabActiveOrders.setBackgroundResource(R.drawable.bg_item_selected)
            binding.tabActiveOrders.setTextColor(requireContext().getColor(R.color.white))
            binding.tabPreviousOrders.background = null
            binding.tabPreviousOrders.setTextColor(requireContext().getColor(R.color.text_muted))
        }

        binding.tabPreviousOrders.setOnClickListener {
            binding.tabPreviousOrders.setBackgroundResource(R.drawable.bg_item_selected)
            binding.tabPreviousOrders.setTextColor(requireContext().getColor(R.color.white))
            binding.tabActiveOrders.background = null
            binding.tabActiveOrders.setTextColor(requireContext().getColor(R.color.text_muted))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
