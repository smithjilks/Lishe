package com.smith.lishe.data.foodlisting.datasource

import com.smith.lishe.model.ListingApiModel
import com.smith.lishe.model.ListingModel
import com.smith.lishe.model.UserLoginInfo
import com.smith.lishe.network.ListingApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ListingRemoteDataSource(
    private val listingAPI: ListingApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchAllListings(): List<ListingModel> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            listingAPI.retrofitService.getAllListings()
        }

    suspend fun createNewListing( title: RequestBody,
                                  description: RequestBody,
                                  latitude: RequestBody,
                                  longitude: RequestBody,
                                  expiration: RequestBody,
                                  individual: RequestBody,
                                  listingImage: MultipartBody.Part): ListingApiModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            listingAPI.retrofitService.createNewListing(title, description, latitude, longitude,
                expiration, individual, listingImage)
        }

    suspend fun fetchUserListings(userId: String): List<ListingModel> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            listingAPI.retrofitService.getUserListings(userId)
        }

    suspend fun fetchOneListing(id: String): ListingModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            listingAPI.retrofitService.getListing(id)
        }

    suspend fun updateListing(id: String): ListingApiModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            listingAPI.retrofitService.updateListing(id)
        }

    suspend fun deleteListing(id: String): ListingApiModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            listingAPI.retrofitService.deleteListing(id)
        }


}
