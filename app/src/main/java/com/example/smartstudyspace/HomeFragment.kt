package com.example.smartstudyspace

import android.content.Intent
import android.net.Uri
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

        binding.tvViewMap.setOnClickListener { openMapForFirstSpot() }
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
                            features = dto.features,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
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
                R.id.chipCafe -> spot.category == "Cafe"
                R.id.chipLibrary -> spot.category == "Library"
                R.id.chipWorkingSpace -> spot.category == "Working Space"
                else -> true
            }
            matchesSearch && matchesCategory
        }

        studySpotAdapter.updateList(filteredList)
    }

    private fun openMapForFirstSpot() {
        if (allSpots.isEmpty()) return
        val spot = allSpots.first()
        if (spot.latitude != 0.0 || spot.longitude != 0.0) {
            openMap(spot.latitude, spot.longitude, spot.name)
        } else {
            fetchAndOpenMap(spot.id, spot.name)
        }
    }

    private fun fetchAndOpenMap(spotId: Int, name: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSpotDetailRaw(spotId)
                if (response.isSuccessful) {
                    val spotObj = response.body()!!.getAsJsonObject("spot")
                    val lat = spotObj.get("latitude")?.asDouble ?: 0.0
                    val lng = spotObj.get("longitude")?.asDouble ?: 0.0
                    openMap(lat, lng, name)
                } else {
                    Toast.makeText(context, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat lokasi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMap(lat: Double, lng: Double, label: String) {
        val uri = Uri.parse("https://www.google.com/maps?q=$lat,$lng")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
