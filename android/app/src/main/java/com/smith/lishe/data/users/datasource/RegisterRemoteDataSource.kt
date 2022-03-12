package com.smith.lishe.data.users.datasource

import com.smith.lishe.model.RegisterApiModel
import com.smith.lishe.network.UserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RegisterRemoteDataSource(
    private val userApi: UserApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Fetches the latest news from the network and returns the result.
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchRegistrationData(firstName: RequestBody,
                                      lastName: RequestBody,
                                      email: RequestBody,
                                      phone: RequestBody,
                                      password: RequestBody,
                                      organisation: RequestBody,
                                      organisationName: RequestBody,
                                      userType: RequestBody,
                                      imageFile: MultipartBody.Part): RegisterApiModel =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous requests.
        withContext(ioDispatcher) {
            userApi.retrofitService.registerUser(
                firstName, lastName, email,
                phone, password, organisation,
                organisationName, userType, imageFile)
        }


}
