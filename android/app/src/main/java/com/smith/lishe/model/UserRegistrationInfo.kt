package com.smith.lishe.model

import com.squareup.moshi.Json

data class UserRegistrationInfo(
    @field:Json(name = "firstName") val firstName: String,
    @field:Json(name = "lastName") val lastName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phone") val phone: Int,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "organisation") val organisation: Boolean,
    @field:Json(name = "organisationName") val organisationName: String,
    @field:Json(name = "userType") val userType: String

    ) {
}