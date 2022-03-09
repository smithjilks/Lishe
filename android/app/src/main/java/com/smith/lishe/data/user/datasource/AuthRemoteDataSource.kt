package com.smith.lishe.data.user.datasource

import com.smith.lishe.model.AuthApiModel
import com.smith.lishe.model.UserLoginInfo
import com.smith.lishe.network.UserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class AuthRemoteDataSource(
    private val userApi: UserApi,
    private val ioDispatcher: CoroutineDispatcher
    ) {
    /**
     * Fetches the latest news from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchAuthData(body: UserLoginInfo): AuthApiModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            userApi.retrofitService.authUser(body)
        }


}
