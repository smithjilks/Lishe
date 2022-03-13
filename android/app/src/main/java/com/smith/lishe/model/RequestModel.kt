package com.smith.lishe.model

import com.squareup.moshi.Json

data class RequestModel(
    @field:Json(name ="_id") val _id: String,
    @field:Json(name ="creator")val creator: String,
    @field:Json(name ="listingId") val listingId: String,
    @field:Json(name ="status") val status: String,
    @field:Json(name ="createdAt") val createdAt: String,
    @field:Json(name ="updatedAt") val updatedAt: String,
    @field:Json(name ="listingDetails") val listingDetails: List<ListingModel>
)
