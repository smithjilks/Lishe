package com.smith.lishe.model

import com.squareup.moshi.Json

data class RequestDetailsModel(
    @field:Json(name ="listingId") val listingId: String,
    @field:Json(name ="creator")val creator: String
)