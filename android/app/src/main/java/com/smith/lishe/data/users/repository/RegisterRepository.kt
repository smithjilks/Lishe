package com.smith.lishe.data.users.repository

import com.smith.lishe.data.users.datasource.RegisterRemoteDataSource
import com.smith.lishe.model.RegisterApiModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RegisterRepository(
    private val registerRemoteDataSource: RegisterRemoteDataSource,
    private val firstName: RequestBody,
    private val lastName: RequestBody,
    private val email: RequestBody,
    private val phone: RequestBody,
    private val password: RequestBody,
    private val organisation: RequestBody,
    private val organisationName: RequestBody,
    private val userType: RequestBody,
    private val profilePhoto: MultipartBody.Part
) {
    suspend fun fetchRegisterData(): RegisterApiModel =
        registerRemoteDataSource.fetchRegistrationData(firstName, lastName, email, phone, password, organisation, organisationName, userType, profilePhoto)
}

