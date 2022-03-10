package com.smith.lishe.model

import com.squareup.moshi.Json
import java.util.*

data class ListingModel(
    @field:Json(name ="_id") val _id: String,
    @field:Json(name ="title")val title: String,
    @field:Json(name ="description") val description: String,
    @field:Json(name ="creator") val creator: String,
    @field:Json(name ="latitude") val latitude: Float,
    @field:Json(name ="longitude") val longitude: Float,
    @field:Json(name ="expiration") val expiration: String,
    @field:Json(name ="status") val status: String,
    @field:Json(name ="individual") val individual: Boolean,
    @field:Json(name ="imageUrl") val imageUrl: String,
    @field:Json(name ="createdAt") val createdAt: String,
    @field:Json(name ="updatedAt") val updatedAt: String
)