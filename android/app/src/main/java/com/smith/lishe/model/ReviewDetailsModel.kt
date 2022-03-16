package com.smith.lishe.model

import com.squareup.moshi.Json

data class ReviewDetailsModel(
    @field:Json(name = "createdFor") val createdFor: String,
    @field:Json(name = "historyId") val historyId: String,
    @field:Json(name = "rating") val rating: Float,
    @field:Json(name = "description") val description: String,
)
