package com.example.smartstudyspace.data.api

import com.example.smartstudyspace.data.model.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/register-step1")
    suspend fun registerStep1(@Body request: RegisterStep1Request): Response<RegisterStep1Response>

    @PUT("api/auth/register-step2/{userId}")
    suspend fun registerStep2(@Path("userId") userId: Int, @Body request: RegisterStep2Request): Response<RegisterStep2Response>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/spots")
    suspend fun getSpots(@Query("search") search: String? = null, @Query("category") category: String? = null): Response<SpotsResponse>

    @GET("api/spots/{id}")
    suspend fun getSpotDetail(@Path("id") id: Int): Response<SpotDetailResponse>

    @GET("api/spots/{id}")
    suspend fun getSpotDetailRaw(@Path("id") id: Int): Response<JsonObject>

    @GET("api/bookings")
    suspend fun getBookings(@Query("status") status: String? = null): Response<BookingsResponse>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<CreateBookingResponse>

    @GET("api/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("api/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>

    @GET("api/preferences")
    suspend fun getPreferences(): Response<PreferencesResponse>

    @PUT("api/preferences")
    suspend fun updatePreferences(@Body request: UpdatePreferencesRequest): Response<SimpleResponse>

    @GET("api/profile/stats")
    suspend fun getProfileStats(): Response<ProfileStatsResponse>

    @GET("api/favorites")
    suspend fun getFavorites(): Response<FavoritesResponse>

    @POST("api/favorites/toggle")
    suspend fun toggleFavorite(@Body request: FavoriteToggleRequest): Response<FavoriteToggleResponse>

    @GET("api/favorites/check/{spotId}")
    suspend fun checkFavorite(@Path("spotId") spotId: Int): Response<FavoriteCheckResponse>

    @GET("api/reviews/spot/{spotId}")
    suspend fun getSpotReviews(@Path("spotId") spotId: Int): Response<ReviewsResponse>

    @POST("api/reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): Response<CreateReviewResponse>
}
