package com.smith.lishe.network

import com.smith.lishe.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "https://6b77-197-232-61-251.ngrok.io/api/v1/"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [reviews] methods
 */
interface ReviewApiService {
    /**
     * Returns an AUTH data and this method can be called from a Coroutine.
     * The @POST annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @Headers("Content-Type: application/json")
    @GET("reviews")
    suspend fun getAllReviews(): List<ReviewModel>

    @Headers("Content-Type: application/json")
    @GET("reviews/{id}")
    suspend fun getReview(@Path("id") id: String): ReviewModel

    @Headers("Content-Type: application/json")
    @GET("reviews/user/{id}")
    suspend fun getUserReviews(@Path("id") id: String): List<ReviewModel>

    @Headers("Content-Type: application/json")
    @PUT("reviews/{id}")
    suspend fun updateReview(@Body updateInfo: ReviewDetailsModel): ReviewApiModel

    @Headers("Content-Type: application/json")
    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): ReviewApiModel

    @POST("reviews")
    suspend fun createNewReview(@Body reviewInfo: ReviewDetailsModel): ReviewApiModel
}


object ReviewApi {
    val retrofitService: ReviewApiService = retrofit.create(ReviewApiService::class.java)
}