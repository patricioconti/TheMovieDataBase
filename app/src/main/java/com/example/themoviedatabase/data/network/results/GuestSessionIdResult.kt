package com.example.themoviedatabase.data.network.results

import com.squareup.moshi.Json

//Result from GET authentication/guest_session/new. With this we can get a valid guest_session_id
data class GuestSessionIdResult(
    @Json(name = "expires_at") val expiresAt : String,
    @Json(name = "guest_session_id") val guestSessionId: String,
    val success: Boolean
)