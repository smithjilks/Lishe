package com.smith.lishe.model

import com.squareup.moshi.Json

data class UpdateRequestModel(
    @field:Json(name ="status") val status: String,
)
