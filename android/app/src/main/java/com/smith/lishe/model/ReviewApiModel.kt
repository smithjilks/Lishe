package com.smith.lishe.model

import com.squareup.moshi.Json

data class ReviewApiModel(
    @field:Json(name ="message") val message: String,
)
