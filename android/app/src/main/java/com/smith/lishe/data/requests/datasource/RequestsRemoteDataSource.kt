package com.smith.lishe.data.requests.datasource

import com.smith.lishe.model.*
import com.smith.lishe.network.RequestApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class RequestsRemoteDataSource(
    private val requestApi: RequestApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchAllRequests(): List<RequestModel> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            requestApi.retrofitService.getAllRequests()
        }

    suspend fun createNewRequest(newRequestDetails: RequestDetailsModel): RequestApiModel =
        withContext(ioDispatcher) {
            requestApi.retrofitService.createNewRequest(newRequestDetails)
        }

    suspend fun fetchUserRequests(userId: String): List<RequestModel> =
        withContext(ioDispatcher) {
            requestApi.retrofitService.getUserRequests(userId)
        }

    suspend fun fetchOneRequest(id: String): RequestModel =
        withContext(ioDispatcher) {
            requestApi.retrofitService.getRequest(id)
        }




}
