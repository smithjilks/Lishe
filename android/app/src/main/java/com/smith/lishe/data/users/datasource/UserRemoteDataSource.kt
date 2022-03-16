package com.smith.lishe.data.users.datasource

import com.smith.lishe.model.UserDetailsModel
import com.smith.lishe.network.UserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserRemoteDataSource(
    private val userApi: UserApi,
    private val ioDispatcher: CoroutineDispatcher
    ) {
    /**
     * Fetches the latest news from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchUserData(userId: String): UserDetailsModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            userApi.retrofitService.getUser(userId)
        }


}
