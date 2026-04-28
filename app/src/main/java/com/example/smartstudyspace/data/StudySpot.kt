package com.example.smartstudyspace.data

data class StudySpot(
    val id: Int,
    val name: String,
    val category: String,
    val distance: String,
    val rating: Double,
    val reviewsCount: Int,
    val availability: String,
    val imageResId: Int,
    val tag: String, // e.g., "Quiet", "Moderate"
    val features: List<String> // e.g., ["WiFi", "AC", "Power"]
)
