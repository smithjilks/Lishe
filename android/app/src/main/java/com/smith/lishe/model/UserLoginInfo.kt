package com.smith.lishe.model

import com.squareup.moshi.Json

data class UserLoginInfo(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String
)