package com.example.smartstudyspace.data

data class Booking(
    val id: Int,
    val spotName: String,
    val category: String,
    val date: String,
    val timeSlot: String,
    val seats: Int,
    val status: String, // "Upcoming", "Active"
    val imageResId: Int = 0,
    val tag: String = ""
)
