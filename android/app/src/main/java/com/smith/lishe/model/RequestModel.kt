package com.smith.lishe.model

import com.squareup.moshi.Json

data class RequestModel(
    @field:Json(name ="_id") val _id: String,
    @field:Json(name ="creator")val title: String,
    @field:Json(name ="listingId") val description: String,
    @field:Json(name ="status") val status: String,
    @field:Json(name ="createdAt") val createdAt: Double,
    @field:Json(name ="updatedAt") val updatedAt: Double,
    @field:Json(name ="listingDetails") val listingDetails: List<ListingModel>
)
