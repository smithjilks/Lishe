package com.smith.lishe.data.requests.repository

import android.graphics.ColorSpace
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.model.ListingModel
import com.smith.lishe.model.RequestModel


class RequestsRepository(
    private val requestsRemoteDataSource: RequestsRemoteDataSource,
    private val id: String
) {
    suspend fun fetchAllRequests(): List<RequestModel> =
        requestsRemoteDataSource.fetchAllRequests()

    suspend fun fetchUserRequests(): List<RequestModel> =
        requestsRemoteDataSource.fetchUserRequests(id)

    suspend fun fetchOneRequest(): RequestModel =
        requestsRemoteDataSource.fetchOneRequest(id)
}