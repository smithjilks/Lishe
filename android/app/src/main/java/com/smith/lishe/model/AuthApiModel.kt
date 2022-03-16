package com.smith.lishe.model

import com.squareup.moshi.Json

data class AuthApiModel(
    @field:Json(name = "userId") val userId: String,
    @field:Json(name = "token") val token: String,
    @field:Json(name = "userType") val userType: String
)
