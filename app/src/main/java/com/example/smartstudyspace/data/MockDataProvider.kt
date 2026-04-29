package com.example.smartstudyspace.data

import android.content.Context
import com.example.smartstudyspace.R

object MockDataProvider {

    fun getStudySpots(context: Context): List<StudySpot> {
        return listOf(
            StudySpot(
                1, "Central Library", context.getString(R.string.cat_library), "0.5 km", 4.8, 124,
                "15/50 seats available", R.drawable.bg_library, "Quiet",
                listOf("WiFi", "AC", "Power")
            ),
            StudySpot(
                2, "Kampus Café", context.getString(R.string.cat_cafe), "0.8 km", 4.8, 124,
                "Limited seats available", R.drawable.img_1, "Moderate",
                listOf("Coffee", "WiFi", "AC")
            ),
            StudySpot(
                3, "Working Hub", context.getString(R.string.cat_working_space), "1.2 km", 4.5, 89,
                "20/30 seats available", R.drawable.img_2, "Quiet",
                listOf("WiFi", "Power", "Printer")
            )
        )
    }

    fun getBookings(context: Context): List<Booking> {
        return listOf(
            Booking(
                1, "Study Hub FIK", context.getString(R.string.cat_working_space), "Apr 20, 2026",
                "10:00 - 12:00", 2, "Upcoming", R.drawable.img_2, "Quiet"
            ),
            Booking(
                2, "Central Library", context.getString(R.string.cat_library), "Apr 18, 2026",
                "13:00 - 15:00", 1, "Active", R.drawable.bg_library, "Quiet"
            ),
            Booking(
                3, "Kampus Café", context.getString(R.string.cat_cafe), "Mar 10, 2026",
                "09:00 - 11:00", 2, "Completed", R.drawable.img_1, "Moderate"
            ),
            Booking(
                4, "Working Hub", context.getString(R.string.cat_working_space), "Feb 25, 2026",
                "14:00 - 16:00", 4, "Cancelled", R.drawable.img_2, "Quiet"
            )
        )
    }
}
