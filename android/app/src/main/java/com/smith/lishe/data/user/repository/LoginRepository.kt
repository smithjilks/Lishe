package com.smith.lishe.data.user.repository

import com.smith.lishe.data.user.datasource.AuthRemoteDataSource
import com.smith.lishe.model.AuthApiModel
import com.smith.lishe.model.UserLoginInfo

class LoginRepository(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val body: UserLoginInfo
) {
    suspend fun fetchAuthData(): AuthApiModel =
        authRemoteDataSource.fetchAuthData(body)
}

