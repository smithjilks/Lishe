package com.smith.lishe.model

import com.squareup.moshi.Json
import org.json.JSONObject

data class ListingApiModel(
    @field:Json(name = "message") val message: String
) {
}