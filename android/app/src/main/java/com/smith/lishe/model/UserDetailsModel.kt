package com.smith.lishe.model

import com.squareup.moshi.Json
import org.json.JSONObject

data class UserDetailsModel(
    @field:Json(name = "firstName") val firstName: String,
    @field:Json(name = "lastName") val lastName: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phone") val phone: Double,
    @field:Json(name = "imageUrl") val imageUrl: String,
    @field:Json(name = "organisation") val organisation: Boolean,
    @field:Json(name = "organisationName") val organisationName: String,
    @field:Json(name = "userType") val userType: String,
    @field:Json(name = "userRating") val userRating: String
) {
}