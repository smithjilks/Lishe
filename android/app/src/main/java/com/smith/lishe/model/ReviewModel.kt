package com.smith.lishe.model

import com.squareup.moshi.Json

data class ReviewModel(
    @field:Json(name ="_id") val _id: String,
    @field:Json(name = "creator") val creator: String,
    @field:Json(name = "createdFor") val createdFor: String,
    @field:Json(name = "historyId") val historyId: String,
    @field:Json(name = "description") val rating: String,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "updatedAt") val updatedAt: String,
    @field:Json(name = "historyDetails") val historyDetails: List<RequestModel>,
    @field:Json(name = "listingDetails") val listingDetails: List<ListingModel>



    )
