package com.smith.lishe.data.users.repository

import com.smith.lishe.data.users.datasource.UserRemoteDataSource
import com.smith.lishe.model.UserDetailsModel

class UserDetailsRepository(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userId: String
) {
    suspend fun fetchUserDetails(): UserDetailsModel =
        userRemoteDataSource.fetchUserData(userId)
}

