package com.smith.lishe.network

import com.smith.lishe.model.ListingApiModel
import com.smith.lishe.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "https://f17c-197-232-61-238.ngrok.io/api/v1/"

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
 * A public interface that exposes the [listings] methods
 */
interface ListingApiService {
    /**
     * Returns an AUTH data and this method can be called from a Coroutine.
     * The @POST annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @Headers("Content-Type: application/json")
    @GET("listings")
    suspend fun getAllListings(): List<ListingModel>

    @Headers("Content-Type: application/json")
    @GET("listings/<listingId>")
    suspend fun getListing(@Path("id") id: String): ListingApiModel

    @Headers("Content-Type: application/json")
    @GET("listings/user/<listingId>")
    suspend fun getUserListings(@Path("id") id: String): List<ListingModel>

    @Headers("Content-Type: application/json")
    @PUT("listings/<listingId>")
    suspend fun updateListing(@Path("id") id: String): ListingApiModel

    @Headers("Content-Type: application/json")
    @DELETE("listings/<listingId>")
    suspend fun deleteListing(@Path("id") id: String): ListingApiModel

    @Multipart
    @POST("/listings")
    suspend fun createNewListing(@Part("title") title: RequestBody,
                             @Part("description") description: RequestBody,
                             @Part("latitude") latitude: RequestBody,
                             @Part("longitude") longitude: RequestBody,
                             @Part("expiration") expiration: RequestBody,
                             @Part("individual") individual: RequestBody,
                             @Part listingImage: MultipartBody.Part): ListingApiModel
}


object ListingApi {
    val retrofitService: ListingApiService = retrofit.create(ListingApiService::class.java)
}