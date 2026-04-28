package com.example.smartstudyspace

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstudyspace.adapter.StudySpotAdapter
import com.example.smartstudyspace.data.StudySpot
import com.example.smartstudyspace.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var studySpotAdapter: StudySpotAdapter
    private val allSpots by lazy {
        listOf(
            StudySpot(
                1, "Central Library", getString(R.string.cat_library), "0.5 km", 4.8, 124,
                "15/50 seats available", R.drawable.bg_library, "Quiet",
                listOf("WiFi", "AC", "Power")
            ),
            StudySpot(
                2, "Kampus Café", getString(R.string.cat_cafe), "0.8 km", 4.8, 124,
                "Limited seats available", R.drawable.img_1, "Moderate",
                listOf("Coffee", "WiFi", "AC")
            ),
            StudySpot(
                3, "Working Hub", getString(R.string.cat_working_space), "1.2 km", 4.5, 89,
                "20/30 seats available", R.drawable.img_2, "Quiet",
                listOf("WiFi", "Power", "Printer")
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        setupSearch()
    }

    private fun setupRecyclerView() {
        studySpotAdapter = StudySpotAdapter(allSpots)
        binding.rvStudySpots.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = studySpotAdapter
        }
    }

    private fun setupFilters() {
        binding.cgCategories.setOnCheckedStateChangeListener { _, _ ->
            filterContent()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContent()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterContent() {
        val searchQuery = binding.etSearch.text.toString().lowercase()
        val checkedChipId = binding.cgCategories.checkedChipId

        val filteredList = allSpots.filter { spot ->
            val matchesSearch = spot.name.lowercase().contains(searchQuery)
            val matchesCategory = when (checkedChipId) {
                R.id.chipCafe -> spot.category == getString(R.string.cat_cafe)
                R.id.chipLibrary -> spot.category == getString(R.string.cat_library)
                R.id.chipWorkingSpace -> spot.category == getString(R.string.cat_working_space)
                else -> true // "All" or none selected
            }
            matchesSearch && matchesCategory
        }

        studySpotAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
