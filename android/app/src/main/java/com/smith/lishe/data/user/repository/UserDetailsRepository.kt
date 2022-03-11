package com.smith.lishe.data.user.repository

import com.smith.lishe.data.user.datasource.AuthRemoteDataSource
import com.smith.lishe.data.user.datasource.UserRemoteDataSource
import com.smith.lishe.model.AuthApiModel
import com.smith.lishe.model.UserDetailsModel
import com.smith.lishe.model.UserLoginInfo

class UserDetailsRepository(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userId: String
) {
    suspend fun fetchUserDetails(): UserDetailsModel =
        userRemoteDataSource.fetchUserData(userId)
}

