package com.smith.lishe.network

import com.smith.lishe.model.AuthApiModel
import com.smith.lishe.model.RegisterApiModel
import com.smith.lishe.model.UserLoginInfo
import com.smith.lishe.model.UserRegistrationInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.File

private const val BASE_URL =
    "https://9b7b-102-217-64-31.ngrok.io/api/v1/users/"

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
 * A public interface that exposes the [authUser] method
 */
interface UserApiService {
    /**
     * Returns an AUTH data and this method can be called from a Coroutine.
     * The @POST annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @Headers("Content-Type: application/json")
    @POST("login")
    suspend fun authUser(@Body userLoginInfo: UserLoginInfo): AuthApiModel

    @Multipart
    @POST("signup")
    suspend fun registerUser(@Part("firstName") firstName: RequestBody,
                             @Part("lastName") lastName: RequestBody,
                             @Part("email") email: RequestBody,
                             @Part("phone") phone: RequestBody,
                             @Part("password") password: RequestBody,
                             @Part("organisation") organisation: RequestBody,
                             @Part("organisationName") organisationName: RequestBody,
                             @Part("userType") userType: RequestBody,
                             @Part profilePicture: MultipartBody.Part): RegisterApiModel
}


object UserApi {
    val retrofitService: UserApiService = retrofit.create(UserApiService::class.java)
}