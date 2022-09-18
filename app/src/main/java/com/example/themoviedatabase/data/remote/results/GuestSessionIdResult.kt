package com.example.themoviedatabase.data.remote.results

import com.squareup.moshi.Json

//Result from GET authentication/guest_session/new. With this we can get a valid guest_session_id
data class GuestSessionIdResult(
    //expiresAt could be null
    @Json(name = "expires_at") val expiresAt : String?,
    //guestSessionId could be null
    @Json(name = "guest_session_id") val guestSessionId: String?,
    @Json(name = "success") val success: Boolean
)