package com.example.smartstudyspace.data

data class Booking(
    val id: Int,
    val spotId: Int = 0,
    val spotName: String,
    val category: String,
    val date: String,
    val timeSlot: String,
    val seats: Int,
    val status: String,
    val imageResId: Int = 0,
    val imageUrl: String = "",
    val tag: String = ""
)
