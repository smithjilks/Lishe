package com.smith.lishe.network

import com.google.gson.JsonObject
import com.smith.lishe.MainActivity
import com.smith.lishe.model.RequestApiModel
import com.smith.lishe.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "https://2d4e-197-232-61-236.ngrok.io/api/v1/"

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
    .baseUrl(MainActivity.BASE_URL)
    .build()

/**
 * A public interface that exposes the [requests] methods
 */
interface RequestApiService {
    /**
     * Returns an AUTH data and this method can be called from a Coroutine.
     * The @POST annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @Headers("Content-Type: application/json")
    @GET("history")
    suspend fun getAllRequests(): List<RequestModel>

    @Headers("Content-Type: application/json")
    @GET("history/{id}")
    suspend fun getRequest(@Path("id") id: String): RequestModel

    @Headers("Content-Type: application/json")
    @GET("history/user/{id}")
    suspend fun getUserRequests(@Path("id") id: String): List<RequestModel>

    @Multipart
    @PUT("history/{id}")
    suspend fun updateRequest(
        @Header("authorization") token: String,
        @Part("status") status: RequestBody,
        @Path("id") id: String
    ): RequestApiModel

    @Headers("Content-Type: application/json")
    @DELETE("history/{id}")
    suspend fun deleteRequest(@Path("id") id: String): RequestApiModel

    @POST("history")
    suspend fun createNewRequest(@Header("authorization") token: String,
                                 @Body requestInfo: RequestDetailsModel): RequestApiModel
}


object RequestApi {
    val retrofitService: RequestApiService = retrofit.create(RequestApiService::class.java)
}