package com.example.smartstudyspace.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class RegisterStep1Request(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterStep1Response(
    val userId: Int,
    val message: String
)

data class RegisterStep2Request(
    val avatar: String = "",
    val university: String = "",
    val major: String = "",
    val preferences: List<String> = emptyList()
)

data class SimpleResponse(val message: String)

data class RegisterStep2Response(
    val token: String,
    val user: UserDto,
    val message: String
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String,
    val university: String,
    val major: String
)

data class SpotsResponse(val spots: List<SpotDto>)

data class SpotDetailResponse(val spot: SpotDto)

data class SpotDto(
    val id: Int,
    val name: String,
    val category: String,
    val distance: String,
    val rating: Double,
    @SerializedName("reviews_count") val reviewsCount: Int,
    val availability: String,
    @SerializedName("image_url") val imageUrl: String,
    val tag: String,
    val features: List<String>,
    val latitude: Double,
    val longitude: Double
)

data class BookingsResponse(val bookings: List<BookingDto>)

data class BookingDto(
    val id: Int,
    @SerializedName("spot_id") val spotId: Int = 0,
    @SerializedName("spot_name") val spotName: String,
    val category: String,
    val date: String,
    @SerializedName("time_slot") val timeSlot: String,
    val seats: Int,
    val status: String,
    @SerializedName("image_url") val imageUrl: String = "",
    val tag: String = ""
)

data class CreateBookingRequest(
    val spotId: Int,
    val date: String,
    val timeSlot: String,
    val seats: Int
)

data class CreateBookingResponse(
    val booking: BookingCreatedDto,
    val message: String
)

data class BookingCreatedDto(val id: Int)

data class ProfileResponse(val user: UserDto)

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    val university: String? = null,
    val major: String? = null
)

data class UpdateProfileResponse(
    val user: UserDto,
    val message: String
)

data class PreferencesResponse(val preferences: List<String>)

data class UpdatePreferencesRequest(val preferences: List<String>)

data class ProfileStatsResponse(
    val totalBookings: Int,
    val totalFavorites: Int,
    val totalReviews: Int
)

data class FavoriteDto(
    val id: Int,
    @SerializedName("spot_id") val spotId: Int,
    @SerializedName("spot_name") val spotName: String,
    val category: String,
    @SerializedName("image_url") val imageUrl: String,
    val tag: String,
    val rating: Double
)

data class FavoritesResponse(val favorites: List<FavoriteDto>)

data class FavoriteToggleRequest(val spotId: Int)

data class FavoriteToggleResponse(
    val favorited: Boolean,
    val message: String
)

data class FavoriteCheckResponse(val favorited: Boolean)

data class ReviewDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_avatar") val userAvatar: String,
    val rating: Int,
    val comment: String
)

data class ReviewsResponse(val reviews: List<ReviewDto>)

data class CreateReviewRequest(
    val spotId: Int,
    val rating: Int,
    val comment: String = ""
)

data class CreateReviewResponse(
    val review: ReviewDto,
    val message: String
)

data class BookingDetailResponse(
    val booking: BookingDto
)

data class CheckInBookingResponse(
    val message: String
)

data class EndBookingResponse(
    val message: String
)

data class CancelBookingResponse(
    val message: String
)