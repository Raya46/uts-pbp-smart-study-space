package com.example.smartstudyspace.data

data class StudySpot(
    val id: Int,
    val name: String,
    val category: String,
    val distance: String,
    val rating: Double,
    val reviewsCount: Int,
    val availability: String,
    val imageResId: Int = 0,
    val imageUrl: String = "",
    val tag: String,
    val features: List<String>,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
