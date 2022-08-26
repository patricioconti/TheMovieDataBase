package com.example.themoviedatabase.data.network.results

import com.squareup.moshi.Json


//Result from POST /movie/{movie_id}/rating
data class MovieRatingResponse(
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "status_message") val statusMessage: String,
    val success: Boolean
)