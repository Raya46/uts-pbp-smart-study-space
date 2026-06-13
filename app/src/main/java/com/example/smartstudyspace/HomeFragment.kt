package com.example.smartstudyspace

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartstudyspace.adapter.StudySpotAdapter
import com.example.smartstudyspace.data.ImageResolver
import com.example.smartstudyspace.data.StudySpot
import com.example.smartstudyspace.data.api.RetrofitClient
import com.example.smartstudyspace.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var studySpotAdapter: StudySpotAdapter
    private var allSpots: List<StudySpot> = emptyList()

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
        fetchSpots()
    }

    private fun setupRecyclerView() {
        studySpotAdapter = StudySpotAdapter(emptyList())
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

    private fun fetchSpots() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSpots()
                if (response.isSuccessful) {
                    val spotDtos = response.body()!!.spots
                    allSpots = spotDtos.map { dto ->
                        StudySpot(
                            id = dto.id,
                            name = dto.name,
                            category = dto.category,
                            distance = dto.distance,
                            rating = dto.rating,
                            reviewsCount = dto.reviewsCount,
                            availability = dto.availability,
                            imageResId = ImageResolver.resolveDrawable(requireContext(), dto.imageUrl),
                            tag = dto.tag,
                            features = dto.features
                        )
                    }
                    studySpotAdapter.updateList(allSpots)
                } else {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
                else -> true
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
