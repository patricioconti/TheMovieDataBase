package com.example.themoviedatabase.data.remote.results

import com.squareup.moshi.Json


//Result from POST /movie/{movie_id}/rating
data class MovieRatingResponse(
    @Json(name = "status_code") val statusCode: Int,
    @Json(name = "status_message") val statusMessage: String,
    @Json(name = "success") val success: Boolean
)