package com.smith.lishe.model

import com.squareup.moshi.Json

data class RequestApiModel(
    @field:Json(name ="message") val message: String,
)
